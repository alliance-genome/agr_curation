package org.alliancegenome.curation_api.resources;

import java.util.*;

import org.testcontainers.elasticsearch.ElasticsearchContainer;

import io.quarkus.test.common.*;

@QuarkusTestResource(TestElasticSearchReourse.Initializer.class)
public class TestElasticSearchReourse {

    public static class Initializer implements QuarkusTestResourceLifecycleManager {

        private ElasticsearchContainer container;
        private ActiveMQContainer mqContainer;
        
        @Override
        public Map<String, String> start() {
            container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.9.0");
            container.start();
            mqContainer = new ActiveMQContainer("vromero/activemq-artemis:2.9.0-alpine");
            mqContainer.start();
            return getConfig();
        }

        private Map<String, String> getConfig() {
            final Map<String, String> map = new HashMap<>();

            map.put("quarkus.hibernate-search-orm.elasticsearch.hosts", container.getHost() + ":" + container.getMappedPort(9200));

            map.put("quarkus.qpid-jms.username", mqContainer.getUsername());
            map.put("quarkus.qpid-jms.password", mqContainer.getPassword());
            map.put("quarkus.qpid-jms.url", mqContainer.getUrl());

            return map;
        }

        @Override
        public void stop() {
            // Don't shutdown the containers manually, the testing framework will do it for us
            // and this prevents errors when the services shutdown before quarkus does
        }
    }
}
