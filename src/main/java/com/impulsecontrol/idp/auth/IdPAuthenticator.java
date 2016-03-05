package com.impulsecontrol.idp.auth;

import com.google.common.base.Optional;
import com.impulsecontrol.idp.core.User;
import com.impulsecontrol.idp.db.UserDAO;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

/**
 * Created by kerrk on 2/19/16.
 */
public class IdPAuthenticator implements Authenticator<BasicCredentials, User> {

    private UserDAO userDAO;


    public IdPAuthenticator(UserDAO userDAO) {
        userDAO = userDAO;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        User user = userDAO.findUserByName(credentials.getUsername());
        if (user != null && user.getPassword().equals(credentials.getPassword())) {
            return Optional.of(user);
        }
        return Optional.absent();
    }

}
