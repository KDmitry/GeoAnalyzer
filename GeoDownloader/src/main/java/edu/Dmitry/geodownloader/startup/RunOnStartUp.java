package edu.Dmitry.geodownloader.startup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunOnStartUp {
    private final static String osName = System.getProperty("os.name");
    private static Logger logger = LogManager.getRootLogger();

    public static void install() {
        logger.info("RunOnStartUp install");
        if (osName.startsWith("Mac OS")) {
            RunOnStartUpMacOS.install();
        } else if (osName.startsWith("Windows")) {
            RunOnStartUpWindows.install();
        }
    }

    public static void uninstall() {
        logger.info("RunOnStartUp uninstall");
        if (osName.startsWith("Mac OS")) {
            RunOnStartUpMacOS.uninstall();
        } else if (osName.startsWith("Windows")) {
            RunOnStartUpWindows.uninstall();
        }
    }
}
