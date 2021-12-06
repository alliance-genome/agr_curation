package org.alliancegenome.curation_api.exceptions;

import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(RuntimeException e) {
        log.error(e);
        e.printStackTrace();
        Response.ResponseBuilder rb = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        return rb.build();
    }
}
