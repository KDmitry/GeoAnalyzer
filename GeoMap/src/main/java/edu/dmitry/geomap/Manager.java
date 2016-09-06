package edu.dmitry.geomap;

import edu.dmitry.geomap.datamodel.LocationStatistic;
import edu.dmitry.geomap.rpc.RpcClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import java.util.*;

public class Manager {
    private Logger logger = LogManager.getRootLogger();
    private MapDlg mapDlg;
    private RpcClient rpcClient;
    private LocationStatisticManager locationStatisticManager;

    public Manager() {
        rpcClient = new RpcClient();
        locationStatisticManager = new LocationStatisticManager(this, rpcClient);
        mapDlg = new MapDlg(this);
    }

    public void startWork() {
        rpcClient.open();
        mapDlg.showDlg();
    }

    public void getLocationsStatistic(DateTime from, DateTime to, boolean realTime) {
        locationStatisticManager.downloadLocationsStatistic(from, to);
    }

    public void getFullLocationStatistic(LocationStatistic locationStatistic) {
        locationStatisticManager.downloadFullLocationsStatistic(locationStatistic);
    }

    public void downloadingEnd() {
        mapDlg.downloadingEnd();
    }

    public void setStatus(String status) {
        mapDlg.setStatus(status);
    }

    public void updateStatus(int count, int allCount) {
        mapDlg.updateStatus(count, allCount);
    }

    public void subscribe(Observer observer) {
        locationStatisticManager.addObserver(observer);
    }

    public void unsubscribe(Observer observer) {
        locationStatisticManager.deleteObserver(observer);
    }


}
