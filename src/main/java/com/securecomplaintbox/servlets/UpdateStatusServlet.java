package com.securecomplaintbox.servlets;

import java.io.IOException;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.securecomplaintbox.util.DBConnection;

@WebServlet("/updateStatus")
public class UpdateStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validate session and org_id
        HttpSession session = request.getSession(false);
        String orgId = (session != null) ? (String) session.getAttribute("org_id") : null;

        if (orgId == null) {
            response.sendRedirect("public/admin_login.html");
            return;
        }

        // Validate reference parameter
        String ref = request.getParameter("ref");
        if (!isValidReference(ref)) {
            handleError(request, response, "Invalid reference code.");
            return;
        }

        try {
            if (updateComplaintStatus(orgId, ref)) {
                response.sendRedirect(request.getContextPath() + "/public/dashboard.html");
            } else {
                handleError(request, response, "Complaint not found or status update failed.");
            }
        } catch (SQLException e) {
            log("Database error during status update", e);
            handleError(request, response, "Database error. Please try again.");
        } catch (Exception e) {
            log("Unexpected error during status update", e);
            handleError(request, response, "An unexpected error occurred. Please try again.");
        }
    }

    private boolean isValidReference(String ref) {
        return ref != null && !ref.trim().isEmpty();
    }

    private boolean updateComplaintStatus(String orgId, String ref) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                         UPDATE complaints
                         SET status = CASE
                                        WHEN status = 'pending'  THEN 'resolved'
                                        WHEN status = 'resolved' THEN 'pending'
                                        ELSE 'pending'
                                      END
                         WHERE org_id = ? AND reference_code = ?
                         """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, orgId);
            ps.setString(2, ref.trim());
            
            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        request.setAttribute("msg", message);
        request.getRequestDispatcher("public/dashboard.html").forward(request, response);
    }
}
