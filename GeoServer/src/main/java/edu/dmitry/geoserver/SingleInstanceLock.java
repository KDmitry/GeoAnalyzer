package edu.dmitry.geoserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class SingleInstanceLock {
    private static final String LOCK_FILEPATH = System.getProperty("java.io.tmpdir") + File.separator + "GeoServer.lock";
    private static final File lock = new File(LOCK_FILEPATH);
    private static boolean locked = false;
    private static Logger logger = LogManager.getRootLogger();

    private SingleInstanceLock() {}

    public static boolean lock() throws IOException {
        logger.info("Lock application");
        if(locked) return true;

        if(lock.exists()) return false;

        logger.info("Create new file");
        lock.createNewFile();
        lock.deleteOnExit();
        locked = true;
        return true;
    }
}