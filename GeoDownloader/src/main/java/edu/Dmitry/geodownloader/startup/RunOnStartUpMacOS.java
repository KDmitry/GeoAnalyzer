package edu.Dmitry.geodownloader.startup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

class RunOnStartUpMacOS {
    private static Logger logger = LogManager.getRootLogger();
    private final static String fileSeparator=System.getProperty("file.separator");
    private final static String userHome=System.getProperty("user.home");

    private RunOnStartUpMacOS() {}

    static void install() {
        logger.info("Install autostart for Mac OS");
        try {
            if (!isInstalled()) {
                installAutoStart();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    static void uninstall() {
        logger.info("Uninstall autostart");
        try {
            File startupFile = getStartupFile();
            if (Files.exists(Paths.get(startupFile.toString()))) {
                Files.delete(Paths.get(startupFile.toString()));
            }
        } catch (Exception e) {
            logger.error("Can't uninstall autostart: " + e.getMessage());
        }
    }

    private static boolean isInstalled() {
        try {
            return Files.exists(Paths.get(getStartupFile().toString()));
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

    private static File getStartupFile() throws Exception {
        logger.info("Get startup file for Mac OS");
        String filePath = userHome+"/Library/LaunchAgents/edu.dmitry.AutoStart" + getJarFile().getName().replaceFirst(".jar",".plist");
        logger.info("Startup file: " + filePath);
        return new File(filePath);
    }

    private static void installAutoStart() throws Exception {
        File startupFile = getStartupFile();
        PrintWriter out = new PrintWriter(new FileWriter(startupFile));
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
        out.println("<plist version=\"1.0\">");
        out.println("<dict>");
        out.println("   <key>Label</key>");
        out.println("   <string>edu.dmtiry.AutoStart"+getJarFile().getName().replaceFirst(".jar","")+"</string>");
        out.println("   <key>ProgramArguments</key>");
        out.println("   <array>");
        out.println("      <string>java</string>");
        out.println("      <string>-jar</string>");
        out.println("      <string>"+getJarFile()+"</string>");
        out.println("   </array>");
        out.println("   <key>WorkingDirectory</key>");
        out.println("   <string>" + getWorkingDirectory() + "</string>");
        out.println("   <key>RunAtLoad</key>");
        out.println("   <true/>");
        out.println("</dict>");
        out.println("</plist>");
        out.close();
    }
}