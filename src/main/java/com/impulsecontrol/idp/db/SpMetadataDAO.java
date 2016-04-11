package com.impulsecontrol.idp.db;


import com.impulsecontrol.idp.core.SpMetadata;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * Created by kerrk on 3/31/16.
 */
public class SpMetadataDAO extends AbstractDAO<SpMetadata> {

    public SpMetadataDAO(SessionFactory factory) {
        super(factory);
    }

    public SpMetadata findMetadataByAppName(String name) {
        Criteria c = currentSession().createCriteria(SpMetadata.class)
                .add(Restrictions.eq("appName", name));
        return (SpMetadata) c.uniqueResult();
    }
}
