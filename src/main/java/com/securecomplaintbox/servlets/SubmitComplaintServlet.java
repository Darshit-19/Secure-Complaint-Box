package com.securecomplaintbox.servlets;

import java.io.IOException;
import java.sql.*;
import java.security.GeneralSecurityException;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.securecomplaintbox.util.AESUtil;
import com.securecomplaintbox.util.DBConnection;

@WebServlet("/submitComplaint")
public class SubmitComplaintServlet extends HttpServlet {

    
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validate input parameters
        String type = request.getParameter("type");
        String description = request.getParameter("description");
        String orgId = request.getParameter("org_id");
        if (orgId != null) orgId = orgId.trim();
        String contactInfo = request.getParameter("contact_info");

        
        if (!isValidInput(type, description, orgId)) {
            handleError(request, response, "All fields are required.");
            return;
        }

        // Clean and validate data
        type = type.trim();
        description = description.trim();
        orgId = orgId.trim();

        if (description.length() < 10) {
            handleError(request, response, "Description must be at least 10 characters long.");
            return;
        }

        // Generate reference code
        String refCode = generateReferenceCode();

        try {
            if (submitComplaint(orgId, refCode, type, description,contactInfo)) {
                handleSuccess(request, response, refCode);
            } else {
                handleError(request, response, "Invalid Organization ID. Please check your organization ID and try again.");
            }
        } catch (SQLException e) {
            log("Database error during complaint submission", e);
            handleError(request, response, "Database error. Please try again.");
        } catch (GeneralSecurityException e) {
            log("Encryption error during complaint submission", e);
            handleError(request, response, "Security error. Please try again.");
        } catch (Exception e) {
            log("Unexpected error during complaint submission", e);
            handleError(request, response, "An unexpected error occurred. Please try again.");
        }
    }

    private boolean isValidInput(String type, String description, String orgId) {
        return type != null && !type.trim().isEmpty() &&
               description != null && !description.trim().isEmpty() &&
               orgId != null && !orgId.trim().isEmpty();
    }

    private String generateReferenceCode() {
        return "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean submitComplaint(String orgId, String refCode, String type, String description ,String contactInfo) 
            throws SQLException, GeneralSecurityException {
        
        try (Connection conn = DBConnection.getConnection()) {
            // First, verify that the organization exists
            if (!isValidOrganization(conn, orgId)) {
                return false;
            }
            
            // Encrypt the complaint text
        	String encDesc    = AESUtil.encrypt(description);
        	String encContact = (contactInfo == null || contactInfo.isBlank())
        	                    ? null
        	                    : AESUtil.encrypt(contactInfo.trim());

            // Store encrypted complaint in MySQL
            String sql = """
                    INSERT INTO complaints
                          (org_id, reference_code, type,
                           description_enc, contact_enc)   -- 🔄 here
                    VALUES (?, ?, ?, ?, ?)
                    """;

            	PreparedStatement ps = conn.prepareStatement(sql);
            	ps.setString(1, orgId);
            	ps.setString(2, refCode);
            	ps.setString(3, type);
            	ps.setString(4, encDesc);
            	ps.setString(5, encContact);         

            	int result = ps.executeUpdate();

            return result > 0;
        }
    }
    
    private boolean isValidOrganization(Connection conn, String orgId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM organizations WHERE org_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orgId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private void handleSuccess(HttpServletRequest request, HttpServletResponse response, String refCode) 
            throws IOException {
        response.setContentType("text/plain");
        response.getWriter().write(refCode);  
    }


    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("text/plain");
        response.getWriter().write(message);  // ✅ Display this in JS error toast
    }

}
