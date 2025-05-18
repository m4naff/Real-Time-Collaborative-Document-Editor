/**
 * Authentication module for the Document Editor API Test Client
 */

// Store authentication data
let authData = {
    accessToken: null,
    refreshToken: null,
    username: null
};

// Check if user is already authenticated (from localStorage)
document.addEventListener('DOMContentLoaded', function() {
    const savedAuth = localStorage.getItem('authData');
    if (savedAuth) {
        try {
            authData = JSON.parse(savedAuth);
            updateAuthStatus(true);
        } catch (e) {
            console.error('Failed to parse saved auth data', e);
            localStorage.removeItem('authData');
        }
    }
});

// Register form submission
document.getElementById('registerForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const username = document.getElementById('registerUsername').value;
    const email = document.getElementById('registerEmail').value;
    const password = document.getElementById('registerPassword').value;

    fetch(`${window.config.API_URL}/auth/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            email: email,
            password: password
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Registration failed: ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        displayResponse('Registration successful', data);

        // Auto-fill login form
        document.getElementById('loginUsername').value = username;
        document.getElementById('loginPassword').value = password;
    })
    .catch(error => {
        displayResponse('Registration error', { error: error.message });
    });
});

// Login form submission
document.getElementById('loginForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    fetch(`${window.config.API_URL}/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Login failed: ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        displayResponse('Login successful', data);

        // Save auth data
        authData = {
            accessToken: data.accessToken,
            refreshToken: data.refreshToken,
            username: username
        };

        // Save to localStorage for persistence
        localStorage.setItem('authData', JSON.stringify(authData));

        // Update UI
        updateAuthStatus(true);
    })
    .catch(error => {
        displayResponse('Login error', { error: error.message });
    });
});

// Logout button
document.getElementById('logoutBtn').addEventListener('click', function() {
    // Clear auth data
    authData = {
        accessToken: null,
        refreshToken: null,
        username: null
    };

    // Remove from localStorage
    localStorage.removeItem('authData');

    // Update UI
    updateAuthStatus(false);

    displayResponse('Logout', { message: 'You have been logged out' });
});

// Update UI based on authentication status
function updateAuthStatus(isAuthenticated) {
    const authStatus = document.getElementById('authStatus');
    const logoutBtn = document.getElementById('logoutBtn');
    const docAuthWarning = document.getElementById('docAuthWarning');
    const documentsContent = document.getElementById('documentsContent');
    const wsAuthWarning = document.getElementById('wsAuthWarning');
    const websocketContent = document.getElementById('websocketContent');

    if (isAuthenticated) {
        authStatus.innerHTML = `Authenticated as <strong>${authData.username}</strong>`;
        logoutBtn.style.display = 'block';

        // Show document and websocket content
        docAuthWarning.style.display = 'none';
        documentsContent.style.display = 'block';
        wsAuthWarning.style.display = 'none';
        websocketContent.style.display = 'block';
    } else {
        authStatus.textContent = 'Not authenticated';
        logoutBtn.style.display = 'none';

        // Hide document and websocket content
        docAuthWarning.style.display = 'block';
        documentsContent.style.display = 'none';
        wsAuthWarning.style.display = 'block';
        websocketContent.style.display = 'none';
    }
}

// Helper function to get auth header
function getAuthHeader() {
    if (!authData.accessToken) {
        return {};
    }
    return {
        'Authorization': `Bearer ${authData.accessToken}`
    };
}

// Display response in the response area
function displayResponse(title, data) {
    const responseArea = document.getElementById('responseArea');
    responseArea.innerHTML = `<strong>${title}</strong>\n${JSON.stringify(data, null, 2)}`;
}

// Export functions and data for use in other modules
window.auth = {
    getAuthHeader,
    displayResponse,
    isAuthenticated: () => !!authData.accessToken,
    getUserId: () => authData.username
};
