package com.impulsecontrol.idp.resources;

import com.impulsecontrol.idp.Exceptions.ConflictException;
import com.impulsecontrol.idp.core.User;
import com.impulsecontrol.idp.db.UserDAO;
import com.impulsecontrol.idp.wrappers.CredentialDTO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by kerrk on 2/19/16.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

    private final UserDAO userDAO;

    public AuthenticationResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @POST
    @Path("signin")
    @UnitOfWork
    public User signIn(@Valid CredentialDTO creds) throws Exception {
        User user = userDAO.findUserByName(creds.email);
        if (user != null && User.verifyPassword(creds.password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @POST
    @Path("signup")
    @UnitOfWork
    public User signUp(@Valid CredentialDTO creds) throws Exception {
        User user = userDAO.findUserByName(creds.email);
        if (user != null) {
            throw new ConflictException("User with the email [" + creds.email + "] already exists.");
        }
        User newUser = new User(creds.email, creds.password);
        return userDAO.saveOrUpdate(newUser);
    }



}
