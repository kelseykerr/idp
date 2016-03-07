package com.impulsecontrol.idp;

import com.impulsecontrol.idp.auth.IdPAuthenticator;
import com.impulsecontrol.idp.auth.IdPAuthorizer;
import com.impulsecontrol.idp.auth.SecurityFilter;
import com.impulsecontrol.idp.core.Role;
import com.impulsecontrol.idp.core.User;
import com.impulsecontrol.idp.core.UserToRole;
import com.impulsecontrol.idp.db.UserDAO;
import com.impulsecontrol.idp.resources.AuthenticationResource;
import com.impulsecontrol.idp.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;


public class IdentityManagementApp extends Application<IdentityManagementConfiguration> {
    public static void main(String[] args) throws Exception {
        new IdentityManagementApp().run(args);
    }

    private final HibernateBundle<IdentityManagementConfiguration> hibernateBundle =
            new HibernateBundle<IdentityManagementConfiguration>(User.class, Role.class, UserToRole.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(IdentityManagementConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public void initialize(Bootstrap<IdentityManagementConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<IdentityManagementConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(IdentityManagementConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
    }


    @Override
    public void run(IdentityManagementConfiguration configuration, Environment environment) {
        final UserDAO userDAO = new UserDAO(hibernateBundle.getSessionFactory());
        SecurityFilter securityFilter = new SecurityFilter(hibernateBundle.getSessionFactory());

        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new IdPAuthenticator(userDAO))
                .setAuthorizer(new IdPAuthorizer())
                .setRealm("IdP Authentication")
                .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new UserResource(userDAO));
        environment.jersey().register(new AuthenticationResource(userDAO));

        FilterRegistration.Dynamic filterRegistration = environment.servlets()
                .addFilter("basicAuthFilter", securityFilter);
        filterRegistration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/api/user");
    }

}