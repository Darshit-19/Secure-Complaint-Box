package com.securecomplaintbox.filter;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

@WebFilter("/*")
public class OrgRoutingFilter implements Filter {

    private static final String[] SERVLET_URLS = {
        "registerOrg", "adminLogin", "verifyOtp", "submitComplaint",
        "viewComplaint", "updateStatus", "dashboard"
    };

    private static final String[] STATIC_EXTENSIONS = {
        ".jsp", ".css", ".js", ".png", ".jpg", ".jpeg", ".gif", ".ico",
        ".woff", ".woff2", ".ttf", ".eot", ".svg", ".pdf", ".xml", ".txt"
    };

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String ctxPath = request.getContextPath();
        String uri = request.getRequestURI();
        String path = uri.substring(ctxPath.length()); // drop /SecureComplaintBox

        if (path.equals("/") || path.isEmpty()) {
            chain.doFilter(req, res);
            return;
        }

        String[] parts = path.split("/");

        // Example: /adminLogin, /submitComplaint
        if (parts.length >= 2 && isServletUrl(parts[1])) {
            chain.doFilter(req, res);
            return;
        }

        // Static files: CSS/JS/JSP/IMG
        if (isStaticResource(path)) {
            chain.doFilter(req, res);
            return;
        }

        // ✅ Case 1: /{org-id}/action → handle /submit or /admin
        if (parts.length == 3) {
            String orgId = parts[1];
            String action = parts[2];

            if (isValidOrgId(orgId) && isValidAction(action)) {
                HttpSession session = request.getSession();
                session.setAttribute("org_id", orgId);
                System.out.println("DEBUG: OrgRoutingFilter set org_id: [" + orgId + "]");

                switch (action) {
                    case "submit":
                        // ✅ Serve the modern HTML form instead of JSP
                        request.getRequestDispatcher("/public/submit_complaint.html")
                               .forward(request, response);
                        return;

                    case "admin":
                        if (session.getAttribute("admin_email") != null) {
                            request.getRequestDispatcher("/public/dashboard.html").forward(request, response);
                        } else {
                            request.getRequestDispatcher("/public/admin_login.html").forward(request, response);
                        }
                        return;

                    default:
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid action: " + action);
                        return;
                }
            }
        }

        // ✅ Case 2: /{org-id} alone → Redirect to main index page
        if (parts.length == 2) {
            String orgId = parts[1];

            if (isValidOrgId(orgId)) {
                HttpSession session = request.getSession();
                session.setAttribute("org_id", orgId);
                System.out.println("DEBUG: OrgRoutingFilter set org_id: [" + orgId + "]");

                // Redirect to main index page instead of org home
                response.sendRedirect(ctxPath + "/public/index.html");
                return;
            }
        }

        // Default: Let other URLs pass through (may trigger 404 later)
        chain.doFilter(req, res);
    }

    private boolean isServletUrl(String path) {
        for (String servletUrl : SERVLET_URLS) {
            if (servletUrl.equals(path)) return true;
        }
        return false;
    }

    private boolean isStaticResource(String path) {
        String lowerPath = path.toLowerCase();
        for (String ext : STATIC_EXTENSIONS) {
            if (lowerPath.endsWith(ext)) return true;
        }
        return false;
    }

    private boolean isValidOrgId(String orgId) {
        if (orgId == null) return false;
        // List of reserved words
        String[] reserved = {"logout", "admin", "submit"};
        for (String word : reserved) {
            if (orgId.equals(word)) return false;
        }
        return orgId.matches("^[a-z0-9-]+$") && !orgId.contains(".");
    }

    private boolean isValidAction(String action) {
        return "submit".equals(action) || "admin".equals(action);
    }
}
