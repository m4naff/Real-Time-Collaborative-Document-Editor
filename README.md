# Real-Time Collaborative Document Editor

A backend for a real-time collaborative document editor, where multiple users can edit a document simultaneously. The system handles WebSocket connections for real-time communication, document versioning, conflict resolution, and user authentication.

## Features

- **Real-Time Editing**: Users can collaborate on documents in real-time with live updates using WebSockets
- **Version Control**: Each edit is stored as a new version, allowing users to view document history
- **Document Sharing**: Users can share documents with others, specifying access permissions (view, edit)
- **Conflict Resolution**: Implements algorithms to resolve conflicts when two users try to edit the same part of the document simultaneously
- **User Authentication**: Secure user authentication and authorization using JWT tokens
- **Caching**: Uses Redis to cache real-time updates and track active users

## Technology Stack

- **Spring Boot**: Java-based framework for building the backend
- **MongoDB**: NoSQL database for storing documents and user data
- **Redis**: In-memory data store for caching real-time updates
- **WebSocket**: For real-time communication between clients and server
- **JWT**: For secure authentication and authorization
- **Spring Security**: For securing the API endpoints

## Architecture

The application follows a layered architecture:

1. **Controller Layer**: Handles HTTP requests and WebSocket messages
2. **Service Layer**: Contains business logic for document management, authentication, etc.
3. **Repository Layer**: Interfaces with the MongoDB database
4. **Utility Layer**: Contains helper classes for versioning, conflict resolution, etc.

### Key Components

- **WebSocketController**: Handles real-time document updates and user presence
- **DocumentController**: REST API for document management
- **AuthController**: REST API for user authentication
- **DocumentService**: Business logic for document operations
- **CacheService**: Handles Redis caching operations
- **ConflictResolutionUtil**: Resolves conflicts between concurrent edits
- **VersioningUtil**: Manages document versions

## API Endpoints

### Authentication

- `POST /api/auth/register`: Register a new user
- `POST /api/auth/authenticate`: Authenticate a user and get JWT tokens

### Document Management

- `POST /api/documents`: Create a new document
- `GET /api/documents/{documentId}`: Get a document by ID
- `PUT /api/documents/{documentId}`: Update a document
- `DELETE /api/documents/{documentId}`: Delete a document
- `POST /api/documents/{documentId}/share`: Share a document with another user
- `GET /api/documents/owned`: Get all documents owned by the current user
- `GET /api/documents/shared`: Get all documents shared with the current user
- `GET /api/documents/{documentId}/versions`: Get document version history

### WebSocket Endpoints

- `/ws`: WebSocket connection endpoint
- `/app/document.update`: Send document updates
- `/app/document.join`: Notify when a user joins a document
- `/app/document.leave`: Notify when a user leaves a document
- `/topic/document.{documentId}`: Receive document updates
- `/topic/document.{documentId}.join`: Receive user join notifications
- `/topic/document.{documentId}.leave`: Receive user leave notifications

## Running the Application

### Prerequisites

- Java 17 or higher
- MongoDB
- Redis

### Configuration

The application can be configured through the `application.yml` file:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://admin:password@localhost:27017/docseditor?authSource=admin
    redis:
      host: localhost
      port: 6379

application:
  security:
    jwt:
      secret-key: your-secret-key
      expiration: 86400000  # 24 hours
      refresh-token:
        expiration: 604800000  # 7 days
```

### Running with Docker

The easiest way to run the application is using Docker Compose:

```bash
docker-compose up
```

This will start the application along with MongoDB and Redis containers.

### Running Locally

1. Start MongoDB and Redis
2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Security Considerations

- JWT tokens are used for authentication
- Document access is controlled through ownership and explicit sharing
- WebSocket connections are secured
- Passwords are encrypted using BCrypt

## Testing WebSocket Connections

For detailed instructions on how to test WebSocket connections using Postman, please refer to the [WebSocket Testing Guide](README-WEBSOCKET-TESTING.md). This guide includes:

- How to format STOMP frames correctly in Postman
- Common errors and their solutions
- Step-by-step examples for connecting, subscribing, and sending messages

## Future Enhancements

- Implement more sophisticated conflict resolution algorithms
- Add support for rich text editing
- Implement document locking for critical sections
- Add support for comments and annotations
- Implement real-time cursor tracking
