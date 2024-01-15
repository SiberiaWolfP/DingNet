# DingNet: Group 5-2
The source code for the DingNet simulator.

**ATTENTION**: Due to time constraints, the implementation of this version is not guaranteed to be bug-free and is not fully tested. Please try to follow the request example under the `./http/` for your experience.


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
4. Run `mvn package -DskipTests` to build the simulator, this will generate a jar file under the target directory: `DingNet-{version}.jar`
5. Run `docker build -t dingnet .` to build the docker image

## Start the simulator
### Local Start
1. Navigate to the root of the repository
2. Run `mvn exec:java -Dexec` to run the simulator
3. Or run `java -jar target/DingNet-{version}.jar` to run the simulator

### Docker Start
1. Run `docker run -p 8080:8080 -it dingnet` to run the simulator in front-end mode, or `docker run -d -p 8080:8080 -it dingnet` to run the simulator in headless mode
2. At this point, the simulator is not running yet, to start the simulator, run `docker exec -it $(docker ps -a -q --filter ancestor=dingnet --format="{{.ID}}") java -jar app.jar`
3. To stop the simulator, run `docker stop $(docker ps -a -q --filter ancestor=dingnet --format="{{.ID}}")`
4. To remove the simulator, run `docker rmi dingnet`

### UPISAS Start
1. Before start by UPISAS, make sure the docker image is built
2. Navigate to the root of the UPISAS repository
3. Install UPISAS dependencies as README.md in UPISAS repository
4. Run `python run.py` to run the simulator from UPISAS

# Rest API

After starting the simulator, the rest API can be accessed at `http://localhost:8080/`.

Swagger documentation can be found at `http://localhost:8080/swagger-ui.html`.

Json API documentation can be found at `http://localhost:8080/v3/api-docs`.

Http requests examples can be found at `./http/`

