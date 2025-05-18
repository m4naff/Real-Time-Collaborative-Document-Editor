# WebSocket Testing with Postman

This document provides instructions on how to properly test WebSocket connections using Postman for the Real-Time Collaborative Document Editor application.

## STOMP Protocol

The application uses the STOMP (Simple Text Oriented Messaging Protocol) over WebSocket for real-time communication. When testing with Postman, it's important to format your messages correctly according to the STOMP protocol.

## Common Error: "No enum constant org.springframework.messaging.simp.stomp.StompCommand."

This error occurs when Postman sends a malformed STOMP frame where the command is either missing or invalid. The valid STOMP commands in Spring's StompCommand enum are:
- CONNECT
- CONNECTED
- SEND
- SUBSCRIBE
- UNSUBSCRIBE
- ACK
- NACK
- BEGIN
- COMMIT
- ABORT
- DISCONNECT
- RECEIPT

## Proper STOMP Frame Format in Postman

When testing WebSocket connections in Postman, follow these steps:

1. Create a new WebSocket request in Postman
2. Connect to the WebSocket endpoint: `ws://localhost:8080/ws` (or your server URL)
3. After connecting, you need to send a STOMP CONNECT frame:

```
CONNECT
accept-version:1.1,1.0
heart-beat:10000,10000
Authorization:Bearer your-jwt-token-here

```

4. Wait for the CONNECTED frame from the server
5. Subscribe to a topic:

```
SUBSCRIBE
id:sub-1
destination:/topic/document.your-document-id

```

6. Send a message to update a document:

```
SEND
destination:/app/document.update
content-type:application/json

{"documentId":"your-document-id","content":"Your updated content","userId":"your-user-id","timestamp":1621234567890}
```

## Important Notes

- Each STOMP frame must start with a valid command (e.g., CONNECT, SUBSCRIBE, SEND)
- Headers follow the command, with each header on a new line in the format `key:value`
- An empty line separates the headers from the body
- For SEND frames with JSON bodies, include the `content-type:application/json` header
- End each frame with an empty line and a null byte (`\0`), though Postman usually adds this automatically

## Example Flow

1. Connect to WebSocket
2. Send CONNECT frame
3. Receive CONNECTED frame
4. Send SUBSCRIBE frame to listen for updates
5. Send SEND frame to update a document
6. Receive message on the subscribed topic when updates occur

By following this format, you should avoid the "No enum constant org.springframework.messaging.simp.stomp.StompCommand." error.