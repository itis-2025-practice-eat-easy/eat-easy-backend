FROM eclipse-temurin:17-jdk
WORKDIR /eat-easy-backend

COPY ./services/user-service/user-impl/build/libs/user-impl.jar ./user-impl.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "user-impl.jar"]
