package edu.Dmitry.geodownloader.wakeup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

class RunOnWakeUpWindows {
    private static Logger logger = LogManager.getRootLogger();
    private final static String propFileName = "settings.properties";

    private RunOnWakeUpWindows() {}

    static void install() {
        Properties prop = new Properties();
        try (InputStream inputStream = new FileInputStream(new File("").getAbsoluteFile() + "\\" + propFileName)) {
            prop.load(inputStream);
        } catch (Exception e) {
            logger.error("Exception: " + e);
            return;
        }

        prop.setProperty("wakeup", "true");

        try (OutputStream outputStream = new FileOutputStream(new File("").getAbsoluteFile() + "\\" + propFileName)) {
            prop.store(outputStream, null);
        } catch (Exception e) {
            logger.error("Exception: " + e);
        }
    }

    static void uninstall() {
        Properties prop = new Properties();
        try (InputStream inputStream = new FileInputStream(new File("").getAbsoluteFile() + "\\" + propFileName)) {
            prop.load(inputStream);
        } catch (Exception e) {
            logger.error("Exception: " + e);
            return;
        }

        prop.setProperty("wakeup", "false");

        try (OutputStream outputStream = new FileOutputStream(new File("").getAbsoluteFile() + "\\" + propFileName)) {
            prop.store(outputStream, null);
        } catch (Exception e) {
            logger.error("Exception: " + e);
        }
    }
}
