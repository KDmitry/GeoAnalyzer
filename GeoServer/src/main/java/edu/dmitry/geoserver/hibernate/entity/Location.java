package edu.dmitry.geoserver.hibernate.entity;

import javax.persistence.*;

@Entity
@Table(name = "Locations", schema = "twitter_schema")
public class Location {
    @Id
    @Column(name = "locationId")
    private long locationId;

    @Column(name = "name")
    private String name;

    @Column(name = "lat")
    private double lat;

    @Column(name = "lng")
    private double lng;

    public long getlocationId() {
        return locationId;
    }

    public void setlocationId(long id) {
        this.locationId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
