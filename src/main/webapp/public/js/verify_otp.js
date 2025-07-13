document.getElementById('otpForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  const form = e.target;
  const data = Object.fromEntries(new FormData(form));

  const toast = document.getElementById('toast');
  const submitButton = form.querySelector('button[type="submit"]');

  // Validate OTP format
  if (!/^\d{6}$/.test(data.otp_input)) {
    toast.textContent = '❌ Please enter a valid 6-digit OTP code.';
    toast.classList.remove('hidden', 'text-green-600');
    toast.classList.add('text-red-600');
    return;
  }

  // Show loading state
  submitButton.disabled = true;
  submitButton.textContent = 'Verifying...';
  toast.textContent = '⏳ Verifying OTP...';
  toast.classList.remove('hidden', 'text-red-600', 'text-green-600');

  try {
    const resp = await fetch(`/SecureComplaintBox/verifyOtp`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: new URLSearchParams(data)
    });

    if (resp.ok) {
      // Success - redirect to dashboard
      toast.textContent = '✅ OTP verified! Redirecting to dashboard...';
      toast.classList.remove('text-red-600');
      toast.classList.add('text-green-600');
      
      // Redirect to dashboard
      setTimeout(() => {
        window.location.href = '/SecureComplaintBox/public/dashboard.html';
      }, 1500);
      
    } else {
      // Handle error response
      const errorText = await resp.text();
      toast.textContent = `❌ ${errorText || 'OTP verification failed. Please try again.'}`;
      toast.classList.remove('text-green-600');
      toast.classList.add('text-red-600');
      
      // Reset button
      submitButton.disabled = false;
      submitButton.textContent = 'Verify OTP';
    }
  } catch (error) {
    console.error('Error during OTP verification:', error);
    toast.textContent = '❌ Network error. Please try again.';
    toast.classList.remove('text-green-600');
    toast.classList.add('text-red-600');
    
    // Reset button
    submitButton.disabled = false;
    submitButton.textContent = 'Verify OTP';
  }
});

// Auto-focus on OTP input and format as user types
document.addEventListener('DOMContentLoaded', () => {
  const otpInput = document.querySelector('input[name="otp_input"]');
  if (otpInput) {
    otpInput.focus();
    
    // Only allow numbers and limit to 6 digits
    otpInput.addEventListener('input', (e) => {
      e.target.value = e.target.value.replace(/\D/g, '').slice(0, 6);
    });
  }
}); 