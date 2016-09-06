package edu.dmitry.geoserver.hibernate.entity;

import javax.persistence.*;

@Entity
@Table(name = "HashTags", schema = "twitter_schema")
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashTagId")
    private long hashTagId;

    @Column(name = "name")
    private String name;

    public long getHashTagId() {
        return hashTagId;
    }

    public void setHashTagId(long hashTagId) {
        this.hashTagId = hashTagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
