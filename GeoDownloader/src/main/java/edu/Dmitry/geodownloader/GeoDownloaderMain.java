package edu.Dmitry.geodownloader;

import edu.Dmitry.geodownloader.singleinstancelock.SingleInstanceLock;
import edu.Dmitry.geodownloader.startup.RunOnStartUp;
import edu.Dmitry.geodownloader.wakeup.RunOnWakeUp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.util.Properties;

public class GeoDownloaderMain {
    private static Logger logger = LogManager.getRootLogger();

    public static void main(String[] args) {
        if (System.getProperty("os.name").startsWith("Windows")) {
            logger.info("Os name: Windows");
            if (args.length == 0 || (args.length == 1 && !args[0].equals("Service"))) {
                needStartOnWindows(true);
                //System.exit(0);
            } else {
                /*
                boolean result = false;
                File directory = new File(new File("").getAbsoluteFile() + "\\Client").getAbsoluteFile();
                if (directory.exists()) {
                    result = (System.setProperty("user.dir", directory.getAbsolutePath()) != null);
                }
                logger.info("Set working directory to " + directory.toString() + ": " + result);
                */
                needStartOnWindows(false);
            }
        } else {
            try {
                if(!SingleInstanceLock.lock()) {
                    logger.error("The program is already running");
                    System.exit(0);
                }
            } catch (Exception e) {
                logger.error("Couldn't create lock file");
                System.exit(0);
            }
        }

        RunOnStartUp.install();
        RunOnWakeUp.install();

        new TrayDlg();
    }

    private static void needStartOnWindows(boolean flag) {
        logger.info("Need to start: " + flag);

        Properties prop = new Properties();

        try (InputStream inputStream = new FileInputStream(new File("").getAbsoluteFile() + "\\settings.properties")) {
            prop.load(inputStream);
        } catch (Exception e) {
            logger.error("Exception: " + e);
            return;
        }

        prop.setProperty("needstart", String.valueOf(flag));

        try (OutputStream outputStream = new FileOutputStream(new File("").getAbsoluteFile() + "\\settings.properties")) {
            prop.store(outputStream, null);
        } catch (Exception e) {
            logger.error("Exception: " + e);
        }
    }
}
