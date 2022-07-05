package org.alliancegenome.curation_api.exceptions;

import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@Provider
public class ApiErrorExceptionMapper implements ExceptionMapper<ApiErrorException> {

	@Override
	@Produces(MediaType.APPLICATION_JSON)
	public Response toResponse(ApiErrorException e) {
		log.info(e);
		e.printStackTrace();
		Response.ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
		// dereference entity to make sure it's not tried to be serialized
		// due to lazy-initialization issues
		// TODO: Once we get the jackson-hibernate HibernateSerialzers working we don't need it any longer.
		e.getObjectResponse().setEntity(null);
		rb.entity(e.getObjectResponse());
		return rb.build();
	}
}
