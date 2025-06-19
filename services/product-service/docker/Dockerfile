FROM eclipse-temurin:17-jdk
WORKDIR /eat-easy-backend

COPY ./services/product-service/product-impl/build/libs/product-impl.jar ./product-impl.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "product-impl.jar"]
