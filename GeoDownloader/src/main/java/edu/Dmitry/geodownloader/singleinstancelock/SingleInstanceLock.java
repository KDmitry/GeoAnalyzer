package edu.Dmitry.geodownloader.singleinstancelock;

public class SingleInstanceLock {
    private final static String osName = System.getProperty("os.name");

    private SingleInstanceLock() {}

    public static boolean lock() throws Exception {
        if(osName.startsWith("Mac OS")) {
            return SingleInstanceLockMacOS.lock();
        } else {
            throw new Exception("Unknown platform isn't supported");
        }
    }
}