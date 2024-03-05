package org.alliancegenome.curation_api.config;

import org.alliancegenome.curation_api.auth.Secured;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.servers.ServerVariable;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@Secured
@ApplicationPath("/api")
@OpenAPIDefinition(
		info = @Info(
				description = "This is the Alliance Curation Java API", 
				title = "Alliance of Genome Resources Curation API " + "!!!" + " Please note: Some Swagger widgets may crash this page when expanded " + "!!!", 
				version = "1.0 Alpha"
			), 
		security = { @SecurityRequirement(name = "api_token") },
		components = @Components(securitySchemes = {
				@SecurityScheme(
						securitySchemeName = "api_token", 
						type = SecuritySchemeType.HTTP, 
						description = "Curator API Token", scheme = "bearer") 
		}),
		externalDocs = @org.eclipse.microprofile.openapi.annotations.ExternalDocumentation(
				description = "For flat file of API documentation click on this text", 
				url = "http://localhost:8080/openapi")
)
public class RestApplication extends Application {

}