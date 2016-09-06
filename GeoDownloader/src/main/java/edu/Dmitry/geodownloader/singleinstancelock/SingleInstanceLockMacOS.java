package edu.Dmitry.geodownloader.singleinstancelock;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.ProcessInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

class SingleInstanceLockMacOS {
    private static final String LOCK_FILEPATH = System.getProperty("java.io.tmpdir") + File.separator + "GeoDownloader.lock";
    private static final File lock = new File(LOCK_FILEPATH);
    private static boolean locked = false;
    private static Logger logger = LogManager.getRootLogger();

    private SingleInstanceLockMacOS() {}

    static boolean lock() throws IOException {
        logger.info(LOCK_FILEPATH);

        if(locked) return true;

        if(lock.exists()) {
            logger.info("Lock file exists, check pid");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(lock));
            int pid;
            try {
                pid = Integer.parseInt(bufferedReader.readLine());
            } catch (Exception e) {
                logger.error(e.getMessage());
                return false;
            }

            logger.info("Pid from file = " + pid);
            JavaSysMon javaSysMon = new JavaSysMon();
            ProcessInfo[] processInfos = javaSysMon.processTable();
            for(ProcessInfo processInfo : processInfos)
            {
                if (processInfo.getPid() == pid) {
                    if (processInfo.getName().equals("GeoDownloaderMain") || processInfo.getName().equals("java") ||
                            processInfo.getName().equals("AppMain")) {
                        logger.info("Application with pid = " + pid + " is working");
                        return false;
                    } else {
                        break;
                    }
                }
            }
            logger.info("Application with pid = " + pid + " isnt working");
        }

        if (lock.createNewFile()) {
            logger.info("Create new lock file");
            JavaSysMon javaSysMon = new JavaSysMon();
            int pid = javaSysMon.currentPid();
            /*
            String proccessName = "";
            ProcessInfo[] processInfos = javaSysMon.processTable();
            for(ProcessInfo processInfo : processInfos) {
                if (processInfo.getPid() == pid) {
                    proccessName = processInfo.getName();
                    break;
                }
            }
            */
            logger.info("Application pid = " + pid);
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(lock))) {
                bufferedWriter.write(String.valueOf(pid));
            } catch (Exception e) {
                logger.error(e.getMessage());
                return false;
            }
        }

        lock.deleteOnExit();
        locked = true;
        return true;
    }
}
