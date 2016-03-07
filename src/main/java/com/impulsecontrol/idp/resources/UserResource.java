package com.impulsecontrol.idp.resources;

import com.impulsecontrol.idp.core.User;
import com.impulsecontrol.idp.db.UserDAO;
import com.impulsecontrol.idp.wrappers.UserDTO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GET
    @UnitOfWork
    public UserDTO getCurrentUser(@Context HttpServletRequest request) {
        User user = (User) request.getAttribute("X-Auth-User");
        return UserDTO.transform(user);
    }
}
