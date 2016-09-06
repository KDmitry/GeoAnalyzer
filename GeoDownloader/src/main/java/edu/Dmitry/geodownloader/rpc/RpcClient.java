package edu.Dmitry.geodownloader.rpc;

import cz.eman.jsonrpc.client.TcpJsonClient;
import edu.Dmitry.geodownloader.ConfigReader;
import edu.Dmitry.geodownloader.datamodel.ApplicationVersion;
import edu.Dmitry.geodownloader.task.Task;
import edu.Dmitry.geodownloader.taskresult.TaskResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RpcClient {
    private ServerProxy proxy;
    private Logger logger = LogManager.getRootLogger();
    private String address;
    private int port;
    private final String defaultAddress = "";
    private final int defaultPort = 0;

    public RpcClient() {

    }

    public boolean open() {
        try {
            address = ConfigReader.getServerAddress();
            if (address == null) {
                address = defaultAddress;
            }

            port = ConfigReader.getServerPort();
            if (port == -1) {
                port = defaultPort;
            }

            logger.info("Open rpc connection : address - " + address + ", port - " + port);
            proxy = new ServerProxy(new TcpJsonClient(new InetSocketAddress(address, port)));
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public ApplicationVersion checkUpdate(String computerName, double version) {
        logger.info("Check update");
        ApplicationVersion applicationVersion = proxy.checkUpdate(computerName, version);
        if (applicationVersion != null) {
            return applicationVersion;
        } else {
            logger.info("No response, application version is null");
            return null;
        }
    }

    public List<Task> getTasks(String computerName) {
        logger.info("Get tasks");
        Task[] tasks = proxy.getTasks(computerName);
        if (tasks != null) {
            List<Task> result = new ArrayList<>();
            for (int i = 0; i < tasks.length; i++) {
                result.add(tasks[i]);
            }
            logger.info("Tasks count : " + result.size());
            return result;
        } else {
            logger.info("No response, tasks are null");
            return null;
        }

    }

    public boolean saveTasksResults(String computerName, List<TaskResult> tasksResults) {
        TaskResult[] tasksResultsArray;
        if (tasksResults!= null) {
            tasksResultsArray = new TaskResult[tasksResults.size()];
            for (int i = 0; i < tasksResults.size(); i++) {
                TaskResult taskResult = tasksResults.get(i);
                tasksResultsArray[i] = taskResult;
            }
        } else {
            tasksResultsArray = new TaskResult[0];
        }

        logger.info("Send task results");
        return proxy.saveTasksResults(computerName, tasksResultsArray);
    }

    public void disconectClient(String computerName) {
        proxy.disconectClient(computerName);
    }

    public void close() {
        if (proxy != null) {
            try {
                proxy.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
