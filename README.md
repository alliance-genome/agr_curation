# AGR Curation API

This repo holds the code for the API and the UI of the AGR curation system.

## Getting Started

These instructions will get you a copy of the project and the API up and running locally. This includes Postgres, ElasticSearch, Cerebro, and ActiveMQ.

## Contents

- [Installing](#installing)
	* [Docker Setup](#docker_setup)
	* [Postgres](#postgres)
	* [ElasticSearch](#elasticsearch)
	* [Cerebro](#cerebro)
	* [ActiveMQ](#activemq)
- [Building](#building)
	* [Building API](#building_api)
	* [Building UI](#building_ui)
- [Running](#running)
	* [Running API](#running_api)
	* [Running UI](#running_ui)
- [Loading Data](#loading_data)



## Installing

Currently there are several scripts in the `docker` directory that can be used to start the various servers, locally if there is not a dev server available.

### Maven

Maven version 3.8.1 should be installed in order to compile all the code and libraries.

```bash
> brew install maven # Mac
> apt-get install maven # Linux ubuntu
> # Windows: Use WSL to run Ubuntu and run entire setup there
```

### Docker setup

Docker desktop can be downloaded via the following link: [Docker Download](https://www.docker.com/products/docker-desktop)

Once docker is setup and installed you will need to issue the following docker command to get a docker network created:

```bash
> docker network create curation
549127add...
```

### Postgres

[Run Postgres Script](docker/run_postgres) which runs the following docker command:

```bash
docker run -d -p 5432:5432 --net curation --name postgres -e POSTGRES_HOST_AUTH_METHOD=trust postgres:13
```

If you are needing to connect to the production version of the database, use your personal postgres credentials that have been provided to you,
or contact a system admin to recover them when you lost them.

When you need admin access into the production database, the following command can be used to get the credentials
(but will only work once you have been setup with an AWS user and configured your local aws environment).

```bash
docker run --rm -it -v ~/.aws:/root/.aws amazon/aws-cli secretsmanager get-secret-value --secret-id curation-db-admin --region us-east-1 --query SecretString --output text
```
The output will look something like the following:

```bash
	{
		"username":"********",
		"password":"********",
		"host":"********",
		"engine":"postgres",
		"port":5432,
		"dbname":"curation",
		"dbInstanceIdentifier":"curation-db"
	}
```

### Elastic Search

[Run Elastic Search Script](docker/run_es) which runs the following docker command:

```bash
docker run -d --net curation -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" --name elasticsearch docker.elastic.co/elasticsearch/elasticsearch:7.9.0
```

If you point a browser over to `http://localhost:9200` you should see something like the following:

```bash
	{
		"name": "8e063394038e",
		"cluster_name": "docker-cluster",
		"cluster_uuid": "M50gdkzjRFeDbu0XBZq85g",
		"version": {
			"number": "7.9.0",
			"build_flavor": "default",
			"build_type": "docker",
			"build_hash": "a479a2a7fce0389512d6a9361301708b92dff667",
			"build_date": "2020-08-11T21:36:48.204330Z",
			"build_snapshot": false,
			"lucene_version": "8.6.0",
			"minimum_wire_compatibility_version": "6.8.0",
			"minimum_index_compatibility_version": "6.0.0-beta1"
		},
		"tagline": "You Know, for Search"
	}
	
```

### Cerebro

[Run Cerebro Script](docker/run_cerebro) which runs the following docker command:

```bash
docker run -d --net curation -p 9000:9000 --name agr.cerebro.server yannart/cerebro
```

Connect to this by browsing to `http://localhost:9000`, this is used to connect to the ES server, Which will be running at `http://elasticsearch:9200` inside docker or `http://localhost:9200` on the local machine.

### Active MQ (Message Queue)

[Run Active MQ Script](docker/run_activemq) which runs the following docker command:

```bash
docker run -d --net curation -p 5672:5672 -p 8161:8161 -p 61616:61616 -e ARTEMIS_USERNAME=quarkus -e ARTEMIS_PASSWORD=quarkus --name activemq vromero/activemq-artemis:2.9.0-alpine

```

The Active MQ is used to queue incoming update requests, the locally running interface can be found at: `http://localhost:8161/console`

### Up and Running Servers

After running all the previous commands issue the docker command: `docker ps -a`

This will show all the services that are up and running.

```
CONTAINER ID   IMAGE                                   COMMAND                  CREATED          STATUS          PORTS                                                                                                                                                                          NAMES
4ee5641a9481   yannart/cerebro                         "./bin/cerebro"          26 minutes ago   Up 26 minutes   0.0.0.0:9000->9000/tcp, :::9000->9000/tcp                                                                                                                                      cerebro
b6ed9006b28e   vromero/activemq-artemis:2.9.0-alpine   "/docker-entrypoint.…"   25 hours ago     Up 25 hours     1883/tcp, 0.0.0.0:5672->5672/tcp, :::5672->5672/tcp, 5445/tcp, 9404/tcp, 0.0.0.0:8161->8161/tcp, :::8161->8161/tcp, 61613/tcp, 0.0.0.0:61616->61616/tcp, :::61616->61616/tcp   activemq
8e063394038e   agrdocker/agr_elasticsearch_env         "/tini -- /usr/local…"   2 days ago       Up 23 hours     0.0.0.0:9200->9200/tcp, :::9200->9200/tcp, 0.0.0.0:9300->9300/tcp, :::9300->9300/tcp                                                                                           elasticsearch
7de3cf028e9c   postgres:13                             "docker-entrypoint.s…"   3 days ago       Up 3 days       0.0.0.0:5432->5432/tcp, :::5432->5432/tcp                                                                                                                                      postgres
```

## Building

These next steps will get either the UI or API or both up and running locally. Most of these commands are done via the Makefile in order to save typing.

### Building API

Once you have cloned the repo issue the following command in the root of the project:

```bash
> make api
mvn -T 8 clean package -Dnative -Dquarkus.native.container-build=true
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
make -B -C src/main/cliapp
npm install
...
```

This will start by building the UI and then will package the UI into the API and build the API, into a single linux binary file under the target directory. 

### Building for Docker Release

If needed, build for a release the following commands can be issued after a full build has been completed:

```bash
> make docker
> make docker-push
```

## Running

Depending on which part of the application you would like to run find the following commands:

### Running API

The API does not have to be build in order to run it in dev mode

```bash
> make apirun
mvn compile quarkus:dev
...
Listening for transport dt_socket at address: 5005
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
...
```

This will run just the API and you can find it running at: [http://localhost:8080](http://localhost:8080), however if the API was built without the UI there will be nothing to see at that url. There is a swagger interface running at: [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui).  
**Windows note**: If you're running the curation software through WSL (on Windows), Java might serve this interface through IPv6, in which case `localhost:8080` will be accessible within WSL, but not from your windows browser. Instead, you can then access the API and the swagger interface in your browser at it's IPv6 counterpart: [http://[::1]:8080](http://[::1]:8080).

Following the prompts at the bottom of the screen after the server is up and running. 

```bash
--
Tests paused
Press [r] to resume testing, [o] Toggle test output, [h] for more options>
```
Some notable commands are `s` for restarting the server to pick up java changes. `j` change the debugging level of the server, `q` or ctrl-c in order to stop the server.

### Running UI

After the UI has been build issue the following command:

```bash
> make uirun
make -B -C src/main/cliapp run
npm start
...
```

This will start the local UI and it will be running at: [http://localhost:3000](http://localhost:3000) this is in develop mode so any changes made to code will automatically restart the UI. The UI also proxies all `/api` requests over to the API. If need be the API_URL can be changed by setting it on the command line before running the UI.

### Running Both

Make sure to fully build the UI before running `make run` in order for the UI to be served via the API server. Otherwise going to `http://localhost:8080` will give a `Resource not found`

```bash
> make run
mvn compile quarkus:dev
[INFO] Scanning for projects...
...
```
If you have made the UI, the UI has been bundled into the API you can find the whole site running at the following: [http://localhost:8080](http://localhost:8080) this will include the UI and give links to Swagger and other resources that are being used.

## Loading Data

Assuming the API is running on localhost, check the Swagger interface for the correct endpoint to post data to. Here is an example of loading the Gene BGI file from the Alliance.

```bash
> curl -vX POST http://localhost:8080/api/gene/bulk/bgifile -d @1.0.1.4_BGI_ZFIN_4.json --header "Content-Type: application/json"
```

Here is an example of loading Do ontology:

```bash
> curl -vX POST http://localhost:8080/api/doterm/bulk/owl -d @doid.owl --header "Content-Type: application/xml"
```

Example of loading Allele's

```bash
> curl -vX POST http://localhost:8080/api/allele/bulk/allelefile -d @1.0.1.4_ALLELE_MGI_0.json --header "Content-Type: application/json"
```

## EB Deployment (beta)
This section is WIP.

CLI deployment to Elastic Beanstalk requires the EB CLI to be installed on your local machine.
Follow the [instructions provided by AWS](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/eb-cli3-install.html) to do so.
The EB CLI uses the same config files and environment variables as the standard AWS CLI to locate the appropriate authentication.

A EB cli configuration file is stored in the `.elasticbeanstalk` repo subdirectory. If you would encounter (local) problems running eb commands, remove this directory and reinitialise it by executing `make eb-init`.

All EB environment configurations are stored in the [`.ebextensions`](.ebextensions/) repo subdirectory.

Before attempting a deployment, define the `QUARKUS_DATASOURCE_USERNAME` and `QUARKUS_DATASOURCE_PASSWORD` appropriately in your environment to allow access to the RDS database.

Then you can run `make eb-create` to create a new environment and `make eb-terminate` to terminate it.

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
