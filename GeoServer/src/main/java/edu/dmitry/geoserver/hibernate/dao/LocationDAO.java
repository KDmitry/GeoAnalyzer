package edu.dmitry.geoserver.hibernate.dao;

import edu.dmitry.geoserver.hibernate.HibernateUtil;
import edu.dmitry.geoserver.hibernate.entity.HashTag;
import edu.dmitry.geoserver.hibernate.entity.Location;
import edu.dmitry.geoserver.hibernate.entity.Post;
import edu.dmitry.geoserver.hibernate.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class LocationDAO {
    private Logger logger = LogManager.getRootLogger();

    public Location getLocationById(Long locationId) {
        logger.info("Get location by id: " + locationId);
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.get(Location.class, locationId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List getAllLocations() {
        logger.info("Get all locations");
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createCriteria(Location.class).list();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List getLocations(long fromlocationId, int count) {
        logger.info("Get locations from " + fromlocationId + ", count = " + count);
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createCriteria(Location.class)
                    .add(Restrictions.gt("locationId", fromlocationId))
                    .addOrder(Order.asc("locationId"))
                    .setMaxResults(count)
                    .list();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void addLocation(Location location) throws Exception {
        logger.info("Add location: " + location.getlocationId());
        HibernateUtil.getSessionFactory().getCurrentSession().save(location);
    }

    /*
    public void deleteLocation(Location location) throws Exception {
        logger.info("Delete location: " + location.getlocationId());
        HibernateUtil.getSessionFactory().getCurrentSession().delete(location);
    }
    */
}
