package edu.dmitry.geoserver.hibernate.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "GeoGroups", schema = "twitter_schema")
public class GeoGroup {
    @Id
    @Column(name = "geoGroupId")
    private int geoGroupId;

    @Column(name = "lat")
    private double lat;

    @Column(name = "lng")
    private double lng;

    @Column(name = "radius")
    private int radius;

    @ElementCollection
    @CollectionTable(name="GeoGroupsZooms", joinColumns=@JoinColumn(name="geoGroupId"))
    @Column(name="zoom")
    private Set<Integer> zooms = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "LocationsGeoGroups", joinColumns = { @JoinColumn(name = "idGeoGroup") }, inverseJoinColumns = { @JoinColumn(name = "idLocation") })
    private Set<Location> locations = new HashSet<>();

    public int getGeoGroupId() {
        return geoGroupId;
    }

    public void setGeoGroupId(int geoGroupId) {
        this.geoGroupId = geoGroupId;
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
