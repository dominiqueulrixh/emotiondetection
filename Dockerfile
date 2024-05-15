#docker buildx build --platform linux/amd64 -t dominiqueulrixh/djl-serving-emotiondetection .
#docker buildx build --platform linux/amd64 -t dominiqueulrixh/djl-serving-emotiondetection -f '/Users/dominiqueulrich/Studium/Semester 6/Model Deployment & Maintenance/MDM_Projekt_2_Java/playground/Dockerfile' '/Users/dominiqueulrich/Studium/Semester 6/Model Deployment & Maintenance/MDM_Projekt_2_Java/playground'
#docker run -p 9000:8082 -d dominiqueulrixh/djl-serving-emotiondetection
#docker push dominiqueulrixh/djl-serving-emotiondetection:latest

FROM openjdk:21-jdk-slim

#Copy Files
WORKDIR /usr/src/app
COPY --from=build /usr/src/app/target/emotiondetection-0.0.1-SNAPSHOT.jar .


# Install
RUN mvn -Dmaven.test.skip=true package

# Docker Run Command
EXPOSE 8080
CMD ["java","-jar","emotiondetection-0.0.1-SNAPSHOT.jar"]