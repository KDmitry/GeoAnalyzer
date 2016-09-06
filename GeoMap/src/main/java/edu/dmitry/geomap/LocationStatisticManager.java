package edu.dmitry.geomap;

import edu.dmitry.geomap.MapInteraction.LocationsStatisticRequest;
import edu.dmitry.geomap.MapInteraction.LocationsStatisticRespond;
import edu.dmitry.geomap.datamodel.LocationStatistic;
import edu.dmitry.geomap.rpc.RpcClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class LocationStatisticManager extends Observable {
    private Logger logger = LogManager.getRootLogger();
    private Manager manager;
    private RpcClient rpcClient;
    private Set<LocationStatistic> allLocationsStatistics = new HashSet<>();
    private Set<LocationStatistic> fullLocationsStatistics = new HashSet<>();

    public LocationStatisticManager(Manager manager, RpcClient rpcClient) {
        this.manager = manager;
        this.rpcClient = rpcClient;
    }

    public void saveLocationsStatistic(DateTime from, DateTime to) {
        new Thread(() -> {
            try {
                if (allLocationsStatistics.size() == 0) {
                    return;
                }

                BufferedWriter writer = null;

                if(!Files.exists(Paths.get("statistics"))) {
                    Files.createDirectory(Paths.get("statistics"));
                }
                if (!Files.exists(Paths.get("statistics/" + from.toString() + "-" + to.toString()))) {
                    Files.createDirectory(Paths.get("statistics/" + from.toString() + "-" + to.toString()));
                } else {
                    return;
                }

                for (LocationStatistic stat : allLocationsStatistics) {
                    writer = new BufferedWriter(new FileWriter("statistics/" + from.toString() + "-" + to.toString() +
                            "/zoom_" + stat.getZoom() + ".txt", true));
                    writer.write(stat.getId() + "\t" + stat.getZoom() + "\t" + stat.getRadius() + "\t" + stat.getLat() + "\t" +
                            stat.getLng() + "\t" + stat.getPostsTimeFrom().toString() + "\t" + stat.getPostsTimeTo().toString() + "\t" +
                            stat.getPeopleCount());
                    writer.newLine();
                    writer.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }).start();
    }

    public void downloadLocationsStatistic(String path) {
        try {
            BufferedReader reader = null;
            for (int i = 2; i < 18; i++) {
                reader = new BufferedReader(new FileReader(Paths.get(path + "/zoom_" + i + ".txt").toString()));

                while (reader.ready()) {
                    List<LocationStatistic> statistics = new ArrayList<>();
                    String[] strArgs = reader.readLine().split("\t");

                    if (strArgs.length == 8) {
                        LocationStatistic locationStatistic = new LocationStatistic();
                        locationStatistic.setId(Integer.parseInt(strArgs[0]));
                        locationStatistic.setZoom(Integer.parseInt(strArgs[1]));
                        locationStatistic.setRadius(Integer.parseInt(strArgs[2]));
                        locationStatistic.setLat(Double.parseDouble(strArgs[3]));
                        locationStatistic.setLng(Double.parseDouble(strArgs[4]));
                        locationStatistic.setPostsTimeFrom(new DateTime(strArgs[5]));
                        locationStatistic.setPostsTimeTo(new DateTime(strArgs[6]));
                        locationStatistic.setPeopleCount(Integer.parseInt(strArgs[7]));
                        allLocationsStatistics.add(locationStatistic);
                        statistics.add(locationStatistic);
                    }

                    setChanged();
                    notifyObservers(statistics);
                }

                reader.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void downloadLocationsStatistic(DateTime from, DateTime to) {
        logger.info("Start download location statistic");
        new Thread(() -> {
            allLocationsStatistics.clear();
            fullLocationsStatistics.clear();

            if (Files.exists(Paths.get("statistics/" + from.toString() + "-" + to.toString()))) {
                downloadLocationsStatistic("statistics/" + from.toString() + "-" + to.toString());
            } else {
                int locationStatisticId = -1;
                int locationsCount = 0;
                int allLocationsCount = 0;
                do {
                    LocationsStatisticRequest request = new LocationsStatisticRequest();
                    request.setFrom(from);
                    request.setTo(to);
                    request.setLastLocationStatisticId(locationStatisticId);

                    LocationsStatisticRespond respond = rpcClient.getLocationsStatistic(request);
                    if (respond != null) {
                        allLocationsStatistics.addAll(respond.getLocationStatistics().stream().collect(Collectors.toList()));
                        locationStatisticId = respond.getLocationStatisticId();
                        if (allLocationsCount == 0) {
                            allLocationsCount = respond.getAllLocationsCount();
                        }

                        setChanged();
                        notifyObservers(respond.getLocationStatistics());
                        manager.updateStatus(++locationsCount, allLocationsCount);
                    }
                } while (locationStatisticId != -1);
            }

            manager.setStatus("Идет сохранение...");
            saveLocationsStatistic(from, to);

            manager.downloadingEnd();
            logger.info("End of downloading location statistic");
        }).start();
    }

    public void downloadFullLocationsStatistic(LocationStatistic locationStatistic) {
        new Thread(() -> {
            for (LocationStatistic stat : fullLocationsStatistics) {
                if (stat.getId() == locationStatistic.getId()) {
                    setChanged();
                    notifyObservers(stat);
                    return;
                }
            }

            LocationStatistic respond = rpcClient.getFullLocationStatistic(locationStatistic);
            if (respond != null) {
                fullLocationsStatistics.add(respond);
                setChanged();
                notifyObservers(respond);
            }
        }).start();
    }
}
