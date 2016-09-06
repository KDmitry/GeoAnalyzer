package edu.dmitry.geoserver.hibernate.dao;

import edu.dmitry.geoserver.hibernate.HibernateUtil;
import edu.dmitry.geoserver.hibernate.entity.LocationMaxPost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LocationMaxPostDAO {
    private Logger logger = LogManager.getRootLogger();

    public List getAllLocationsMaxPosts() {
        logger.info("Get all locations max posts");
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createCriteria(LocationMaxPost.class).list();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void updateLocationMaxPost(LocationMaxPost locationMaxPost) throws Exception {
        logger.info("Update location " + locationMaxPost.getLocationId() + " max post " + locationMaxPost.getMaxPostId());
        HibernateUtil.getSessionFactory().getCurrentSession().update(locationMaxPost);
    }
}
