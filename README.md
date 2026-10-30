#  ChefIA — AI-Powered Recipe Generator

ChefIA is a recipe generation application built with Java and Spring Boot that uses artificial intelligence to suggest recipes based on the ingredients available in the user's pantry.

The project was inspired by a common everyday problem: wanting to prepare something different but discovering that some ingredients are missing. ChefIA addresses this challenge by generating practical recipes based on registered ingredients, their quantities, and expiration dates, helping users reduce food waste.

##  Features

- **Ingredient Management:** Register, list, update, and delete pantry ingredients.
- **AI-Powered Recipe Generation:** Generate personalized recipes using the OpenAI API.
- **Expiration Date Awareness:** Include ingredient expiration dates in the AI prompt to prioritize ingredients that expire sooner.
- **Recipe Instructions:** Receive recipes with names, ingredient quantities, and step-by-step preparation instructions.
- **Reactive HTTP Communication:** Integrate with the OpenAI API using Spring WebFlux, WebClient, and Project Reactor's Mono.
- **Database Persistence:** Store ingredient information using an H2 in-memory database.
- **Database Versioning:** Manage database schema changes with Flyway migrations.

##  Technologies

| Technology | Purpose |
|---|---|
| Java | Main programming language |
| Spring Boot | Application framework |
| Spring Web | REST API development |
| Spring Data JPA | Database access and persistence |
| Spring WebFlux | Reactive HTTP communication |
| WebClient | HTTP requests to the OpenAI API |
| Project Reactor (Mono) | Reactive programming |
| OpenAI API | AI-powered recipe generation |
| H2 Database | In-memory relational database |
| Flyway | Database schema migrations |
| Maven | Dependency management and build automation |

##  Architecture

The application follows a layered architecture to separate responsibilities:

```text
src/main/java/dev/tributino/ChefIA/
├── config/
│   └── WebClientConfig.java
├── controller/
│   ├── FoodItemController.java
│   └── RecipeController.java
├── enums/
│   └── CategoriaReceita.java
├── model/
│   └── FoodItem.java
├── repository/
│   └── FoodItemRepository.java
└── service/
    ├── ChatGptService.java
    └── FoodItemService.java
```

### Layer Responsibilities

- **Controller:** Exposes REST endpoints and handles HTTP requests.
- **Service:** Implements ingredient management and recipe generation logic.
- **Repository:** Provides database access through Spring Data JPA.
- **Model:** Represents the pantry ingredient entity.
- **Enums:** Defines recipe categories.
- **Config:** Configures the WebClient used for communication with the OpenAI API.

##  Getting Started

### Prerequisites

Before running the application, make sure you have:

- Java 21 or a compatible Java version supported by the project configuration.
- Maven, or the Maven Wrapper included in the project.
- An OpenAI API key.

### 1. Clone the Repository

```bash
git clone https://github.com/Tributino03/chefia-recipe-generator.git
cd chefia-recipe-generator
```

### 2. Configure the OpenAI API Key

The application reads the API key from an environment variable, keeping credentials outside the source code.

**Windows PowerShell:**

```powershell
$env:API_KEY="your-openai-api-key"
```

**Linux/macOS:**

```bash
export API_KEY="your-openai-api-key"
```

Replace `your-openai-api-key` with your own API key.

### 3. Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

Alternatively, if the Maven Wrapper is available:

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**

```bash
./mvnw spring-boot:run
```

The application will start on the default Spring Boot port:

```text
http://localhost:8080
```

## 📡 API Endpoints

### Ingredient Management

Base path: `/food`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/food` | Retrieve all registered ingredients |
| POST | `/food` | Register a new ingredient |
| PUT | `/food/{id}` | Update an existing ingredient |
| DELETE | `/food/{id}` | Delete an ingredient |

### Generate a Recipe

| Method | Endpoint | Description |
|---|---|---|
| GET | `/generate` | Generate a recipe based on registered ingredients |

The recipe generation endpoint retrieves the registered ingredients and sends their information to the OpenAI API, which generates a recipe based on the provided prompt.

##  API Usage Examples

### Register an Ingredient

**Request:**

```http
POST /food
Content-Type: application/json
```

**Request Body:**

```json
{
  "nome": "Tomate",
  "categoriaReceita": "SALADAS",
  "quantidade": 4,
  "validade": "2026-11-05"
}
```

The `categoriaReceita` field must contain a valid value from the `CategoriaReceita` enum defined in the application.

### Retrieve All Ingredients

```http
GET /food
```

### Update an Ingredient

```http
PUT /food/1
Content-Type: application/json
```

```json
{
  "nome": "Tomate",
  "categoriaReceita": "SALADAS",
  "quantidade": 3,
  "validade": "2026-11-05"
}
```

### Delete an Ingredient

```http
DELETE /food/1
```

A successful deletion returns HTTP `204 No Content`.

### Generate a Recipe

```http
GET /generate
```

The application sends the available ingredients to the OpenAI API and returns the generated recipe as text.

The AI prompt instructs the model to prioritize ingredients with expiration dates approaching sooner, suggest practical recipes, and provide ingredient quantities and preparation steps.

**Note:** The current implementation includes expiration dates in the prompt but does not explicitly sort the ingredients by expiration date before sending them to the AI.

## 🗄️ Database

ChefIA currently uses an H2 in-memory database.

The database configuration is defined in `application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:chefia_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=validate

spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
```

The database schema is managed through Flyway migrations.

### H2 Console

The H2 console is enabled for development and can be accessed at:

```text
http://localhost:8080/h2-console
```

Use the following connection settings:

| Setting | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:chefia_db` |
| User Name | `sa` |
| Password | Leave empty |

**Important:** The database is stored in memory. Ingredient records are not preserved when the application terminates.

The H2 console should be disabled or appropriately protected in production environments.

##  OpenAI Integration

ChefIA communicates with the OpenAI API through Spring's WebClient.

The integration includes:

- Configurable API endpoint.
- Bearer token authentication.
- JSON request payloads.
- System and user prompts.
- Reactive HTTP response handling with `Mono`.
- Extraction of the generated text from the API response.

The current implementation uses the `gpt-4o` model through the Chat Completions API.

The generated recipe is based on the registered ingredients, their quantities, and expiration dates. The prompt also allows the AI to suggest small quantities of common pantry staples, such as salt, oil, water, and pepper.

**Note:** API access, model availability, and usage costs depend on the OpenAI account and its configuration.

##  Security Considerations

The application uses an environment variable to provide the OpenAI API key.

Recommended practices include:

- Never commit API keys or credentials to version control.
- Keep secrets outside application source code.
- Configure environment variables securely in deployment environments.
- Disable or protect development tools such as the H2 console in production.
- Implement appropriate exception handling for external API failures.
- Validate incoming requests before persisting data.
