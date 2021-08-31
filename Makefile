PROCS = -T 8
PACKAGE = clean package
#FLAGS = -DskipTests=true -ntp -Dorg.slf4j.simpleLogger.defaultLogLevel=WARN
FLAGS = -Dquarkus.package.type=uber-jar
RELEASE = 0.0.7
REG = 100225593120.dkr.ecr.us-east-1.amazonaws.com

OPTS = $(PROCS) $(PACKAGE) $(FLAGS)

.PHONY: docker all

all: ui api

api:
	mvn ${OPTS}

ui:
	make -B -C src/main/cliapp
	make -B -C src/main/cliapp build

uirun:
	make -B -C src/main/cliapp run

run:
	java -jar target/agr_curation_api-runner.jar

apirun:
	mvn compile quarkus:dev

docker:
	docker build -t ${REG}/agr_curation:${RELEASE} .
docker-push:
	docker push ${REG}/agr_curation:${RELEASE}
docker-run:
	docker run --rm -it -p 8080:8080 -e QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://192.168.1.251:5432/curation -e QUARKUS_ARTEMIS_URL=tcp://192.168.1.251:61616 -e QUARKUS_HIBERNATE_SEARCH_ORM_ELASTICSEARCH_HOSTS=192.168.1.251:9200 ${REG}/agr_curation:${RELEASE}

debug:
	java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5045 -jar target/agr_curation_api-bootable.jar

test:
	mvn test

verify:
	mvn verify
