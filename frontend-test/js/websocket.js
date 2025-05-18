/**
 * WebSocket module for the Document Editor API Test Client
 */

document.addEventListener('DOMContentLoaded', function() {
    // WebSocket variables
    let stompClient = null;
    let currentDocumentId = null;
    let isConnected = false;
    let editorChangeTimeout = null;
    let lastSentContent = '';

    // DOM elements
    const connectBtn = document.getElementById('connectWsBtn');
    const disconnectBtn = document.getElementById('disconnectWsBtn');
    const refreshDocBtn = document.getElementById('refreshDocBtn');
    const wsDocIdInput = document.getElementById('wsDocId');
    const editor = document.getElementById('editor');
    const activeUsersDiv = document.getElementById('activeUsers');

    // Connect to WebSocket
    connectBtn.addEventListener('click', function() {
        if (!auth.isAuthenticated()) {
            auth.displayResponse('Error', { error: 'You must be authenticated to use WebSocket' });
            return;
        }

        currentDocumentId = wsDocIdInput.value.trim();
        if (!currentDocumentId) {
            auth.displayResponse('Error', { error: 'Please enter a document ID' });
            return;
        }

        // First, get the document to verify access and get initial content
        fetch(`${window.config.API_URL}/documents/${currentDocumentId}`, {
            method: 'GET',
            headers: auth.getAuthHeader()
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to access document: ' + response.status);
            }
            return response.json();
        })
        .then(document => {
            // Set initial content
            editor.innerHTML = document.content || '';
            lastSentContent = document.content || '';

            // Connect to WebSocket
            connectToWebSocket();
        })
        .catch(error => {
            auth.displayResponse('Error', { error: error.message });
        });
    });

    // Disconnect from WebSocket
    disconnectBtn.addEventListener('click', function() {
        disconnectFromWebSocket();
    });

    // Refresh document content
    refreshDocBtn.addEventListener('click', function() {
        if (isConnected && currentDocumentId) {
            sendLoadDocumentMessage();
            auth.displayResponse('Refreshing', { status: 'Requesting latest document content' });
        }
    });

    // Handle editor changes
    editor.addEventListener('input', function() {
        if (!isConnected || !currentDocumentId) {
            return;
        }

        // Debounce to avoid sending too many updates
        clearTimeout(editorChangeTimeout);
        editorChangeTimeout = setTimeout(function() {
            const newContent = editor.innerHTML;

            // Only send if content has changed
            if (newContent !== lastSentContent) {
                sendDocumentUpdate(newContent);
                lastSentContent = newContent;
            }
        }, 500); // 500ms debounce
    });

    // Connect to WebSocket
    function connectToWebSocket() {
        // Create SockJS and STOMP client
        const socket = new SockJS(window.config.WS_URL);
        stompClient = Stomp.over(socket);

        // Disable debug logging
        stompClient.debug = null;

        // Connect to the WebSocket server with authentication token
        const headers = {};
        if (auth.isAuthenticated()) {
            headers['Authorization'] = `Bearer ${localStorage.getItem('authData') ? JSON.parse(localStorage.getItem('authData')).accessToken : ''}`;
        }

        console.log('Attempting to connect to WebSocket server with headers:', headers);
        stompClient.connect(headers, function(frame) {
            console.log('WebSocket connected successfully:', frame);
            isConnected = true;
            auth.displayResponse('WebSocket Connected', { status: 'Connected to WebSocket server' });

            // Update UI
            connectBtn.disabled = true;
            disconnectBtn.disabled = false;
            refreshDocBtn.disabled = false;
            wsDocIdInput.disabled = true;

            // Subscribe to document updates
            stompClient.subscribe(`/topic/document.${currentDocumentId}`, function(message) {
                handleDocumentUpdate(JSON.parse(message.body));
            });

            // Subscribe to user join events
            stompClient.subscribe(`/topic/document.${currentDocumentId}.join`, function(message) {
                const userId = message.body;
                addActiveUser(userId);
                auth.displayResponse('User Joined', { userId: userId });
            });

            // Subscribe to user leave events
            stompClient.subscribe(`/topic/document.${currentDocumentId}.leave`, function(message) {
                const userId = message.body;
                removeActiveUser(userId);
                auth.displayResponse('User Left', { userId: userId });
            });

            // Subscribe to active users list (sent only to this user)
            stompClient.subscribe(`/user/queue/document.${currentDocumentId}.users`, function(message) {
                const users = JSON.parse(message.body);
                updateActiveUsersList(users);
            });

            // Subscribe to document content updates (sent only to this user)
            stompClient.subscribe(`/user/queue/document.${currentDocumentId}.content`, function(message) {
                handleDocumentContent(JSON.parse(message.body));
            });

            // Send join message
            sendJoinMessage();
        }, function(error) {
            console.error('WebSocket connection error:', error);
            // Log more details about the error
            if (error.headers) {
                console.error('Error headers:', error.headers);
            }
            if (error.body) {
                console.error('Error body:', error.body);
            }
            auth.displayResponse('WebSocket Error', { error: JSON.stringify(error) });
            disconnectFromWebSocket();
        });
    }

    // Disconnect from WebSocket
    function disconnectFromWebSocket() {
        if (stompClient && isConnected) {
            // Send leave message before disconnecting
            if (currentDocumentId) {
                sendLeaveMessage();
            }

            // Disconnect
            stompClient.disconnect();
            stompClient = null;
            isConnected = false;

            // Update UI
            connectBtn.disabled = false;
            disconnectBtn.disabled = true;
            refreshDocBtn.disabled = true;
            wsDocIdInput.disabled = false;
            activeUsersDiv.innerHTML = '';

            auth.displayResponse('WebSocket Disconnected', { status: 'Disconnected from WebSocket server' });
        }
    }

    // Send document update message
    function sendDocumentUpdate(content) {
        if (!isConnected || !stompClient || !currentDocumentId) {
            return;
        }

        const message = {
            documentId: currentDocumentId,
            content: content,
            userId: auth.getUserId(),
            timestamp: Date.now()
        };

        stompClient.send('/app/document.update', {}, JSON.stringify(message));
    }

    // Send join message
    function sendJoinMessage() {
        if (!isConnected || !stompClient || !currentDocumentId) {
            return;
        }

        const message = {
            documentId: currentDocumentId,
            userId: auth.getUserId(),
            timestamp: Date.now()
        };

        stompClient.send('/app/document.join', {}, JSON.stringify(message));

        // Also explicitly request the latest document content
        sendLoadDocumentMessage();
    }

    // Send load document message
    function sendLoadDocumentMessage() {
        if (!isConnected || !stompClient || !currentDocumentId) {
            return;
        }

        const message = {
            documentId: currentDocumentId,
            userId: auth.getUserId(),
            timestamp: Date.now()
        };

        stompClient.send('/app/document.load', {}, JSON.stringify(message));
    }

    // Send leave message
    function sendLeaveMessage() {
        if (!isConnected || !stompClient || !currentDocumentId) {
            return;
        }

        const message = {
            documentId: currentDocumentId,
            userId: auth.getUserId(),
            timestamp: Date.now()
        };

        stompClient.send('/app/document.leave', {}, JSON.stringify(message));
    }

    // Handle document update message
    function handleDocumentUpdate(message) {
        // Only update if the content is from another user
        if (message.userId !== auth.getUserId()) {
            // Update editor content
            editor.innerHTML = message.content;
            lastSentContent = message.content;

            auth.displayResponse('Document Updated', { 
                from: message.userId, 
                timestamp: new Date(message.timestamp).toLocaleTimeString() 
            });
        }
    }

    // Handle document content message (response to document.load)
    function handleDocumentContent(message) {
        // Update editor content with the latest version
        editor.innerHTML = message.content;
        lastSentContent = message.content;

        auth.displayResponse('Document Loaded', { 
            timestamp: new Date(message.timestamp).toLocaleTimeString() 
        });
    }

    // Add active user to the list
    function addActiveUser(userId) {
        // Check if user is already in the list
        if (document.querySelector(`.user-badge[data-user-id="${userId}"]`)) {
            return;
        }

        const userBadge = document.createElement('span');
        userBadge.className = 'user-badge';
        userBadge.setAttribute('data-user-id', userId);
        userBadge.textContent = userId;

        activeUsersDiv.appendChild(userBadge);
    }

    // Remove active user from the list
    function removeActiveUser(userId) {
        const userBadge = document.querySelector(`.user-badge[data-user-id="${userId}"]`);
        if (userBadge) {
            userBadge.remove();
        }
    }

    // Update active users list
    function updateActiveUsersList(users) {
        activeUsersDiv.innerHTML = '';

        if (!users || users.length === 0) {
            return;
        }

        users.forEach(userId => {
            addActiveUser(userId);
        });
    }

    // Clean up on page unload
    window.addEventListener('beforeunload', function() {
        disconnectFromWebSocket();
    });
});
