# Load Balancer (Round Robin) - Spring Boot

A simple load balancer implemented with a round-robin strategy. The controller delegates incoming `/balance` requests to a pool of healthy worker nodes and exposes endpoints to register workers and view the current pool.

## Highlights

- Round-robin distribution of requests to worker nodes
- Dynamic registration of workers via `/register`
- Retrieval of registered workers via `/workers`
- Transparent forwarding of incoming requests to the selected worker
- Preserves headers and request body while proxying



## How It Works

- Incoming requests to paths under `/balance/**` are proxied to the next healthy worker node as determined by a round-robin strategy.
- The original request path is preserved by removing the `/balance` prefix before forwarding
  for example http:/localhost:8080/balance/api/home will be directed to /api/home
- All request headers and the body are forwarded to the selected worker.
- The response from the worker is returned to the original client with the same status, headers, and body.
- If no worker is available, a 503 Service Unavailable is returned with a message.

## Endpoints

### Forwarding
- **Path:** `/balance/**`
- **Method:** Any HTTP method (GET, POST, PUT, DELETE, etc.)
- **Behavior:** Proxies the request to the next healthy worker node

### Register a New Worker
- **Path:** `/register`
- **Method:** POST
- **Body:** `WorkerNodeRequest` (contains the worker URL)
- **Response:** 201 Created with the worker URL

### List All Workers
- **Path:** `/workers`
- **Method:** GET
- **Response:** 200 OK with a list of worker URLs

## How to Run

1. Ensure you have Java 17+ (or as required by your project) and Maven/Gradle installed.
2. Clone the project
3. Build the project:
    - Maven: `mvn clean package`
    - Gradle: `./gradlew build`
4. Run the application:
    - With Maven: `mvn spring-boot:run`
    - Or run the generated jar: `java -jar target/your-app.jar`


## Example Usage

### Register a Worker
```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"url": "http://localhost:9001"}'

```
### List all workers
```bash
curl http://localhost:8080/workers
```

###  Using the loadbalancer for any request
```bash
curl -X POST http://localhost:8080/balance/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com"}'
```
- Here the request will be rerouted to /api/users of the workers
