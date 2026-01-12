package org.alliancegenome.curation_api.config;

import org.alliancegenome.curation_api.auth.Secured;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@Secured
@ApplicationPath("/api")
@OpenAPIDefinition(
	info = @Info(
		description = " <h3> This is the Alliance Curation Java API </h3>"
			+ "<h1> !!! Please note: Some Swagger widgets may crash this page when expanded !!! </h1>"
			+ "<h1>For flat file of API documentation click on the link below <a href=\"/openapi\"><h1>OpenAPI definition download</h1></a></h1>",
		title = "Alliance of Genome Resources Curation API ",
		version = "1.0 Alpha"
	),
	security = {
		@SecurityRequirement(name = "APIToken"),
		@SecurityRequirement(name = "Bearer")
	},
	components = @Components(
		securitySchemes = {
			@SecurityScheme(
				securitySchemeName = "APIToken",
				type = SecuritySchemeType.APIKEY,
				in = SecuritySchemeIn.HEADER,
				apiKeyName = "Authorization",
				description = "APIToken {apiToken} # Curation Short API Token",
				scheme = "api-key"
			),
			@SecurityScheme(
				securitySchemeName = "Bearer",
				type = SecuritySchemeType.APIKEY,
				in = SecuritySchemeIn.HEADER,
				apiKeyName = "Authorization",
				description = "<p>Bearer {accessToken} # Cognito Access Token</p>"
					+ "<p>Bearer {adminToken} # Admin Token Generated from the CurationAPI-Admin</p>",
				scheme = "api-key"
			)
		}
	)
)
public class RestApplication extends Application {

}
