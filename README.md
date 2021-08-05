# AGR Curation API

This repo holds the code for the API and the UI of the AGR curation system.

## Getting Started

These instructions will get you a copy of the project and the API up and running locally. This include Postgres and ElasticSearch.

## Contents

- [Installing](#installing)
	* [Postgres](#postgres)
	* [ElasticSearch](#elasticsearch)
- [Building](#building)
	* [Building API](#building_api)
	* [Building UI](#building_ui)
- [Running](#running)
	* [Running API](#running_api)
	* [Running UI](#running_ui)
- [Loading Data](#loading_data)



## Installing

Currently there are several scripts in the `docker` directory that can be used to start the various servers.

### Postgres

[Run Postgres Script](docker/run_postgres) which basically runs the following docker command:

```bash
docker run -d -p 5432:5432 --net curation --name postgres -e POSTGRES_HOST_AUTH_METHOD=trust postgres:13
```

In order to see if the database is up and running you can issue the command `docker ps -a` and it will have the following output:

```
CONTAINER ID        IMAGE                  COMMAND                  CREATED             STATUS              PORTS                                            NAMES
a16b7f90afd4        postgres:13            "docker-entrypoint.s…"   8 days ago          Up 19 hours         0.0.0.0:5432->5432/tcp                           postgres
```

If you are needing to connect to the production version of the database run the following command to get the credentials once you have been setup with an AWS account.

```bash
docker run --rm -it -v ~/.aws:/root/.aws amazon/aws-cli secretsmanager get-secret-value --secret-id curation-db-admin --region us-east-1 --query SecretString --output text
```

### Elastic Search

[Run Elastic Search Script](docker/run_es) which basically runs the following docker command:

```bash
docker run --rm -d --net curation -p 9200:9200 -p 9300:9300 -e network.bind_host=0.0.0.0 -e transport.bind_host:0.0.0.0 -e xpack.security.enabled=false -e ELASTICSEARCH_NODE_NAME=elasticsearch  -e ELASTICSEARCH_CLUSTER_HOSTS=elasticsearch,elasticsearch2 --name elasticsearch elasticsearch:5.6.16
docker run --rm -d --net curation -p 9201:9200 -p 9301:9300 -e network.bind_host=0.0.0.0 -e transport.bind_host:0.0.0.0 -e xpack.security.enabled=false -e ELASTICSEARCH_NODE_NAME=elasticsearch2 -e ELASTICSEARCH_CLUSTER_HOSTS=elasticsearch,elasticsearch2 --name elasticsearch2 elasticsearch:5.6.16
```

Currently the Elastic Search is disabled due to using an old version of `hibernate-elasticsearch` which requires the older version of ES. This will be updated in the future.

## Building

These next steps will get either the UI or API or both up and running locally. Most of these commands are done via the Makefile in order to save typing.

### Building API

Once you have cloned the repo issue the following command in the root of the project:

```bash
> make api
mvn -T 8 clean package -DskipTests=true
[INFO] Scanning for projects...
...
```

This will download all the dependencies from maven central and build out a bootable jar file.

### Building UI

```bash
> make ui
make -B -C src/main/cliapp
npm install
...
```
This will download all npm dependencies and produce a compacted js files ready for deployment.

### Building Both

If you are looking just to run everything at once just issue the following:

```bash
> make
```
This will start by building the UI and then will package the UI into the API and build the API.

## Running

Depending on which part of the application you would like to run find the following commands:

### Running API

After the API has been built issue the following command:

```bash
> make apirun
java -jar target/agr_curation_api-bootable.jar -b=0.0.0.0
...
```

This will run just the API and you can find it running at: [http://localhost:8080](http://localhost:8080) however if the API was built without the UI there will be nothing to see at that url. However there is a swagger interface running at: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Running UI

After the UI has been build issue the following command:

```bash
> make uirun
make -B -C src/main/cliapp run
npm start
...
```

This will start the local UI and it will be running at: [http://localhost:3000](http://localhost:3000) this is in develop mode so any changes made to code will automatically restart the UI.

### Running Both

After running `make` in the root of the project issue the following command to run both UI and API.

```bash
> make run
java -jar target/agr_curation_api-bootable.jar -b=0.0.0.0
...
```
Given that the UI has at this point been bundled into the API you can find the whole site running at the following: [http://localhost:8080](http://localhost:8080) this will include the UI and give links to Swagger and other resources that are being used.

## Loading Data

Assuming the API is running on localhost, check the Swagger interface for the correct endpoint to post data to. Here is an example of loading the Gene BGI file from the Alliance.

```bash
> curl -vX POST http://localhost:8080/api/gene/bulk/bgi -d @1.0.1.4_BGI_ZFIN_4.json --header "Content-Type: application/json"
```

Here is an example of loading Do ontology:

```bash
> curl -vX POST http://localhost:8080/api/doterm/bulk/owl -d @doid.owl --header "Content-Type: application/xml"
```

## Maintainers

Current maintainers:

 * Adam Gibson - [https://github.com/adamgibs](https://github.com/adamgibs)
 * Andrés Becerra Sandoval - [https://github.com/abecerra](https://github.com/abecerra)
 * Christian Pich - [https://github.com/cmpich](https://github.com/cmpich)
 * Jyothi Thota - [https://github.com/jt15](https://github.com/jt15)
 * Mark Quinton-Tulloch - [https://github.com/markquintontulloch](https://github.com/markquintontulloch)
 * Manuel Luypaert - [https://github.com/mluypaert](https://github.com/mluypaert)
 * Marek Tutaj - [https://github.com/tutajm](https://github.com/tutaj)
 * Olin Blodgett - [https://github.com/oblodgett](https://github.com/oblodgett)