package edu.dmitry.geoserver;

import edu.dmitry.geoserver.datamodel.InstagramPost;
import edu.dmitry.geoserver.hibernate.HibernateUtil;
import edu.dmitry.geoserver.hibernate.dao.*;
import edu.dmitry.geoserver.hibernate.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import java.util.*;

//jdbc:mysql://192.168.0.202/twitter_schema?characterEncoding=utf8 - с сервера
//jdbc:mysql://194.226.56.147/twitter_schema?characterEncoding=utf8 - из дома

public class DataBaseAccess extends Observable {
    private PostDAO postDAO = new PostDAO();
    private HashTagDAO hashTagDAO = new HashTagDAO();
    private UserDAO userDAO = new UserDAO();
    private LocationDAO locationDAO = new LocationDAO();
    private LocationMaxPostDAO locationsMaxPostsDAO = new LocationMaxPostDAO();
    private GeoGroupDAO geoGroupDAO = new GeoGroupDAO();
    private long postsCount = getPostsCount();
    private int geoGroupsCount = getGeoGroupsCount();
    private Logger logger = LogManager.getRootLogger();

    public boolean addNewPost(InstagramPost instagramPost) {
        logger.info("Add new post: " + instagramPost.id);
        Transaction transaction = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            Post post = new Post();
            post.setPostId(instagramPost.id);
            post.setLikesCount(instagramPost.likesCount);
            post.setCreatedTime(new java.util.Date(instagramPost.createdTime.getMillis()));
            post.setLocation(locationDAO.getLocationById(instagramPost.locationId));

            Set<HashTag> hashTagSet = new HashSet<>();
            for (String hashTagName : instagramPost.hashtags) {
                if (checkHashTag(hashTagName)) {
                    HashTag hashTag = hashTagDAO.getHashTagByName(hashTagName);
                    if (hashTag == null) {
                        hashTag = new HashTag();
                        hashTag.setName(hashTagName);
                        hashTagDAO.addHashTag(hashTag);
                    }
                    hashTagSet.add(hashTag);
                }
            }
            post.setHashTags(hashTagSet);

            Set<User> instagramNickNames = new HashSet<>();
            for (String nickName : instagramPost.usertags) {
                User user = userDAO.getUserByName(nickName);
                if (user == null) {
                    user = new User();
                    user.setInstagramNickName(nickName);
                    userDAO.addUser(user);
                }
                instagramNickNames.add(user);
            }
            post.setNickNames(instagramNickNames);

            postDAO.addPost(post);
            transaction.commit();
            postsCount++;
            logger.info("Posts count: " + postsCount);

            logger.info("Notify observers");
            setChanged();
            notifyObservers(postsCount);

            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Transaction rollback");
            transaction.rollback();
            return false;
        }
    }

    private boolean checkHashTag(String hashTag) {
        if (hashTag.matches("#[0-9a-zA-Zа-яА-Я]+")) {
           return true;
        }
        return false;
    }

    public List<Location> getAllLocations() {
        List locationsDB = locationDAO.getAllLocations();
        List<Location> locations = new ArrayList<>();
        for (Object object : locationsDB) {
            locations.add((Location)object);
        }
        return locations;
    }


    public List<Long> getAllLocationsId() {
        List locations = locationDAO.getAllLocations();
        List<Long> locationsId = new ArrayList<>();
        for (Object object : locations) {
            locationsId.add(((Location)object).getlocationId());
        }
        /*
        for (Long id : locationsId) {
            saveLocationMaxPost(id,0);
        }
        */

        return locationsId;
    }

    public Map<Long, Long> getLocationsMaxPosts() {
        logger.info("Get locations max posts");
        List locations = locationsMaxPostsDAO.getAllLocationsMaxPosts();
        if (locations != null) {
            logger.info("Locations max posts count: " + locations.size());
            Map<Long, Long> locationsMaxPostId = new LinkedHashMap<>();
            for (Object object : locations) {
                LocationMaxPost locationMaxPost = (LocationMaxPost) object;
                locationsMaxPostId.put(locationMaxPost.getLocationId(), locationMaxPost.getMaxPostId());
            }
            return locationsMaxPostId;
        }
        return null;
    }

    public void saveLocationMaxPost(long locationId, long maxPostId) {
        logger.info("Save location " + locationId + " max post " + maxPostId);
        Transaction transaction = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            LocationMaxPost locationMaxPost = new LocationMaxPost();
            locationMaxPost.setLocationId(locationId);
            locationMaxPost.setMaxPostId(maxPostId);
            locationsMaxPostsDAO.updateLocationMaxPost(locationMaxPost);
            transaction.commit();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Transaction rollback");
            transaction.rollback();
        }
    }

    public void addLocation(long code, String name, double lng, double lat) {
        Transaction transaction = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            Location location = new Location();
            location.setlocationId(code);
            location.setName(name);
            location.setLng(lng);
            location.setLat(lat);
            locationDAO.addLocation(location);
            transaction.commit();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Transaction rollback");
            transaction.rollback();
        }
    }

    public long getPostsCount() {
        if (postsCount == 0) {
            postsCount = postDAO.getPostsCount();
        }
        return postsCount;
    }

    public int getGeoGroupsCount() {
        if (geoGroupsCount == 0) {
            geoGroupsCount = geoGroupDAO.getGeoGroupsCount();
        }
        return geoGroupsCount;
    }

    public GeoGroup getGeoGroup(int id) {
        return geoGroupDAO.getGeoGroup(id);
    }

    public int getNextGeoGroupId(int lastId) {
        return geoGroupDAO.getNextGeoGroupId(lastId);
    }

    public Set<GeoGroup> getGeoGroups(int zoom) {
        Set<GeoGroup> result = new HashSet<>();
        List geoGroups = geoGroupDAO.getAllGeoGroups();

        for (Object geoGroupObj : geoGroups) {
            GeoGroup geoGroup = (GeoGroup) geoGroupObj;
            if (geoGroup.getZooms().contains(zoom)) {
                result.add(geoGroup);
            }
        }

        return result;
    }

    public int getLocationsPostsCount(DateTime from, DateTime to, Location location) {
        return postDAO.getPostsCount(from.toDate(), to.toDate(), location);
    }

    public Set<Post> getLocationsPosts(DateTime from, DateTime to, Location location) {
        Set<Post> result = new HashSet<>();
        List posts = postDAO.getPosts(from.toDate(), to.toDate(), location);
        if (posts != null) {
            Set<Post> locationPosts = new HashSet<>();
            for (Object postObj : posts) {
                locationPosts.add((Post) postObj);
            }
            result.addAll(locationPosts);
        }
        return result;
    }

    public Map<Location, List<Post>> getLocationsPosts(DateTime from, DateTime to, long maxLocationId, int count) {
        Map<Location, List<Post>> result = new LinkedHashMap<>();
        List locations = locationDAO.getLocations(maxLocationId, count);
        for (Object locationObj : locations) {
            Location location = (Location) locationObj;

            List posts = postDAO.getPosts(from.toDate(), to.toDate(), location);
            if (posts != null) {
                List<Post> locationPosts = new ArrayList<>();
                for (Object postObj : posts) {
                    locationPosts.add((Post) postObj);
                }
                result.put(location, locationPosts);
            }
        }
        return result;
    }

    public void addGeoGroup(edu.dmitry.geoserver.LocationGroup.GeoGroup geoGroup) {
        Transaction transaction = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            edu.dmitry.geoserver.hibernate.entity.GeoGroup geoGroupDB = new edu.dmitry.geoserver.hibernate.entity.GeoGroup();
            geoGroupDB.setGeoGroupId(geoGroup.getId());
            geoGroupDB.setLng(geoGroup.getLng());
            geoGroupDB.setLat(geoGroup.getLat());
            geoGroupDB.setZooms(geoGroup.getZooms());
            geoGroupDB.setRadius(geoGroup.getRadius());
            geoGroupDB.setLocations(geoGroup.getLocations());
            geoGroupDAO.addGeoGroup(geoGroupDB);
            transaction.commit();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Transaction rollback");
            transaction.rollback();
        }
    }


    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        setChanged();
        notifyObservers(postsCount);
    }
}
