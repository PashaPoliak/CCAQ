# Claude Certified Architect

## Running the Application

### REST API (Spring Boot)

```bash
./gradlew :rest:bootRun
```

Runs on port **8081**

### LLM Service (Spring Boot)

```bash
./gradlew :llm:bootRun
```

Runs on port **8082** (edit `llm/src/main/resources/application.yaml` to change)

### UI (React)

```bash
./gradlew :ui:frontendRun
```

Runs on port **3000**
