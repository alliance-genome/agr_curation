PROCS = -T 8
PACKAGE = clean package
#FLAGS = -DskipTests=true -ntp -Dorg.slf4j.simpleLogger.defaultLogLevel=WARN
FLAGS = -DskipTests=true

OPTS = $(PROCS) $(PACKAGE) $(FLAGS)

all: ui api

api:
	mvn ${OPTS}

ui:
	make -B -C src/main/cliapp
	make -B -C src/main/cliapp build

uirun:
	make -B -C src/main/cliapp run

run:
	java -jar target/agr_curation_api-bootable.jar -b=0.0.0.0

apirun:
	java -jar target/agr_curation_api-bootable.jar -b=0.0.0.0



debug:
	java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5045 -jar target/agr_curation_api-bootable.jar

test:
	mvn test

verify:
	mvn verify

%:
	mvn $(OPTS) -pl $@ -am
