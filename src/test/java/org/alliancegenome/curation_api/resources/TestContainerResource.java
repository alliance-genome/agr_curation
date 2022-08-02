package org.alliancegenome.curation_api.resources;

import java.util.*;

import org.testcontainers.containers.PostgreSQLContainer;

import io.quarkus.test.common.*;

@QuarkusTestResource(TestContainerResource.Initializer.class)
public class TestContainerResource {

	public static class Initializer implements QuarkusTestResourceLifecycleManager {

		private OpenSearchContainer container;
		private PostgreSQLContainer pgContainer;

		@Override
		public Map<String, String> start() {

			container = new OpenSearchContainer("opensearchproject/opensearch:1.2.4");
			pgContainer = new PostgreSQLContainer("postgres:14.2");
			
			pgContainer.withEnv("POSTGRES_HOST_AUTH_METHOD", "trust");
			pgContainer.withDatabaseName("curation");
			pgContainer.withUsername("postgres");
			pgContainer.withPassword("");

			container.start();
			pgContainer.start();

			return getConfig();
		}

		private Map<String, String> getConfig() {
			final Map<String, String> map = new HashMap<>();

			map.put("quarkus.hibernate-search-orm.elasticsearch.hosts", container.getHost() + ":" + container.getMappedPort(9200));
			map.put("quarkus.hibernate-search-orm.elasticsearch.version", "openseach:2.1.0");
			
			map.put("quarkus.datasource.jdbc.url", "jdbc:postgresql://" + pgContainer.getHost() + ":" + pgContainer.getMappedPort(5432) + "/curation");
			
			//map.put("quarkus.elasticsearch.hosts", container.getHost() + ":" + container.getMappedPort(9200));
			//map.put("quarkus.elasticsearch.version", "openseach:2.1.0");

			//map.put("quarkus.log.level", "DEBUG");

			return map;
		}

		@Override
		public void stop() {
			// Don't shutdown the containers manually, the testing framework will do it for us
			// and this prevents errors when the services shutdown before quarkus does
		}
	}
}
