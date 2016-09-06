package edu.dmitry.geoserver.hibernate.dao;

import edu.dmitry.geoserver.hibernate.HibernateUtil;
import edu.dmitry.geoserver.hibernate.entity.HashTag;
import edu.dmitry.geoserver.hibernate.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserDAO {
    private Logger logger = LogManager.getRootLogger();

    public void addUser(User user) throws ExecutionException {
        logger.info("Add user: " + user.getInstagramNickName());
        HibernateUtil.getSessionFactory().getCurrentSession().save(user);
    }

    public User getUserByName(String name) {
        logger.info("Get User by Name: " + name);
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            List list = session.createCriteria(User.class).add(Restrictions.eq("instagramNickName", name)).list();
            if (list.size() == 1) {
                return (User) list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
