package edu.Dmitry.geodownloader.datamodel;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class InstagramPost {
    public long id = -1;
    public DateTime createdTime;
    public long locationId = -1;
    public String locationName;
    public Set<String> usertags;
    public Set<String> hashtags;
    public int likesCount;
}
