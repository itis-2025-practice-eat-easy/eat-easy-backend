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

if [ -n "$ENABLE_DEBUG" ]; then
  echo "ℹ️  DEBUG-режим включен (ENABLE_DEBUG=$ENABLE_DEBUG)"
  echo "   • Будут активированы подробные логи"
else
  echo "ℹ️  DEBUG-режим отключен (переменная ENABLE_DEBUG не задана)"
fi

echo "Запуск сборки сервисов..."
./gradlew :service:gateway-service:bootJar --no-daemon
./gradlew :services:authentication-service:bootJar --no-daemon
./gradlew :services:user-service:user-impl:bootJar --no-daemon

echo "Запуск Docker-контейнеров..."
docker compose -f docker/docker-compose.server.yml -p eat-easy up -d
docker compose -f services/gateway-service/docker/docker-compose.server.yml -p eat-easy up -d --build
docker compose -f services/authentication-service/docker/docker-compose.server.yml -p eat-easy up -d --build
docker compose -f services/user-service/docker/docker-compose.server.yml -p eat-easy up -d --build

echo "Все сервисы успешно запущены!"
