// Auto-fill organization ID from URL if present
document.addEventListener('DOMContentLoaded', function() {
  const pathParts = location.pathname.split('/');
  const orgIdFromUrl = pathParts[2]; // Get org ID from URL like /SecureComplaintBox/org-id/submit
  
  if (orgIdFromUrl && orgIdFromUrl !== 'submit' && orgIdFromUrl !== 'public') {
    const orgIdInput = document.querySelector('input[name="org_id"]');
    const orgIdHelp = document.getElementById('orgIdHelp');
    
    if (orgIdInput) {
      orgIdInput.value = orgIdFromUrl;
      orgIdInput.readOnly = true; // Make it read-only since it's auto-filled
      orgIdInput.classList.add('bg-gray-100'); // Visual indication that it's auto-filled
      
      // Show the help message only
      if (orgIdHelp) {
        orgIdHelp.classList.remove('hidden');
      }
      
      console.log('Auto-filled organization ID:', orgIdFromUrl);
    }
  }
});

document.getElementById('complaintForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  const form = e.target;
  const orgIdInput = form.querySelector('[name="org_id"]');
  if (orgIdInput) {
    console.log('Org ID input value before trim:', orgIdInput.value);
  }
  const data = Object.fromEntries(new FormData(form));
  console.log('FormData org_id:', data.org_id);

  // Always require org_id from the form; do not fallback to 'public'
  if (!data.org_id || !data.org_id.trim()) {
    const toast = document.getElementById('toast');
    toast.textContent = '❌ Organization ID is required.';
    toast.classList.remove('text-green-600');
    toast.classList.add('text-red-600');
    toast.classList.remove('hidden');
    return;
  } else {
    data.org_id = data.org_id.trim();
  }

  console.log('Submitting data:', data);

  const toast = document.getElementById('toast');
  const submitButton = form.querySelector('button[type="submit"]');

  // Show loading state
  submitButton.disabled = true;
  submitButton.textContent = 'Submitting...';
  toast.textContent = '⏳ Submitting your complaint...';
  toast.classList.remove('hidden', 'text-red-600', 'text-green-600');

  try {
    const payload = new URLSearchParams(data);
    console.log('Payload being sent:', payload.toString());
    const resp = await fetch(`/SecureComplaintBox/submitComplaint`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: payload
    });

    if (resp.ok) {
      const ref = await resp.text();
      toast.textContent = `✅ Complaint submitted successfully! Reference: ${ref}`;
      toast.classList.remove('text-red-600');
      toast.classList.add('text-green-600');
      
      // Copy reference to clipboard
      try {
        await navigator.clipboard.writeText(ref);
        toast.textContent += ' (Copied to clipboard)';
      } catch (clipboardError) {
        console.log('Clipboard copy failed:', clipboardError);
      }
      
      form.reset();
    } else {
      // Handle error response
      const errorText = await resp.text();
      toast.textContent = `❌ ${errorText || 'Failed to submit complaint. Please try again.'}`;
      toast.classList.remove('text-green-600');
      toast.classList.add('text-red-600');
    }
  } catch (error) {
    console.error('Error submitting complaint:', error);
    toast.textContent = '❌ Network error. Please try again.';
    toast.classList.remove('text-green-600');
    toast.classList.add('text-red-600');
  } finally {
    // Reset button
    submitButton.disabled = false;
    submitButton.textContent = 'Submit Complaint';
  }
});

// Function to go back to home page
function goBack() {
  const pathParts = location.pathname.split('/');
  const orgId = pathParts[2];
  if (orgId) {
    window.location.href = `/SecureComplaintBox/${orgId}`;
  } else {
    window.location.href = '/SecureComplaintBox/';
  }
}
