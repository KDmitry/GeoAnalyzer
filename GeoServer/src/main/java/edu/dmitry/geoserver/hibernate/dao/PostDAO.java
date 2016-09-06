package edu.dmitry.geoserver.hibernate.dao;

import edu.dmitry.geoserver.hibernate.HibernateUtil;
import edu.dmitry.geoserver.hibernate.entity.Location;
import edu.dmitry.geoserver.hibernate.entity.Post;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import java.util.Date;
import java.util.List;


public class PostDAO {
    private Logger logger = LogManager.getRootLogger();

    public void addPost(Post post) throws Exception {
        logger.info("Add post: " + post.getPostId());
        HibernateUtil.getSessionFactory().getCurrentSession().save(post);
    }

    public long getPostsCount() {
        logger.info("Get posts count");
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return ((Number)session.createCriteria(Post.class).setProjection(Projections.rowCount()).uniqueResult()).longValue();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public int getPostsCount(Date from, Date to, Location location) {
        logger.info("Get posts count by time  from " + from + " to " + to + ", locationId = " + location.getlocationId());
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return ((Number)session.createCriteria(Post.class)
                    .add(Restrictions.between("createdTime", from, to))
                    .add(Restrictions.eq("location", location))
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

    public List getPosts(Date from, Date to, Location location) {
        logger.info("Get posts by time  from " + from + " to " + to + ", locationId = " + location.getlocationId());
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            List posts = session.createCriteria(Post.class)
                    .add(Restrictions.between("createdTime", from, to))
                    .add(Restrictions.eq("location", location))
                    .list();


            for (Object obj : posts) {
                Hibernate.initialize(((Post)obj).getHashTags());
                Hibernate.initialize(((Post)obj).getNickNames());
            }



            return posts;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /*
    public List getPostsByLocation(long locationId) {
        logger.info("Get posts by location " + locationId);
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createCriteria(Post.class).add(Restrictions.eq("locationId", locationId)).list();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    */
}
