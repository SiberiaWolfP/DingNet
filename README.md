# DingNet: Group 5-2
The source code for the DingNet simulator.

Current up to date version: **1.2.1.**


## Building the simulator

### Local build
1. Install [Maven](https://maven.apache.org/install.html)
2. Clone the repository
3. Navigate to the root of the repository
4. Run `mvn compile` to build the simulator

### Package and Dockerized package
1. Install [Docker](https://docs.docker.com/get-docker/)
2. Clone the repository
3. Navigate to the root of the repository
4. Run `mvn package -DskipTests` to build the simulator, this will generate a jar file under the target directory: `DingNet-{version}-jar-with-dependencies.jar`
5. Run `docker build -t dingnet .` to build the docker image

## Start the simulator

### Arguments

List of arguments by order:
1. Configuration file name: the name of the environment configuration file to be used, valid names are one of `basic_graph.xml`, `basic_routing.xml`, `demo.xml`, `empty.xml`, `goal-change-new.xml`.
2. Adaptation algorithm: the name of the adaptation algorithm to be used, valid names are one of `No Adaptation`, `Signal-based`, `Distance-based`.
3. Adaptation goal: the name of the adaptation goal to be used, currently only `ReliableEfficient` is supported.
4. Simulation speed: a number between 1 and 10, where 1 is the slowest and 10 is the fastest.

A valid argument list would be `basic_graph.xml Signal-based ReliableEfficient 5`.

### Local Start
1. Navigate to the root of the repository
2. Run `mvn exec:java -Dexec.args="args"` to run the simulator, where `args` is a list of arguments separated by spaces.
3. Or run `java -jar target/DingNet-{version}-jar-with-dependencies.jar args` to run the simulator, where `args` is a list of arguments separated by spaces.

### Docker Start
1. Run `docker run -p 8080:8080 -it dingnet` to run the simulator in front-end mode, or `docker run -d -p 8080:8080 -it dingnet` to run the simulator in headless mode
2. To stop the simulator, run `docker stop $(docker ps -a -q --filter ancestor=dingnet --format="{{.ID}}")`

### UPISAS Start

# Rest API

After starting the simulator, the rest API can be accessed at `http://localhost:8080/`.

