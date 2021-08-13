package org.alliancegenome.curation_api.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.*;

@Liveness
@ApplicationScoped  
public class SiteHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Simple health check");
    }
}