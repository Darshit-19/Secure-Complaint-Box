package com.securecomplaintbox.servlets;

import java.io.IOException;
import java.sql.*;
import java.security.GeneralSecurityException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.securecomplaintbox.util.DBConnection;
import com.securecomplaintbox.util.AESUtil;

@WebServlet("/viewComplaint")
public class ViewComplaintServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		String orgId = (session != null) ? (String) session.getAttribute("org_id") : null;

		if (orgId == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Not logged in\"}");
			return;
		}

		String ref = request.getParameter("ref");
		if (!isValidReference(ref)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Invalid reference code\"}");
			return;
		}

		try (Connection conn = DBConnection.getConnection()) {
			String sql = """
					SELECT type, description_enc, status, created_at, contact_enc
					FROM complaints
					WHERE org_id = ? AND reference_code = ?
					""";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, orgId);
				ps.setString(2, ref.trim());
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						String desc = AESUtil.decrypt(rs.getString("description_enc"));
						String contact = null;
						String encContact = rs.getString("contact_enc");
						if (encContact != null) {
							contact = AESUtil.decrypt(encContact);
						}
						String type = rs.getString("type");
						String status = rs.getString("status");
						String date = rs.getTimestamp("created_at").toString();
						response.setContentType("application/json");
						response.setCharacterEncoding("UTF-8");
						String json = String.format(
							"{\"ref\":\"%s\",\"type\":\"%s\",\"status\":\"%s\",\"date\":\"%s\",\"desc\":\"%s\",\"contact_info\":\"%s\"}",
							escapeJson(ref), escapeJson(type), escapeJson(status), escapeJson(date), escapeJson(desc), contact == null ? "" : escapeJson(contact)
						);
						response.getWriter().write(json);
						return;
					}
				}
			}
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Complaint not found\"}");
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Server error\"}");
		}
	}

	private boolean isValidReference(String ref) {
		return ref != null && !ref.trim().isEmpty();
	}

	private boolean viewComplaint(HttpServletRequest request, HttpServletResponse response, String orgId, String ref)
			throws SQLException, GeneralSecurityException {

		try (Connection conn = DBConnection.getConnection()) {
			String sql = """
					SELECT type,
					       description_enc,
					       status,
					       created_at,
					       contact_enc            -- 🔄 was contact_info
					FROM complaints
					WHERE org_id = ? AND reference_code = ?
					""";

			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, orgId);
				ps.setString(2, ref.trim());

				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {

						// decrypt description
						String desc = AESUtil.decrypt(rs.getString("description_enc"));

						// decrypt contact (may be null)
						String contact = null;
						String encContact = rs.getString("contact_enc");
						if (encContact != null) {
							contact = AESUtil.decrypt(encContact);
						}

						// forward to JSP
						request.setAttribute("ref", ref);
						request.setAttribute("type", rs.getString("type"));
						request.setAttribute("status", rs.getString("status"));
						request.setAttribute("date", rs.getTimestamp("created_at"));
						request.setAttribute("desc", desc);
						request.setAttribute("contact_info", contact); // 👈 JSP expects this key

						return true;
					}
				}
			}
		}
		return false;
	}

	private void handleError(HttpServletRequest request, HttpServletResponse response, String message)
			throws ServletException, IOException {
		request.setAttribute("msg", message);
		request.getRequestDispatcher("public/dashboard.html").forward(request, response);
	}

	private String escapeJson(String s) {
		if (s == null) return "";
		return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
	}
}
