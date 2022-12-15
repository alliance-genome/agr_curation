package org.alliancegenome.curation_api.config;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.alliancegenome.curation_api.auth.Secured;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

@Secured
@ApplicationPath("/api")
@OpenAPIDefinition(info = @Info(description = "This is the Alliance Curation Java API", title = "Alliance of Genome Resources Curation API", version = "1.0 Alpha"), security = {
	@SecurityRequirement(name = "api_token") }, components = @Components(securitySchemes = {
		@SecurityScheme(securitySchemeName = "api_token", type = SecuritySchemeType.HTTP, description = "Curator API Token", scheme = "bearer") }))
public class RestApplication extends Application {

}