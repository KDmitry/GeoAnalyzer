package edu.dmitry.geoserver.hibernate.entity;

import javax.persistence.*;

@Entity
@Table(name = "Users", schema = "twitter_schema")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private long userId;

    @Column(name = "instagramNickName")
    private String instagramNickName;

    public long getId() {
        return userId;
    }

    public void setId(long id) {
        this.userId = id;
    }

    public String getInstagramNickName() {
        return instagramNickName;
    }

    public void setInstagramNickName(String instagramNickName) {
        this.instagramNickName = instagramNickName;
    }
}
