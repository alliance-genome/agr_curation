package org.alliancegenome.curation_api.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class SiteHealthCheck implements HealthCheck {

	@Override
	public HealthCheckResponse call() {
		return HealthCheckResponse.up("Simple health check");
	}
}