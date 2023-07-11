package org.alliancegenome.curation_api.config;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
		responseContext.getHeaders().add("Cross-Origin-Opener-Policy-Report-Only", "*");
		responseContext.getHeaders().add("Cross-Origin-Embedder-Policy-Report-Only", "*");
		responseContext.getHeaders().add("Access-Control-Allow-Methods", "*");
		responseContext.getHeaders().add("Access-Control-Max-Age", "-1");
		responseContext.getHeaders().add("Access-Control-Allow-Headers", "*");
	}
}
