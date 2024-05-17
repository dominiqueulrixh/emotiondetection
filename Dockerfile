#docker buildx build --platform linux/amd64 -t dominiqueulrixh/djl-serving-emotiondetection .
#docker buildx build --platform linux/amd64 -t dominiqueulrixh/djl-serving-emotiondetection -f '/Users/dominiqueulrich/Studium/Semester 6/Model Deployment & Maintenance/MDM_Projekt_2_Java/playground/Dockerfile' '/Users/dominiqueulrich/Studium/Semester 6/Model Deployment & Maintenance/MDM_Projekt_2_Java/playground'
#docker run -p 9000:8082 -d dominiqueulrixh/djl-serving-emotiondetection
#docker push dominiqueulrixh/djl-serving-emotiondetection:latest

# Erste Stage: Build des Projekts
FROM openjdk:21-jdk-slim AS build

# Setzen Sie das Arbeitsverzeichnis
WORKDIR /usr/src/app

# Installieren Sie Maven
RUN apt-get update && apt-get install -y maven

# Kopieren Sie alle Projektdateien ins Arbeitsverzeichnis
COPY . .

# Bauen Sie das Projekt, überspringen Sie dabei die Tests
RUN mvn -Dmaven.test.skip=true package

# Zweite Stage: Laufen der Anwendung
FROM openjdk:21-jdk-slim

# Setzen Sie das Arbeitsverzeichnis
WORKDIR /usr/src/app

# Kopieren Sie das JAR aus der ersten Stage
COPY --from=build /usr/src/app/target/emotiondetection-0.0.1-SNAPSHOT.jar /usr/src/app/emotiondetection-0.0.1-SNAPSHOT.jar

# Kopieren Sie das Modellverzeichnis in das Arbeitsverzeichnis
COPY azuremodel /usr/src/app/azuremodel

# Stellen Sie den Service-Port bereit
EXPOSE 8082

# Definieren Sie den Startbefehl
CMD ["java", "-jar", "emotiondetection-0.0.1-SNAPSHOT.jar"]