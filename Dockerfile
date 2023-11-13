FROM consol/debian-xfce-vnc:latest
LABEL Description="Docker image for DingNet with GUI"

# install java
USER root
RUN apt-get update
RUN apt-get install -y openjdk-17-jdk

# install maven
RUN apt-get install -y maven

# Build DingNet
WORKDIR /app
COPY . /app

RUN mvn clean install -DskipTests

# move jar to root
RUN mv /app/target/DingNet-1.2.1.jar /app.jar

# move settings file to root
RUN mv /app/settings /settings

#CMD ["--server.port=8080"]
WORKDIR /
EXPOSE 8080

#ENTRYPOINT ["java","-jar","/app.jar", "--server.port=8080"]
#CMD [ "sleep", "infinity" ]
