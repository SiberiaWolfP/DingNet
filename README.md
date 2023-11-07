# DingNet: Group 5-2
The source code for the DingNet simulator.

Current up to date version: **1.2.1.**


## Building the simulator

### Local build
1. Install [Maven](https://maven.apache.org/install.html)
2. Clone the repository
3. Navigate to the root of the repository
4. Run `mvn compile` to build the simulator
5. Run `mvn exec:java` to run the simulator

### Package build and Docker
1. Install [Docker](https://docs.docker.com/get-docker/)
2. Clone the repository
3. Navigate to the root of the repository
4. Run `mvn package -DskipTests` to build the simulator, this will generate a jar file under the target directory: `DingNet-{version}-jar-with-dependencies.jar`
5. Run `docker build -t dingnet .` to build the docker image
6. Run `docker run -p 8080:8080 -it dingnet` to run the simulator in front-end mode, or `docker run -d -p 8080:8080 -it dingnet` to run the simulator in headless mode
7. Navigate to `localhost:8080` in your browser to access the simulator
8. To stop the simulator, run `docker stop $(docker ps -a -q --filter ancestor=dingnet --format="{{.ID}}")`

## Running the simulator

Either run the jar file generated from the previous step, or use the maven exec plugin.
