/**
 * Documents module for the Document Editor API Test Client
 */

document.addEventListener('DOMContentLoaded', function() {
    // Create Document Form
    document.getElementById('createDocForm').addEventListener('submit', function(e) {
        e.preventDefault();

        if (!auth.isAuthenticated()) {
            auth.displayResponse('Error', { error: 'You must be authenticated to create a document' });
            return;
        }

        const title = document.getElementById('docTitle').value;
        const content = document.getElementById('docContent').value;

        fetch(`${window.config.API_URL}/documents`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...auth.getAuthHeader()
            },
            body: JSON.stringify({
                title: title,
                content: content
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to create document: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            auth.displayResponse('Document Created', data);
            // Clear form
            document.getElementById('docTitle').value = '';
            document.getElementById('docContent').value = '';
        })
        .catch(error => {
            auth.displayResponse('Error', { error: error.message });
        });
    });

    // Get Document Form
    document.getElementById('getDocForm').addEventListener('submit', function(e) {
        e.preventDefault();

        if (!auth.isAuthenticated()) {
            auth.displayResponse('Error', { error: 'You must be authenticated to get a document' });
            return;
        }

        const documentId = document.getElementById('getDocId').value;

        fetch(`${window.config.API_URL}/documents/${documentId}`, {
            method: 'GET',
            headers: auth.getAuthHeader()
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to get document: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            auth.displayResponse('Document Retrieved', data);
        })
        .catch(error => {
            auth.displayResponse('Error', { error: error.message });
        });
    });

    // Update Document Form
    document.getElementById('updateDocForm').addEventListener('submit', function(e) {
        e.preventDefault();

        if (!auth.isAuthenticated()) {
            auth.displayResponse('Error', { error: 'You must be authenticated to update a document' });
            return;
        }

        const documentId = document.getElementById('updateDocId').value;
        const content = document.getElementById('updateDocContent').value;

        fetch(`${window.config.API_URL}/documents/${documentId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                ...auth.getAuthHeader()
            },
            body: JSON.stringify({
                content: content
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update document: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            auth.displayResponse('Document Updated', data);
        })
        .catch(error => {
            auth.displayResponse('Error', { error: error.message });
        });
    });

    // Share Document Form
    document.getElementById('shareDocForm').addEventListener('submit', function(e) {
        e.preventDefault();

        if (!auth.isAuthenticated()) {
            auth.displayResponse('Error', { error: 'You must be authenticated to share a document' });
            return;
        }

        const documentId = document.getElementById('shareDocId').value;
        const targetUserId = document.getElementById('targetUserId').value;
        const role = document.getElementById('shareRole').value;

        fetch(`${window.config.API_URL}/documents/${documentId}/share`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...auth.getAuthHeader()
            },
            body: JSON.stringify({
                targetUserId: targetUserId,
                role: role
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to share document: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            auth.displayResponse('Document Shared', data);
        })
        .catch(error => {
            auth.displayResponse('Error', { error: error.message });
        });
    });

    // Delete Document Form
    document.getElementById('deleteDocForm').addEventListener('submit', function(e) {
        e.preventDefault();

        if (!auth.isAuthenticated()) {
            auth.displayResponse('Error', { error: 'You must be authenticated to delete a document' });
            return;
        }

        const documentId = document.getElementById('deleteDocId').value;

        if (!confirm('Are you sure you want to delete this document?')) {
            return;
        }

        fetch(`${window.config.API_URL}/documents/${documentId}`, {
            method: 'DELETE',
            headers: auth.getAuthHeader()
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete document: ' + response.status);
            }
            auth.displayResponse('Document Deleted', { message: 'Document deleted successfully' });
        })
        .catch(error => {
            auth.displayResponse('Error', { error: error.message });
        });
    });

    // Get Document Versions Form
    document.getElementById('getVersionsForm').addEventListener('submit', function(e) {
        e.preventDefault();

        if (!auth.isAuthenticated()) {
            auth.displayResponse('Error', { error: 'You must be authenticated to get document versions' });
            return;
        }

        const documentId = document.getElementById('versionsDocId').value;

        fetch(`${window.config.API_URL}/documents/${documentId}/versions`, {
            method: 'GET',
            headers: auth.getAuthHeader()
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to get document versions: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            auth.displayResponse('Document Versions', data);
        })
        .catch(error => {
            auth.displayResponse('Error', { error: error.message });
        });
    });

    // Get Owned Documents Button
    document.getElementById('getOwnedDocsBtn').addEventListener('click', function() {
        if (!auth.isAuthenticated()) {
            auth.displayResponse('Error', { error: 'You must be authenticated to get your documents' });
            return;
        }

        fetch(`${window.config.API_URL}/documents/owned`, {
            method: 'GET',
            headers: auth.getAuthHeader()
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to get owned documents: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            auth.displayResponse('My Documents', data);
            displayDocumentsList(data, 'ownedDocsList');
        })
        .catch(error => {
            auth.displayResponse('Error', { error: error.message });
        });
    });

    // Get Shared Documents Button
    document.getElementById('getSharedDocsBtn').addEventListener('click', function() {
        if (!auth.isAuthenticated()) {
            auth.displayResponse('Error', { error: 'You must be authenticated to get shared documents' });
            return;
        }

        fetch(`${window.config.API_URL}/documents/shared`, {
            method: 'GET',
            headers: auth.getAuthHeader()
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to get shared documents: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            auth.displayResponse('Shared With Me', data);
            displayDocumentsList(data, 'sharedDocsList');
        })
        .catch(error => {
            auth.displayResponse('Error', { error: error.message });
        });
    });

    // Helper function to display documents list
    function displayDocumentsList(documents, elementId) {
        const container = document.getElementById(elementId);
        container.innerHTML = '';

        if (!documents || documents.length === 0) {
            container.innerHTML = '<p>No documents found</p>';
            return;
        }

        const list = document.createElement('ul');
        list.className = 'list-group';

        documents.forEach(doc => {
            const item = document.createElement('li');
            item.className = 'list-group-item';

            const title = document.createElement('h5');
            title.textContent = doc.title;

            const id = document.createElement('p');
            id.className = 'small text-muted';
            id.textContent = `ID: ${doc.id}`;

            const owner = document.createElement('p');
            owner.className = 'small';
            owner.textContent = `Owner: ${doc.ownerId}`;

            const btnGroup = document.createElement('div');
            btnGroup.className = 'btn-group mt-2';

            const viewBtn = document.createElement('button');
            viewBtn.className = 'btn btn-sm btn-primary';
            viewBtn.textContent = 'View';
            viewBtn.onclick = function() {
                document.getElementById('getDocId').value = doc.id;
                document.getElementById('getDocForm').dispatchEvent(new Event('submit'));
            };

            const editBtn = document.createElement('button');
            editBtn.className = 'btn btn-sm btn-secondary';
            editBtn.textContent = 'Edit in WebSocket';
            editBtn.onclick = function() {
                document.getElementById('wsDocId').value = doc.id;
                document.getElementById('websocket-tab').click();
            };

            btnGroup.appendChild(viewBtn);
            btnGroup.appendChild(editBtn);

            item.appendChild(title);
            item.appendChild(id);
            item.appendChild(owner);
            item.appendChild(btnGroup);

            list.appendChild(item);
        });

        container.appendChild(list);
    }
});
