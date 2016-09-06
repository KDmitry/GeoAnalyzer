package edu.Dmitry.geodownloader.startup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

class RunOnStartUpWindows {
    private static Logger logger = LogManager.getRootLogger();
    private final static String propFileName = "settings.properties";

    private RunOnStartUpWindows() {}

    static void install() {
        Properties prop = new Properties();
        try (InputStream inputStream = new FileInputStream(new File("").getAbsoluteFile() + "\\" + propFileName)) {
            prop.load(inputStream);
        } catch (Exception e) {
            logger.error("Exception: " + e);
            return;
        }

        prop.setProperty("autostart", "true");

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

        prop.setProperty("autostart", "false");

        try (OutputStream outputStream = new FileOutputStream(new File("").getAbsoluteFile() + "\\" + propFileName)) {
            prop.store(outputStream, null);
        } catch (Exception e) {
            logger.error("Exception: " + e);
        }
    }
}
