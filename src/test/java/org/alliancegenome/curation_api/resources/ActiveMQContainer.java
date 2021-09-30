package org.alliancegenome.curation_api.resources;

import static java.net.HttpURLConnection.*;

import java.time.Duration;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.*;

public class ActiveMQContainer extends GenericContainer<ActiveMQContainer> {

    private static final Integer DEFAULT_PORT = 5672;
    private static final Integer DEFAULT_WEB_PORT = 8161;

    private String username = "quarkus";
    private String password = "quarkus";
    
    public ActiveMQContainer(String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }
    
    public ActiveMQContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);

        logger().info("Starting an activemq container using [{}]", dockerImageName);
        withNetworkAliases("elasticsearch-" + Base58.randomString(6));
        withEnv("ARTEMIS_USERNAME", username);
        withEnv("ARTEMIS_PASSWORD", password);
        addExposedPorts(DEFAULT_PORT);
        addExposedPorts(DEFAULT_WEB_PORT);
        
        setWaitStrategy(new HttpWaitStrategy()
            .forPort(DEFAULT_WEB_PORT)
            .forStatusCodeMatching(response -> response == HTTP_OK || response == HTTP_UNAUTHORIZED)
            .withStartupTimeout(Duration.ofMinutes(2)));
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return "amqp://" + getHost() + ":" + getMappedPort(DEFAULT_PORT);
    }

}
