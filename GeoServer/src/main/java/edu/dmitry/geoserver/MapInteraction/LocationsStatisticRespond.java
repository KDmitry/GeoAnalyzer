package edu.dmitry.geoserver.MapInteraction;

import edu.dmitry.geoserver.datamodel.LocationStatistic;
import java.util.ArrayList;
import java.util.List;

public class LocationsStatisticRespond {
    private int locationStatisticId;
    private int allLocationsCount;
    private List<LocationStatistic> locationStatistics = new ArrayList<>();

    public List<LocationStatistic> getLocationStatistics() {
        return locationStatistics;
    }

    public void setLocationStatistics(List<LocationStatistic> locationStatistics) {
        this.locationStatistics = locationStatistics;
    }

    public int getLocationStatisticId() {
        return locationStatisticId;
    }

    public void setLocationStatisticId(int locationStatisticId) {
        this.locationStatisticId = locationStatisticId;
    }

    public int getAllLocationsCount() {
        return allLocationsCount;
    }

    public void setAllLocationsCount(int allLocationsCount) {
        this.allLocationsCount = allLocationsCount;
    }
}
