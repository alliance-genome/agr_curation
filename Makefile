PROCS = -T 8
PACKAGE = clean package
#FLAGS = -DskipTests=true -ntp -Dorg.slf4j.simpleLogger.defaultLogLevel=WARN
FLAGS = -DskipTests=true

OPTS = $(PROCS) $(PACKAGE) $(FLAGS)

all:
	mvn ${OPTS}

%:
	mvn $(OPTS) -pl $@ -am

run:
	java -jar curation_api/target/curation_api-bootable.jar

run-dev:
	java -jar target/curation_api-bootable.jar -DES_INDEX=site_index_dev

debug:
	java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5045 -jar target/curation_api-bootable.jar

docker-run-command:
	java -jar target/curation_api-bootable.jar

test:
	mvn test

verify:
	mvn verify
