package edu.Dmitry.geodownloader.wakeup;

import com.jezhumble.javasysmon.JavaSysMon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

class RunOnWakeUpMacOS {
    private static Logger logger = LogManager.getRootLogger();
    private final static String fileSeparator=System.getProperty("file.separator");
    private final static String userHome=System.getProperty("user.home");

    private RunOnWakeUpMacOS() {}

    static void install() {
        logger.info("Install wakeup for Mac OS");

        try {
            if (!isInstalled()) {
                installWakeUpMacOS();
            }
            changeScript(true);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static void uninstall() {
        logger.info("Uninstall WakeUp Mac OS");
        try {
            logger.info("Get WakeUp file");
            File wakeUpFile = getWakeUpFile();
            logger.info("If exist plist");
            if (Files.exists(Paths.get(wakeUpFile.toString()))) {
                logger.info("launchctl unload");
                Runtime.getRuntime().exec("launchctl unload " + wakeUpFile.toString());
                logger.info("Delete plist");
                Files.delete(Paths.get(wakeUpFile.toString()));
            }
            logger.info("Change script");
            changeScript(false);
        } catch (Exception e) {
            logger.error("Can't uninstall: " + e.getMessage());
        }
    }

    private static boolean isInstalled() {
        try {
            return Files.exists(Paths.get(getWakeUpFile().toString()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    private static File getJarFile() throws URISyntaxException {
        String str = new File("").getAbsolutePath() + fileSeparator + "GeoDownloader.jar";
        logger.info("Get jar file: " + str);
        return new File(str);
    }

    private static String getWorkingDirectory() throws URISyntaxException {
        String directory = new File("").getAbsolutePath();
        logger.info("Working directory: " + directory);
        return directory;
    }

    private static File getWakeUpFile() throws Exception {
        logger.info("Get wakeup file for Mac OS");
        String filePath = userHome + "/Library/LaunchAgents/edu.dmitry.WakeUp"+getJarFile().getName().replaceFirst(".jar",".plist");
        logger.info("WakeUp file: " + filePath);
        return new File(filePath);
    }

    private static void installWakeUpMacOS() throws Exception {
        File wakeUpFile = getWakeUpFile();

        PrintWriter out = new PrintWriter(new FileWriter(wakeUpFile));
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
        out.println("<plist version=\"1.0\">");
        out.println("<dict>");
        out.println("   <key>Label</key>");
        out.println("   <string>edu.dmtiry.WakeUp"+getJarFile().getName().replaceFirst(".jar","")+"</string>");
        out.println("   <key>ProgramArguments</key>");
        out.println("   <array>");
        out.println("      <string>" + getWorkingDirectory() + "/SleepWatcherMacOS/sleepwatcher</string>");
        out.println("      <string>--verbose</string>");
        out.println("      <string>--wakeup</string>");
        out.println("      <string>" + getWorkingDirectory() + "/SleepWatcherMacOS/GeoDownloader.wakeup</string>");
        out.println("   </array>");
        out.println("   <key>RunAtLoad</key>");
        out.println("   <true/>");
        out.println("   <key>KeepAlive</key>");
        out.println("   <true/>");
        out.println("</dict>");
        out.println("</plist>");
        out.close();

        Runtime.getRuntime().exec("launchctl load " + getWakeUpFile());
    }

    public static void changeScript(boolean flag) {
        if (flag) {
            JavaSysMon javaSysMon = new JavaSysMon();
            int pid = javaSysMon.currentPid();
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("SleepWatcherMacOS/GeoDownloader.wakeup"))) {
                bufferedWriter.write("kill " + pid);
                bufferedWriter.newLine();
                bufferedWriter.write("cd " + new File("").getAbsolutePath());
                bufferedWriter.newLine();
                bufferedWriter.write("java -jar GeoDownloader.jar");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } else {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("SleepWatcherMacOS/GeoDownloader.wakeup"));
                bufferedWriter.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}