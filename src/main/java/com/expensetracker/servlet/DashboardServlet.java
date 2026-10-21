package com.expensetracker.servlet;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        YearMonth now = YearMonth.now();
        LocalDate from = now.atDay(1);
        LocalDate to = now.plusMonths(1).atDay(1);

        try {
            BigDecimal monthTotal = expenseDAO.totalBetween(user.getId(), from, to);
            req.setAttribute("monthLabel", now.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)));
            req.setAttribute("monthTotal", monthTotal);
            req.setAttribute("allTotal", expenseDAO.totalAll(user.getId()));
            req.setAttribute("categoryTotals", expenseDAO.categoryTotals(user.getId(), from, to, monthTotal));
            req.setAttribute("recent", expenseDAO.recent(user.getId(), 5));
        } catch (SQLException e) {
            throw new ServletException("Could not load dashboard", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}
