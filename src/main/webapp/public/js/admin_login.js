document.getElementById('adminLoginForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  const form = e.target;
  const data = Object.fromEntries(new FormData(form));

  const toast = document.getElementById('toast');
  const submitButton = form.querySelector('button[type="submit"]');

  // Show loading state
  submitButton.disabled = true;
  submitButton.textContent = 'Logging in...';
  toast.textContent = '⏳ Authenticating...';
  toast.classList.remove('hidden', 'text-red-600', 'text-green-600');

  try {
    const resp = await fetch(`/SecureComplaintBox/adminLogin`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: new URLSearchParams(data)
    });

    if (resp.ok) {
      // Success - redirect to OTP verification
      toast.textContent = '✅ Login successful! Redirecting to OTP verification...';
      toast.classList.remove('text-red-600');
      toast.classList.add('text-green-600');
      
      // Redirect to OTP verification page
      setTimeout(() => {
        window.location.href = '/SecureComplaintBox/public/verify_otp.html';
      }, 1500);
      
    } else {
      // Handle error response
      const errorText = await resp.text();
      toast.textContent = `❌ ${errorText || 'Login failed. Please check your credentials.'}`;
      toast.classList.remove('text-green-600');
      toast.classList.add('text-red-600');
      
      // Reset button
      submitButton.disabled = false;
      submitButton.textContent = 'Login';
    }
  } catch (error) {
    console.error('Error during login:', error);
    toast.textContent = '❌ Network error. Please try again.';
    toast.classList.remove('text-green-600');
    toast.classList.add('text-red-600');
    
    // Reset button
    submitButton.disabled = false;
    submitButton.textContent = 'Login';
  }
}); 