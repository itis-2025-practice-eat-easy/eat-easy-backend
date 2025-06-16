if [ -z "$SERVER_ADDRESS" ]; then
  echo "❌ Переменная среды SERVER_ADDRESS не задана. Пожалуйста, задайте её командой:"
  echo "export SERVER_ADDRESS=your-server-address"
  exit 1
fi

docker compose -f docker/docker-compose.server.yml -p eat-easy up -d
docker compose -f services/gateway-service/docker/docker-compose.server.yml -p eat-easy up -d
docker compose -f services/authentication-service/docker/docker-compose.server.yml -p eat-easy up -d
docker compose -f services/user-service/docker/docker-compose.server.yml -p eat-easy up -d
