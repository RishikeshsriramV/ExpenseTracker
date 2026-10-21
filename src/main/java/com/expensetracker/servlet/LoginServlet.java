package com.expensetracker.servlet;

import com.expensetracker.dao.UserDAO;
import com.expensetracker.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email") == null ? "" : req.getParameter("email").trim().toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");

        String error = null;
        try {
            User user = userDAO.findByEmail(email);
            if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
                // new session after login (prevents session fixation)
                HttpSession old = req.getSession(false);
                if (old != null) {
                    old.invalidate();
                }
                HttpSession session = req.getSession(true);
                user.setPasswordHash(null); // never keep the hash in the session
                session.setAttribute("user", user);
                resp.sendRedirect(req.getContextPath() + "/dashboard");
                return;
            }
            error = "Invalid email or password.";
        } catch (SQLException e) {
            getServletContext().log("Login failed", e);
            error = "Database error. Check that MySQL is running and db.properties is correct.";
        }

        req.setAttribute("error", error);
        req.setAttribute("email", email);
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }
}
