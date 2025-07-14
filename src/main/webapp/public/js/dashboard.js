// Load complaints when page loads
document.addEventListener('DOMContentLoaded', () => {
    // Check if user is authenticated before loading data
    checkAuthentication().then(isAuthenticated => {
        if (isAuthenticated) {
            loadComplaints();
            setupBackToOrgLink();
        } else {
            // Redirect to login if not authenticated
            window.location.replace('/SecureComplaintBox/public/admin_login.html');
        }
    });
});

async function checkAuthentication() {
    try {
        const resp = await fetch('/SecureComplaintBox/dashboard', {
            method: 'GET',
            credentials: 'same-origin'
        });
        
        return resp.ok;
    } catch (error) {
        console.error('Authentication check failed:', error);
        return false;
    }
}

async function loadComplaints() {
    const loading = document.getElementById('loading');
    const tableBody = document.getElementById('complaintsTableBody');
    const emptyState = document.getElementById('emptyState');
    const errorState = document.getElementById('errorState');

    // Show loading state
    loading.classList.remove('hidden');
    tableBody.innerHTML = '';
    emptyState.classList.add('hidden');
    errorState.classList.add('hidden');

    try {
        // Fetch complaints data from API
        const resp = await fetch('/SecureComplaintBox/dashboard');
        
        if (!resp.ok) {
            throw new Error('Failed to load complaints');
        }
        
        const data = await resp.json();
        
        // Set organization ID
        if (data.org_id) {
            document.getElementById('orgId').textContent = data.org_id;
            // Set up back to org home link with full URL
            const fullEmpUrl = window.location.origin + '/SecureComplaintBox/' + data.org_id + '/submit';
            document.getElementById('backToOrg').href = `/SecureComplaintBox/public/signup_success.html?org_id=${data.org_id}&emp_url=${encodeURIComponent(fullEmpUrl)}`;
            document.getElementById('backToOrg').textContent = '← Back to Org Home';
        }

        // Display complaints
        if (data.complaints && data.complaints.length > 0) {
            data.complaints.forEach(complaint => {
                const tr = document.createElement('tr');
                tr.className = 'border-b border-gray-200 hover:bg-gray-50 transition duration-200';
                tr.innerHTML = `
                    <td class="p-6 font-mono text-sm">${complaint.ref}</td>
                    <td class="p-6 font-medium">${complaint.type}</td>
                    <td class="p-6">
                        <span class="px-3 py-1 text-xs font-semibold rounded-full ${
                            complaint.status === 'resolved' ? 'bg-green-100 text-green-800' : 
                            complaint.status === 'pending' ? 'bg-yellow-100 text-yellow-800' : 
                            'bg-gray-100 text-gray-800'
                        }">
                            ${complaint.status}
                        </span>
                    </td>
                    <td class="p-6 text-sm text-gray-600">${complaint.date}</td>
                    <td class="p-6">
                        <button onclick="viewComplaint('${complaint.ref}')" 
                                class="bg-indigo-100 text-indigo-700 hover:bg-indigo-200 px-3 py-1 rounded-md text-sm font-medium transition duration-200">
                            👁️ View Details
                        </button>
                    </td>
                `;
                tableBody.appendChild(tr);
            });
        } else {
            emptyState.classList.remove('hidden');
        }

    } catch (error) {
        console.error('Error loading complaints:', error);
        errorState.classList.remove('hidden');
        showToast('Error loading complaints', 'error');
    } finally {
        loading.classList.add('hidden');
    }
}

function setupBackToOrgLink() {
    // This will be set when complaints are loaded
}

function viewComplaint(ref) {
    window.location.href = `/SecureComplaintBox/public/view_complaint.html?ref=${encodeURIComponent(ref)}`;
}

 