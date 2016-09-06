package edu.Dmitry.geodownloader.wakeup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunOnWakeUp {
    private final static String osName = System.getProperty("os.name");
    private static Logger logger = LogManager.getRootLogger();

    public static void install() {
        logger.info("RunOnWakeUp install");
        if (osName.startsWith("Mac OS")) {
            RunOnWakeUpMacOS.install();
        } else if (osName.startsWith("Windows")) {
            RunOnWakeUpWindows.install();
        }
    }

    public static void uninstall() {
        logger.info("RunOnWakeUp uninstall");
        if (osName.startsWith("Mac OS")) {
            RunOnWakeUpMacOS.uninstall();
        } else if (osName.startsWith("Windows")) {
            RunOnWakeUpWindows.uninstall();
        }
    }
}
