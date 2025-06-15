FROM eclipse-temurin:17-jdk AS builder
WORKDIR /eat-easy-backend

COPY ../../../. .

RUN ./gradlew :services:gateway-service:bootJar --no-daemon --no-watch-fs

FROM eclipse-temurin:17-jdk

WORKDIR /eat-easy-backend

COPY --from=builder /eat-easy-backend/services/gateway-service/build/libs/gateway-service.jar ./gateway-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "gateway-service.jar"]
