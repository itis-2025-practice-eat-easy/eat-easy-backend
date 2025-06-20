FROM eclipse-temurin:17-jdk
WORKDIR /eat-easy-backend

COPY ./services/cart-service/cart-impl/build/libs/cart-impl.jar ./cart-impl.jar

ENTRYPOINT ["java", "-jar", "cart-impl.jar"]
