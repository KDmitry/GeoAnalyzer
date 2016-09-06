package edu.dmitry.geoserver.LocationSaver;

import java.io.*;

public class LocationSaver {
    public void saveLocations(String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/dmitrij/Desktop/LocationParser/locations/requests.txt"))) {

            while (reader.ready()) {
                String location = reader.readLine();
                String[] args = location.split("\\t");
                if (args.length == 4) {
                    try {
                        long code = Long.parseLong(args[0]);
                        String name = args[1];
                        double lng = Double.parseDouble(args[2]);
                        double lat = Double.parseDouble(args[3]);
                        if (lat < 55.994201 && lat > 55.496345 && lng > 37.235497 && lng < 37.997952) {
                            String request = "INSERT INTO Locations (locationId, name, lat, lng) " +
                                    "SELECT * FROM (SELECT " + code + ", '"+ name + "', " + lat + ", " + lng + ") AS tmp\n" +
                                    "WHERE NOT EXISTS (SELECT locationId FROM Locations WHERE locationId = " + code + ") LIMIT 1;";
                            writer.write(request);
                            writer.newLine();
                            request = "INSERT INTO LocationsMaxPosts (locationId, maxPostId) " +
                                    "SELECT * FROM (SELECT " + code + ", " + 0 +") AS tmp\n" +
                                    "WHERE NOT EXISTS (SELECT locationId FROM LocationsMaxPosts WHERE locationId = " + code + ") LIMIT 1;";
                            writer.write(request);
                            writer.newLine();
                        }

                    } catch (Exception e) {

                    }
                }
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }

    }

    public static void main(String[] args) {
        new LocationSaver().saveLocations("/Users/dmitrij/Desktop/LocationParser/locations/моямосква_locations.txt");
    }

}
