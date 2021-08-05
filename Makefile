PROCS = -T 8
PACKAGE = clean package
#FLAGS = -DskipTests=true -ntp -Dorg.slf4j.simpleLogger.defaultLogLevel=WARN
FLAGS = -DskipTests=true

OPTS = $(PROCS) $(PACKAGE) $(FLAGS)

all: cliapp
	mvn ${OPTS}

%:
	mvn $(OPTS) -pl $@ -am

run:
	java -jar target/agr_curation_api-bootable.jar -b=0.0.0.0

ui: cliapp

ui-run:
	npm start 

cliapp:
	make -B -C src/main/cliapp
	make -B -C src/main/cliapp build

run-dev:
	java -jar target/agr_curation_api-bootable.jar  -DES_INDEX=site_index_dev

debug:
	java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5045 -jar target/agr_curation_api-bootable.jar

docker-run-command:
	java -jar target/agr_curation_api-bootable.jar

test:
	mvn test

verify:
	mvn verify
