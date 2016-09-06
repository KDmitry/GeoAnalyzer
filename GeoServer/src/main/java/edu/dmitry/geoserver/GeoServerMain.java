package edu.dmitry.geoserver;

import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GeoServerMain {
    private static Logger logger = LogManager.getRootLogger();

    public static void main(String[] args) {
        Application.launch(TrayDlg.class);

        /*
        try {
            if(!SingleInstanceLock.lock()) {
                logger.error("The program is already running");
                System.exit(0);
            } else {
                Application.launch(TrayDlg.class);
            }
        } catch (IOException e) {
            logger.error("Couldn't create lock file");
            System.exit(0);
        }
        */
    }
}
