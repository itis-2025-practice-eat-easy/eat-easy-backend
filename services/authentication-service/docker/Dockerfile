FROM eclipse-temurin:17-jdk
WORKDIR /eat-easy-backend

COPY ./services/authentication-service/build/libs/authentication-service.jar ./authentication-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "authentication-service.jar"]
