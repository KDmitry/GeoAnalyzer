package edu.Dmitry.geodownloader;

import edu.Dmitry.geodownloader.command.CommandExecutor;
import edu.Dmitry.geodownloader.task.Task;
import edu.Dmitry.geodownloader.taskresult.TaskResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private Logger logger = LogManager.getRootLogger();

    public List<TaskResult> executeTasks(List<Task> tasks) {
        List<TaskResult> tasksResults = new ArrayList<>();
        for (Task task : tasks) {
            TaskResult taskResult = CommandExecutor.execute(task);
            tasksResults.add(taskResult);
            if (taskResult != null && !taskResult.isSuccess()) {
                logger.error(taskResult.getTaskResultError().getMessage());
            }
        }
        return tasksResults;
    }
}
