package edu.dmitry.geoserver;

import edu.dmitry.geoserver.datamodel.Downloader;
import edu.dmitry.geoserver.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadersManager extends Observable {
    private TaskManager taskManager;
    private Map<String, Downloader> downloaders = new ConcurrentHashMap<>();
    private Timer timer;
    private Logger logger = LogManager.getRootLogger();

    private class CheckClientTimeTask extends TimerTask {
        @Override
        public void run() {
            logger.info("Check clients time");
            DateTime now = new DateTime();
            List<Integer> tasksIdList = new ArrayList<>();
            for (Map.Entry<String, Downloader> pair : downloaders.entrySet()) {
                if (pair.getValue().getStartTasksDateTime() != null &&
                        now.getMillis() - pair.getValue().getStartTasksDateTime().getMillis() > 300000) { // больше чем 5 минут
                    logger.info("Client " + pair.getKey() + " dont respond more than 5 minutes");
                    tasksIdList.addAll(pair.getValue().getTasks());
                    downloaders.remove(pair.getKey());
                    setChanged();
                    notifyObservers(downloaders.values());
                }
            }

            for (Integer taskId : tasksIdList) {
                Task task = taskManager.getSolvingTask(taskId);
                taskManager.deleteSolvingTask(taskId);
                taskManager.addFistTask(task);
            }
        }
    }

    public DownloadersManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public Map<String, Downloader> getDownloaders() {
        return new HashMap<>(downloaders);
    }

    public void addNewDownloader(String computerName) {
        if (!downloaders.containsKey(computerName)) {
            logger.info("Add new downloader: " + computerName);

            String[] args = computerName.split(":::");
            String name = args[0];
            String uuid = args[1];

            downloaders.put(computerName, new Downloader(name, uuid));
            setChanged();
            notifyObservers(downloaders.values());

            if (timer == null) {
                timer = new Timer();
                timer.schedule(new CheckClientTimeTask(), 60000, 60000); // раз в минуту
            }
        }
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        setChanged();
        notifyObservers(downloaders.values());
    }

    public void addDoneTasks(String computerName, int count) {
        logger.info("Add done tasks count: " + count + " for " + computerName);
        downloaders.get(computerName).setDoneTasksCount(count);
        setChanged();
        notifyObservers(downloaders.values());
    }

    public void setDownloaderTasks(String computerName, List<Integer> tasks) {
        logger.info("Set tasks for " + computerName);

        if (!downloaders.containsKey(computerName)) {
            addNewDownloader(computerName);
        }

        Downloader downloader = downloaders.get(computerName);
        downloader.setTasks(tasks);
        downloader.setStartTasksDateTime(new DateTime());
    }

    public void clearDownloaderTasks(String computerName) {
        logger.info("Clear tasks " + computerName);

        Downloader downloader = downloaders.get(computerName);
        downloader.setTasks(new ArrayList<>());
        downloader.setStartTasksDateTime(null);
    }

    public List<Integer> getDownloaderTasks(String computerName) {
        return downloaders.get(computerName).getTasks();
    }

    public void deleteDownloader(String computerName) {
        logger.info("Delete downloader " + computerName);
        downloaders.remove(computerName);

        setChanged();
        notifyObservers(downloaders.values());

        if (downloaders.size() == 0) {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }

    public boolean isDownloaderRight(String computerName) {
        logger.info("Check " + computerName);
        String[] args = computerName.split(":::");
        if (args.length != 2) {
            logger.info("Wrong computer name format");
            return false;
        }

        if (downloaders.containsKey(computerName)){
            if (downloaders.get(computerName).getTasks().size() != 0) {
                logger.error("Name and uuid exist");
                return false;
            }
        }
        return true;
    }
}
