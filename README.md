# AGR Curation API

This repo holds the code for the API and the UI of the AGR curation system.

## Getting Started

These instructions will get you a copy of the project and the API up and running locally. This includes Postgres, ElasticSearch and Cerebro.

## Contents

- [Developing](#developing)
	* [Branching](#branching)
- [Installing](#installing)
	* [Docker Setup](#docker-setup)
	* [Postgres](#postgres)
	* [ElasticSearch](#elastic-search)
	* [Cerebro](#cerebro)
- [Building](#building)
	* [Building Docker Image](#building-docker-image)
- [Running](#running)
	* [Running API](#running-api)
	* [Running UI](#running-ui)
- [Releasing and Deploying](#releasing-and-deploying)
	* [Deployment environments](#deployment-environments)
   * [Promoting code versions](#promoting-code-versions)
   * [Release versioning](#release-versioning)
	* [Release Creation](#release-creation)
- [Loading Data](#loading-data)
- [Submitting Data](#submitting-data)

## Developing
Before starting coding for a new feature or making a bugfix,
it is important to know the intended goal of your code:
 * Developing fixes, new features and major changes to be released in some future release (standard)
   => Must be developed and tested on alpha
 * Smaller (bug)fixes to a version currently being previewed on beta, in preparation of a release to production
   => Must be developed and tested on beta
 * Hotfixes, urgent bugfixes to the current production version which cannot wait on the next full release (bundled with other updates currently under development) to be deployed
   => Must be developed off the latest production release.

For more details about the intended use of and deployment to each of the environments, see [deployment environments](#deployment-environments).

### Branching
The three permanent branches in this repository represent the code to be deployed to their respective environments: `alpha`, `beta` and `production`.

 * To create code that must solely go on the alpha environment (most day-to-day development):

   - Create a feature branch to work on that starts off alpha and have the name contain the ticket number and a short description of it.
	 ```bash
	 > git checkout alpha
	 > git pull
	 > git checkout -b feature/${JIRA-TICKET-NR}_short-description
	 ```

   - Do your coding and testing, and push the branch to github
	 ```bash
	 #Coding and testing here
	 > git push origin feature/${JIRA-TICKET-NR}_short-description
	 ```

   - Once coding and testing completed, submit a pull request in github to merge back to alpha.
	 Deployment to alpha will automatically trigger once the PR is approved and merged.

 * To make fixes to the version currently deployed on the beta environment:

   - Create a betafix branch to work on that starts off beta and have the name contain the ticket number (if applicable) and a short description.
	 ```bash
	 > git checkout beta
	 > git pull
	 > git checkout -b betafix/${JIRA-TICKET-NR}_short-description
	 ```

   - Do your coding and testing, and push the branch to github
	 ```bash
	 #Coding and testing here
	 > git push origin betafix/${JIRA-TICKET-NR}_short-description
	 ```

   - Once coding and testing completed, submit a pull request in github to merge back to beta.
	 For deployment to beta, extra steps need to be taken after PR approval and merge, which are described [here](#release-creation).

 * To make fixes to the version currently deployed on the production environment:

   - Create a prodfix branch to work on that starts off production and have the name contain the ticket number and a short description.
	 ```bash
	 > git checkout production
	 > git pull
	 > git checkout -b prodfix/${JIRA-TICKET-NR}_short-description
	 ```

   - Do your coding and testing, and push the branch to github
	 ```bash
	 #Coding and testing here
	 > git push origin prodfix/${JIRA-TICKET-NR}_short-description
	 ```

   - Once coding and testing completed, submit a pull request in github to merge back to production.
	 For deployment to production, extra steps need to be taken after PR approval and merge, which are described [here](#release-creation).


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

[Run Postgres Script](docker/run_postgres) which will launch an empty postgres instance locally from the official postgres docker image.

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

[Run Elastic Search Script](docker/run_es) which will launch elasticsearch from the appropriate elastic.co docker image.

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

[Run Cerebro Script](docker/run_cerebro) which will launch cerebro, an ES control plane, from the appropriate docker image.

Connect to this by browsing to `http://localhost:9000`, this is used to connect to the ES server, which can be accessed by
entering `http://elasticsearch:9200` (the internal docker address) in the Node address field, while elasticsearch is directly
accessible at `http://localhost:9200` on the local machine.

### Up and Running Servers

After running all the previous commands issue the docker command: `docker ps -a`

This will show all the services that are up and running.

```
CONTAINER ID   IMAGE                                   COMMAND                  CREATED          STATUS          PORTS                                                                                                                                                                          NAMES
4ee5641a9481   yannart/cerebro                         "./bin/cerebro"          26 minutes ago   Up 26 minutes   0.0.0.0:9000->9000/tcp, :::9000->9000/tcp                                                                                                                                      cerebro
8e063394038e   agrdocker/agr_elasticsearch_env         "/tini -- /usr/local…"   2 days ago       Up 23 hours     0.0.0.0:9200->9200/tcp, :::9200->9200/tcp, 0.0.0.0:9300->9300/tcp, :::9300->9300/tcp                                                                                           elasticsearch
7de3cf028e9c   postgres:13                             "docker-entrypoint.s…"   3 days ago       Up 3 days       0.0.0.0:5432->5432/tcp, :::5432->5432/tcp                                                                                                                                      postgres
```

## Building

Both the UI or API can be run locally without needing a separate build step.
For instructions on how to do so, see [Running](#running-api).

Building the application uberjar is done as part of the Docker image creation,
instructions on how to use this process for local image building can be found [below](#building-docker-image).

### Building Docker Image

When needed, the following command can be issued to build a complete runnable application as a docker image:

```bash
> make docker
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

To build and run the UI, issue the following command:

```bash
> make uirun
...
```

This will start the local UI and it will be running at: [http://localhost:3000](http://localhost:3000) this is in develop mode so any changes made to code will automatically restart the UI. The UI also proxies all `/api` requests over to the API. 

If need be the API_URL can be changed by setting it on the command line before running the UI:

```bash
> export API_URL=https://alpha-curation.alliancegenome.org  # to send proxied requests to the alpha server.
```

Additionally, there are two convenience commands that will proxy `/api` requests to either the alpha or beta environment

```bash
> make uirunalpha
```

```bash
> make uirunbeta
```

### Running the docker image

To run the complete application as the [locally built docker image](#building-docker-image), execute the following command:
```bash
> make docker-run
```

## Releasing and deploying
### Deployment environments
There are three environments to which code automatically gets deployed at different stages during development:
 * The alpha environment should be regarded as the developers environment, where active code development can be happening
   at any moment, and things are expected to break every now and then.
   This environment receives new deployments on every push made to the alpha branch.
 * The beta environment should be regarded as the testers environment, where (external) testers can have a first look at
   newly developed functionality before it is ready to be pushed to production. It is more stable than alpha,
   but is still subject to regular change as feedback is collected and the final kinks are being ironed out.
   This environment receives new deployments for every pre-release created on Github.
 * The production environment is the final stage of deployment, where users can rely on full-tested and ready-to-use
   features and functionality at any moment. This is the most stable and reliable environment.
   This environment receives new deployments for every full (stable) release created on Github.

All deployments are fully automated through Github actions, for which the configuration files can be found in the [`.github/workflows/` directory](.github/workflows/).
Deployments to the alpha environment happen automatically as code gets pushed to the alpha branch (after merging a PR), but for
deployments to beta and production, a small number of steps needs to be taken in order to create a release and trigger deployment,
which are described below.

### Promoting code versions
The general flow from coding to a production release goes something like this:
1. Coding and testing for most tickets happens on alpha (throughout the sprint)
2. When a stable state of alpha gets reached (usually at sprint review),
	this stable state gets promoted to beta as a release candidate, available
	for external user testing.
3. On approval from the external users, and in agreement with the product manager,
	this release candidate gets promoted to a new stable release on production.

As the code goes through the different stages, it becomes more and more stable as it gets closer to production.

In order to promote changes from alpha to beta:

1. Decide on a proper release version number to be used for the new prerelease
	that will be created as a result of this promotion (see [Release versioning](#release-versioning)).  
	Generally speaking, for beta (pre)releases that means either
    * Incrementing the release candidate version on the latest release candidate
      when it was decided the previous release candidate will not be promoted to a full release
      and the latest changes need to be added in order to be able to create a full release.
    or  
    * Incrementing the `MAJOR` or `MINOR` release numbers as appropriate when
      the previous release candidate was (or will be) promoted to a full release
      and reset to rc1 for the next release.
2. Create a release/v`x`.`y`.`z`-rc`a` branch from alpha or use a specific commit
   from history to promote if alpha moved on since that stable state to be promoted.
   ```bash
   git pull
   git checkout -b release/vx.y.z-rca alpha
   git push origin release/vx.y.z-rca
   ```
3. Create a pull request to merge this release branch in beta
4. After PR approval and merge, continue with [release creation](#release-creation) to trigger deloyment to the beta environment.
5. After prerelease creation and deployment, merge the beta branch back to alpha and push to github
   ```bash
   git pull
   git checkout alpha
   git merge beta
   git push
   ```

In order to promote changes from beta to production:

1. Decide on a proper release version number to be used for the new release
	that will be created as a result of this promotion (see [Release versioning](#release-versioning)).  
	Generally speaking, for production (full) releases that means removing the release-candidate extension
   from the release number used by the latest release candidate, which will be promoted to a full release here.
2. Create a release/v`x`.`y`.`z` branch from beta or use a specific commit
   from history to promote if a new release canidate for the next release was already added to beta.
   ```bash
   git pull
   git checkout -b release/vx.y.z beta
   ```
3. Update the [RELEASE-NOTES.md](RELEASE-NOTES.md) file to contain
   a section describing all (noteworthy) changes made since the last full release,
   and include JIRA ticket and/or PR references where possible. Commit after update.
4. Push the release branch to github
   ```bash
   git push origin release/vx.y.z
   ```
5. Create a pull request to merge this release branch in production
6. After PR approval and merge, continue with [release creation](#release-creation) to trigger deloyment to the production environment.
7. After release creation and deployment, merge the production branch back to beta and push to github
   ```bash
   git pull
   git checkout beta
   git merge production
   git push
   ```

### Release versioning
For our release versioning, we apply [Semantic Versioning](https://semver.org/) whereby

 * `MAJOR`.`MINOR`.`PATCH` is used for full releases
 * Release-candidate extensions are used for prereleases in the format `MAJOR`.`MINOR`.`PATCH`-rc`x`
   where x is an increment starting at one.
 * `PATCH` version upgrades should only be used for hotfixing bugs on the production environment
   (e.g. a bugfix applied to v0.1.0 becomes v0.1.1).

__Note:__ For the time being, the `MAJOR` release number is kept at 0 to indicate the early development phase,
until the product is ready for active usage by users external to the development team, in a stable
production environment.


### Release Creation
To create a new (pre-)release and deploy to beta or production, do the following steps:

1. Ensure you're on the branch you intend to create a new release for and pull the latest code.
	```bash
	git checkout beta
	git pull
	```

2. Confirm the release number proposed earlier through release candidates or release/* branches
   is appropriate (see [Release versioning](#release-versioning)).

3. Ensure you have a clean working directory before continuing, you can save any local changes for later using `git stash` if needed.

4. Tag the release and push the tag to github. Prefix the release nr with `v` as tagname,
   and provide a short description for the release. Beta releases should contain a reference to the
   date they were demoed at sprint review when applicable.
   ```bash
   # Tag name should be the release nr to be release, prefixed with v (eg. v0.2.0-rc1)
   # Tag message for beta releases can be something like 'As sprint review Jan 19th 2022'
   git tag -a vx.y.z-rca -m 'As sprint review MMM DDth YYYY'
   git push origin vx.y.z-rca
   ```

5. Go to the [AGR curation release page](https://github.com/alliance-genome/agr_curation/releases) on github, create a new release by clicking the "Draft a new release" button at the top.
	* In the "Choose a tag" selection box, select the git tag you created in the previous step
	* Give the release a proper title ("AGR Curation `release version` Release" for full releases, "Prerelease `release version`" for prereleases)
	* **Ensure** the "This is a pre-release" checkbox at the bottom is checked appropriately.
		* **Checking** this box creates a prerelease, which only get deployed to the **beta** environment.
		* Leaving the box **unchecked** (the default) creates a full release which gets deployed to the **production** environment.

6. Confirm all entered details are correct and publish the release.

Once published, github actions kicks in and the release will get deployed to the appropriate environments.
Completion of these deployments is reported in the #a-team-code slack channel.

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

## Submitting Data

Here is an example using curl:

```bash
curl \
	-H "Authorization: Bearer 2C07D715..." \
	-X POST "https://${Curation_System}.alliancegenome.org/api/data/submit" \
	-F "LoadType_SubType=@/full/path/to/file1.json" \
	-F "LoadType_SubType=@/full/path/to/file2.json"
```

Valid values for LoadType, and SubType can be found in the examples below.

### Curation System

This value can be one of the following list.

| Name | Description |
| --- | --- |
| alpha-curation | Alpha Curation Site |
| beta-curation | Beta Curation Site |
| curation | Production Curation Site |

DQMs can find information regarding the LinkML version and data classes currently supported by the curation at the following link:
https://${Curation_System}.alliancegenome.org/api/version

### API Access Token

This will be a key that is generated via logging into the curation website for the DQM's to use for uploading files.

### Load Type

Load type corisponds with the type of file that load needs.

| Name | Description |
| --- | --- |
| GENE | LinkML Gene |
| ALLELE | LinkML Allele |
| AGM | LinkML AGM |
| DISEASE_ANNOTATION | LinkML Disease Annotations |

### Sub Type

This is a grouping mechanism to group files together

| Name | Description |
| --- | --- |
| FB    | Fly Base |
| HUMAN | Human Supplied by RGD |
| MGI   | Mouse Genome Database |
| RGD   | Rat Genome Database |
| SGD   | Saccharomyces Genome Database |
| WB    | Worm Base |
| ZFIN  | Zebrafish Information Network |

## EB Deployment (beta)
This section is WIP.

CLI deployment to Elastic Beanstalk requires the EB CLI to be installed on your local machine.
Follow the [instructions provided by AWS](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/eb-cli3-install.html) to do so.
The EB CLI uses the same config files and environment variables as the standard AWS CLI to locate the appropriate authentication.

A EB cli configuration file is stored in the `.elasticbeanstalk` repo subdirectory. If you would encounter (local) problems running eb commands, remove this directory and reinitialise it by executing `make eb-init`.

All EB environment configurations are stored in the [`.ebextensions`](.ebextensions/) repo subdirectory.

Before attempting a sandbox environment creation, inspect the `eb-create` Makefile target
and export all variables defined in it with the appropriate values in your local shell environment
to allow storage of all required configurations in the EB environment configurations.

Then you can run `make eb-create` to create a new environment and `make eb-terminate` to terminate it.

## Maintainers

Current maintainers:

 * Adam Gibson - [https://github.com/adamgibs](https://github.com/adamgibs)
 * Andrés Becerra Sandoval - [https://github.com/abecerra](https://github.com/abecerra)
 * Christian Pich - [https://github.com/cmpich](https://github.com/cmpich)
 * Jyothi Thota - [https://github.com/jt15](https://github.com/jt15)
 * Ketaki Thorat - [https://github.com/kthorat-prog](https://github.com/kthorat-prog)
 * Mark Quinton-Tulloch - [https://github.com/markquintontulloch](https://github.com/markquintontulloch)
 * Manuel Luypaert - [https://github.com/mluypaert](https://github.com/mluypaert)
 * Olin Blodgett - [https://github.com/oblodgett](https://github.com/oblodgett)
