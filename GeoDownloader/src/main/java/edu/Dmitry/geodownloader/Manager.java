package edu.Dmitry.geodownloader;

import edu.Dmitry.geodownloader.datamodel.ApplicationVersion;
import edu.Dmitry.geodownloader.rpc.RpcClient;
import edu.Dmitry.geodownloader.task.Task;
import edu.Dmitry.geodownloader.taskresult.TaskResult;
import edu.Dmitry.geodownloader.wakeup.RunOnWakeUp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Manager extends Thread {
    private TrayDlg trayDlg;
    private RpcClient rpcClient;
    private TaskManager taskManager;
    private Boolean workingStatus = true;
    private Boolean updateStatus = false;
    private Logger logger = LogManager.getRootLogger();
    private Timer updateTimer;
    private String computerName;
    private double version;

    public Manager(TrayDlg trayDlg) {
        this.trayDlg = trayDlg;
        rpcClient = new RpcClient();
        taskManager = new TaskManager();
        setClientInfo();
    }

    public class CheckUpdateTask extends TimerTask {
        @Override
        public void run() {
            checkUpdate();
        }

        public void checkUpdate() {
            trayDlg.setStatus(TrayDlg.ProgramStatus.checkUpdate);
            ApplicationVersion applicationVersion = rpcClient.checkUpdate(computerName, version);
            if (applicationVersion != null && applicationVersion.isNewVersion()) {
                logger.info("New version available");
                setUpdateStatus(writeZipFromByte(applicationVersion.getApplicationPackage()));
            } else {
                logger.info("No new version");
            }
            trayDlg.setStatus(TrayDlg.ProgramStatus.working);
        }

        private boolean writeZipFromByte(byte[] fileContent) {
            try {
                Files.write(Paths.get("GeoDownloader.zip"), fileContent);
                return true;
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            return false;
        }
    }

    @Override
    public void run() {
        logger.info("Start manager run");

        openConnection();

        if (isWorking()) {
            logger.info("Check update");
            new CheckUpdateTask().checkUpdate();

            logger.info("Start timer for checking update");
            updateTimer = new Timer();
            updateTimer.schedule(new CheckUpdateTask(), 3600000, 3600000); // раз в час

            logger.info("Computer name : " + computerName);

            while (isWorking() && !isNeedUpdate()) {
                logger.info("Get tasks");
                List<Task> tasks = rpcClient.getTasks(computerName);
                if (tasks != null) {
                    logger.info("Tasks count : " + tasks.size());
                    if (tasks.size() > 0) {
                        logger.info("Execute tasks");
                        List<TaskResult> tasksResults = taskManager.executeTasks(tasks);
                        logger.info("Task results count : " + tasksResults.size());
                        logger.info("Send task results");
                        if (!rpcClient.saveTasksResults(computerName, tasksResults)) {
                            logger.info("Cant send results, try reopen connection");
                            openConnection();
                        }
                    } else {
                        logger.info("No tasks");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                        }
                    }
                } else {
                    logger.info("No response, try reopen connection");
                    openConnection();
                }
            }
            rpcClient.disconectClient(computerName);

            if (isNeedUpdate()) {
                close();
            }
        }
        logger.info("Stop manager run");
    }

    public void close() {
        new Thread(() -> {
            logger.info("Stop program");
            trayDlg.setStatus(TrayDlg.ProgramStatus.closing);
            isWorking(false);
            try {
                join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
            rpcClient.close();

            RunOnWakeUp.uninstall();

            if (isNeedUpdate()) {
                try {
                    logger.info("Start launcher");
                    Runtime.getRuntime().exec("java -jar Launcher.jar");
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }

            trayDlg.closeTray();
        }).start();
    }

    public synchronized void isWorking(boolean status) {
        workingStatus = status;
    }

    public synchronized boolean isWorking() {
        return workingStatus;
    }

    public synchronized boolean isNeedUpdate() {
        return updateStatus;
    }

    public synchronized void setUpdateStatus(Boolean updateStatus) {
        this.updateStatus = updateStatus;
    }

    private void openConnection() {
        trayDlg.setStatus(TrayDlg.ProgramStatus.serverConnecting);

        logger.info("Open RPC connection");
        if (!rpcClient.open()) {
            logger.info("RPC connection doesnt opened");
            waitRpcConnection();
        }
        if (isWorking()) {
            logger.info("RPC connection are opened");
        } else {
            logger.info("Program is stopped");
        }

        trayDlg.setStatus(TrayDlg.ProgramStatus.working);
    }

    private void setClientInfo() {
        try {
            version = ConfigReader.getProgramVersion();
            String hostName = InetAddress.getLocalHost().getHostName();
            UUID uuid = UUID.randomUUID();
            if (version != -1) {
                computerName = hostName + "-" + version + ":::" + uuid;
            } else {
                computerName = hostName + ":::" + uuid;
            }
        } catch (UnknownHostException ex) {
            logger.error(ex.getMessage());
            computerName = "UnknownName";
        }
    }

    private void waitRpcConnection() {
        logger.info("Open RPC connection");
        boolean rpcConExist = rpcClient.open();
        while (!rpcConExist && isWorking()) {
            logger.info("RPC connection doesnt opened");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
            logger.info("Open RPC connection");
            rpcConExist = rpcClient.open();
        }
    }
}
