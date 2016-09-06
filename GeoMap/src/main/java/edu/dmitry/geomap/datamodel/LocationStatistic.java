package edu.dmitry.geomap.datamodel;

import org.joda.time.DateTime;
import java.util.HashSet;
import java.util.Set;

public class LocationStatistic {
    private int id;
    private double lat;
    private double lng;
    private int radius;
    private int zoom;
    private int peopleCount;
    private DateTime postsTimeFrom;
    private DateTime postsTimeTo;
    private Set<String> names = new HashSet<>();
    private Set<String> hashTags = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
    }

    public Set<String> getHashTags() {
        return hashTags;
    }

    public void setHashTags(Set<String> hashTags) {
        this.hashTags = hashTags;
    }

    public DateTime getPostsTimeFrom() {
        return postsTimeFrom;
    }

    public void setPostsTimeFrom(DateTime postsTimeFrom) {
        this.postsTimeFrom = postsTimeFrom;
    }

    public DateTime getPostsTimeTo() {
        return postsTimeTo;
    }

    public void setPostsTimeTo(DateTime postsTimeTo) {
        this.postsTimeTo = postsTimeTo;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }
}