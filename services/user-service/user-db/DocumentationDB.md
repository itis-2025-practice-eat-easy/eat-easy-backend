# Для запуска необходимо в корне модуля /user-service/user-db создать файл "gradle.properties" с пропертями

# Пример файла gradle.properties:
```properties
db.url=jdbc:postgresql://localhost:5432/user_service_dev
db.username=postgres
db.password=12345
db.driver=org.postgresql.Driver
db.changelogFile=db.changelog-master.yaml
```
