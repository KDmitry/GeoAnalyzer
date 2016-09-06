package edu.dmitry.geoserver;

import edu.dmitry.geoserver.MapInteraction.LocationsStatisticRequest;
import edu.dmitry.geoserver.MapInteraction.LocationsStatisticRespond;
import edu.dmitry.geoserver.datamodel.LocationStatistic;
import edu.dmitry.geoserver.hibernate.entity.GeoGroup;
import edu.dmitry.geoserver.hibernate.entity.HashTag;
import edu.dmitry.geoserver.hibernate.entity.Location;
import edu.dmitry.geoserver.hibernate.entity.Post;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.util.*;

public class MapRespondManager {
    private Logger logger = LogManager.getRootLogger();
    private DataBaseAccess dataBaseAccess;
    private Map<Long, Integer> locationPeopleCount = new HashMap<>();

    public MapRespondManager(DataBaseAccess dataBaseAccess) {
        this.dataBaseAccess = dataBaseAccess;
    }

    public LocationsStatisticRespond getLocationsStatistic(LocationsStatisticRequest locationsStatisticRequest) {
        logger.info("Get locations statistic");

        if (locationsStatisticRequest == null) {
            return null;
        }

        GeoGroup geoGroup = dataBaseAccess.getGeoGroup(dataBaseAccess.getNextGeoGroupId(locationsStatisticRequest.getLastLocationStatisticId()));

        LocationsStatisticRespond respond = new LocationsStatisticRespond();

        if (geoGroup != null) {
            respond.setLocationStatisticId(geoGroup.getGeoGroupId());
            respond.setAllLocationsCount(dataBaseAccess.getGeoGroupsCount());

            int peopleCount = 0;
            for (Location location : geoGroup.getLocations()) {
                if (locationPeopleCount.containsKey(location.getlocationId())) {
                    peopleCount += locationPeopleCount.get(location.getlocationId());
                } else {
                    int locPeopleCount = 0;
                    Set<Post> locationPosts = dataBaseAccess.getLocationsPosts(locationsStatisticRequest.getFrom(),
                            locationsStatisticRequest.getTo(), location);

                    for (Post post : locationPosts) {
                        peopleCount += post.getNickNames().size();
                        locPeopleCount += post.getNickNames().size();
                    }

                    locationPeopleCount.put(location.getlocationId(), locPeopleCount);
                }
            }

            for (Integer zoom : geoGroup.getZooms()) {
                LocationStatistic locationStatistic = new LocationStatistic();
                locationStatistic.setId(geoGroup.getGeoGroupId());
                locationStatistic.setLat(geoGroup.getLat());
                locationStatistic.setLng(geoGroup.getLng());
                locationStatistic.setZoom(zoom);
                locationStatistic.setRadius(geoGroup.getRadius());
                locationStatistic.setPostsTimeFrom(locationsStatisticRequest.getFrom());
                locationStatistic.setPostsTimeTo(locationsStatisticRequest.getTo());
                locationStatistic.setPeopleCount(peopleCount);
                respond.getLocationStatistics().add(locationStatistic);
            }
        } else {
            respond.setLocationStatisticId(-1);
            locationPeopleCount.clear();
        }

        return respond;
    }

    public LocationStatistic getFullLocationStatistic(LocationStatistic locationStatistic) {
        GeoGroup geoGroup = dataBaseAccess.getGeoGroup(locationStatistic.getId());

        if (geoGroup != null) {
            for (Location location : geoGroup.getLocations()) {
                locationStatistic.getNames().add(location.getName());

                Set<Post> locationPosts = dataBaseAccess.getLocationsPosts(locationStatistic.getPostsTimeFrom(),
                        locationStatistic.getPostsTimeTo(), location);

                for (Post post : locationPosts) {
                    for (HashTag hashTag : post.getHashTags()) {
                        locationStatistic.getHashTags().add(hashTag.getName());
                    }
                }
            }

        }

        return locationStatistic;
    }
}
