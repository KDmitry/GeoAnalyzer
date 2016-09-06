package edu.dmitry.geoserver.LocationGroup;

import edu.dmitry.geoserver.DataBaseAccess;
import edu.dmitry.geoserver.hibernate.entity.Location;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupsFinder {
    private List<Integer> zooms = new ArrayList<>();
    private DataBaseAccess dataBaseAccess = new DataBaseAccess();

    public GroupsFinder() {
        zooms.add(-1);
        zooms.add(-1);
        zooms.add(30);
        zooms.add(50);
        zooms.add(100);
        zooms.add(200);
        zooms.add(400);
        zooms.add(800);
        zooms.add(2000);
        zooms.add(3000);
        zooms.add(6000);
        zooms.add(10000);
        zooms.add(30000);
        zooms.add(50000);
        zooms.add(100000);
        zooms.add(200000);
        zooms.add(400000);
        zooms.add(800000);
    }

    public void findGroups() {
        List<Location> locations = dataBaseAccess.getAllLocations();

        int groupId = 1;
        List<GeoGroup> geoGroups = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getlocationId() == 151576 ||
                    locations.get(i).getlocationId() == 560184 ||
                    locations.get(i).getlocationId() == 213557550 ||
                    locations.get(i).getlocationId() == 221034356 ||
                    locations.get(i).getlocationId() == 227679614 ||
                    locations.get(i).getlocationId() == 1028243399) {

                Location location = locations.get(i);
                GeoGroup geoGroup = new GeoGroup();
                geoGroup.setId(groupId++);
                geoGroup.setLat(locations.get(i).getLat());
                geoGroup.setLng(locations.get(i).getLng());
                geoGroup.setRadius(0);
                geoGroup.setDeep(0);
                geoGroup.setDistanceBetween(0);
                for (int j = 2; j < zooms.size(); j++) {
                    geoGroup.getZooms().add(j);
                }
                geoGroup.getLocations().add(locations.get(i));
                geoGroup.setGeoGroup1(null);
                geoGroup.setGeoGroup2(null);
                geoGroups.add(geoGroup);
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("Кластеризация.txt"));

            while (geoGroups.size() != 1) {
                int minI = -1;
                int minJ = -1;
                int minDistance = Integer.MAX_VALUE;
                int[][] matr = new int[geoGroups.size()][geoGroups.size()];

                for (int i = 0; i < matr.length; i++) {
                    for (int j = 0; j < matr[i].length; j++) {
                        if (i != j) {
                            matr[i][j] = getDistance(geoGroups.get(i), geoGroups.get(j));
                            if (matr[i][j] < minDistance) {
                                minDistance = matr[i][j];
                                minI = i;
                                minJ = j;
                            }
                        }
                    }
                }

                for (int i = 0; i < matr.length; i++) {
                    writer.write(geoGroups.get(i).getId() + "\t");
                    for (int j = 0; j < matr[i].length; j++) {
                        writer.write(matr[i][j] + "\t");
                    }
                    writer.newLine();
                }
                writer.newLine();

                GeoGroup group1 = geoGroups.get(minI);
                GeoGroup group2 = geoGroups.get(minJ);

                geoGroups.remove(group1);
                geoGroups.remove(group2);

                GeoGroup geoGroup = new GeoGroup();

                int mnoz = 10;
                if (String.valueOf(group2.getId()).length() == 2) {
                    mnoz = 100;
                } else if (String.valueOf(group2.getId()).length() == 3) {
                    mnoz = 1000;
                } else if (String.valueOf(group2.getId()).length() == 4) {
                    mnoz = 10000;
                } else if (String.valueOf(group2.getId()).length() == 5) {
                    mnoz = 100000;
                } else if (String.valueOf(group2.getId()).length() == 6) {
                    mnoz = 1000000;
                } else if (String.valueOf(group2.getId()).length() == 7) {
                    mnoz = 10000000;
                } else if (String.valueOf(group2.getId()).length() == 8) {
                    mnoz = 100000000;
                }

                geoGroup.setId(group1.getId() * mnoz + group2.getId());
                geoGroup.setLat(getLatCenter(group1.getLat(), group2.getLat()));
                geoGroup.setLng(getLngCenter(group1.getLng(), group2.getLng()));
                geoGroup.setRadius(getRadius(group1, group2));
                geoGroup.setDeep(group1.getDeep() >= group2.getDeep() ? group1.getDeep() + 1 : group2.getDeep() + 1);
                geoGroup.setDistanceBetween(minDistance);
                geoGroup.getLocations().addAll(group1.getLocations());
                geoGroup.getLocations().addAll(group2.getLocations());
                geoGroup.setGeoGroup1(group1);
                geoGroup.setGeoGroup2(group2);
                addZooms(geoGroup);
                geoGroups.add(geoGroup);
            }

            writer.close();

        } catch (IOException e) {

        }

        /*
        if (geoGroups.size() == 1) {
            nextGeoGroup(geoGroups.get(0));
        }
        */
        if (geoGroups.size() == 1) {

        }
    }

    /*
    public void findGroups() {
        List<Location> locations = dataBaseAccess.getAllLocations();

        int groupId = 1;
        List<GeoGroup> geoGroups = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            GeoGroup geoGroup = new GeoGroup();
            geoGroup.setId(groupId++);
            geoGroup.setLat(locations.get(i).getLat());
            geoGroup.setLng(locations.get(i).getLng());
            geoGroup.setRadius(0);
            geoGroup.setDeep(0);
            geoGroup.setDistanceBetween(0);
            for (int j = 2; j < zooms.size(); j++) {
                geoGroup.getZooms().add(j);
            }
            geoGroup.getLocations().add(locations.get(i));
            geoGroup.setGeoGroup1(null);
            geoGroup.setGeoGroup2(null);
            geoGroups.add(geoGroup);
        }

        while (geoGroups.size() != 1) {
            int minI = -1;
            int minJ = -1;
            int minDistance = Integer.MAX_VALUE;
            int[][] matr = new int[geoGroups.size()][geoGroups.size()];

            for (int i = 0; i < matr.length; i++) {
                for (int j = 0; j < matr[i].length; j++) {
                    if (i != j) {
                        matr[i][j] = getDistance(geoGroups.get(i), geoGroups.get(j));
                        if (matr[i][j] < minDistance) {
                            minDistance = matr[i][j];
                            minI = i;
                            minJ = j;
                        }
                    }
                }
            }

            for (int i = 0; i < matr.length; i++) {
                for (int j = 0; j < matr[i].length; j++) {

                }
            }

            GeoGroup group1 = geoGroups.get(minI);
            GeoGroup group2 = geoGroups.get(minJ);

            geoGroups.remove(group1);
            geoGroups.remove(group2);

            GeoGroup geoGroup = new GeoGroup();
            geoGroup.setId(groupId++);
            geoGroup.setLat(getLatCenter(group1.getLat(), group2.getLat()));
            geoGroup.setLng(getLngCenter(group1.getLng(), group2.getLng()));
            geoGroup.setRadius(getRadius(group1, group2));
            geoGroup.setDeep(group1.getDeep() >= group2.getDeep() ? group1.getDeep() + 1 : group2.getDeep() + 1);
            geoGroup.setDistanceBetween(minDistance);
            geoGroup.getLocations().addAll(group1.getLocations());
            geoGroup.getLocations().addAll(group2.getLocations());
            geoGroup.setGeoGroup1(group1);
            geoGroup.setGeoGroup2(group2);
            addZooms(geoGroup);
            geoGroups.add(geoGroup);
        }

        if (geoGroups.size() == 1) {
            nextGeoGroup(geoGroups.get(0));
        }

        if (geoGroups.size() == 1) {

        }
    }
    */

    private void nextGeoGroup(GeoGroup geoGroup) {
        if (geoGroup == null) {
            return;
        }

        if (geoGroup.getZooms().size() > 0) {
            dataBaseAccess.addGeoGroup(geoGroup);
        }
        nextGeoGroup(geoGroup.getGeoGroup1());
        nextGeoGroup(geoGroup.getGeoGroup2());
    }

    private void addZooms(GeoGroup geoGroup) {
        int radius = geoGroup.getRadius();
        int minZoom = 0;
        for (int i = 3; i < zooms.size(); i++) {
            if (radius < zooms.get(i)) {
                minZoom = i - 1;
                for (int j = minZoom; j < zooms.size(); j++) {
                    geoGroup.getZooms().add(j);
                }
                break;
            }
        }

        int minZoom1 = Integer.MAX_VALUE;
        int minZoom2 = Integer.MAX_VALUE;

        for (Integer zoom : geoGroup.getGeoGroup1().getZooms()) {
            if (zoom < minZoom1) {
                minZoom1 = zoom;
            }
        }

        for (Integer zoom : geoGroup.getGeoGroup2().getZooms()) {
            if (zoom < minZoom2) {
                minZoom2 = zoom;
            }
        }

        geoGroup.getGeoGroup1().getZooms().clear();
        geoGroup.getGeoGroup2().getZooms().clear();

        for (int i = minZoom - 1; i >= minZoom1; i--) {
            geoGroup.getGeoGroup1().getZooms().add(i);
        }

        for (int i = minZoom - 1; i >= minZoom2; i--) {
            geoGroup.getGeoGroup2().getZooms().add(i);
        }
    }

    private int getRadius(GeoGroup geoGroup1, GeoGroup geoGroup2) {
        double lngCenter = getLngCenter(geoGroup1.getLng(), geoGroup2.getLng());
        double latCenter = getLatCenter(geoGroup1.getLat(), geoGroup2.getLat());

        int radius1 = GeoUtls.intDistance(new GeoPoint(geoGroup1.getLng(), geoGroup1.getLat()),
                new GeoPoint(lngCenter, latCenter)) + geoGroup1.getRadius();

        int radius2 = GeoUtls.intDistance(new GeoPoint(geoGroup2.getLng(), geoGroup2.getLat()),
                new GeoPoint(lngCenter, latCenter)) + geoGroup2.getRadius();

        return radius1 > radius2 ? radius1 : radius2;
    }

    private double getLngCenter(double lng1, double lng2) {
        return (lng1 + lng2) / 2;
    }

    private double getLatCenter(double lat1, double lat2) {
        return (lat1 + lat2) / 2;
    }

    private int getDistance(GeoGroup geoGroup1, GeoGroup geoGroup2) {
        return GeoUtls.intDistance(new GeoPoint(geoGroup1.getLng(), geoGroup1.getLat()),
                new GeoPoint(geoGroup2.getLng(), geoGroup2.getLat()));
    }

    public static void main(String[] args) {
        new GroupsFinder().findGroups();
    }
}
