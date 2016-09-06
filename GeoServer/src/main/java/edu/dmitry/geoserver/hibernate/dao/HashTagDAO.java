package edu.dmitry.geoserver.hibernate.dao;

import edu.dmitry.geoserver.hibernate.HibernateUtil;
import edu.dmitry.geoserver.hibernate.entity.HashTag;
import edu.dmitry.geoserver.hibernate.entity.Location;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class HashTagDAO {
    private Logger logger = LogManager.getRootLogger();

    public HashTag getHashTagByName(String name) {
        logger.info("Get HashTag by Name: " + name);
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            List list = session.createCriteria(HashTag.class).add(Restrictions.eq("name", name)).list();
            if (list.size() == 1) {
                return (HashTag) list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void addHashTag(HashTag hashTag) throws Exception {
        logger.info("Add hashTag: " + hashTag.getName());
        HibernateUtil.getSessionFactory().getCurrentSession().save(hashTag);
    }
}
