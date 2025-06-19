FROM eclipse-temurin:17-jdk
WORKDIR /eat-easy-backend

COPY ./services/order-service/order-impl/build/libs/order-impl.jar ./order-impl.jar

ENTRYPOINT ["java", "-jar", "order-impl.jar"]
