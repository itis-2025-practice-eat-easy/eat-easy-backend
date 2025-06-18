if [ -z "$BACKEND_ADDRESS" ]; then
  echo "❌ Переменная среды BACKEND_ADDRESS не задана. Пожалуйста, задайте её командой:"
  echo "export BACKEND_ADDRESS=your-backend-address"
  exit 1
fi

if [ -z "$FRONTEND_ADDRESS" ]; then
  echo "❌ Переменная среды FRONTEND_ADDRESS не задана. Пожалуйста, задайте её командой:"
  echo "export FRONTEND_ADDRESS=your-backend-address"
  exit 1
fi

./gradlew :service:gateway-service:bootJar
./gradlew :services:authentication-service:bootJar
./gradlew :services:user-service:user-impl:bootJar

docker compose -f docker/docker-compose.server.yml -p eat-easy up -d
docker compose -f services/gateway-service/docker/docker-compose.server.yml -p eat-easy up -d --build
docker compose -f services/authentication-service/docker/docker-compose.server.yml -p eat-easy up -d --build
docker compose -f services/user-service/docker/docker-compose.server.yml -p eat-easy up -d --build
