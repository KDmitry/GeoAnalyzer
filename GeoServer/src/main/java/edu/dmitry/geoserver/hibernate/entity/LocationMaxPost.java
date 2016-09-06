package edu.dmitry.geoserver.hibernate.entity;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "LocationsMaxPosts", schema = "twitter_schema")
public class LocationMaxPost {
    @Id
    @Column(name = "locationId")
    private long locationId;

    @Column(name = "maxPostId")
    private long maxPostId;

    public long getMaxPostId() {
        return maxPostId;
    }

    public void setMaxPostId(long maxPostId) {
        this.maxPostId = maxPostId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }
}
