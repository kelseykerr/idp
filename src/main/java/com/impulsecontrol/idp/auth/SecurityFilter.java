package com.impulsecontrol.idp.auth;

import com.impulsecontrol.idp.core.User;
import io.dropwizard.auth.basic.BasicCredentials;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

public class SecurityFilter extends OncePerRequestFilter {

    private SessionFactory sessionFactory;


    public SecurityFilter(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //TODO: error if header isn't there
        BasicCredentials creds = decode(request.getHeader("Authorization"));
        Session session = sessionFactory.openSession();
        session.setDefaultReadOnly(true);
        session.setCacheMode(CacheMode.NORMAL);
        session.setFlushMode(FlushMode.MANUAL);
        User user = null;
        Criteria c = session.createCriteria(User.class)
                .add(Restrictions.eq("username", creds.getUsername()));
        User foundUser = (User) c.uniqueResult();
        if (foundUser != null && User.verifyPassword(creds.getPassword(), foundUser.getPassword())) {
            user = foundUser;
        } else {
            logger.error("Not Authorized");
            response.setStatus(401);
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON.toString());
        }
        logger.debug("[" + user.getUsername() + "] is authorized");
        request.setAttribute("X-Auth-User", user);
        filterChain.doFilter(request, response);

        session.close();
    }

    public BasicCredentials decode(String auth) {

        //Replacing "Basic THE_BASE_64" to "THE_BASE_64" directly
        auth = auth.replaceFirst("[B|b]asic ", "");

        //Decode the Base64 into byte[]
        byte[] decodedBytes = DatatypeConverter.parseBase64Binary(auth);

        //If the decode fails in any case
        if (decodedBytes == null || decodedBytes.length == 0) {
            return null;
        }

        //Now we can convert the byte[] into a splitted array :
        //  - the first one is email,
        //  - the second one password
        String[] authParams = new String(decodedBytes).split(":", 2);

        return new BasicCredentials(authParams[0], authParams[1]);
    }
}


