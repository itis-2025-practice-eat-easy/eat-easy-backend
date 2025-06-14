FROM eclipse-temurin:17-jdk AS builder
WORKDIR /eat-easy-backend

COPY ../../../. .

RUN ./gradlew :services:authentication-service:bootJar --no-daemon --no-watch-fs

FROM eclipse-temurin:17-jdk

WORKDIR /eat-easy-backend

COPY --from=builder /eat-easy-backend/services/authentication-service/build/libs/authentication-service.jar ./authentication-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "authentication-service.jar"]
