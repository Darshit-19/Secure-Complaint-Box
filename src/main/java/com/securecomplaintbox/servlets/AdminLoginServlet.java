package com.securecomplaintbox.servlets;

import java.io.IOException;
import java.sql.*;
import java.sql.SQLException;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.securecomplaintbox.util.DBConnection;
import com.securecomplaintbox.util.PasswordUtil;
import com.securecomplaintbox.util.EmailUtil;

@WebServlet("/adminLogin")
public class AdminLoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        
        // Get org_id from session or request parameter
        String orgId = getOrgId(request, session);
        if (orgId == null) {
            handleError(request, response, "Organization ID is required.");
            return;
        }

        // Check if orgId exists
        try {
            if (!orgIdExists(orgId)) {
                // Clear session to prevent stale org_id
                session.removeAttribute("org_id");
                handleError(request, response, "Invalid Organization ID.");
                return;
            }
        } catch (SQLException e) {
            log("Database error during org ID check", e);
            handleError(request, response, "Database error. Please try again.");
            return;
        }

        // Validate input parameters
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        if (!isValidInput(email, password)) {
            handleError(request, response, "Email and password are required.");
            return;
        }

        // Attempt authentication
        try {
            if (authenticateAdmin(orgId, email, password, session)) {
                // Generate and send OTP
                if (sendOtp(email, session)) {
                    response.sendRedirect("public/verify_otp.html");
                } else {
                    handleError(request, response, "Failed to send OTP. Please try again.");
                }
            } else {
                // Clear session to prevent stale data
                session.removeAttribute("org_id");
                session.removeAttribute("admin_email");
                handleError(request, response, "Invalid email or password.");
            }
        } catch (SQLException e) {
            log("Database error during admin login", e);
            handleError(request, response, "Database error. Please try again.");
        } catch (Exception e) {
            log("Unexpected error during admin login", e);
            handleError(request, response, "An unexpected error occurred. Please try again.");
        }
    }

    private String getOrgId(HttpServletRequest request, HttpSession session) {
        // Always prioritize the request parameter over session
        String orgId = request.getParameter("org_id");
        if (orgId != null && !orgId.trim().isEmpty()) {
            orgId = orgId.trim();
            System.out.println("DEBUG: org_id received from user: [" + orgId + "]");
            session.setAttribute("org_id", orgId);
            return orgId;
        }
        
        // Fallback to session if no request parameter
        orgId = (String) session.getAttribute("org_id");
        if (orgId != null) {
            orgId = orgId.trim();
        }
        return orgId;
    }

    private boolean isValidInput(String email, String password) {
        return email != null && !email.trim().isEmpty() && 
               password != null && !password.trim().isEmpty();
    }

    private boolean authenticateAdmin(String orgId, String email, String password, HttpSession session) 
            throws SQLException {
        if (orgId == null) return false;
        orgId = orgId.trim();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT admin_pass_hash FROM organizations WHERE org_id = ? AND admin_email = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, orgId);
            ps.setString(2, email.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("admin_pass_hash");
                if (PasswordUtil.verify(password, storedHash)) {
                    session.setAttribute("admin_email", email.trim());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean sendOtp(String email, HttpSession session) {
        try {
            int otp = generateOtp();
            session.setAttribute("otp", otp);
            session.setAttribute("otp_time", System.currentTimeMillis());
            
            EmailUtil.sendOtp(email, otp);
            return true;
        } catch (MessagingException e) {
            log("Failed to send OTP email", e);
            return false;
        } catch (Exception e) {
            log("Unexpected error sending OTP", e);
            return false;
        }
    }

    private int generateOtp() {
        return (int) (Math.random() * 900000) + 100000; // 6-digit OTP
    }

    private boolean orgIdExists(String orgId) throws SQLException {
        if (orgId == null) return false;
        orgId = orgId.trim();
        System.out.println("DEBUG: Checking org_id in DB: [" + orgId + "]");
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT 1 FROM organizations WHERE org_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, orgId);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            System.out.println("DEBUG: org_id exists? " + exists);
            return exists;
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }
}


