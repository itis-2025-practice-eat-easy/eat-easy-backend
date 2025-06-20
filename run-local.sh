./gradlew :service:gateway-service:bootJar
./gradlew :services:authentication-service:bootJar
./gradlew :services:user-service:user-impl:bootJar
./gradlew :services:product-service:product-impl:bootJar
./gradlew :services:order-service:order-impl:bootJar
./gradlew :services:cart-service:cart-impl:bootJar

docker compose -f docker/docker-compose.local.yml -p eat-easy up -d
docker compose -f services/gateway-service/docker/docker-compose.local.yml -p eat-easy up -d --build
docker compose -f services/authentication-service/docker/docker-compose.local.yml -p eat-easy up -d --build
docker compose -f services/user-service/docker/docker-compose.local.yml -p eat-easy up -d --build
docker compose -f services/product-service/docker/docker-compose.local.yml -p eat-easy up -d --build
docker compose -f services/order-service/docker/docker-compose.local.yml -p eat-easy up -d --build
docker compose -f services/cart-service/docker/docker-compose.local.yml -p eat-easy up -d --build


