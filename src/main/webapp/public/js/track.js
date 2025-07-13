document.getElementById('trackForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  const form = e.target;
  const data = Object.fromEntries(new FormData(form));

  console.log('Form data:', data); // Debug log

  const toast = document.getElementById('toast');
  const results = document.getElementById('results');
  const submitButton = form.querySelector('button[type="submit"]');

  // Show loading state
  submitButton.disabled = true;
  submitButton.textContent = 'Tracking...';
  toast.textContent = '⏳ Searching for complaint...';
  toast.classList.remove('hidden', 'text-red-600', 'text-green-600');
  results.classList.add('hidden');

  try {
    const url = `/SecureComplaintBox/trackComplaint?org_id=${encodeURIComponent(data.org_id)}&ref=${encodeURIComponent(data.ref)}`;
    console.log('Fetching URL:', url); // Debug log
    
    const resp = await fetch(url);
    const text = await resp.text();
    
    console.log('Response status:', resp.status); // Debug log
    console.log('Response text:', text); // Debug log

    if (resp.ok && text.startsWith("SUCCESS|")) {
      const parts = text.split("|");
      const ref = parts[1];
      const type = parts[2];
      const status = parts[3];
      const date = parts[4];
      
      console.log('Parsed data:', { ref, type, status, date }); // Debug log
      
      // Display complaint details
      document.getElementById('ref-code').textContent = ref;
      document.getElementById('complaint-type').textContent = type;
      document.getElementById('complaint-status').textContent = status;
      document.getElementById('created-at').textContent = date;
      
      // Show results and success message
      results.classList.remove('hidden');
      toast.textContent = '✅ Complaint found!';
      toast.classList.remove('text-red-600');
      toast.classList.add('text-green-600');
      
    } else {
      // Handle error message
      results.classList.add('hidden');
      toast.textContent = `❌ ${text}`;
      toast.classList.remove('text-green-600');
      toast.classList.add('text-red-600');
    }
  } catch (error) {
    console.error('Error tracking complaint:', error);
    results.classList.add('hidden');
    toast.textContent = '❌ Network error. Please try again.';
    toast.classList.remove('text-green-600');
    toast.classList.add('text-red-600');
  } finally {
    // Reset button
    submitButton.disabled = false;
    submitButton.textContent = 'Track Complaint';
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
