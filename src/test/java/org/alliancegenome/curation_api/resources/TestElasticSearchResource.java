package org.alliancegenome.curation_api.resources;

import java.util.*;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;

import io.quarkus.test.common.*;

@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
public class TestElasticSearchResource {

	public static class Initializer implements QuarkusTestResourceLifecycleManager {

		private GenericContainer container;

		@Override
		public Map<String, String> start() {
			container = new GenericContainer("opensearchproject/opensearch:latest");

			container.withExposedPorts(9200);
			container.setWaitStrategy((new HttpWaitStrategy()).forPort(9200).forStatusCodeMatching(response -> response == 200 || response == 401));
			container.withEnv("discovery.type", "single-node");
			container.withEnv("DISABLE_INSTALL_DEMO_CONFIG", "true");
			container.withEnv("DISABLE_SECURITY_PLUGIN", "true");

			container.start();

			return getConfig();
		}


		private Map<String, String> getConfig() {
			final Map<String, String> map = new HashMap<>();

			map.put("quarkus.hibernate-search-orm.elasticsearch.hosts", container.getHost() + ":" + container.getMappedPort(9200));

			return map;
		}

		@Override
		public void stop() {
			// Don't shutdown the containers manually, the testing framework will do it for us
			// and this prevents errors when the services shutdown before quarkus does
		}
	}
}
