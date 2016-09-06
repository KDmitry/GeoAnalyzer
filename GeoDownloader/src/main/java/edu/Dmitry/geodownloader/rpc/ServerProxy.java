package edu.Dmitry.geodownloader.rpc;

import cz.eman.jsonrpc.client.AbstractClientProxy;
import cz.eman.jsonrpc.client.ClientProvider;
import edu.Dmitry.geodownloader.datamodel.ApplicationVersion;
import edu.Dmitry.geodownloader.task.Task;
import edu.Dmitry.geodownloader.taskresult.TaskResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class ServerProxy extends AbstractClientProxy<ClientMethods> implements ClientMethods {
    private Logger logger = LogManager.getRootLogger();

    public ServerProxy(ClientProvider clientProvider) {
        super(ClientMethods.class, clientProvider);
    }

    @Override
    public Task[] getTasks(String computerName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonArray = mapper.writeValueAsString(super.callMethod("getTasks", computerName));
            return mapper.readValue(jsonArray, Task[].class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean saveTasksResults(String computerName, TaskResult[] tasksResults) {
        try {
            super.callMethod("saveTasksResults", computerName, tasksResults);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void disconectClient(String computerName) {
        try {
            super.callMethod("disconectClient", computerName);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public ApplicationVersion checkUpdate(String computerName, double version) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonArray = mapper.writeValueAsString(super.callMethod("checkUpdate", new Object[] {computerName, version}));
            return mapper.readValue(jsonArray, ApplicationVersion.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}