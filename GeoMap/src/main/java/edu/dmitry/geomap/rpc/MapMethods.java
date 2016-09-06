package edu.dmitry.geomap.rpc;

import edu.dmitry.geomap.MapInteraction.LocationsStatisticRequest;
import edu.dmitry.geomap.MapInteraction.LocationsStatisticRespond;
import edu.dmitry.geomap.datamodel.LocationStatistic;

public interface MapMethods {
    LocationsStatisticRespond getLocationsStatistic(LocationsStatisticRequest locationsStatisticRequest);
    LocationStatistic getFullLocationStatistic(LocationStatistic locationStatistic);
}