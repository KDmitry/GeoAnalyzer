package edu.dmitry.geoserver.rpc;

import edu.dmitry.geoserver.MapInteraction.LocationsStatisticRequest;
import edu.dmitry.geoserver.MapInteraction.LocationsStatisticRespond;
import edu.dmitry.geoserver.datamodel.LocationStatistic;

public interface MapMethods {
    LocationsStatisticRespond getLocationsStatistic(LocationsStatisticRequest locationsStatisticRequest);
    LocationStatistic getFullLocationStatistic(LocationStatistic locationStatistic);
}