package com.impulsecontrol.idp.resources;

import com.impulsecontrol.idp.Exceptions.ConflictException;
import com.impulsecontrol.idp.core.SpMetadata;
import com.impulsecontrol.idp.core.User;
import com.impulsecontrol.idp.db.SpMetadataDAO;
import com.impulsecontrol.idp.db.UserDAO;
import com.impulsecontrol.idp.services.SamlService;
import com.impulsecontrol.idp.wrappers.CredentialDTO;
import com.impulsecontrol.idp.wrappers.SamlResponseDTO;
import com.impulsecontrol.idp.wrappers.UserDTO;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.hibernate.UnitOfWork;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by kerrk on 2/19/16.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

    private final UserDAO userDAO;
    private final SpMetadataDAO spMetadataDAO;
    private final SamlService samlService = new SamlService();

    public AuthenticationResource(UserDAO userDAO, SpMetadataDAO spMetadataDAO) {
        this.userDAO = userDAO;
        this.spMetadataDAO = spMetadataDAO;
    }

    @POST
    @Path("signin")
    @UnitOfWork
    public UserDTO signIn(@Valid CredentialDTO creds) throws Exception {
        User user = userDAO.findUserByName(creds.email);
        if (user != null && User.verifyPassword(creds.password, user.getPassword())) {
            return UserDTO.transform(user);
        }
        return null;
    }

    @POST
    @Path("signup")
    @UnitOfWork
    public UserDTO signUp(@Valid CredentialDTO creds) throws Exception {
        User user = userDAO.findUserByName(creds.email);
        if (user != null) {
            throw new ConflictException("User with the email [" + creds.email + "] already exists.");
        }
        User newUser = new User(creds.email, creds.password);
        userDAO.saveOrUpdate(newUser);
        return UserDTO.transform(newUser);
    }


    @POST
    @Path("/SSO/{app}")
    @UnitOfWork
    public SamlResponseDTO IdPSSO(@Context HttpServletRequest request,
                                  @Context HttpServletResponse response,
                                  @PathParam("app") String appName) throws Exception {
        try {
            User user = (User) request.getAttribute("X-Auth-User");
            SpMetadata spMetadata = spMetadataDAO.findMetadataByAppName(appName);
            Response samlResponse = samlService.buildResponse(user, spMetadata);
            samlResponse = samlService.signSamlResponseObject(samlResponse);
            ResponseMarshaller marshaller = new ResponseMarshaller();
            Element plaintextElement = marshaller.marshall(samlResponse);
            String originalResponseString = XMLHelper.nodeToString(plaintextElement);
            String encodedResponse = samlService.encodeSamlResponse(originalResponseString);
            SamlResponseDTO dto = new SamlResponseDTO();
            dto.acsUrl = spMetadata.getAcsUrl();
            dto.samlResponse = encodedResponse;
            return dto;
        } catch (MarshallingException e) {
            throw new Exception("Error authenticating user: " + e.getMessage());
        }
    }



}
