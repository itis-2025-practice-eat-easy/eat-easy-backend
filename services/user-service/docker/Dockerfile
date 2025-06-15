FROM eclipse-temurin:17-jdk AS builder
WORKDIR /eat-easy-backend

COPY ../../../. .

RUN ./gradlew :services:user-service:user-impl:bootJar --no-daemon --no-watch-fs

FROM eclipse-temurin:17-jdk

WORKDIR /eat-easy-backend

COPY --from=builder /eat-easy-backend/services/user-service/user-impl/build/libs/user-impl.jar ./user-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "user-service.jar"]
