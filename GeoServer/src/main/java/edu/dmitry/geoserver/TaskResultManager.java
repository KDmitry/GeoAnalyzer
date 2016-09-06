package edu.dmitry.geoserver;

import edu.dmitry.geoserver.taskresult.InstagramLocationLinksResult;
import edu.dmitry.geoserver.task.InstagramLocationLinksTask;
import edu.dmitry.geoserver.task.Task;
import edu.dmitry.geoserver.taskresult.InstagramLocationPostResult;
import edu.dmitry.geoserver.taskresult.TaskResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskResultManager {
    private TaskManager taskManager;
    private DataBaseAccess dataBaseAccess;
    private Queue<TaskResult> tasksResultsQueue = new ConcurrentLinkedQueue<>();
    private Queue<TaskResult> errorTasksResultsQueue = new ConcurrentLinkedQueue<>();
    private Map<Integer, Integer> errorTasks = new HashMap<>();
    private Logger logger = LogManager.getRootLogger();

    public TaskResultManager(TaskManager taskManager, DataBaseAccess dataBaseAccess) {
        this.taskManager = taskManager;
        this.dataBaseAccess = dataBaseAccess;
    }

    public void processingResult() {
        TaskResult taskResult = tasksResultsQueue.poll();
        if (taskResult != null) {
            logger.info("Tasks results queue size = " + (tasksResultsQueue.size() + 1));
            if (taskResult instanceof InstagramLocationPostResult) {
                logger.info("Processing Instagram location post result");
                InstagramLocationPostResult locationListsResult = (InstagramLocationPostResult) taskResult;
                logger.info("Add new post: " + locationListsResult.getInstagramPost().id);
                dataBaseAccess.addNewPost(locationListsResult.getInstagramPost());
            } else if (taskResult instanceof InstagramLocationLinksResult) {
                logger.info("Processing Instagram location links result");
                InstagramLocationLinksResult locationLinksResult = (InstagramLocationLinksResult) taskResult;
                InstagramLocationLinksTask task =
                        (InstagramLocationLinksTask)taskManager.getSolvingTask(taskResult.getTaskId());

                if (locationLinksResult.getPostMaxId() != -1) {
                    taskManager.changeLocationMaxPost(task.getLocationId(), locationLinksResult.getPostMaxId());
                }

                for (String link : locationLinksResult.getPostsLinks()) {
                    logger.info("Add link: " + link);
                    taskManager.addLastTask(taskManager.createInstagramLocationPostTask(link));
                }

                if (locationLinksResult.getNextPage() != null) {
                    logger.info("Exist next page, create link task for downloading next posts' links");
                    taskManager.addFistTask(taskManager.createInstagramLocationLinksTask(task.getLocationId(), -1,
                            locationLinksResult.getPrevPostMaxId(),
                            locationLinksResult.getNextPage()));
                }
            }
            logger.info("Delete solving task " + taskResult.getTaskId());
            taskManager.deleteSolvingTask(taskResult.getTaskId());
        }


        taskResult = errorTasksResultsQueue.poll();
        if (taskResult != null) {
            logger.info("Error tasks results queue size = " + (errorTasksResultsQueue.size() + 1));
            logger.info("Error task result: " + taskResult.getTaskId());

            if (!errorTasks.containsKey(taskResult.getTaskId())) {
                errorTasks.put(taskResult.getTaskId(), 0);
            }

            int count = errorTasks.get(taskResult.getTaskId()) + 1;
            logger.info("Try count: " + count);

            if (count > 3) {
                errorTasks.remove(taskResult.getTaskId());
                taskManager.deleteSolvingTask(taskResult.getTaskId());
                logger.info("Cant do task: " + taskResult.getTaskId());
            } else {
                Task task = taskManager.getSolvingTask(taskResult.getTaskId());
                taskManager.deleteSolvingTask(taskResult.getTaskId());
                errorTasks.put(task.getTaskId(), count);
                logger.info("Add task: " + task.getTaskId());
                taskManager.addLastTask(task);
            }
        }
    }

    public int saveTaskResults(TaskResult[] tasksResults, List<Integer> requiredTasksId) {
        int count = 0;
        if (tasksResults != null) {
            if (checkTaskResults(requiredTasksId, tasksResults)) {
                for (int i = 0; i < tasksResults.length; i++) {
                    if (tasksResults[i].isSuccess()) {
                        logger.info("Add Task result id " + tasksResults[i].getTaskId() + " to tasks results queue");
                        tasksResultsQueue.add(tasksResults[i]);
                        count++;
                    } else {
                        logger.error("Add Task result id " + tasksResults[i].getTaskId() + " to error results queue, error: "
                                + tasksResults[i].getTaskResultError().getMessage());
                        errorTasksResultsQueue.add(tasksResults[i]);
                    }
                }
            } else {
                logger.info("Wrong results");
            }
        }
        return count;
    }

    public boolean checkTaskResults(List<Integer> requiredTasksId, TaskResult[] tasksResults) {
        for (int i = 0; i < tasksResults.length; i++) {
            if (!requiredTasksId.contains(tasksResults[i].getTaskId())) {
                return false;
            }
        }
        return true;
    }
}
