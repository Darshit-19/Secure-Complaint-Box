package com.securecomplaintbox.servlets;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/verifyOtp")
public class OtpVerifyServlet extends HttpServlet {

    private static final long OTP_EXPIRY_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            handleError(request, response, "Session expired. Please login again.");
            return;
        }

        // Validate input
        String inputOtp = request.getParameter("otp_input");
        if (!isValidOtpInput(inputOtp)) {
            handleError(request, response, "Please enter a valid 6-digit OTP.");
            return;
        }

        // Get OTP data from session
        Integer realOtp = (Integer) session.getAttribute("otp");
        Long otpTime = (Long) session.getAttribute("otp_time");

        if (realOtp == null || otpTime == null) {
            log("OTP verification failed: Missing OTP data in session");
            handleError(request, response, "Session expired. Please login again.");
            return;
        }

        // Check if OTP is expired
        boolean expired = System.currentTimeMillis() - otpTime > OTP_EXPIRY_TIME;
        if (expired) {
            log("OTP verification failed: OTP expired");
            handleError(request, response, "OTP has expired. Please login again.");
            return;
        }

        // Verify OTP
        if (inputOtp.equals(String.valueOf(realOtp))) {
            // Success - clear OTP data and redirect to dashboard
            session.removeAttribute("otp");
            session.removeAttribute("otp_time");
            response.sendRedirect("public/dashboard.html");
        } else {
            log("OTP verification failed: Invalid OTP provided");
            handleError(request, response, "Invalid OTP. Please try again.");
        }
    }

    private boolean isValidOtpInput(String inputOtp) {
        return inputOtp != null && inputOtp.trim().matches("^\\d{6}$");
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }
}
