package edu.dmitry.geoserver;

import edu.dmitry.geoserver.datamodel.Downloader;
import edu.dmitry.geoserver.datamodel.DownloaderTableData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class WorkPanelDlg implements Observer {
    private Manager manager;
    private TrayDlg trayDlg;
    private Timer updateTimeTimer;
    private Logger logger = LogManager.getRootLogger();

    @FXML
    private TableView<DownloaderTableData> downloadersTable;
    @FXML
    private TableColumn downloaderNameCol;
    @FXML
    private TableColumn tasksCountCol;
    @FXML
    private TableColumn workTimeCol;
    @FXML
    private Label linksTasksCount;
    @FXML
    private Label postsTasksCount;
    @FXML
    public Label solvingTasksCount;
    @FXML
    private Label postsHour;
    @FXML
    private Label postsCount;

    private ObservableList<DownloaderTableData> data = FXCollections.observableArrayList();

    public WorkPanelDlg() {
    }

    private class UpdateTimeTask extends TimerTask {
        @Override
        public void run() {
            for (DownloaderTableData downloaderTD : data) {
                downloaderTD.setWorkTime(downloaderTD.getWorkTime());
            }
        }
    }

    public void init(TrayDlg trayDlg,Manager manager) {
        this.manager = manager;
        this.trayDlg = trayDlg;
        manager.subscribe(this);

        downloaderNameCol.setCellValueFactory(new PropertyValueFactory<>("downloaderName"));
        tasksCountCol.setCellValueFactory(new PropertyValueFactory<>("tasksCount"));
        workTimeCol.setCellValueFactory(new PropertyValueFactory<>("workTime"));
        downloadersTable.setItems(data);

        updateTimeTimer = new Timer();
        updateTimeTimer.schedule(new UpdateTimeTask(), 0, 1000);
    }

    public void close() {
        updateTimeTimer.cancel();
        manager.unsubscribe(this);
        trayDlg.closedWorkPanelDlg();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof DownloadersManager) {
            logger.info("Update downloader's table");
            Collection<Downloader> downloaders = (Collection<Downloader>) arg;

            Set downloadersTmp = new HashSet<>(data);

            for (Downloader downloader : downloaders) {
                boolean find = false;
                for (DownloaderTableData downloaderTD : data) {
                    if (downloaderTD.getDownloaderName().equals(downloader.getName()) &&
                            downloaderTD.getDownloaderUuid().equals(downloader.getUuid())) {
                        downloaderTD.setTasksCount(String.valueOf(downloader.getDoneTasksCount()));
                        downloadersTmp.remove(downloaderTD);
                        find = true;
                    }
                }

                if (!find) {
                    data.add(new DownloaderTableData(downloader.getName(), downloader.getUuid(), downloader.getDoneTasksCount(),
                            downloader.getStartWorkTime()));
                }
            }

            for (Object downloaderTD : downloadersTmp) {
                logger.info("Remove downloader: " + ((DownloaderTableData)downloaderTD).getDownloaderName());
                data.remove(downloaderTD);
            }
        } else if (o instanceof TaskManager) {
            logger.info("Update queues");
            TaskManager.TaskManagerArg taskArg = (TaskManager.TaskManagerArg) arg;

            Platform.runLater(() -> {
                if (taskArg.getQueueType() == TaskManager.TaskManagerArg.QueueType.postsTasksQueue) {
                    logger.info("Posts tasks count: " + taskArg.getCount());
                    postsTasksCount.setText(String.valueOf(taskArg.getCount()));
                } else if (taskArg.getQueueType() == TaskManager.TaskManagerArg.QueueType.linksTasksQueue) {
                    logger.info("Links tasks count: " + taskArg.getCount());
                    linksTasksCount.setText(String.valueOf(taskArg.getCount()));
                } else if (taskArg.getQueueType() == TaskManager.TaskManagerArg.QueueType.solvingTasksQueue) {
                    logger.info("Solving tasks count: " + taskArg.getCount());
                    solvingTasksCount.setText(String.valueOf(taskArg.getCount()));
                }
            });
        } else if (o instanceof DataBaseAccess) {
            logger.info("Posts count");
            long postsCount = (long) arg;

            Platform.runLater(() -> {
                logger.info("Posts count: " + postsCount);
                this.postsCount.setText(String.valueOf(postsCount));
            });
        }
    }

    public Label getLinksTasksCount() {
        return linksTasksCount;
    }

    public void setLinksTasksCount(Label linksTasksCount) {
        this.linksTasksCount = linksTasksCount;
    }

    public Label getPostsTasksCount() {
        return postsTasksCount;
    }

    public void setPostsTasksCount(Label postsTasksCount) {
        this.postsTasksCount = postsTasksCount;
    }

    public Label getPostsHour() {
        return postsHour;
    }

    public void setPostsHour(Label postsHour) {
        this.postsHour = postsHour;
    }

    public Label getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(Label postsCount) {
        this.postsCount = postsCount;
    }
}
