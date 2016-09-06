package edu.dmitry.geoserver.rpc;

import cz.eman.jsonrpc.server.tcp.TcpJsonMultiServer;
import edu.dmitry.geoserver.Manager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class RpcServer extends Thread {
    private Logger logger = LogManager.getRootLogger();
    private TcpJsonMultiServer server;
    private Manager manager;

    public RpcServer(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            logger.info("Listen port: " + 0);
            server =  new TcpJsonMultiServer(new ServerListener(manager), 0);
            logger.info("Start server");
            server.start();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void close() {
        try {
            logger.info("close");
            server.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
