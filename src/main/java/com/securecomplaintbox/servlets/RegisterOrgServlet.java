package com.securecomplaintbox.servlets;

import java.io.IOException;
import java.sql.*;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.securecomplaintbox.util.DBConnection;
import com.securecomplaintbox.util.PasswordUtil;

@WebServlet("/registerOrg")
public class RegisterOrgServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String orgName = request.getParameter("org_name");
        String adminEmail = request.getParameter("admin_email");
        String password = request.getParameter("password");

        if (!isValidInput(orgName, adminEmail, password)) {
            handleError(request, response, "All fields are required and must be valid.");
            return;
        }

        orgName = orgName.trim();
        adminEmail = adminEmail.trim().toLowerCase();

        if (!isValidEmail(adminEmail)) {
            handleError(request, response, "Please enter a valid email address.");
            return;
        }

        if (!isValidPassword(password)) {
            handleError(request, response, "Password must be at least 6 characters long.");
            return;
        }

        String orgId = generateOrgId(orgName);
        if (orgId == null) {
            handleError(request, response, "Invalid organization name. Please use only letters, numbers, and spaces.");
            return;
        }

        try {
            if (createOrganization(orgId, orgName, adminEmail, password)) {
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                forwardToSuccess(request, response, orgId);
            } else {
                handleError(request, response, "Failed to create organization. Please try again.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            log("Duplicate organization attempt: " + orgName, e);
            handleError(request, response, "Organization already exists. Try another name.");
        } catch (SQLException e) {
            log("Database error during organization creation", e);
            handleError(request, response, "Database error. Please try again.");
        } catch (Exception e) {
            log("Unexpected error during organization creation", e);
            handleError(request, response, "An unexpected error occurred. Please try again.");
        }
    }

    private boolean isValidInput(String orgName, String adminEmail, String password) {
        return orgName != null && !orgName.trim().isEmpty() &&
               adminEmail != null && !adminEmail.trim().isEmpty() &&
               password != null && !password.isEmpty();
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    private String generateOrgId(String orgName) {
        try {
            String baseSlug = orgName.toLowerCase(Locale.ROOT)
                                     .replaceAll("[^a-z0-9]+", "-")
                                     .replaceAll("(^-|-$)", "");
            
            if (baseSlug.isEmpty()) {
                return null;
            }
            
            String randomPart = UUID.randomUUID().toString().substring(0, 4).toLowerCase();
            return baseSlug + "-" + randomPart;
        } catch (Exception e) {
            log("Error generating org ID", e);
            return null;
        }
    }

    private boolean createOrganization(String orgId, String orgName, String adminEmail, String password)
            throws SQLException {
        
        try (Connection conn = DBConnection.getConnection()) {
            String hashedPassword = PasswordUtil.hash(password);
            String sql = """
                         INSERT INTO organizations
                           (org_id, org_name, admin_email, admin_pass_hash)
                         VALUES (?, ?, ?, ?)
                         """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, orgId);
            ps.setString(2, orgName);
            ps.setString(3, adminEmail);
            ps.setString(4, hashedPassword);
            
            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    private void forwardToSuccess(HttpServletRequest request, HttpServletResponse response, String orgId)
            throws ServletException, IOException {
        
        System.out.println("DEBUG: Entering forwardToSuccess method.");

        String ctx = request.getContextPath();
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String fullEmpUrl;
        
        if (serverPort == 80 || serverPort == 443) {
            fullEmpUrl = scheme + "://" + serverName + ctx + "/" + orgId + "/submit";
        } else {
            fullEmpUrl = scheme + "://" + serverName + ":" + serverPort + ctx + "/" + orgId + "/submit";
        }
        
        System.out.println("DEBUG: Generated Full Employee URL: " + fullEmpUrl);

        String redirectUrl = ctx + "/public/signup_success.html" +
                           "?org_id=" + java.net.URLEncoder.encode(orgId, "UTF-8") +
                           "&emp_url=" + java.net.URLEncoder.encode(fullEmpUrl, "UTF-8");

        System.out.println("DEBUG: Redirecting to: " + redirectUrl);
        
        response.sendRedirect(redirectUrl);
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
            response.sendRedirect(request.getContextPath() + "/public/signup.html?error=" +
                                java.net.URLEncoder.encode(message, "UTF-8"));
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            response.getWriter().write(message);
        }
    }
}