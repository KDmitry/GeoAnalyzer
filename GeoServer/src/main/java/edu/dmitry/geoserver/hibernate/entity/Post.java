package edu.dmitry.geoserver.hibernate.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Posts", schema = "twitter_schema")
public class Post {
    @Id
    @Column(name = "postId")
    private long postId;

    @ManyToOne
    @JoinColumn(name="locationId")
    private Location location;

    @Column(name = "likesCount")
    private int likesCount;

    @Column(name = "createdTime", columnDefinition="DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @ManyToMany//(cascade = CascadeType.ALL)
    @JoinTable(name = "PostsUsers", joinColumns = { @JoinColumn(name = "idPost") }, inverseJoinColumns = { @JoinColumn(name = "userId") })
    private Set<User> nickNames = new HashSet<>();

    @ManyToMany//(cascade = CascadeType.ALL)
    @JoinTable(name = "PostsHashTags", joinColumns = { @JoinColumn(name = "postId") }, inverseJoinColumns = { @JoinColumn(name = "hashTagId") })
    private Set<HashTag> hashTags = new HashSet<>();


    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Set<User> getNickNames() {
        return nickNames;
    }

    public void setNickNames(Set<User> nickNames) {
        this.nickNames = nickNames;
    }

    public Set<HashTag> getHashTags() {
        return hashTags;
    }

    public void setHashTags(Set<HashTag> hashTags) {
        this.hashTags = hashTags;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
}
