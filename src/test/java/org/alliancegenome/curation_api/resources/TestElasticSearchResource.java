package org.alliancegenome.curation_api.resources;

import java.util.*;

import org.testcontainers.elasticsearch.ElasticsearchContainer;

import io.quarkus.test.common.*;

@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
public class TestElasticSearchResource {

	public static class Initializer implements QuarkusTestResourceLifecycleManager {

		private ElasticsearchContainer container;
		
		@Override
		public Map<String, String> start() {
			container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.10.2");
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
