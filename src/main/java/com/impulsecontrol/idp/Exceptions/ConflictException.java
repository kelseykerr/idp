package com.impulsecontrol.idp.Exceptions;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

/**
 * Created by kerrk on 2/29/16.
 */
public class ConflictException extends ClientErrorException {
    public ConflictException(String message) {
        super(message, Response.Status.CONFLICT);
    }
}
