docker compose -f docker/docker-compose.local.yml -p eat-easy up -d
docker compose -f services/gateway-service/docker/docker-compose.local.yml -p eat-easy up -d
docker compose -f services/authentication-service/docker/docker-compose.local.yml -p eat-easy up -d
docker compose -f services/user-service/docker/docker-compose.local.yml -p eat-easy up -d

