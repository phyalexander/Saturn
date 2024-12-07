FROM openjdk:17
COPY jars /
COPY language/std /root/.Saturn/std
WORKDIR /
ENTRYPOINT ["java", "-jar", "Saturn.jar"]
