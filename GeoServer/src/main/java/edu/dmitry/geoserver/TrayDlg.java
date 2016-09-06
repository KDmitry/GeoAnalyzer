package edu.dmitry.geoserver;

import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.awt.*;
import java.io.IOException;

//https://gist.github.com/jewelsea/e231e89e8d36ef4e5d8a
public class TrayDlg extends Application {
    public enum  ProgramStatus {
        starting,
        working,
        closing
    }

    private Stage workPanelStage;
    private MenuItem statusItem;
    private SystemTray tray;
    private Manager manager;
    private TrayIcon trayIcon;
    private MenuItem workPanelItem;
    private MenuItem exitItem;
    private Logger logger = LogManager.getRootLogger();

    public TrayDlg() {
        manager = new Manager(this);
    }

    @Override
    public void start(final Stage stage) {
        addAppToTray();
        manager.start();
    }

    public void setStatus(ProgramStatus status) {
        switch (status) {
            case starting :
                statusItem.setLabel("Starting...");
                break;
            case working :
                statusItem.setLabel("Working...");
                break;
            case closing:
                statusItem.setLabel("Closing...");
                workPanelItem.setEnabled(false);
                exitItem.setEnabled(false);
                break;
            default:
                break;
        }
    }

    private void createWorkPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/WorkPanelDlg.fxml"));
            Parent window = loader.load();

            workPanelStage = new Stage();
            workPanelStage.setTitle("Working panel");
            workPanelStage.setScene(new Scene(window));
            workPanelStage.setResizable(false);

            WorkPanelDlg workPanelDlg = loader.<WorkPanelDlg>getController();
            workPanelDlg.init(this, manager);

            workPanelStage.setOnCloseRequest(we -> workPanelDlg.close());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void closedWorkPanelDlg() {
        workPanelStage = null;
        workPanelItem.setEnabled(true);
    }

    public void close() {
        logger.info("Close trayIcon");
        if (workPanelStage != null && workPanelStage.isShowing()) {
            workPanelStage.close();
        }
        logger.info("remove trayIcon");
        tray.remove(trayIcon);
        logger.info("Platform exit");
        Platform.exit();
        logger.info("System exit");
        System.exit(0);
    }

    private void addAppToTray() {
        try {
            Toolkit.getDefaultToolkit();

            if (!SystemTray.isSupported()) {
                logger.error("No system tray support, application exiting.");
                Platform.exit();
                System.exit(0);
            }

            Platform.setImplicitExit(false);

            PopupMenu popup = new PopupMenu();

            statusItem = new MenuItem();
            statusItem.setEnabled(false);
            popup.add(statusItem);
            popup.addSeparator();

            workPanelItem = new MenuItem("Working panel");
            workPanelItem.addActionListener(event -> {
                Platform.runLater(() -> {
                    workPanelItem.setEnabled(false);
                    createWorkPanel();
                    showWorkPanel();
                });
            });
            popup.add(workPanelItem);
            popup.addSeparator();

            exitItem = new MenuItem("Exit");
            exitItem.addActionListener(event -> {
                Platform.runLater(this::closeWorkPanel);
                logger.info("Close application");
                manager.close();
            });
            popup.add(exitItem);

            //Image image = ImageIO.read(getClass().getResourceAsStream("/images/icon.png"));
            Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icon.png"));
            trayIcon = new TrayIcon(image, "GeoDownloader", popup);
            trayIcon.setImageAutoSize(false);

            tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
        } catch (Exception e) {
            logger.error("Unable to init system tray");
        }
    }

    private void closeWorkPanel() {
        logger.info("Close work panel");
        if (workPanelStage != null && workPanelStage.isShowing()) {
            workPanelStage.close();
        }
    }

    private void showWorkPanel() {
        logger.info("Show work panel");
        if (workPanelStage != null) {
            workPanelStage.show();
        }
    }
}