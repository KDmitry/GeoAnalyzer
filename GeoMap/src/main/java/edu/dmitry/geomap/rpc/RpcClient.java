package edu.dmitry.geomap.rpc;

import cz.eman.jsonrpc.client.TcpJsonClient;
import edu.dmitry.geomap.ConfigReader;
import edu.dmitry.geomap.MapInteraction.LocationsStatisticRequest;
import edu.dmitry.geomap.MapInteraction.LocationsStatisticRespond;
import edu.dmitry.geomap.datamodel.LocationStatistic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.InetSocketAddress;

public class RpcClient {
    private ServerProxy proxy;
    private Logger logger = LogManager.getRootLogger();
    private String address;
    private int port;
    private final String defaultAddress = "";
    private final int defaultPort = 0;

    public RpcClient() {
    }

    public boolean open() {
        try {
            address = ConfigReader.getServerAddress();
            if (address == null) {
                address = defaultAddress;
            }

            port = ConfigReader.getServerPort();
            if (port == -1) {
                port = defaultPort;
            }

            logger.info("Open rpc connection : address - " + address + ", port - " + port);
            proxy = new ServerProxy(new TcpJsonClient(new InetSocketAddress(address, port)));
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public void close() {
        if (proxy != null) {
            try {
                proxy.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public LocationsStatisticRespond getLocationsStatistic(LocationsStatisticRequest locationsStatisticRequest) {
        logger.info("Get locations statistic");
        LocationsStatisticRespond locationsStatisticRespond = proxy.getLocationsStatistic(locationsStatisticRequest);
        if (locationsStatisticRespond != null) {
            return locationsStatisticRespond;
        } else {
            logger.info("No response, locations respond is null");
            return null;
        }
    }

    public LocationStatistic getFullLocationStatistic(LocationStatistic locationStatistic) {
        logger.info("Get location statistic");
        LocationStatistic result = proxy.getFullLocationStatistic(locationStatistic);
        if (result != null) {
            return result;
        } else {
            logger.info("No response, location statistc is null");
            return null;
        }
    }
}
