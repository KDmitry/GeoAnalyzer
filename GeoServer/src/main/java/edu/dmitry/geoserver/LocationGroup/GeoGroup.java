package edu.dmitry.geoserver.LocationGroup;

import edu.dmitry.geoserver.hibernate.entity.Location;
import java.util.HashSet;
import java.util.Set;

public class GeoGroup {
    private int id;
    private double lat;
    private double lng;
    private int radius;
    private int deep;
    private int distanceBetween;
    private Set<Integer> zooms = new HashSet<>();
    private Set<Location> locations = new HashSet<>();
    private GeoGroup geoGroup1;
    private GeoGroup geoGroup2;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public GeoGroup getGeoGroup1() {
        return geoGroup1;
    }

    public void setGeoGroup1(GeoGroup geoGroup1) {
        this.geoGroup1 = geoGroup1;
    }

    public GeoGroup getGeoGroup2() {
        return geoGroup2;
    }

    public void setGeoGroup2(GeoGroup geoGroup2) {
        this.geoGroup2 = geoGroup2;
    }

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public int getDistanceBetween() {
        return distanceBetween;
    }

    public void setDistanceBetween(int distanceBetween) {
        this.distanceBetween = distanceBetween;
    }

    public Set<Integer> getZooms() {
        return zooms;
    }

    public void setZooms(Set<Integer> zooms) {
        this.zooms = zooms;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }
}
