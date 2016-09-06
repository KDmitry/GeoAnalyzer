package edu.dmitry.geoserver;

import edu.dmitry.geoserver.MapInteraction.LocationsStatisticRequest;
import edu.dmitry.geoserver.MapInteraction.LocationsStatisticRespond;
import edu.dmitry.geoserver.datamodel.ApplicationVersion;
import edu.dmitry.geoserver.datamodel.Downloader;
import edu.dmitry.geoserver.datamodel.LocationStatistic;
import edu.dmitry.geoserver.hibernate.HibernateUtil;
import edu.dmitry.geoserver.rpc.RpcServer;
import edu.dmitry.geoserver.task.Task;
import edu.dmitry.geoserver.taskresult.TaskResult;

import edu.dmitry.geoserver.telegram.TelegramBotApi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Manager extends Thread {
    private Logger logger = LogManager.getRootLogger();
    private RpcServer rpcServer;
    private TrayDlg trayDlg;
    private DataBaseAccess dataBaseAccess = new DataBaseAccess();
    private TaskManager taskManager;
    private MapRespondManager mapRespondManager;
    private DownloadersManager downloadersManager;
    private TaskResultManager taskResultManager;
    private Boolean workingStatus = true;
    //private TelegramBotApi telegramBotApi;

    public Manager(TrayDlg trayDlg) {
        this.trayDlg = trayDlg;
        rpcServer = new RpcServer(this);
        taskManager = new TaskManager(dataBaseAccess);
        mapRespondManager = new MapRespondManager(dataBaseAccess);
        downloadersManager = new DownloadersManager(taskManager);
        taskResultManager = new TaskResultManager(taskManager, dataBaseAccess);
       // telegramBotApi = new TelegramBotApi(this);
    }

    @Override
    public void run() {
        logger.info("Run manager");
        trayDlg.setStatus(TrayDlg.ProgramStatus.starting);
        logger.info("Start rpcServer");
        rpcServer.start();
        trayDlg.setStatus(TrayDlg.ProgramStatus.working);
        logger.info("Start taskManager");
        taskManager.start();
        logger.info("Working");
        while (isWorking()) {
            taskResultManager.processingResult();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
        logger.info("End working");
        logger.info("Stop taskManager");
        taskManager.stop();
    }

    public void close() {
        logger.info("Close manager");
        new Thread(() -> {
            trayDlg.setStatus(TrayDlg.ProgramStatus.closing);
            isWorking(false);
            try {
                join();
            } catch (InterruptedException e) {
            }
            logger.info("Send telegram message about closing");
            //telegramBotApi.sendMessage("Выключение сервера!");
            logger.info("rpcServer close");
            rpcServer.close();
            logger.info("save posts task to file");
            taskManager.savePostsTasks();
            logger.info("Shutdown Hibernate");
            HibernateUtil.shutdown();
            logger.info("trayDlg close");
            trayDlg.close();
        }).start();
    }

    public void subscribe(Observer observer) {
        logger.info("Subscribe: " + observer);
        if (observer instanceof WorkPanelDlg) {
            logger.info("DownloadersManager subscribe");
            downloadersManager.addObserver(observer);
            logger.info("TaskManager subscribe");
            taskManager.addObserver(observer);
            logger.info("DataBaseAccess subscribe");
            dataBaseAccess.addObserver(observer);
        }
    }

    public void unsubscribe(Observer observer) {
        logger.info("Unsubscribe: " + observer);
        if (observer instanceof WorkPanelDlg) {
            logger.info("DownloadersManager unsubscribe");
            downloadersManager.deleteObserver(observer);
        }
    }

    public String getStatistic() {
        StringBuilder statistic = new StringBuilder();
        Map<String, Downloader> downloaders = downloadersManager.getDownloaders();
        statistic.append("Клиент: Ков-во задач - Время работы\n\n");
        for (Map.Entry<String, Downloader> pair : downloaders.entrySet()) {
            Period period = new Period(pair.getValue().getStartWorkTime(), new DateTime());
            int days = period.getYears() * 365 + period.getMonths() * 30 + period.getWeeks() * 7 + period.getDays();
            statistic.append(pair.getValue().getName() + ": " + pair.getValue().getDoneTasksCount() + " - " +
                    String.format("%02d:%02d:%02d:%02d", days, period.getHours(), period.getMinutes(), period.getSeconds()) + "\n");
        }
        statistic.append("\nКол-во постов: " + dataBaseAccess.getPostsCount());
        return statistic.toString();
    }

    public Task[] getTasks(String computerName) {
        if (!downloadersManager.isDownloaderRight(computerName)) {
            return new Task[0];
        }

        logger.info("Get tasks for " + computerName);
        List<Task> tasksList = taskManager.getTasks();
        logger.info("Tasks count: " + tasksList.size());
        List<Integer> tasksId = new ArrayList<>();

        if (tasksList.size() > 0) {
            Task[] tasks = new Task[tasksList.size()];
            for (int i = 0; i < tasksList.size(); i++) {
                tasks[i] = tasksList.get(i);
                tasksId.add(tasksList.get(i).getTaskId());
            }
            logger.info("Set tasks " + tasksId + " for " + computerName);
            downloadersManager.setDownloaderTasks(computerName, tasksId);
            return tasks;
        } else {
            logger.info("Add new downloader: " + computerName);
            downloadersManager.addNewDownloader(computerName);
            return new Task[0];
        }
    }

    public void saveTasksResults(String computerName,  TaskResult[] tasksResults) {
        logger.info("Save task results from " + computerName);
        int countTaskResulsSuccess = taskResultManager.saveTaskResults(tasksResults,
                downloadersManager.getDownloaderTasks(computerName));
        logger.info("Count task resuls success: " + countTaskResulsSuccess);
        logger.info("Add done tasks count " + countTaskResulsSuccess + " for " + computerName);
        downloadersManager.addDoneTasks(computerName, countTaskResulsSuccess);
        logger.info("Clear tasks for " + computerName);
        downloadersManager.clearDownloaderTasks(computerName);
    }

    public ApplicationVersion checkUpdate(double version) {
        double newVersion = getApplicationVersion();
        ApplicationVersion applicationVersion = new ApplicationVersion();
        if (newVersion > version) {
            applicationVersion.setNewVersionStatus(true);
            applicationVersion.setApplicationPackage(convertZipToByte());
        } else {
            applicationVersion.setNewVersionStatus(false);
        }
        return applicationVersion;
    }

    public double getApplicationVersion() {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "ClientApplication/version.properties";
            inputStream = new FileInputStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                logger.error("property file '" + propFileName + "' not found in the classpath");
            }

            return Double.parseDouble(prop.getProperty("version"));
        } catch (Exception e) {
            logger.error("Exception: " + e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("Exception: " + e);
                }
            }
        }
        return -1;
    }

    public byte[] convertZipToByte(){
        try {
            return Files.readAllBytes(Paths.get("ClientApplication/GeoDownloader.zip"));
        } catch (IOException e) {
            return null;
        }
    }

    public void deleteClient(String computerName) {
        logger.info("Delete client: " + computerName);
        try {
           // telegramBotApi.sendMessage("Отключение клиента: " + computerName.split(":::")[0]);
        } catch (Exception e) {
            logger.error(e);
        }
        downloadersManager.deleteDownloader(computerName);
    }

    public LocationsStatisticRespond getLocations(LocationsStatisticRequest locationsStatisticRequest) {
        return mapRespondManager.getLocationsStatistic(locationsStatisticRequest);
    }

    public LocationStatistic getFullLocationStatistic(LocationStatistic locationStatistic) {
        return mapRespondManager.getFullLocationStatistic(locationStatistic);
    }

    public synchronized void isWorking(boolean status) {
        workingStatus = status;
    }

    public synchronized boolean isWorking() {
        return workingStatus;
    }
}
