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

```
┌─────────────────────────────────────────────────────────┐
│                     Client Layer                        │
│  (Web Browsers, Mobile Apps, Desktop Applications)      │
└───────────────────────────┬─────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                      API Layer                          │
│  ┌───────────────┐  ┌────────────────┐  ┌────────────┐  │
│  │AuthController │  │DocumentController│ │WebSocketCtrl│ │
│  └───────────────┘  └────────────────┘  └────────────┘  │
└───────────────────────────┬─────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                    Service Layer                        │
│  ┌─────────────┐ ┌──────────┐ ┌────────────┐ ┌───────┐  │
│  │AuthService  │ │DocService│ │WebSocketSvc│ │JwtSvc │  │
│  └─────────────┘ └──────────┘ └────────────┘ └───────┘  │
└───────────────────────────┬─────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                  Repository Layer                       │
│     ┌───────────────────┐    ┌────────────────────┐     │
│     │ UserRepository    │    │ DocumentRepository │     │
│     └───────────────────┘    └────────────────────┘     │
└───────────────────────────┬─────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                     Data Layer                          │
│        ┌─────────────────┐    ┌─────────────┐           │
│        │     MongoDB     │    │    Redis    │           │
│        └─────────────────┘    └─────────────┘           │
└─────────────────────────────────────────────────────────┘
```

### Key Components

- **WebSocketController**: Handles real-time document updates and user presence
- **DocumentController**: REST API for document management
- **AuthController**: REST API for user authentication
- **DocumentService**: Business logic for document operations
- **CacheService**: Handles Redis caching operations
- **ConflictResolutionUtil**: Resolves conflicts between concurrent edits
- **VersioningUtil**: Manages document versions

## Authentication Flow

```
┌──────────┐                                  ┌────────────┐                                ┌─────────────┐
│  Client  │                                  │API Gateway │                                │Auth Service │
└────┬─────┘                                  └──────┬─────┘                                └──────┬──────┘
     │                                              │                                              │
     │ 1. POST /api/auth/register or /api/auth/login│                                              │
     │ ─────────────────────────────────────────────>                                              │
     │                                              │                                              │
     │                                              │ 2. Forward authentication request            │
     │                                              │ ─────────────────────────────────────────────>
     │                                              │                                              │
     │                                              │                                              │
     │                                              │ 3. Validate credentials & generate JWT       │
     │                                              │ <─────────────────────────────────────────────
     │                                              │                                              │
     │ 4. Return JWT token                          │                                              │
     │ <─────────────────────────────────────────────                                              │
     │                                              │                                              │
     │ 5. Include JWT in Authorization header       │                                              │
     │ ─────────────────────────────────────────────>                                              │
     │                                              │                                              │
     │                                              │ 6. Validate JWT token                        │
     │                                              │ ─────────────────────────────────────────────>
     │                                              │                                              │
     │                                              │ 7. Token valid/invalid response              │
     │                                              │ <─────────────────────────────────────────────
     │                                              │                                              │
     │ 8. Return requested resource or 401 error    │                                              │
     │ <─────────────────────────────────────────────                                              │
     │                                              │                                              │
┌────┴─────┐                                  ┌──────┴─────┐                                ┌──────┴──────┐
│  Client  │                                  │API Gateway │                                │Auth Service │
└──────────┘                                  └────────────┘                                └─────────────┘
```

## WebSocket Communication Flow

```
┌──────────┐                                  ┌────────────────┐                            ┌─────────────┐
│  Client  │                                  │WebSocketService│                            │DocumentSvc  │
└────┬─────┘                                  └───────┬────────┘                            └──────┬──────┘
     │                                                │                                            │
     │ 1. Connect to WebSocket (/ws)                  │                                            │
     │ ─────────────────────────────────────────────────>                                          │
     │                                                │                                            │
     │ 2. Subscribe to topics                         │                                            │
     │ (/topic/document.{id}, /topic/document.{id}.join)                                           │
     │ ─────────────────────────────────────────────────>                                          │
     │                                                │                                            │
     │ 3. Send message to join document               │                                            │
     │ (/app/document.join)                           │                                            │
     │ ─────────────────────────────────────────────────>                                          │
     │                                                │                                            │
     │                                                │ 4. Process join request                    │
     │                                                │ ─────────────────────────────────────────────>
     │                                                │                                            │
     │                                                │ 5. Update active users                     │
     │                                                │ <─────────────────────────────────────────────
     │                                                │                                            │
     │                                                │ 6. Broadcast join notification             │
     │ <─────────────────────────────────────────────────                                          │
     │                                                │                                            │
     │ 7. Send document updates                       │                                            │
     │ (/app/document.update)                         │                                            │
     │ ─────────────────────────────────────────────────>                                          │
     │                                                │                                            │
     │                                                │ 8. Process update                          │
     │                                                │ ─────────────────────────────────────────────>
     │                                                │                                            │
     │                                                │ 9. Save document version                   │
     │                                                │ <─────────────────────────────────────────────
     │                                                │                                            │
     │                                                │ 10. Broadcast update to all subscribers    │
     │ <─────────────────────────────────────────────────                                          │
     │                                                │                                            │
┌────┴─────┐                                  ┌───────┴────────┐                            ┌──────┴──────┐
│  Client  │                                  │WebSocketService│                            │DocumentSvc  │
└──────────┘                                  └────────────────┘                            └─────────────┘
```

## API Endpoints
You may use frontend-test folder to test the project.

### Authentication

#### Register a new user

```
POST /api/auth/register
```
![image](https://github.com/user-attachments/assets/0c1f2b89-8a4d-4a06-be4d-2ea7d72ee60b)


**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response Codes:**
- `200 OK`: User registered successfully
- `400 Bad Request`: Username or email already exists
- `500 Internal Server Error`: Server error

#### Authenticate a user

```
POST /api/auth/login
```
![image](https://github.com/user-attachments/assets/4fa17975-8fa1-4706-a5b1-2c43eb7e49d3)

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response Codes:**
- `200 OK`: Authentication successful
- `401 Unauthorized`: Invalid credentials
- `500 Internal Server Error`: Server error

### Document Management

#### Create a new document

```
POST /api/documents
```
![image](https://github.com/user-attachments/assets/e3533ea9-0001-4925-91d9-24a9af91291f)

**Request Body:**
```json
{
  "title": "My Document",
  "content": "Initial content of the document"
}
```

**Response:**
```json
{
  "id": "60a1e2c3d4e5f6a7b8c9d0e1",
  "title": "My Document",
  "content": "Initial content of the document",
  "owner": "johndoe",
  "createdAt": "2023-05-16T12:00:00Z",
  "updatedAt": "2023-05-16T12:00:00Z",
  "sharedWith": []
}
```

**Response Codes:**
- `200 OK`: Document created successfully
- `400 Bad Request`: Invalid request
- `401 Unauthorized`: User not authenticated
- `500 Internal Server Error`: Server error

#### Get a document by ID

```
GET /api/documents/{documentId}
```
![image](https://github.com/user-attachments/assets/84107de4-7c29-4c73-89eb-c35d2d6c65eb)

**Response:**
```json
{
  "id": "60a1e2c3d4e5f6a7b8c9d0e1",
  "title": "My Document",
  "content": "Current content of the document",
  "owner": "johndoe",
  "createdAt": "2023-05-16T12:00:00Z",
  "updatedAt": "2023-05-16T12:30:00Z",
  "sharedWith": [
    {
      "username": "janedoe",
      "accessLevel": "EDIT"
    }
  ]
}
```

**Response Codes:**
- `200 OK`: Document retrieved successfully
- `401 Unauthorized`: User not authenticated
- `403 Forbidden`: User does not have access to the document
- `404 Not Found`: Document not found
- `500 Internal Server Error`: Server error

#### Update a document

```
PUT /api/documents/{documentId}
```
![image](https://github.com/user-attachments/assets/7e6af66e-9d24-423b-ac84-e7e64b07c3e0)

**Request Body:**
```json
{
  "title": "Updated Document Title",
  "content": "Updated content of the document"
}
```

**Response:**
```json
{
  "id": "60a1e2c3d4e5f6a7b8c9d0e1",
  "title": "Updated Document Title",
  "content": "Updated content of the document",
  "owner": "johndoe",
  "createdAt": "2023-05-16T12:00:00Z",
  "updatedAt": "2023-05-16T13:00:00Z",
  "sharedWith": [
    {
      "username": "janedoe",
      "accessLevel": "EDIT"
    }
  ]
}
```

**Response Codes:**
- `200 OK`: Document updated successfully
- `400 Bad Request`: Invalid request
- `401 Unauthorized`: User not authenticated
- `403 Forbidden`: User does not have edit access to the document
- `404 Not Found`: Document not found
- `500 Internal Server Error`: Server error

#### Delete a document

```
DELETE /api/documents/{documentId}
```
![image](https://github.com/user-attachments/assets/b822309d-5576-4cf1-be30-de7429fa9805)

**Response Codes:**
- `204 No Content`: Document deleted successfully
- `401 Unauthorized`: User not authenticated
- `403 Forbidden`: User is not the owner of the document
- `404 Not Found`: Document not found
- `500 Internal Server Error`: Server error

#### Share a document with another user

```
POST /api/documents/{documentId}/share
```
![image](https://github.com/user-attachments/assets/5fdb5e2d-aab4-4845-921b-2636cf957da1)

**Request Body:**
```json
{
  "username": "janedoe",
  "accessLevel": "EDIT"
}
```

Note: The `accessLevel` field can be either "VIEW" or "EDIT".

**Response:**
```json
{
  "id": "60a1e2c3d4e5f6a7b8c9d0e1",
  "title": "My Document",
  "content": "Current content of the document",
  "owner": "johndoe",
  "createdAt": "2023-05-16T12:00:00Z",
  "updatedAt": "2023-05-16T13:30:00Z",
  "sharedWith": [
    {
      "username": "janedoe",
      "accessLevel": "EDIT"
    }
  ]
}
```

**Response Codes:**
- `200 OK`: Document shared successfully
- `400 Bad Request`: Invalid request or user not found
- `401 Unauthorized`: User not authenticated
- `403 Forbidden`: User is not the owner of the document
- `404 Not Found`: Document not found
- `500 Internal Server Error`: Server error

#### Get all documents owned by the current user

```
GET /api/documents/owned
```
![image](https://github.com/user-attachments/assets/fb2c4d55-7496-4106-995a-36bd26aedb64)

**Response:**
```json
[
  {
    "id": "60a1e2c3d4e5f6a7b8c9d0e1",
    "title": "My Document 1",
    "content": "Content of document 1",
    "owner": "johndoe",
    "createdAt": "2023-05-16T12:00:00Z",
    "updatedAt": "2023-05-16T13:30:00Z",
    "sharedWith": []
  },
  {
    "id": "60a1e2c3d4e5f6a7b8c9d0e2",
    "title": "My Document 2",
    "content": "Content of document 2",
    "owner": "johndoe",
    "createdAt": "2023-05-17T10:00:00Z",
    "updatedAt": "2023-05-17T11:00:00Z",
    "sharedWith": [
      {
        "username": "janedoe",
        "accessLevel": "VIEW"
      }
    ]
  }
]
```

**Response Codes:**
- `200 OK`: Documents retrieved successfully
- `401 Unauthorized`: User not authenticated
- `500 Internal Server Error`: Server error

#### Get all documents shared with the current user

```
GET /api/documents/shared
```
![image](https://github.com/user-attachments/assets/bc9588fa-c037-4c3d-904b-768c07b9d691)

**Response:**
```json
[
  {
    "id": "60a1e2c3d4e5f6a7b8c9d0e3",
    "title": "Shared Document 1",
    "content": "Content of shared document 1",
    "owner": "janedoe",
    "createdAt": "2023-05-15T09:00:00Z",
    "updatedAt": "2023-05-15T10:00:00Z",
    "sharedWith": [
      {
        "username": "johndoe",
        "accessLevel": "EDIT"
      }
    ]
  },
  {
    "id": "60a1e2c3d4e5f6a7b8c9d0e4",
    "title": "Shared Document 2",
    "content": "Content of shared document 2",
    "owner": "bobsmith",
    "createdAt": "2023-05-18T14:00:00Z",
    "updatedAt": "2023-05-18T15:00:00Z",
    "sharedWith": [
      {
        "username": "johndoe",
        "accessLevel": "VIEW"
      }
    ]
  }
]
```

**Response Codes:**
- `200 OK`: Documents retrieved successfully
- `401 Unauthorized`: User not authenticated
- `500 Internal Server Error`: Server error

#### Get document version history

```
GET /api/documents/{documentId}/versions
```
![image](https://github.com/user-attachments/assets/093320f7-599a-46df-b3d3-27e290cb0f1c)

**Response:**
```json
[
  {
    "id": "v1-60a1e2c3d4e5f6a7b8c9d0e1",
    "documentId": "60a1e2c3d4e5f6a7b8c9d0e1",
    "content": "Initial content of the document",
    "editor": "johndoe",
    "timestamp": "2023-05-16T12:00:00Z",
    "versionNumber": 1
  },
  {
    "id": "v2-60a1e2c3d4e5f6a7b8c9d0e1",
    "documentId": "60a1e2c3d4e5f6a7b8c9d0e1",
    "content": "Updated content of the document",
    "editor": "johndoe",
    "timestamp": "2023-05-16T13:00:00Z",
    "versionNumber": 2
  },
  {
    "id": "v3-60a1e2c3d4e5f6a7b8c9d0e1",
    "documentId": "60a1e2c3d4e5f6a7b8c9d0e1",
    "content": "Further updated content with additional information",
    "editor": "janedoe",
    "timestamp": "2023-05-16T14:00:00Z",
    "versionNumber": 3
  }
]
```

**Response Codes:**
- `200 OK`: Document versions retrieved successfully
- `401 Unauthorized`: User not authenticated
- `403 Forbidden`: User does not have access to the document
- `404 Not Found`: Document not found
- `500 Internal Server Error`: Server error

### WebSocket Endpoints

#### WebSocket Connection

```
GET /ws
```

This is the main WebSocket connection endpoint. Clients connect to this endpoint to establish a WebSocket connection.

#### Document Load

```
SEND /app/document.load
```

**Message Payload:**
```json
{
  "documentId": "60a1e2c3d4e5f6a7b8c9d0e1",
  "userId": "johndoe",
  "content": null,
  "timestamp": "2023-05-16T15:00:00Z"
}
```

This endpoint is used to load a document's content. The server will respond by sending the document content to the client.

#### Document Update

```
SEND /app/document.update
```

**Message Payload:**
```json
{
  "documentId": "60a1e2c3d4e5f6a7b8c9d0e1",
  "userId": "johndoe",
  "content": "Updated content with real-time changes",
  "timestamp": "2023-05-16T15:05:00Z"
}
```

This endpoint is used to send document updates when a user makes changes to a document. The server will broadcast the update to all connected clients.

#### User Join Notification

```
SEND /app/document.join
```

**Message Payload:**
```json
{
  "documentId": "60a1e2c3d4e5f6a7b8c9d0e1",
  "userId": "johndoe",
  "content": null,
  "timestamp": "2023-05-16T15:00:00Z"
}
```

This endpoint is used to notify when a user joins a document editing session. The server will broadcast the join notification to all connected clients.

#### User Leave Notification

```
SEND /app/document.leave
```

**Message Payload:**
```json
{
  "documentId": "60a1e2c3d4e5f6a7b8c9d0e1",
  "userId": "johndoe",
  "content": null,
  "timestamp": "2023-05-16T16:00:00Z"
}
```

This endpoint is used to notify when a user leaves a document editing session. The server will broadcast the leave notification to all connected clients.

#### Subscribe to Document Updates

```
SUBSCRIBE /topic/document.{documentId}
```

Clients subscribe to this topic to receive real-time updates for a specific document.

#### Subscribe to User Join Notifications

```
SUBSCRIBE /topic/document.{documentId}.join
```

Clients subscribe to this topic to receive notifications when users join a document editing session.

#### Subscribe to User Leave Notifications

```
SUBSCRIBE /topic/document.{documentId}.leave
```

Clients subscribe to this topic to receive notifications when users leave a document editing session.

## Error Handling

The application uses a global exception handler to handle various types of exceptions and return appropriate HTTP status codes and error messages.

Common error responses:

```json
{
  "status": 404,
  "message": "Document not found",
  "timestamp": "2023-05-16T15:30:00Z"
}
```

```json
{
  "status": 403,
  "message": "You do not have permission to edit this document",
  "timestamp": "2023-05-16T15:35:00Z"
}
```

```json
{
  "status": 401,
  "message": "Invalid authentication token",
  "timestamp": "2023-05-16T15:40:00Z"
}
```

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
