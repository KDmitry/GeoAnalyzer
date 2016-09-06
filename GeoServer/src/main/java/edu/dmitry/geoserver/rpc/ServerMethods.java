package edu.dmitry.geoserver.rpc;

import edu.dmitry.geoserver.datamodel.ApplicationVersion;
import edu.dmitry.geoserver.taskresult.TaskResult;
import edu.dmitry.geoserver.task.Task;

public interface ServerMethods {
    Task[] getTasks(String computerName);
    void saveTasksResults(String computerName, TaskResult[] tasksResults);
    void disconectClient(String computerName);
    ApplicationVersion checkUpdate(String computerName, double version);
}
