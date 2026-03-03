# 📚 Book Microservice - Arquitetura de Microsserviços com Spring Cloud

Repositório de estudo abordando boas práticas, padrões e ferramentas modernas para construir uma arquitetura robusta de microsserviços. Este projeto implementa conceitos avançados de engenharia de software com foco em escalabilidade, resiliência e observabilidade.

## 🎯 Índice

1. [Visão Geral da Arquitetura](#visão-geral-da-arquitetura)
2. [Ferramentas e Tecnologias](#ferramentas-e-tecnologias)
3. [Spring Cloud Configuration](#spring-cloud-configuration)
4. [Spring Boot Actuator](#spring-boot-actuator)
5. [Feign Client](#feign-client)
6. [Eureka - Service Discovery](#eureka---service-discovery)
7. [Spring Cloud LoadBalancer](#spring-cloud-loadbalancer)
8. [Spring Cloud Gateway](#spring-cloud-gateway)
9. [Resilience4J - Circuit Breaker](#resilience4j---circuit-breaker)
10. [Swagger OpenAPI](#swagger-openapi)
11. [Zipkin - Distributed Tracing](#zipkin---distributed-tracing)
12. [GitHub Actions](#github-actions)
13. [Kubernetes](#kubernetes)
14. [Como Executar o Projeto](#como-executar-o-projeto)
15. [Arquitetura Detalhada](#arquitetura-detalhada)
16. [Boas Práticas Implementadas](#boas-práticas-implementadas)

---

## 🏛️ Visão Geral da Arquitetura

Este projeto implementa uma **arquitetura de microsserviços em nuvem** com os seguintes componentes:

```
┌─────────────────────────────────────────────────────────────┐
│                        Cliente                              │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
                 ┌──────────────────┐
                 │   API Gateway    │  (Port: 8765)
                 └────────┬─────────┘
                          │
        ┌─────────────────┼─────────────────┐
        ▼                 ▼                 ▼
   ┌─────────┐      ┌──────────┐      ┌──────────────┐
   │  Book   │      │ Exchange │      │ Naming       │
   │ Service │      │ Service  │      │ Server       │
   │(8080)   │      │(8081)    │      │ (Eureka)     │
   └────┬────┘      └────┬─────┘      │ (8761)       │
        │                │            └──────────────┘
        └────────────┬───┘
                     │
        ┌────────────┴────────────┐
        ▼                         ▼
   ┌──────────┐             ┌──────────────┐
   │  MySQL   │             │    Zipkin    │
   │ (3310)   │             │ Tracing      │
   └──────────┘             │ (9411)       │
                            └──────────────┘
```

---

## 🛠️ Ferramentas e Tecnologias

| Ferramenta | Versão | Propósito |
|-----------|--------|----------|
| **Java** | 21 | Linguagem de programação |
| **Spring Boot** | 3.5.10 | Framework base |
| **Spring Cloud** | 2025.0.1 | Orquestração de microsserviços |
| **Maven** | 3.x | Gerenciador de dependências |
| **Docker** | Latest | Containerização |
| **Docker Compose** | Latest | Orquestração local |
| **MySQL** | 9.3.0 | Banco de dados |
| **Kubernetes** | 1.x | Orquestração em produção |

---

## 🔧 Spring Cloud Configuration

### 📋 O que é?

Spring Cloud Configuration é um serviço centralizado para gerenciar configurações de aplicações em um ambiente distribuído. Permite versionamento, controle de acesso e atualização dinâmica de configurações sem reiniciar os serviços.

### 🎯 Para que serve?

- **Centralização**: Um único ponto de verdade para todas as configurações
- **Versionamento**: Histórico de mudanças com Git
- **Ambiente específico**: Diferentes configurações para dev, test, prod
- **Atualização dinâmica**: Mudanças sem downtime
- **Segurança**: Criptografia de dados sensíveis

### ⏰ Quando usar?

```
✓ Múltiplos ambientes (dev, staging, prod)
✓ Configurações que mudam frequentemente
✓ Equipes grandes com múltiplos serviços
✓ Necessidade de auditoria de mudanças
✓ Aplicações em containers/kubernetes
```

### 💡 Como utilizar?

#### 1. Adicionar dependência no `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

#### 2. No servidor de config, adicionar anotação:
```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

#### 3. Configurar `application.yml` no servidor:
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/seu-usuario/configuracoes
          default-label: main
```

#### 4. No cliente, adicionar `bootstrap.yml`:
```yaml
spring:
  cloud:
    config:
      uri: http://config-server:8888
      name: book-service
  application:
    name: book-service
```

### 🔍 Exemplo Prático

Arquivo de configuração remoto (`book-service.yml`):
```yaml
logging:
  level:
    root: INFO
    br.com.vandre: DEBUG

database:
  pool:
    max-size: 10
    min-size: 5
```

No código Java:
```java
@Configuration
@ConfigurationProperties(prefix = "database.pool")
public class DatabaseConfig {
    private Integer maxSize;
    private Integer minSize;
    
    // getters e setters
}
```

### 📚 Referências importantes

- **Refresh automático**: Use `@RefreshScope` para recarregar beans sem reiniciar
- **Encryption**: Configure Spring Cloud Config com encriptação de senhas
- **Health checks**: Monitore a conectividade com o config server

---

## 📊 Spring Boot Actuator

### 📋 O que é?

Spring Boot Actuator é um módulo que expõe endpoints HTTP para monitorar e gerenciar sua aplicação em tempo de execução. Fornece métricas, saúde, informações de aplicação e muito mais.

### 🎯 Para que serve?

- **Health Checks**: Verificar status da aplicação
- **Métricas**: Monitorar performance e uso de recursos
- **Environment**: Visualizar propriedades e configurações
- **Logs**: Alterar níveis de log dinamicamente
- **Traces**: Ver últimas requisições HTTP

### ⏰ Quando usar?

```
✓ Sempre em produção (essencial)
✓ Monitoramento contínuo
✓ Debugging de problemas
✓ Integração com ferramentas de APM
✓ Health checks para load balancers
```

### 💡 Como utilizar?

#### 1. Adicionar dependência:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

#### 2. Configurar em `application.yaml`:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,loggers,httptrace
  endpoint:
    health:
      show-details: always
    metrics:
      enabled: true
```

#### 3. Acessar endpoints:

| Endpoint | Descrição | URL |
|----------|-----------|-----|
| Health | Status da aplicação | `GET /actuator/health` |
| Metrics | Métricas em tempo real | `GET /actuator/metrics` |
| Info | Informações da app | `GET /actuator/info` |
| Loggers | Configurar níveis de log | `GET/POST /actuator/loggers` |
| Prometheus | Formato Prometheus | `GET /actuator/prometheus` |

#### 4. Personalizar Health Check:
```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        boolean isConnected = checkDatabaseConnection();
        
        if (isConnected) {
            return Health.up()
                .withDetail("database", "Connected")
                .build();
        } else {
            return Health.down()
                .withDetail("database", "Disconnected")
                .build();
        }
    }
}
```

#### 5. Adicionar informações customizadas:
```yaml
info:
  app:
    name: Book Service
    description: Microserviço para gerenciamento de livros
    version: @project.version@
    encoding: @project.build.sourceEncoding@
```

### 📝 Exemplos de Respostas

**Health Check Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 410410741760,
        "threshold": 10485760
      }
    }
  }
}
```

---

## 🔗 Feign Client

### 📋 O que é?

Feign é um cliente HTTP declarativo que simplifica a comunicação entre microsserviços. Define clientes REST usando anotações, similar ao Spring Data JPA.

### 🎯 Para que serve?

- **Comunicação simplificada**: Escrever clientes HTTP como interfaces
- **Integração com Eureka**: Descoberta automática de serviços
- **Integração com LoadBalancer**: Balanceamento de carga automático
- **Circuit Breaker**: Falhas tratadas graciosamente
- **Retry e Timeout**: Configuração automática de comportamentos

### ⏰ Quando usar?

```
✓ Chamar outros microsserviços
✓ APIs externas com padrão REST
✓ Comunicação síncrona
✓ Cuando há múltiplas instâncias (load balancing)
```

### 💡 Como utilizar?

#### 1. Adicionar dependência:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

#### 2. Ativar Feign na aplicação:
```java
@SpringBootApplication
@EnableFeignClients
public class BookServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }
}
```

#### 3. Criar interface Feign Client:
```java
@FeignClient(
    name = "exchange-service",
    url = "${feign.client.config.exchange-service.url:http://localhost:8081}"
)
public interface ExchangeServiceClient {
    
    @GetMapping("/api/exchange/convert")
    ExchangeRateResponse getExchangeRate(
        @RequestParam("from") String from,
        @RequestParam("to") String to
    );
    
    @PostMapping("/api/exchange/convert")
    ConvertedValueResponse convertCurrency(
        @RequestBody ConversionRequest request
    );
}
```

#### 4. Usar o cliente em um serviço:
```java
@Service
public class BookServiceImpl {
    
    @Autowired
    private ExchangeServiceClient exchangeClient;
    
    public Book getBookWithExchange(Long id, String targetCurrency) {
        Book book = bookRepository.findById(id).orElseThrow();
        
        ExchangeRateResponse rate = exchangeClient.getExchangeRate("USD", targetCurrency);
        book.setPriceInCurrency(book.getPrice() * rate.getRate());
        
        return book;
    }
}
```

#### 5. Configurar Feign em `application.yaml`:
```yaml
feign:
  client:
    config:
      exchange-service:
        connect-timeout: 10000
        read-timeout: 10000
        logger-level: full
        error-decoder: br.com.vandre.config.CustomErrorDecoder
  compression:
    request:
      enabled: true
      min-request-size: 2048
```

#### 6. Implementar fallback para resiliência:
```java
@Component
public class ExchangeServiceFallback implements ExchangeServiceClient {
    
    @Override
    public ExchangeRateResponse getExchangeRate(String from, String to) {
        return new ExchangeRateResponse(from, to, 1.0, "Fallback");
    }
}

@FeignClient(
    name = "exchange-service",
    fallback = ExchangeServiceFallback.class
)
public interface ExchangeServiceClient {
    @GetMapping("/api/exchange/convert")
    ExchangeRateResponse getExchangeRate(
        @RequestParam("from") String from,
        @RequestParam("to") String to
    );
}
```

### 🔍 Recursos Avançados

**Interceptor personalizado:**
```java
@Component
public class AuthenticationInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String token = SecurityContextHolder.getContext()
            .getAuthentication()
            .getCredentials()
            .toString();
        template.header("Authorization", "Bearer " + token);
    }
}
```

**Decodificador de erro customizado:**
```java
@Component
public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400 && response.status() <= 499) {
            return new BadRequestException("Bad request");
        }
        if (response.status() >= 500) {
            return new ServerException("Server error");
        }
        return new Exception("Unknown error");
    }
}
```

---

## 🔍 Eureka - Service Discovery

### 📋 O que é?

Eureka é um serviço de registro e descoberta de serviços desenvolvido pela Netflix. Permite que microsserviços se registrem e descubram uns aos outros dinamicamente, sem necessidade de configuração hardcoded de URLs.

### 🎯 Para que serve?

- **Descoberta automática**: Encontrar serviços sem URLs hardcoded
- **Balanceamento de carga**: Distribuir requisições entre instâncias
- **Resiliência**: Remover automaticamente serviços indisponíveis
- **Auto-scaling**: Suportar adicionar/remover instâncias
- **Health monitoring**: Verificar saúde dos serviços

### ⏰ Quando usar?

```
✓ Arquitetura de microsserviços
✓ Múltiplas instâncias do mesmo serviço
✓ Ambientes dinâmicos (containers, kubernetes)
✓ Necessidade de descentralização
✓ Auto-scaling automático
```

### 💡 Como utilizar?

#### 1. Criar Servidor Eureka (Naming Server)

**Dependência no `pom.xml`:**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

**Anotação na classe principal:**
```java
@SpringBootApplication
@EnableEurekaServer
public class NamingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NamingServiceApplication.class, args);
    }
}
```

**Configuração em `application.yaml`:**
```yaml
spring:
  application:
    name: naming-server

server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

#### 2. Registrar Cliente Eureka

**Dependência:**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

**Configuração em `application.yaml`:**
```yaml
spring:
  application:
    name: book-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always
```

#### 3. Acessar o Dashboard

Abra o navegador: `http://localhost:8761`

### 📊 Propriedades Importantes

| Propriedade | Descrição | Default |
|------------|-----------|---------|
| `eureka.client.register-with-eureka` | Registrar este serviço | true |
| `eureka.client.fetch-registry` | Buscar registro de serviços | true |
| `eureka.instance.lease-renewal-interval-in-seconds` | Intervalo de heartbeat | 30 |
| `eureka.instance.lease-expiration-duration-in-seconds` | Tempo para expirar | 90 |
| `eureka.instance.prefer-ip-address` | Usar IP ao invés de hostname | false |

### 🔍 Verificar Serviços Registrados

```bash
curl http://localhost:8761/eureka/apps
```

Resposta em XML com todos os serviços registrados.

---

## ⚖️ Spring Cloud LoadBalancer

### 📋 O que é?

Spring Cloud LoadBalancer é o substituto moderno do Ribbon, fornecendo balanceamento de carga do lado do cliente para distribuir requisições entre múltiplas instâncias de um serviço.

### 🎯 Para que serve?

- **Distribuição de carga**: Distribuir requisições entre instâncias
- **Round-robin**: Alternância entre servidores
- **Health checks**: Remover servidores indisponíveis
- **Integração com Eureka**: Descoberta automática de instâncias
- **Retry e Timeout**: Comportamentos configuráveis

### ⏰ Quando usar?

```
✓ Múltiplas instâncias do mesmo serviço
✓ Comunicação via Feign
✓ Necessidade de distribuição de carga
✓ Ambientes de produção
✓ Alta disponibilidade
```

### 💡 Como utilizar?

#### 1. Adicionar dependência:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

#### 2. Usar com Feign (automático):
```java
@FeignClient(name = "book-service")  // Usa LoadBalancer automaticamente
public interface BookServiceClient {
    @GetMapping("/api/books/{id}")
    Book getBook(@PathVariable Long id);
}
```

#### 3. Usar LoadBalancer diretamente:
```java
@Service
public class BookServiceImpl {
    
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    
    public Book getBook(Long id) {
        ServiceInstance instance = loadBalancerClient
            .choose("book-service");
        
        String url = instance.getUri().toString();
        // Fazer requisição para url + endpoint
    }
}
```

#### 4. Configurar estratégias de balanceamento:
```yaml
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false  # Desabilitar Ribbon (usar LoadBalancer)
      cache:
        ttl: 30  # TTL em segundos para cache de instâncias
      retry:
        enabled: true
        max-retries: 3
        max-retry-request: 1
      health-check:
        initial-delay: 0
        interval: 30000  # 30 segundos
```

#### 5. Custom Load Balancer Strategy:
```java
@Configuration
public class LoadBalancerConfig {
    
    @Bean
    public ReactorServiceInstanceListSupplier
        discoveryClientServiceInstanceListSupplier(
            ConfigurableApplicationContext context) {
        return new ReactorServiceInstanceListSupplier(
            context.getBean(DiscoveryClient.class));
    }
}
```

### 🔄 Algoritmos de Balanceamento

- **Round-robin**: Distribui requisições em sequência (padrão)
- **Random**: Seleciona instância aleatória
- **LeastConnection**: Escolhe a com menos conexões ativas
- **WeightedRandom**: Considerando peso das instâncias

---

## 🚪 Spring Cloud Gateway

### 📋 O que é?

Spring Cloud Gateway é um roteador inteligente que funciona como ponto único de entrada (API Gateway) para todos os clientes externos. Roteia requisições para microsserviços apropriados com suporte a filtros, transformações e segurança.

### 🎯 Para que serve?

- **Roteamento centralizado**: Ponto único de entrada
- **Filtros**: Adicionar lógica antes/depois de requisições
- **Autenticação**: Validar tokens antes de rotear
- **Rate limiting**: Controlar taxa de requisições
- **Transformação**: Modificar headers, paths, etc
- **Circuit breaker**: Proteção contra serviços fora do ar
- **Composição de APIs**: Agregar múltiplos serviços

### ⏰ Quando usar?

```
✓ Sempre em arquitetura de microsserviços
✓ Controle de acesso centralizado
✓ Transformação de requisições
✓ Agregação de APIs
✓ Proteção contra sobrecarga
✓ Versionamento de APIs
```

### 💡 Como utilizar?

#### 1. Criar projeto API Gateway

**Dependência:**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

#### 2. Configurar rotas em `application.yaml`:
```yaml
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: book-service
          uri: lb://book-service  # lb = load balancer via Eureka
          predicates:
            - Path=/book-service/**
          filters:
            - StripPrefix=1  # Remove /book-service do path
            - AddRequestHeader=X-Request-Id,${random.uuid}
            
        - id: exchange-service
          uri: lb://exchange-service
          predicates:
            - Path=/exchange-service/**
          filters:
            - StripPrefix=1
            - AddResponseHeader=X-Service-Name,exchange-service

server:
  port: 8765

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

#### 3. Criar filtro global customizado:
```java
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    public AuthenticationFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Extrair token do header
            String token = extractToken(request);
            
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // Adicionar informações do usuário ao context
                exchange.getAttributes()
                    .put("userId", jwtTokenProvider.getUserId(token));
                return chain.filter(exchange);
            }
            
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }
    
    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
    public static class Config {
        // Configurações customizadas se necessário
    }
}
```

#### 4. Aplicar filtro globalmente:
```yaml
spring:
  cloud:
    gateway:
      default-filters:
        - name: AuthenticationFilter
      routes:
        - id: book-service
          uri: lb://book-service
          predicates:
            - Path=/book-service/**
          filters:
            - StripPrefix=1
```

#### 5. Rate Limiting com Redis:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: book-service
          uri: lb://book-service
          predicates:
            - Path=/book-service/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10  # 10 requests
                redis-rate-limiter.burstCapacity: 20  # Burst to 20
                key-resolver: "#{@keyResolver}"
```

### 🔧 Predicados Disponíveis

```yaml
predicates:
  - Path=/path/**              # Validar path
  - Method=GET,POST            # Validar método HTTP
  - Host=example.com           # Validar host
  - Header=X-Request-Id,\d+    # Validar header (regex)
  - Query=param,value          # Validar query parameter
  - After=2020-01-01T00:00:00Z # Válido após data
  - Weight=group1,10           # Peso para roteamento
```

### 📊 Filtros Disponíveis

| Filtro | Descrição |
|--------|-----------|
| `StripPrefix` | Remove N elementos do path |
| `AddRequestHeader` | Adiciona header na requisição |
| `AddResponseHeader` | Adiciona header na resposta |
| `RewritePath` | Reescreve o path |
| `CircuitBreaker` | Proteção contra falhas |
| `Retry` | Retry automático |
| `RequestRateLimiter` | Limitação de taxa |

---

## 🛡️ Resilience4J - Circuit Breaker

### 📋 O que é?

Resilience4J é uma biblioteca leve para tornar aplicações resilientes a falhas. Implementa padrões como Circuit Breaker, Retry, Timeout, Rate Limiter e Bulkhead.

### 🎯 Para que serve?

- **Circuit Breaker**: Interromper chamadas a serviços falhos
- **Retry**: Repetir requisições falhadas
- **Timeout**: Limitar tempo de espera
- **Rate Limiter**: Controlar frequência de requisições
- **Bulkhead**: Isolar recursos (threads)
- **Cache**: Cache de respostas
- **Observabilidade**: Métricas e eventos

### ⏰ Quando usar?

```
✓ Chamadas entre microsserviços
✓ Integração com serviços externos
✓ APIs com latência variável
✓ Sistemas que precisam de resiliência
✓ Proteção contra cascata de falhas
```

### 💡 Como utilizar?

#### 1. Adicionar dependência:
```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-circuitbreaker</artifactId>
</dependency>
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-retry</artifactId>
</dependency>
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-micrometer</artifactId>
</dependency>
```

#### 2. Configurar em `application.yaml`:
```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        registerHealthIndicator: true
        slidingWindowSize: 10
        failureRateThreshold: 50
        slowCallRateThreshold: 50
        slowCallDurationThreshold: 2000
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 5000
    instances:
      exchange-service:
        baseConfig: default
        
  retry:
    configs:
      default:
        maxAttempts: 3
        waitDuration: 1000
        retryExceptions:
          - java.net.ConnectException
          - java.io.IOException
    instances:
      exchange-service:
        baseConfig: default
        
  timelimiter:
    configs:
      default:
        cancelRunningFuture: false
        timeoutDuration: 2000
    instances:
      exchange-service:
        baseConfig: default
```

#### 3. Usar Circuit Breaker em serviço:
```java
@Service
public class ExchangeRateService {
    
    @Autowired
    private ExchangeServiceClient exchangeClient;
    
    @CircuitBreaker(name = "exchange-service", fallbackMethod = "fallback")
    @Retry(name = "exchange-service")
    @TimeLimiter(name = "exchange-service")
    public ExchangeRate getRate(String from, String to) {
        return exchangeClient.getExchangeRate(from, to);
    }
    
    public ExchangeRate fallback(String from, String to, Exception e) {
        log.error("Circuit breaker ativado para exchange service", e);
        return new ExchangeRate(from, to, 1.0, "fallback");
    }
}
```

#### 4. Usar com Feign:
```java
@FeignClient(
    name = "exchange-service",
    fallback = ExchangeServiceFallback.class
)
public interface ExchangeServiceClient {
    @GetMapping("/api/exchange/convert")
    ExchangeRate getExchangeRate(
        @RequestParam String from,
        @RequestParam String to
    );
}

@Component
public class ExchangeServiceFallback implements ExchangeServiceClient {
    @Override
    public ExchangeRate getExchangeRate(String from, String to) {
        return new ExchangeRate(from, to, 1.0, "fallback");
    }
}
```

#### 5. Decoradores customizados:
```java
@Service
public class ResilientExchangeService {
    
    private final ExchangeServiceClient client;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final TimeLimiter timeLimiter;
    
    public ResilientExchangeService(ExchangeServiceClient client,
                                   CircuitBreakerRegistry registry) {
        this.client = client;
        this.circuitBreaker = registry.circuitBreaker("exchange-service");
        this.retry = Retry.of("exchange-service", RetryConfig.builder()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            .build());
        this.timeLimiter = TimeLimiter.of("exchange-service", 
            TimeLimiterConfig.builder()
                .timeoutDuration(Duration.ofSeconds(5))
                .build());
    }
    
    public ExchangeRate getRate(String from, String to) {
        return Decorators.ofSupplier(() -> 
                client.getExchangeRate(from, to))
            .withCircuitBreaker(circuitBreaker)
            .withRetry(retry)
            .withTimeLimiter(timeLimiter)
            .get();
    }
}
```

### 📊 Estados do Circuit Breaker

```
CLOSED (Normal)
    ↓ (quando failure rate > threshold)
OPEN (Rejeitando requisições)
    ↓ (após waitDuration)
HALF_OPEN (Testando recuperação)
    ↓ (sucesso)
CLOSED (Recuperado)

HALF_OPEN
    ↓ (falha)
OPEN (Voltou a falhar)
```

### 🔍 Monitorar Circuit Breaker

```bash
# Health check
curl http://localhost:8080/actuator/health

# Métricas
curl http://localhost:8080/actuator/metrics/resilience4j.circuitbreaker.calls
```

---

## 📚 Swagger OpenAPI

### 📋 O que é?

Swagger/OpenAPI é uma especificação para descrever APIs RESTful de forma padronizada e interativa. Gera documentação automática e interface gráfica para testar endpoints.

### 🎯 Para que serve?

- **Documentação automática**: Gera docs a partir do código
- **Interface interativa**: Testar endpoints direto do navegador
- **Contrato da API**: Definir o contrato antes de implementar
- **Geração de código**: Gerar clientes automaticamente
- **Descoberta**: Facilitar descoberta de endpoints

### ⏰ Quando usar?

```
✓ Sempre em APIs públicas
✓ Documentar endpoints
✓ Facilitar integração com clientes
✓ Teste manual de APIs
✓ Comunicação com frontend
```

### 💡 Como utilizar?

#### 1. Adicionar SpringDoc OpenAPI:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.9</version>
</dependency>
```

#### 2. Configurar em `application.yaml`:
```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operations-sorter: method  # Ordenar por método HTTP
    tags-sorter: alpha         # Ordenar tags alfabeticamente
```

#### 3. Documentar controllers:
```java
@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "APIs para gerenciar livros")
public class BookController {
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar livro por ID",
        description = "Retorna um livro específico pelo ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Livro encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Book.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Livro não encontrado"
        )
    })
    public ResponseEntity<Book> getBook(
        @Parameter(description = "ID do livro")
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(bookService.findById(id));
    }
    
    @PostMapping
    @Operation(summary = "Criar novo livro")
    public ResponseEntity<Book> createBook(
        @RequestBody 
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados do livro",
            required = true
        )
        BookRequest request
    ) {
        return ResponseEntity.created(null)
            .body(bookService.create(request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar livro")
    @ApiResponse(responseCode = "204", description = "Livro deletado com sucesso")
    public ResponseEntity<Void> deleteBook(
        @Parameter(description = "ID do livro")
        @PathVariable Long id
    ) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

#### 4. Documentar modelos:
```java
@Data
@Schema(description = "Entidade representando um livro")
public class Book {
    
    @Schema(
        description = "ID único do livro",
        example = "1"
    )
    private Long id;
    
    @Schema(
        description = "Título do livro",
        example = "Clean Code",
        minLength = 1,
        maxLength = 255
    )
    private String title;
    
    @Schema(
        description = "Autor do livro",
        example = "Robert C. Martin"
    )
    private String author;
    
    @Schema(
        description = "Preço do livro em USD",
        example = "50.00",
        minimum = "0"
    )
    @DecimalMin("0")
    private BigDecimal price;
}
```

#### 5. Acessar Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

### 🔧 Anotações Comuns

| Anotação | Propósito |
|----------|-----------|
| `@Tag` | Agrupar operações |
| `@Operation` | Descrever operação |
| `@Parameter` | Descrever parâmetro |
| `@RequestBody` | Descrever corpo da requisição |
| `@ApiResponse` | Descrever resposta |
| `@Schema` | Descrever modelo |

---

## 🔍 Zipkin - Distributed Tracing

### 📋 O que é?

Zipkin é um sistema de rastreamento distribuído que coleta e visualiza informações sobre latência em sistemas distribuídos. Permite rastrear uma requisição através de múltiplos serviços.

### 🎯 Para que serve?

- **Rastreamento**: Seguir requisição entre serviços
- **Análise de latência**: Identificar gargalos
- **Debugging**: Encontrar onde falhas ocorrem
- **Visualização**: Dashboard interativo
- **Performance**: Otimizar tempo de resposta
- **Troubleshooting**: Rastrear erros

### ⏰ Quando usar?

```
✓ Sempre em microsserviços
✓ Debugging de problemas
✓ Análise de performance
✓ Rastreamento de requisições
✓ Identificar gargalos
```

### 💡 Como utilizar?

#### 1. Subir Zipkin com Docker:
```yaml
# docker-compose.yml
zipkin-server:
  image: openzipkin/zipkin:3.5.1
  container_name: zipkin-server
  ports:
    - "9411:9411"
  restart: always
```

Acessar em: `http://localhost:9411`

#### 2. Adicionar dependências no `pom.xml`:
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-sender-urlconnection</artifactId>
</dependency>
```

#### 3. Configurar em `application.yaml`:
```yaml
spring:
  application:
    name: book-service
  zipkin:
    base-url: ${SPRING_ZIPKIN_BASEURL:http://localhost:9411}
  management:
    tracing:
      sampling:
        probability: 1.0  # 100% de sampling (0.1 = 10% em produção)
```

#### 4. Adicionar informações customizadas ao trace:
```java
@Service
public class BookService {
    
    @Autowired
    private Tracer tracer;
    
    public Book createBook(BookRequest request) {
        Span span = tracer.startSpan("create_book");
        
        try (Tracer.SpanInScope scope = tracer.withSpan(span)) {
            span.tag("book.title", request.getTitle());
            span.tag("book.author", request.getAuthor());
            
            // Lógica de criação
            Book book = new Book();
            book.setTitle(request.getTitle());
            book.setAuthor(request.getAuthor());
            
            span.tag("book.id", book.getId().toString());
            
            return book;
        } finally {
            span.finish();
        }
    }
}
```

#### 5. Com Spring Cloud Sleuth (automático):
```java
@Service
public class BookService {
    
    private static final Logger log = LoggerFactory.getLogger(BookService.class);
    
    public Book getBook(Long id) {
        // Sleuth adiciona automaticamente traceId e spanId aos logs
        log.info("Buscando livro com id: {}", id);
        return bookRepository.findById(id).orElseThrow();
    }
}
```

### 📊 Compreendendo Traces

```
Trace: Requisição completa do cliente até a resposta
├── Span: Operação dentro da requisição
│   ├── Tags: Metadados (ex: book.id=1)
│   ├── Logs: Eventos durante execução
│   └── Duration: Tempo de execução
└── Span: Operação em outro serviço
    └── ...
```

### 🔍 Query no Zipkin UI

1. Abrir `http://localhost:9411`
2. Selecionar serviço em "Service Name"
3. Ajustar filtros de data/hora
4. Clicar em "Find Traces"
5. Clicar em um trace para ver detalhes

---

## 🚀 GitHub Actions

### 📋 O que é?

GitHub Actions é uma plataforma de CI/CD (Integração Contínua/Entrega Contínua) nativa do GitHub. Permite automatizar testes, builds e deployments através de workflows.

### 🎯 Para que serve?

- **Testes automáticos**: Executar testes a cada push
- **Build**: Compilar código automaticamente
- **Deploy**: Publicar automaticamente em servidores
- **Quality gates**: Garantir qualidade de código
- **Release**: Versionar e publicar releases
- **Notificações**: Alertar sobre status

### ⏰ Quando usar?

```
✓ Sempre em repositórios públicos
✓ Antes de merge no main
✓ Deploy automático
✓ Testes contínuos
✓ Verificação de qualidade
```

### 💡 Como utilizar?

#### 1. Criar arquivo de workflow:

Criar: `.github/workflows/build-deploy.yml`

```yaml
name: Build and Deploy

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

env:
  REGISTRY: docker.io
  IMAGE_NAME: vandre856

jobs:
  build:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        service: [book-service, exchange-service, naming-service, api-gateway]
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven
      
      - name: Build with Maven
        run: |
          cd ${{ matrix.service }}
          mvn clean package -DskipTests
      
      - name: Run tests
        run: |
          cd ${{ matrix.service }}
          mvn test
      
      - name: Login to Docker Hub
        if: github.ref == 'refs/heads/main'
        uses: docker/login-action@v2
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
      
      - name: Build and push Docker image
        if: github.ref == 'refs/heads/main'
        run: |
          cd ${{ matrix.service }}
          docker build -t ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}/${{ matrix.service }}:${{ github.sha }} .
          docker push ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}/${{ matrix.service }}:${{ github.sha }}

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Deploy to Kubernetes
        run: |
          mkdir -p $HOME/.kube
          echo "${{ secrets.KUBE_CONFIG }}" | base64 -d > $HOME/.kube/config
          kubectl apply -f k8s/
```

#### 2. Adicionar Secrets no GitHub

1. Ir para Settings → Secrets and variables → Actions
2. Adicionar:
   - `DOCKER_USERNAME`
   - `DOCKER_PASSWORD`
   - `KUBE_CONFIG`

#### 3. Estrutura do repositório:
```
.github/
  workflows/
    build-deploy.yml
    code-quality.yml
    security-scan.yml
```

#### 4. Exemplo de verificação de qualidade:
```yaml
# .github/workflows/code-quality.yml
name: Code Quality

on: [push, pull_request]

jobs:
  quality:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: SonarQube Scan
        run: |
          mvn clean verify sonar:sonar \
            -Dsonar.projectKey=book-microservice \
            -Dsonar.host.url=https://sonarcloud.io \
            -Dsonar.login=${{ secrets.SONAR_TOKEN }}
```

---

## ☸️ Kubernetes

### 📋 O que é?

Kubernetes é um orquestrador de containers que automatiza deployment, scaling e operação de aplicações containerizadas em clusters.

### 🎯 Para que serve?

- **Orquestração**: Gerenciar containers automaticamente
- **Auto-scaling**: Aumentar/diminuir instâncias conforme demanda
- **Load balancing**: Distribuir tráfego entre pods
- **Rolling updates**: Deploy zero-downtime
- **Self-healing**: Reiniciar containers que falham
- **Networking**: Comunicação entre services
- **Storage**: Gerenciar volumes persistentes

### ⏰ Quando usar?

```
✓ Produção com microsserviços
✓ Alta disponibilidade necessária
✓ Auto-scaling necessário
✓ Múltiplos ambientes
✓ Deploy contínuo
```

### 💡 Como utilizar?

#### 1. Criar Dockerfile para cada serviço:

```dockerfile
# book-service/Dockerfile
FROM eclipse-temurin:21-jre-alpine

ARG JAR_FILE=target/book-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### 2. Criar arquivos de configuração Kubernetes:

Criar: `k8s/book-service-deployment.yaml`

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: book-service-config
  namespace: default
data:
  application.yaml: |
    spring:
      application:
        name: book-service
      datasource:
        url: jdbc:mysql://mysql-service:3306/book_service
        username: admin
        password: ${DB_PASSWORD}
      jpa:
        hibernate:
          ddl-auto: validate
    management:
      endpoints:
        web:
          exposure:
            include: health,info,metrics,prometheus
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: book-service
  namespace: default
  labels:
    app: book-service
    version: v1
spec:
  replicas: 3  # Quantos pods deseja executar
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0  # Zero downtime
  selector:
    matchLabels:
      app: book-service
  template:
    metadata:
      labels:
        app: book-service
        version: v1
    spec:
      containers:
      - name: book-service
        image: vandre856/book-service:1.0.0  # Especificar tag
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8080
          name: http
        
        env:
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
        - name: SPRING_ZIPKIN_BASEURL
          value: http://zipkin-service:9411
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          value: http://eureka-service:8761/eureka
        
        # Health checks
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 3
        
        # Recursos
        resources:
          requests:
            cpu: 250m
            memory: 512Mi
          limits:
            cpu: 500m
            memory: 1Gi
        
        volumeMounts:
        - name: config
          mountPath: /etc/config
      
      volumes:
      - name: config
        configMap:
          name: book-service-config

---
apiVersion: v1
kind: Service
metadata:
  name: book-service
  namespace: default
  labels:
    app: book-service
spec:
  type: ClusterIP  # Interno ao cluster
  selector:
    app: book-service
  ports:
  - port: 8080
    targetPort: 8080
    protocol: TCP
    name: http
```

#### 3. Criar Secret para credenciais:
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
  namespace: default
type: Opaque
stringData:
  username: admin
  password: your-password-here
```

#### 4. Criar HPA (Horizontal Pod Autoscaler):
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: book-service-hpa
  namespace: default
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: book-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

#### 5. Deployar em Kubernetes:
```bash
# Aplicar configurações
kubectl apply -f k8s/

# Verificar status
kubectl get pods
kubectl get services
kubectl get deployments

# Ver logs
kubectl logs -f deployment/book-service

# Fazer port-forward
kubectl port-forward svc/book-service 8080:8080
```

#### 6. Ingress para exposição externa:
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: microservice-ingress
  namespace: default
spec:
  ingressClassName: nginx
  rules:
  - host: api.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: api-gateway
            port:
              number: 8765
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

- **Java 21+**
- **Maven 3.8+**
- **Docker & Docker Compose**
- **Git**

### Localmente (Sem Docker)

```bash
# 1. Clonar repositório
git clone https://github.com/seu-usuario/book-microservice.git
cd book-microservice

# 2. Iniciar Zipkin
docker run -d -p 9411:9411 openzipkin/zipkin:3.5.1

# 3. Iniciar MySQL
docker run -d \
  -p 3310:3306 \
  -e MYSQL_ROOT_PASSWORD=123 \
  -e MYSQL_DATABASE=book_service \
  mysql:9.3.0

# 4. Build de cada serviço
cd naming-service && mvn clean package && java -jar target/naming-service-0.0.1-SNAPSHOT.jar
cd ../book-service && mvn clean package && java -jar target/book-service-0.0.1-SNAPSHOT.jar
cd ../exchange-service && mvn clean package && java -jar target/exchange-service-0.0.1-SNAPSHOT.jar
cd ../api-gateway && mvn clean package && java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

### Com Docker Compose

```bash
# Subir todos os serviços
docker-compose up -d

# Verificar status
docker-compose ps

# Ver logs
docker-compose logs -f

# Parar serviços
docker-compose down
```

### Acessar Aplicação

| Serviço | URL |
|---------|-----|
| API Gateway | `http://localhost:8765` |
| Book Service | `http://localhost:8080` |
| Exchange Service | `http://localhost:8081` |
| Eureka Dashboard | `http://localhost:8761` |
| Zipkin | `http://localhost:9411` |
| Swagger UI | `http://localhost:8765/swagger-ui.html` |

---

## 🏗️ Arquitetura Detalhada

### Fluxo de uma Requisição

```
1. Cliente faz requisição para API Gateway (8765)
   ↓
2. API Gateway roteia para Book Service conforme predicado
   ↓
3. API Gateway usa LoadBalancer para descobrir instância via Eureka
   ↓
4. Book Service recebe requisição
   ↓
5. Book Service precisa chamar Exchange Service (via Feign)
   ↓
6. Feign descobre Exchange Service no Eureka
   ↓
7. Circuit Breaker verifica se Exchange Service está saudável
   ↓
8. Se falha, ativa fallback
   ↓
9. Micrômetro + Zipkin rastreia toda a requisição
   ↓
10. Resposta retorna ao cliente
```

### Componentes Principais

**API Gateway (Port 8765)**
- Ponto único de entrada
- Roteamento de requisições
- Autenticação/Autorização
- Rate limiting

**Book Service (Port 8080)**
- CRUD de livros
- Comunicação com Exchange Service
- Persistência em MySQL
- Health checks

**Exchange Service (Port 8081)**
- Cálculo de taxas de câmbio
- Conversão de moedas
- Integração externa (opcional)

**Naming Server/Eureka (Port 8761)**
- Registro de serviços
- Descoberta dinâmica
- Health monitoring
- Dashboard visual

**Zipkin (Port 9411)**
- Rastreamento distribuído
- Análise de latência
- Visualização de traces

**MySQL (Port 3310)**
- Persistência de dados
- Migrations com Flyway

---

## ✅ Boas Práticas Implementadas

### 1. **Versionamento de API**
```java
@RestController
@RequestMapping("/api/v1/books")
public class BookControllerV1 {
    // Endpoints v1
}

@RestController
@RequestMapping("/api/v2/books")
public class BookControllerV2 {
    // Endpoints v2 (com mudanças)
}
```

### 2. **Tratamento de Exceções Centralizado**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(e.getMessage()));
    }
}
```

### 3. **Logging estruturado**
```java
@Service
public class BookService {
    private static final Logger log = 
        LoggerFactory.getLogger(BookService.class);
    
    public Book getBook(Long id) {
        log.info("Fetching book with id: {}", id);
        return bookRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Book not found: {}", id);
                return new ResourceNotFoundException("Book not found");
            });
    }
}
```

### 4. **Testes unitários e de integração**
```java
@SpringBootTest
class BookServiceTest {
    
    @MockBean
    private BookRepository bookRepository;
    
    @Autowired
    private BookService bookService;
    
    @Test
    void shouldGetBook() {
        Book book = new Book(1L, "Clean Code", "Robert Martin");
        when(bookRepository.findById(1L))
            .thenReturn(Optional.of(book));
        
        Book result = bookService.getBook(1L);
        
        assertThat(result).isEqualTo(book);
        verify(bookRepository).findById(1L);
    }
}
```

### 5. **Uso de DTOs**
```java
@Data
public class BookRequest {
    @NotBlank
    private String title;
    
    @NotBlank
    private String author;
    
    @DecimalMin("0")
    private BigDecimal price;
}

// No serviço
public Book create(BookRequest request) {
    Book book = new Book();
    book.setTitle(request.getTitle());
    book.setAuthor(request.getAuthor());
    book.setPrice(request.getPrice());
    return bookRepository.save(book);
}
```

### 6. **Versionamento com Git**
- Manter `main` sempre estável
- Usar branches para features
- Pull requests com revisões
- Tags para releases

### 7. **Monitoramento e Observabilidade**
- Health checks regularmente
- Métricas de performance
- Logs estruturados
- Distributed tracing

### 8. **Segurança**
- Validação de entrada
- Sanitização de dados
- Autenticação/Autorização
- HTTPS em produção
- Secrets gerenciados seguramente

---

## 🔗 Referências e Recursos

### Documentação Oficial
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Spring Boot Reference](https://spring.io/projects/spring-boot)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Docker Documentation](https://docs.docker.com/)
- [OpenAPI Specification](https://spec.openapis.org/)

### Blogs e Tutoriais
- [Baeldung - Spring Cloud Guides](https://www.baeldung.com/spring-cloud)
- [Spring Official Blog](https://spring.io/blog)
- [Kubernetes Best Practices](https://kubernetes.io/docs/concepts/configuration/overview/)

---

## 📝 Notas Importantes para Revisão Futura

### Decisões de Design
- **Por que Eureka?** Service discovery dinâmico sem configuração hardcoded
- **Por que Resilience4J?** Circuito breaker simples e eficaz
- **Por que API Gateway?** Ponto centralizado para roteamento e segurança
- **Por que Zipkin?** Rastreamento distribuído essencial em microsserviços

### Problemas Comuns
1. **Timeout de conexão**: Aumentar valores em retries
2. **Cascata de falhas**: Ativar circuit breaker mais cedo
3. **Memory leak**: Monitorar com Actuator
4. **Latência alta**: Analisar traces no Zipkin

### Performance
- Use connection pooling em banco de dados
- Cache respostas frequentes
- Implemente lazy loading em relacionamentos
- Use índices apropriados no banco

---
