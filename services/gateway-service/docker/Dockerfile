FROM eclipse-temurin:17-jdk
WORKDIR /eat-easy-backend

COPY ./services/gateway-service/build/libs/gateway-service.jar ./gateway-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "gateway-service.jar"]
