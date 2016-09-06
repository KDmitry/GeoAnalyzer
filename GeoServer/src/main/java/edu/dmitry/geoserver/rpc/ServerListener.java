package edu.dmitry.geoserver.rpc;

import edu.dmitry.geoserver.MapInteraction.LocationsStatisticRequest;
import edu.dmitry.geoserver.MapInteraction.LocationsStatisticRespond;
import edu.dmitry.geoserver.datamodel.ApplicationVersion;
import edu.dmitry.geoserver.datamodel.LocationStatistic;
import edu.dmitry.geoserver.taskresult.TaskResult;
import edu.dmitry.geoserver.Manager;
import edu.dmitry.geoserver.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerListener implements ServerMethods, MapMethods {
    private Manager manager;
    private Logger logger = LogManager.getRootLogger();

    public ServerListener(Manager manager) {
        this.manager = manager;
    }

    @Override
    public Task[] getTasks(String computerName) {
        logger.info("Get tasks for " + computerName);
        return manager.getTasks(computerName);
    }

    @Override
    public void saveTasksResults(String computerName,  TaskResult[] tasksResults) {
        logger.info("Save tasks results from " + computerName);
        manager.saveTasksResults(computerName, tasksResults);
    }

    @Override
    public void disconectClient(String computerName) {
        logger.info("Disconect client: " + computerName);
        manager.deleteClient(computerName);
    }

    @Override
    public ApplicationVersion checkUpdate(String computerName, double version) {
        logger.info("Check update from client: " + computerName);
        return manager.checkUpdate(version);
    }

    @Override
    public LocationsStatisticRespond getLocationsStatistic(LocationsStatisticRequest locationsStatisticRequest) {
        logger.info("Get locations for map");
        return manager.getLocations(locationsStatisticRequest);
    }

    @Override
    public LocationStatistic getFullLocationStatistic(LocationStatistic locationStatistic) {
        return manager.getFullLocationStatistic(locationStatistic);
    }
}
