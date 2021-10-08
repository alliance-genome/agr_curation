package org.alliancegenome.curation_api.exceptions;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestErrorExceptionMapper implements ExceptionMapper<RestErrorException> {

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(RestErrorException e) {
        Response.ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
        rb.entity(e.getObjectResponse());
        return rb.build();
    }
}
