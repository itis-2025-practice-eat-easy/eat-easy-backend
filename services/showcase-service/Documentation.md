# Документация к Api Gateway и Discovery - consul

## Шаги для выполнения, чтобы все работало корректно:
## 1) Запустить `Consul`  
```bash
docker run -d --name=consul -p 8500:8500 hashicorp/consul agent -dev -client=0.0.0.0
```
Образ будет автоматически скачан и контейнер запущен.

Consul будет доступен на порту 8500 (порт по умолчанию).

Флаг -dev запускает Consul в режиме разработки (одиночный сервер, с авто-отключением по умолчанию — для локальной разработки).

## 2) Регистрация сервиса в `Consul`

### 2.1) Добавить зависимости
```groovy
// Переменная для указания версии BOM (Bill of Materials) Spring Cloud
ext {
    springCloudVersion = "2024.0.1"
}

dependencies {
    // Базовый Spring Boot Web стартер
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // Зависимость для интеграции с Consul (регистрация и обнаружение сервисов)
    implementation 'org.springframework.cloud:spring-cloud-starter-consul-discovery'

    // Actuator для мониторинга состояния сервиса (/actuator/health)
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}

// Управление версиями Spring Cloud для предотвращения конфликтов зависимостей
dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion"
    }
}

// Принудительная установка версии Apache HttpClient (временно для стабильности)
configurations.all {
    resolutionStrategy {
        force 'org.apache.httpcomponents:httpclient:4.5.14'
    }
}

```
### 2.2) Регистрируемся в `Consul`
```yaml
spring:
  application:
    name: showcase-service # Имя сервиса, под которым он будет зарегистрирован в Consul.

  # регистрируем сервис в консуле
  cloud:
    consul:
      host: localhost
      port: 8500 # Порт, на котором доступен Consul.
      discovery:
        register: true 
        enabled: true
        service-name: ${spring.application.name}
        prefer-ip-address: true
        health-check-path: /actuator/health # Вот тут используется актуатор
        health-check-interval: 10s
        scheme: http
        deregister: true # Если сервис упадет, он автоматически удалится из пространства консула
server:
  port: 8081 # На каком порте будет развернут наш сервис 
  # 8080 резервируем под API Gateway.

```


После настройки сервис регистрируется в Consul и становится доступным для вызовов из других микросервисов не напрямую по URL, а по имени сервиса:

```curl
# Прямой вызов (нежелателен, обход Consul и Gateway)
http://localhost:8081/api/v1/users
```
```curl
# Через API Gateway с использованием имени сервиса
http://localhost:8080/user-service/api/v1/users
```
Так, с помощью `load balancer`, gateway сам отдаст наименее нагруженный `instance` сервиса (если их несколько)

(сейчас gateway не пропускает подобные попытки запросов, поскольку общение между сервисами происходит в обход gateway)

## 3) Отправка запросов к другому сервису (если необходимо)

Отправка запросов производится через OpenFeign

### 3.1) Добавить зависимости

```groovy
// Зависимость для использования OpenFeign — декларативного HTTP-клиента для межсервисных запросов.
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
```

### 3.2) @EnableFeignClients
```java
@SpringBootApplication
@EnableFeignClients //буквально: "этот сервис будет слать запросы другим"
public class ShowcaseApp {
    public static void main(String[] args) {
        SpringApplication.run(ShowcaseApp.class, args);
    }
}
```
### 3.3) Создание FeignClient
```java
@FeignClient(name = "user-service", //имя сервиса в Consul
    path = "/api/v1/users") //базовый путь как в API
public interface UserClient {
    @PostMapping
    UserDto createUser(@RequestBody UserDto userDto);
    //получаем данные через свой dto

    @GetMapping
    List<UserDto> getAllUsers();

    // для примера сохраним пользователя и получим список всех пользователей
}
```

По большому счету основная часть работы проделана. Теперь методы интерфейса можно вызывать например из контроллера или сервиса.

Пример вызова:

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/showcase")
public class Controller {

    private final Service service;

    @PostMapping
    public UserDto showcase() {
        return service.createUser(UserDto.builder()
                .username("superUser123")
                .email("superuser@example.com")
                .password("StrongP@ssw0rd!")
                .firstName("Super")
                .lastName("User")
                .role("ADMIN")
                .build());
    }

    @GetMapping
    public List<UserDto> showcase2() {
        return service.getAllUsers();
    }
}
```

в сервисе просто дергаются методы интерфейса:
```java
@RequiredArgsConstructor
@Service
public class Service {
    private final UserClient userClient;


    public UserDto createUser(UserDto user){
       return userClient.createUser(user);
    }

    public List<UserDto> getAllUsers(){
        return userClient.getAllUsers();
    }
}
```

Пример вывода результата в консоль:

```curl
curl -X POST http://localhost:8080/showcase-service/showcase
```

```json
{"username":"superUser123","email":"superuser@example.com","password":null,"firstName":null,"lastName":null,"role":"ADMIN"}
```
Создался новый пользователь, проверим его наличие в общем списке:

```curl
curl -X GET http://localhost:8080/showcase-service/showcase
```

```json
[{"username":"superUser123","email":"superuser@example.com","password":null,"firstName":null,"lastName":null,"role":"ADMIN"}]
```
Действительно, пользователь появился в общем списке