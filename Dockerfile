FROM openjdk:17

COPY ./target/DingNet-1.2.1-jar-with-dependencies.jar /app.jar

#CMD ["--server.port=8080"]

EXPOSE 8080

#ENTRYPOINT ["java","-jar","/app.jar", "--server.port=8080"]
CMD [ "sleep", "infinity" ]
