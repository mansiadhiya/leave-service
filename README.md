# Leave Service

Microservice for managing employee leave requests, approvals, and leave balance tracking.

## Overview

The Leave Service handles leave request submissions, approval workflows, leave balance management, and leave history tracking for employees.

## Architecture Overview

### System Architecture
```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ HTTP/REST + JWT
       ▼
┌─────────────────────────────────┐
│   Leave Service (8083)          │
│  ┌──────────────────────────┐   │
│  │  JWT Filter              │   │
│  └────────┬─────────────────┘   │
│           ▼                      │
│  ┌──────────────────────────┐   │
│  │  Leave Controller        │   │
│  └────────┬─────────────────┘   │
│           ▼                      │
│  ┌──────────────────────────┐   │
│  │  Leave Service           │   │
│  │  - Request Management    │   │
│  │  - Approval Workflow     │   │
│  │  - Balance Calculation   │   │
│  └────────┬─────────────────┘   │
│           ▼                      │
│  ┌──────────────────────────┐   │
│  │  Leave Repository        │   │
│  │  Balance Repository      │   │
│  └────────┬─────────────────┘   │
└───────────┼─────────────────────┘
            ▼
    ┌──────────────┐
    │  MySQL DB    │
    │  (leave_db)  │
    └──────────────┘
            ▲
            │ WebClient
    ┌───────┴────────┐
    │                │
┌───▼────┐    ┌─────▼──────┐
│Auth    │    │Employee    │
│Service │    │Service     │
│(8081)  │    │(8082)      │
└────────┘    └────────────┘
```

### Component Responsibilities
- **JWT Filter**: Token validation
- **Leave Controller**: REST API endpoints
- **Leave Service**: Business logic, approval workflow, balance management
- **Leave Repository**: Leave request database operations
- **Balance Repository**: Leave balance tracking
- **WebClient**: Employee validation

### Approval Workflow
1. Employee submits leave request
2. System validates leave balance
3. Request status set to PENDING
4. Manager reviews request
5. Manager approves/rejects
6. Balance updated if approved
7. Employee notified of decision

## Assumptions

### Technical Assumptions
- MySQL database accessible on localhost:3306
- JWT secret matches Auth Service
- Employee Service available for validation
- Leave balance stored separately from requests
- Date calculations exclude weekends (configurable)

### Business Assumptions
- Default leave balances: Annual=20, Sick=10, Casual=5
- Leave balance resets annually on January 1st
- Maximum 30 consecutive days per request
- Annual leave requires 7 days advance notice
- Sick leave can be retroactive with certificate
- Maximum 3 pending requests per employee
- Unused annual leave carries forward (max 5 days)
- No half-day leave support
- Manager approval required for all leave types
- No automatic approval

### Operational Assumptions
- Service runs on port 8083
- Pagination default: 10 items per page
- No email notifications (can be integrated)
- Logging enabled for SQL queries
- No leave calendar view

## Technology Stack

- **Java**: 21
- **Spring Boot**: 4.0.3
- **Spring Security**: JWT validation
- **Spring Data JPA**: Database operations
- **Spring WebFlux**: Reactive HTTP client
- **MySQL**: Database
- **Lombok**: Reduce boilerplate code
- **JWT**: io.jsonwebtoken (0.11.5)
- **Mockito**: Testing framework

## Prerequisites

- JDK 21 or higher
- Maven 3.6+
- MySQL 8.0+
- Auth Service running (for JWT validation)
- Employee Service running (for employee validation)

## Dependencies

```xml
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- spring-boot-starter-webmvc
- spring-boot-starter-security
- spring-boot-starter-webflux
- mysql-connector-j
- lombok
- jjwt-api (0.11.5)
- jjwt-impl (0.11.5)
- jjwt-jackson (0.11.5)
- mockito-core
```

## Environment Variables

Create `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8083

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/leave_db
spring.datasource.username=<db_username>
spring.datasource.password=<db_password>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT Configuration
jwt.secret=<your_secret_key>

# Service URLs
auth.service.url=http://localhost:8081
employee.service.url=http://localhost:8082

# Leave Configuration
leave.annual.default=20
leave.sick.default=10
leave.casual.default=5
```

## Setup Instructions

1. **Navigate to project directory**
   ```bash
   cd leave-service
   ```

2. **Create MySQL database**
   ```sql
   CREATE DATABASE leave_db;
   ```

3. **Configure application.properties**
   - Update database credentials
   - Set JWT secret key (must match auth-service)
   - Configure service URLs
   - Set default leave balances

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

All endpoints require JWT authentication via `Authorization: Bearer <token>` header.

### Leave Request Management

#### Submit Leave Request
```http
POST /api/leaves
Authorization: Bearer <token>
Content-Type: application/json

{
  "employeeId": 8,
  "startDate": "2026-03-26",
  "endDate": "2026-04-28",
  "reason": "Family"
}
```

#### Get Employee Leave Requests
```http
GET /api/leaves/employee/{employeeId}
Authorization: Bearer <token>
```

#### Update Leave Request
```http
PUT /api/leaves/{id}/status?status={status}
Authorization: Bearer <token>
Content-Type: application/json

```

## Leave Types

- **ANNUAL**: Annual/vacation leave
- **SICK**: Sick leave
- **CASUAL**: Casual leave
- **UNPAID**: Unpaid leave
- **MATERNITY**: Maternity leave
- **PATERNITY**: Paternity leave

## Leave Status

- **PENDING**: Awaiting approval
- **APPROVED**: Approved by manager
- **REJECTED**: Rejected by manager
- **CANCELLED**: Cancelled by employee

## Project Structure

```
leave-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/company/leave/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── enums/
│   │   │       ├── exception/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       └── service/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Testing Instructions

### Unit Tests
Run all unit tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=LeaveServiceTest
```

### Integration Tests
Run integration tests:
```bash
mvn verify
```

### Test Coverage
Generate coverage report:
```bash
mvn clean test jacoco:report
```
View at: `target/site/jacoco/index.html`

### Manual API Testing

#### 1. Get JWT token
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"user123"}'
```

#### 2. Submit leave request
```bash
curl -X POST http://localhost:8083/api/leaves \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"employeeId":1,"leaveType":"ANNUAL","startDate":"2024-03-01","endDate":"2024-03-05","reason":"Vacation","contactNumber":"+1234567890"}'
```

#### 3. Get employee leave requests
```bash
curl -X GET http://localhost:8083/api/leaves/employee/1 \
  -H "Authorization: Bearer <token>"
```

#### 4. Get leave balance
```bash
curl -X GET http://localhost:8083/api/leaves/balance/1 \
  -H "Authorization: Bearer <token>"
```

#### 5. Approve leave request
```bash
curl -X POST http://localhost:8083/api/leaves/1/approve \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"approverId":5,"comments":"Approved"}'
```

#### 6. Reject leave request
```bash
curl -X POST http://localhost:8083/api/leaves/1/reject \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"approverId":5,"comments":"Insufficient balance"}'
```

#### 7. Get leave history
```bash
curl -X GET "http://localhost:8083/api/leaves/history/1?year=2024" \
  -H "Authorization: Bearer <token>"
```

### Test Data Setup
```sql
USE leave_db;
-- Insert leave balances
INSERT INTO leave_balances (employee_id, annual_leave, sick_leave, casual_leave, year) VALUES 
(1, 20, 10, 5, 2024),
(2, 15, 8, 3, 2024);

-- Insert leave requests
INSERT INTO leave_requests (employee_id, leave_type, start_date, end_date, reason, status, contact_number) VALUES 
(1, 'ANNUAL', '2024-02-01', '2024-02-05', 'Vacation', 'APPROVED', '+1111111111'),
(2, 'SICK', '2024-02-10', '2024-02-11', 'Flu', 'PENDING', '+2222222222');
```

### Testing Checklist
- [ ] Submit leave request with valid data
- [ ] Submit leave with insufficient balance
- [ ] Submit leave with past dates
- [ ] Submit leave exceeding 30 days
- [ ] Approve leave request
- [ ] Reject leave request
- [ ] Cancel pending leave request
- [ ] Update leave request dates
- [ ] Get leave balance
- [ ] Get leave history by year
- [ ] Test leave balance deduction
- [ ] Test maximum pending requests limit
- [ ] Validate employee exists

## Test Cases Documentation

### Test Coverage Summary
- **Total Tests**: 50+
- **Test Classes**: 17
- **Coverage Target**: 80%+

### Test Structure

#### 1. Service Layer Tests
**LeaveServiceImplTest** (9 tests)
- `createLeave_Success`: Validates leave request creation
- `createLeave_EmployeeNotFound`: Tests employee validation
- `createLeave_PublishEventFails`: Tests event publishing failure handling
- `updateStatus_Success`: Tests status update to APPROVED
- `updateStatus_LeaveNotFound`: Tests exception for non-existent leave
- `updateStatus_PublishEventFails`: Tests event failure on status update
- `getByEmployee_Success`: Tests retrieving employee leaves
- `getByEmployee_EmployeeNotFound`: Tests validation for invalid employee
- `getByEmployee_PublishEventFails`: Tests event failure on retrieval

#### 2. Repository Layer Tests
**LeaveRepositoryTest** (5 tests)
- `save_Success`: Tests saving leave request
- `findById_Success`: Tests finding leave by ID
- `findByEmployeeId_Success`: Tests finding leaves by employee
- `delete_Success`: Tests deleting leave request
- `existsById_Success`: Tests checking leave existence

#### 3. Controller Layer Tests
**LeaveControllerTest** (3 tests)
- `create_Success`: Tests POST endpoint with valid data
- `updateStatus_Success`: Tests PUT endpoint for status update
- `list_Success`: Tests GET endpoint for employee leaves

#### 4. Entity Tests
**LeaveRequestTest** (2 tests)
- `setAndGetFields`: Validates entity field operations
- `prePersist_SetsCreatedAt`: Tests automatic timestamp setting

#### 5. DTO Tests
**LeaveRequestDtoTest** (3 tests)
- `setAndGetFields`: Validates DTO field operations
- `validation_Success`: Tests valid DTO
- `validation_Fails`: Tests validation constraints

**ApiResponseTest** (3 tests)
- `success_CreatesSuccessResponse`: Tests success response builder
- `error_CreatesErrorResponse`: Tests error response builder
- `setAndGetFields`: Validates response fields

**LeaveStatusChangedEventTest** (3 tests)
- `setAndGetFields`: Validates event DTO fields
- `builder_CreatesEvent`: Tests builder pattern
- `allArgsConstructor_CreatesEvent`: Tests constructor

**StatusUpdateDtoTest** (1 test)
- `setAndGetStatus`: Validates status DTO

#### 6. Mapper Tests
**LeaveMapperTest** (1 test)
- `mapperInterface_Exists`: Validates MapStruct mapper

#### 7. Client Tests
**EmployeeClientTest** (1 test)
- `validateEmployee_Success`: Tests employee service integration

#### 8. Security Tests
**JwtUtilTest** (2 tests)
- `extractUsername_Success`: Tests JWT username extraction
- `validateToken_Success`: Tests JWT token validation

**JwtFilterTest** (3 tests)
- `doFilterInternal_ValidToken`: Tests filter with valid JWT
- `doFilterInternal_InvalidToken`: Tests filter with invalid JWT
- `doFilterInternal_NoToken`: Tests filter without token

#### 9. Event Publisher Tests
**LeaveEventPublisherTest** (2 tests)
- `publishLeaveStatusChanged_Success`: Tests event publishing
- `publishLeaveStatusChanged_Failure`: Tests error handling

#### 10. Configuration Tests
**RabbitMQConfigTest** (8 tests)
- `leaveEventsQueue_CreatesQueue`: Tests queue creation
- `leaveEventsDLQ_CreatesDeadLetterQueue`: Tests DLQ creation
- `leaveEventsExchange_CreatesExchange`: Tests exchange creation
- `leaveEventsDLX_CreatesDeadLetterExchange`: Tests DLX creation
- `leaveEventsBinding_CreatesBinding`: Tests queue binding
- `messageConverter_CreatesConverter`: Tests message converter
- `rabbitTemplate_CreatesTemplate`: Tests RabbitMQ template
- `rabbitListenerContainerFactory_CreatesFactory`: Tests listener factory

**SecurityConfigTest** (1 test)
- `filter_CreatesSecurityFilterChain`: Tests security configuration

**LoggingFilterTest** (3 tests)
- `doFilter_AddsTraceIdFromHeader`: Tests trace ID propagation
- `doFilter_GeneratesTraceIdWhenMissing`: Tests trace ID generation
- `doFilter_ClearsMDCAfterProcessing`: Tests MDC cleanup

#### 11. Exception Tests
**GlobalExceptionHandlerTest** (3 tests)
- `notFound_ReturnsNotFoundResponse`: Tests 404 handling
- `badRequest_ReturnsBadRequestResponse`: Tests 400 handling
- `generic_ReturnsInternalServerError`: Tests 500 handling

**ExceptionTest** (3 tests)
- `badRequestException_CreatesWithMessage`: Tests BadRequestException
- `resourceNotFoundException_CreatesWithMessage`: Tests ResourceNotFoundException
- `externalServiceException_CreatesWithMessage`: Tests ExternalServiceException

### Running Tests

```bash
# Run all tests
mvn clean test

# Run with coverage report
mvn clean test jacoco:report

# Run specific test class
mvn test -Dtest=LeaveServiceImplTest

# View coverage report
open target/site/jacoco/index.html
```

### Test Results
```
Tests run: 50+, Failures: 0, Errors: 0, Skipped: 0
```

## Docker Support

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/leave-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
docker build -t leave-service .
docker run -p 8083:8083 leave-service
```

## Validation Rules

- Start date cannot be in the past
- End date must be after start date
- Leave balance must be sufficient
- Maximum 30 consecutive days
- Minimum 1 day notice for casual leave
- Contact number required

## Business Rules

- Annual leave requires 7 days advance notice
- Sick leave can be applied retroactively with medical certificate
- Maximum 3 pending requests per employee
- Leave balance resets annually
- Unused annual leave can be carried forward (max 5 days)

## Error Handling

Standard error response format:
```json
{
  "timestamp": "2024-02-22T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient leave balance",
  "path": "/api/leaves"
}
```

## Integration with Other Services

- **Auth Service**: JWT token validation
- **Employee Service**: Employee information validation

## Contributing

1. Create feature branch
2. Commit changes
3. Push to branch
4. Create Pull Request

## License

Proprietary
