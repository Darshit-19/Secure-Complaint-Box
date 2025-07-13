package com.securecomplaintbox.servlets;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.securecomplaintbox.util.DBConnection;

@WebServlet("/trackComplaint")
public class TrackComplaintServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String orgId = req.getParameter("org_id");
        if (orgId != null) orgId = orgId.trim();
        String ref   = req.getParameter("ref");

        // Validate input
        if (orgId == null || orgId.isBlank() || ref == null || ref.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Please enter both Organization ID and Reference Number.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                         SELECT type, status, created_at
                         FROM complaints
                         WHERE org_id = ? AND reference_code = ?
                         """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, orgId);
            ps.setString(2, ref.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String type = rs.getString("type");
                String status = rs.getString("status");
                String date = rs.getTimestamp("created_at").toString();
                resp.getWriter().write(String.format("SUCCESS|%s|%s|%s|%s", ref.trim(), type, status, date));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("No complaint found for that reference number.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Server error. Please try again later.");
        }
    }
}
