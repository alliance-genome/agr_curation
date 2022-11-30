package org.alliancegenome.curation_api.resources;

import java.time.Duration;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.*;
import org.testcontainers.utility.*;

public class OpenSearchContainer extends GenericContainer<OpenSearchContainer> {

	public OpenSearchContainer(String dockerImageName) {
		this(DockerImageName.parse(dockerImageName));
	}

	public OpenSearchContainer(final DockerImageName dockerImageName) {
		super(dockerImageName);

		logger().info("Starting an elasticsearch container using [{}]", dockerImageName);

		withNetworkAliases("opensearch-" + Base58.randomString(6));
		withEnv("discovery.type", "single-node");
		withEnv("DISABLE_INSTALL_DEMO_CONFIG", "true");
		withEnv("DISABLE_SECURITY_PLUGIN", "true");
		addExposedPort(9200);

		// String regex = ".*(\"message\":\\s?\"started\".*|] started\n$)";
		// setWaitStrategy(new LogMessageWaitStrategy().withRegEx(regex));
		setWaitStrategy((new HttpWaitStrategy())
			.forPort(9200)
			.forStatusCodeMatching(response -> response == 200 || response == 401)
			.withStartupTimeout(Duration.ofSeconds(300))
		);

	}
}