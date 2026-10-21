package com.expensetracker.servlet;

import com.expensetracker.dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = trim(req.getParameter("name"));
        String email = trim(req.getParameter("email")).toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String confirm = req.getParameter("confirmPassword") == null ? "" : req.getParameter("confirmPassword");

        String error = null;
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            error = "All fields are required.";
        } else if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            error = "Please enter a valid email address.";
        } else if (password.length() < 6) {
            error = "Password must be at least 6 characters.";
        } else if (!password.equals(confirm)) {
            error = "Passwords do not match.";
        }

        if (error == null) {
            try {
                String hash = BCrypt.hashpw(password, BCrypt.gensalt());
                if (userDAO.create(name, email, hash)) {
                    resp.sendRedirect(req.getContextPath() + "/login?registered=1");
                    return;
                }
                error = "This email is already registered. Try logging in.";
            } catch (SQLException e) {
                getServletContext().log("Register failed", e);
                error = "Database error. Check that MySQL is running and db.properties is correct.";
            }
        }

        req.setAttribute("error", error);
        req.setAttribute("name", name);
        req.setAttribute("email", email);
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
