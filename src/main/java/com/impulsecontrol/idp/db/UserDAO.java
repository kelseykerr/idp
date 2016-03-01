package com.impulsecontrol.idp.db;

import com.impulsecontrol.idp.core.User;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kerrk on 2/19/16.
 */
public class UserDAO extends AbstractDAO<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDAO.class);

    public UserDAO(SessionFactory factory) {
        super(factory);
    }

    public User findUserByName(String name) {
        Criteria c = currentSession().createCriteria(User.class)
                .add(Restrictions.eq("username", name));
        return (User) c.uniqueResult();
    }

    public User saveOrUpdate(User user) {
        currentSession().saveOrUpdate(user);
        return user;
    }
}
