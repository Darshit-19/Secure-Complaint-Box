package com.securecomplaintbox.servlets;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.securecomplaintbox.util.DBConnection;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin_email") == null || session.getAttribute("otp") != null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Not authenticated\"}");
            return;
        }

        String orgId = (String) session.getAttribute("org_id");
        if (orgId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Organization ID not found\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT reference_code, type, status, created_at " +
                        "FROM complaints WHERE org_id=? ORDER BY created_at DESC";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, orgId);
                
                try (ResultSet rs = ps.executeQuery()) {
                    StringBuilder json = new StringBuilder();
                    json.append("{\"org_id\":\"").append(escapeJson(orgId)).append("\",\"complaints\":[");
                    
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) json.append(",");
                        json.append("{");
                        json.append("\"ref\":\"").append(escapeJson(rs.getString("reference_code"))).append("\",");
                        json.append("\"type\":\"").append(escapeJson(rs.getString("type"))).append("\",");
                        json.append("\"status\":\"").append(escapeJson(rs.getString("status"))).append("\",");
                        json.append("\"date\":\"").append(escapeJson(rs.getTimestamp("created_at").toString())).append("\"");
                        json.append("}");
                        first = false;
                    }
                    
                    json.append("]}");
                    
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(json.toString());
                }
            }
        } catch (SQLException e) {
            log("Database error in DashboardServlet", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Database error\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
} 