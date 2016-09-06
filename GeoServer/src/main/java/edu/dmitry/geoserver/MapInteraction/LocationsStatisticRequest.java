package edu.dmitry.geoserver.MapInteraction;

import org.joda.time.DateTime;

public class LocationsStatisticRequest {
    private DateTime from;
    private DateTime to;
    private int lastLocationStatisticId;

    public DateTime getFrom() {
        return from;
    }

    public void setFrom(DateTime from) {
        this.from = from;
    }

    public DateTime getTo() {
        return to;
    }

    public void setTo(DateTime to) {
        this.to = to;
    }

    public int getLastLocationStatisticId() {
        return lastLocationStatisticId;
    }

    public void setLastLocationStatisticId(int lastLocationStatisticId) {
        this.lastLocationStatisticId = lastLocationStatisticId;
    }

}
