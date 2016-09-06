package edu.Dmitry.geodownloader.rpc;

import edu.Dmitry.geodownloader.datamodel.ApplicationVersion;
import edu.Dmitry.geodownloader.task.Task;
import edu.Dmitry.geodownloader.taskresult.TaskResult;

import java.util.List;

public interface ClientMethods {
    Task[] getTasks(String computerName);
    boolean saveTasksResults(String computerName, TaskResult[] tasksResults);
    void disconectClient(String computerName);
    ApplicationVersion checkUpdate(String computerName, double version);
}