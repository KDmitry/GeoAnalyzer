package edu.Dmitry.geodownloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Logger logger = LogManager.getRootLogger();
    private final static String propFileName = "config.properties";
    private final static String fileSeparator = System.getProperty("file.separator");

    public static String getServerAddress() {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            inputStream = new FileInputStream(new File("").getAbsoluteFile() + fileSeparator + propFileName);
            prop.load(inputStream);
            return prop.getProperty("address");
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
        return null;
    }

    public static int getServerPort() {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            inputStream = new FileInputStream(new File("").getAbsoluteFile() + fileSeparator + propFileName);
            prop.load(inputStream);
            return Integer.parseInt(prop.getProperty("port"));
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

    public static double getProgramVersion() {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            inputStream = new FileInputStream(new File("").getAbsoluteFile() + fileSeparator + propFileName);
            prop.load(inputStream);
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

}
