Description
This is a Spring Boot-based API for a bookstore application. It provides endpoints for managing
customers,books, and purchases, including CRUD operations and additional functionalities like
loyalty points for customers and tracking purchases.

Features
 * Customer Management: Create, read, update, delete customers.
 * Book Management: Create, read, update, delete books.
 * Purchase Management: Create and manage purchases, with support for loyalty points.
 * Validation: Input validation using annotations like @NotNull and @Min to ensure data integrity.
 * Transactional Operations: Supports rollback on failure to ensure consistency in the database.
 * Error Handling: Proper exception handling with clear messages for invalid or non-existent resources.

Tech Stack
 * Java 17
 * Spring Boot
 * Spring Data JPA
 * H2 Database (for development)
 * Maven for dependency management
 * Lombok for reducing boilerplate code
 * MapStruct for DTO mapping
 * JUnit/Mockito for testing

Requirements
 * JDK 17 or higher
 * Maven 3.6 or higher

Installation
 1) Clone the repository:
	git clone https://github.com/FanisFS/bookstore
 2) Navigate to the project directory:
	cd bookstore
 3) Install dependencies using Maven:
	mvn clean install

Running the Application
   mvn spring-boot:run
   This will start the application on http://localhost:8080 by default.

API Endpoints
 1) Customers
	* GET /api/customers - Get all customers
	* GET /api/customers/{id} - Get a customer by ID
	* POST /api/customers - Create a new customer
	* PUT /api/customers/{id} - Update an existing customer
	* DELETE /api/customers/{id} - Delete a customer
 2) Books
	* GET /api/books - Get all books
	* GET /api/books/{id} - Get a book by ID
	* POST /api/books - Create a new book
	* PUT /api/books/{id} - Update an existing book
	* DELETE /api/books/{id} - Delete a book
 3) Purchases
	* GET /api/purchases - Get all purchases
	* GET /api/purchases/{id} - Get a purchase by ID
	* POST /api/purchases - Create a purchase book
	* PUT /api/purchases/{id} - Update an existing purchase
	* DELETE /purchases/books/{id} - Delete a purchase

Testing
   mvn test

Explanation of the Decisions Made Along the Way
1. Technology Stack Choices
	I chose Spring Boot for its rapid development capabilities and flexibility in
	integrating with databases. Java 17 was selected for its long-term support and
	performance improvements. For database interactions, Spring Data JPA was used to
	simplify CRUD operations, and H2 Database was chosen for development due to its
	simplicity.

	MapStruct was used to map DTOs to entities, reducing boilerplate code, and
	Lombok was incorporated to auto-generate getter/setter methods and constructors
	to improve readability and development speed.

2. Architectural Decisions
	The application follows a Layered Architecture, separating concerns across
	controllers, services, and repositories to ensure modularity and maintainability.
	DTOs were implemented to decouple the internal data model from the API responses,
	ensuring flexibility and data security.

3. API Design Decisions
	The RESTful API uses standard HTTP methods: GET for data retrieval, POST for
	creating resources, PUT for updates, and DELETE for resource removal.
	Path variables (e.g., /api/customers/{id}) are used for clear, meaningful URLs.

4. Validation and Error Handling
	Bean Validation annotations (e.g., @NotNull, @Size) ensure incoming data meets
	required constraints. Custom exception handling with @ControllerAdvice is used to
	catch errors like CustomerNotFoundException, providing meaningful error messages.

5. Testing Decisions
	The application uses JUnit for unit testing and Spring Boot Test for integration
	testing to verify the correctness of both individual components and the entire system.
	Mockito is used to mock external dependencies for isolated testing.