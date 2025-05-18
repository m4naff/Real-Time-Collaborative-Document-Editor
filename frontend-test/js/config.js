/**
 * Configuration for the Document Editor API Test Client
 */

// Backend server URL - change this to point to your backend server
const BACKEND_URL = 'http://localhost:8080';

// Export configuration
window.config = {
    BACKEND_URL,
    API_URL: `${BACKEND_URL}/api`,
    WS_URL: `${BACKEND_URL}/ws`
};