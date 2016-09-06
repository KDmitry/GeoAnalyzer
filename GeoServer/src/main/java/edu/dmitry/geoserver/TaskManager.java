package edu.dmitry.geoserver;

import edu.dmitry.geoserver.task.InstagramLocationPostTask;
import edu.dmitry.geoserver.task.InstagramLocationLinksTask;
import edu.dmitry.geoserver.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskManager extends Observable {
    private Deque<Task> linksTasksQueue = new ConcurrentLinkedDeque<>();
    private Deque<Task> postsTasksQueue = new ConcurrentLinkedDeque<>();
    private Map<Integer, Task> solvingTasksList = new ConcurrentHashMap<>();
    private DataBaseAccess dataBaseAccess;
    private Timer timer;
    private volatile Integer taskId = 0;
    private Map<Long, Long> locationsMaxPosts = new HashMap<>();
    private Logger logger = LogManager.getRootLogger();

    private class LocationLinksPusherTask extends TimerTask {
        @Override
        public void run() {
            if (linksTasksQueue.size() == 0) {
                logger.info("Push location links tasks");
                logger.info("Get locations max posts");
                Map<Long, Long> locationsMaxPostsDB = dataBaseAccess.getLocationsMaxPosts();
                if (locationsMaxPostsDB != null) {
                    logger.info("Locations max posts count: " + locationsMaxPostsDB.size());
                    Map<Long, Long> locationsMaxPostsTmp = new HashMap<>(locationsMaxPosts);
                    for (Map.Entry<Long, Long> pair : locationsMaxPostsDB.entrySet()) {
                        if (!locationsMaxPosts.containsKey(pair.getKey())) {
                            logger.info("Add to map of locations max posts: " + pair.getKey());
                            locationsMaxPosts.put(pair.getKey(), pair.getValue());
                        }
                        locationsMaxPostsTmp.remove(pair.getKey());
                    }

                    for (Map.Entry<Long, Long> pair : locationsMaxPostsTmp.entrySet()) {
                        logger.info("Delete from map of locations max posts " + pair.getKey());
                        locationsMaxPosts.remove(pair.getKey());
                    }
                }

                for (Map.Entry<Long, Long> pair : locationsMaxPosts.entrySet()) {
                    if (pair.getValue() != -1) {
                        addLastTask(createInstagramLocationLinksTask(pair.getKey(), pair.getValue(), pair.getValue(), null));
                        pair.setValue(-1L);
                    } else {
                        logger.error("Don't create task for locationId = " + pair.getKey() + ", max post id = -1");
                    }
                }
            }
        }
    }

    public static class TaskManagerArg {
        enum QueueType {
            postsTasksQueue,
            linksTasksQueue,
            solvingTasksQueue
        }

        private QueueType queueType;
        private int count;

        public TaskManagerArg(QueueType queueType, int count) {
            this.queueType = queueType;
            this.count = count;
        }

        public QueueType getQueueType() {
            return queueType;
        }

        public void setQueueType(QueueType queueType) {
            this.queueType = queueType;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public TaskManager(DataBaseAccess dataBaseAccess) {
        this.dataBaseAccess = dataBaseAccess;
        getPostsTasks();
    }

    public void changeLocationMaxPost(long locationId, long maxPostId) {
        logger.info("Change location " + locationId + " max post " + maxPostId);
        if (locationsMaxPosts.containsKey(locationId)) {
            logger.info("Save location max post in DB");
            dataBaseAccess.saveLocationMaxPost(locationId, maxPostId);
            logger.info("Save location max post in list");
            locationsMaxPosts.put(locationId, maxPostId);
        }
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        notifyAll(new TaskManagerArg(TaskManagerArg.QueueType.postsTasksQueue, postsTasksQueue.size()));
        notifyAll(new TaskManagerArg(TaskManagerArg.QueueType.linksTasksQueue, linksTasksQueue.size()));
        notifyAll(new TaskManagerArg(TaskManagerArg.QueueType.solvingTasksQueue, solvingTasksList.size()));
    }

    public void getPostsTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader("PostsTasks.txt"))) {
            while (reader.ready()) {
                String links = reader.readLine();
                addLastTask(createInstagramLocationPostTask(links));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        File file = new File("PostsTasks.txt");
        file.delete();
    }

    public void savePostsTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("PostsTasks.txt"))) {
            InstagramLocationPostTask task = (InstagramLocationPostTask) postsTasksQueue.poll();
            while (task != null) {
                writer.write(task.getPostLink());
                writer.newLine();
                task = (InstagramLocationPostTask) postsTasksQueue.poll();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public int getTaskId() {
        synchronized (taskId) {
            if (taskId > 10000000) {
                taskId = 0;
            }
            logger.info("Next task id = " + (taskId + 1));
            return ++taskId;
        }
    }

    public void start() {
        logger.info("Start timer");
        timer = new Timer();
        timer.schedule(new LocationLinksPusherTask(), 0, 5000);
    }

    public void stop() {
        if (timer != null) {
            logger.info("Stop timer");
            timer.cancel();
            timer = null;
        }
    }

    public Task createInstagramLocationLinksTask(long locationId, long maxPostId, long prevPostMaxId, String nextPage) {
        int taskId = getTaskId();
        logger.info("Create Instagram location links task: taskId = " +
                taskId + " locationId = " + locationId + " max post id = " + maxPostId +
                " prev post id = " + prevPostMaxId + " next page = " + nextPage);
        return new InstagramLocationLinksTask(taskId, locationId, maxPostId, prevPostMaxId, nextPage);
    }

    public Task createInstagramLocationPostTask(String link) {
        int taskId = getTaskId();
        logger.info("Create Instagram location post task: taskId = " + taskId + " link = " + link);
        return new InstagramLocationPostTask(taskId, link);
    }

    public void addLastTask(Task task) {
        if (task instanceof InstagramLocationPostTask) {
            logger.info("Add task " + task.getTaskId() + " to posts tasks queue");
            postsTasksQueue.addLast(task);
            notifyAll(new TaskManagerArg(TaskManagerArg.QueueType.postsTasksQueue, postsTasksQueue.size()));
        } else if (task instanceof InstagramLocationLinksTask) {
            logger.info("Add task " + task.getTaskId() + " to links tasks queue");
            linksTasksQueue.addLast(task);
            notifyAll(new TaskManagerArg(TaskManagerArg.QueueType.linksTasksQueue, linksTasksQueue.size()));
        }
    }

    public void addFistTask(Task task) {
        if (task instanceof InstagramLocationPostTask) {
            logger.info("Add task " + task.getTaskId() + " to first position of posts tasks queue");
            postsTasksQueue.addFirst(task);
            notifyAll(new TaskManagerArg(TaskManagerArg.QueueType.postsTasksQueue, postsTasksQueue.size()));
        } else if (task instanceof InstagramLocationLinksTask) {
            logger.info("Add task " + task.getTaskId() + " to first position of links tasks queue");
            linksTasksQueue.addFirst(task);
            notifyAll(new TaskManagerArg(TaskManagerArg.QueueType.linksTasksQueue, linksTasksQueue.size()));
        }
    }

    public void notifyAll(TaskManagerArg taskManagerArg) {
        setChanged();
        notifyObservers(taskManagerArg);
    }

    public Task getSolvingTask(int taskId) {
        return solvingTasksList.get(taskId);
    }

    public void deleteSolvingTask(int taskId) {
        logger.info("Delete solving task " + taskId);
        solvingTasksList.remove(taskId);
        notifyAll(new TaskManagerArg(TaskManagerArg.QueueType.solvingTasksQueue, solvingTasksList.size()));
    }

    public List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();

        int postsTasksCount = 20;
        int linksTasksCount = 5;

        if (postsTasksQueue.size() > 1000) {
            postsTasksCount = 30;
            linksTasksCount = 0;
        }

        Task task = postsTasksQueue.poll();
        notifyAll(new TaskManagerArg(TaskManagerArg.QueueType.postsTasksQueue, postsTasksQueue.size()));
        for (int i = 0; i < postsTasksCount && task != null; i++) {
            logger.info("Get " + task.getTaskId() + " from posts tasks queue");
            tasksList.add(task);
            solvingTasksList.put(task.getTaskId(), task);
            notifyAll(new TaskManagerArg(TaskManagerArg.QueueType.solvingTasksQueue, solvingTasksList.size()));
            if (i + 1 < postsTasksCount) {
                task = postsTasksQueue.poll();
            }
        }

        if (linksTasksCount != 0) {
            task = linksTasksQueue.poll();
            notifyAll(new TaskManagerArg(TaskManagerArg.QueueType.linksTasksQueue, linksTasksQueue.size()));
            for (int i = 0; i < linksTasksCount && task != null; i++) {
                logger.info("Get " + task.getTaskId() + " from links tasks queue");
                tasksList.add(task);
                solvingTasksList.put(task.getTaskId(), task);
                notifyAll(new TaskManagerArg(TaskManagerArg.QueueType.solvingTasksQueue, solvingTasksList.size()));
                if (i + 1 < linksTasksCount) {
                    task = linksTasksQueue.poll();
                }
            }
        }

        return tasksList;
    }
}
