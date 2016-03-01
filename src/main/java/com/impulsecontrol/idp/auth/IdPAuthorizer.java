package com.impulsecontrol.idp.auth;

import com.impulsecontrol.idp.core.User;
import com.impulsecontrol.idp.core.UserToRole;
import io.dropwizard.auth.Authorizer;

import java.util.List;

/**
 * Created by kerrk on 2/19/16.
 */
public class IdPAuthorizer implements Authorizer<User> {
    @Override
    public boolean authorize(User user, String role) {
        List<UserToRole> userRoles = user.getUserRoles();
        for (UserToRole userRole:userRoles) {
            if (userRole.getRole().getName() == role) {
                return true;
            }
        }
        return true;
    }
}
