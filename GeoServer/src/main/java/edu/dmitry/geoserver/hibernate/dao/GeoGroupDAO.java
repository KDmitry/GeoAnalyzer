package edu.dmitry.geoserver.hibernate.dao;

import edu.dmitry.geoserver.hibernate.HibernateUtil;
import edu.dmitry.geoserver.hibernate.entity.GeoGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import java.util.List;

public class GeoGroupDAO {
    private Logger logger = LogManager.getRootLogger();

    public List getAllGeoGroups() {
        logger.info("Get all geo groups");
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            List geoGroups = session.createCriteria(GeoGroup.class).list();

            for (Object obj : geoGroups) {
                Hibernate.initialize(((GeoGroup)obj).getZooms());
                Hibernate.initialize(((GeoGroup)obj).getLocations());
            }

            return geoGroups;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public int getGeoGroupsCount() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return ((Number)session.createCriteria(GeoGroup.class)
                    .setProjection(Projections.rowCount()).uniqueResult()).intValue();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List getGeoGroups(int zoom) {
        logger.info("Get geo groups with zoom " + zoom);
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createCriteria(GeoGroup.class)
                    .add(Restrictions.eq("zoom", zoom))
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

    public int getNextGeoGroupId(int lastId) {
        logger.info("Get next geo group id after: " + lastId);
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            List list = session.createCriteria(GeoGroup.class)
                    .add(Restrictions.gt("geoGroupId", lastId))
                    .setMaxResults(1)
                    .list();

            if (list.size() == 1) {
                return ((GeoGroup)list.get(0)).getGeoGroupId();
            } else {
                return -1;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return -1;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public GeoGroup getGeoGroup(int id) {
        logger.info("Get geo group for id: " + id);
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            List list;
            if (id != 0) {
                list = session.createCriteria(GeoGroup.class)
                        .add(Restrictions.eq("geoGroupId", id))
                        .list();
            } else {
                list = session.createCriteria(GeoGroup.class)
                        .addOrder(Order.asc("geoGroupId"))
                        .setMaxResults(1)
                        .list();
            }

            if (list.size() == 1) {
                GeoGroup geoGroup = (GeoGroup)list.get(0);
                Hibernate.initialize(geoGroup.getZooms());
                Hibernate.initialize(geoGroup.getLocations());
                return geoGroup;
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void addGeoGroup(GeoGroup geoGroup) throws Exception {
        logger.info("Add geo group");
        HibernateUtil.getSessionFactory().getCurrentSession().save(geoGroup);
    }
}
