package org.alliancegenome.curation_api.resources;

import java.util.*;

import org.testcontainers.containers.GenericContainer;

import io.quarkus.test.common.*;

@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
public class TestElasticSearchResource {

	public static class Initializer implements QuarkusTestResourceLifecycleManager {

		private GenericContainer container;
		
		@Override
		public Map<String, String> start() {
			container = new GenericContainer("opensearchproject/opensearch:latest");
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
