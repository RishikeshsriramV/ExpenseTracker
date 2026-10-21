package com.expensetracker.servlet;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.util.Categories;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * GET  /expenses                      -> list (filters: month, category)
 * GET  /expenses?action=new           -> empty form
 * GET  /expenses?action=edit&id=5     -> form filled with expense 5
 * POST /expenses (action=save)        -> insert or update
 * POST /expenses (action=delete)      -> delete
 */
@WebServlet("/expenses")
public class ExpenseServlet extends HttpServlet {
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("99999999.99");
    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        String action = req.getParameter("action");

        try {
            if ("new".equals(action)) {
                showForm(req, resp, null);
            } else if ("edit".equals(action)) {
                Expense expense = expenseDAO.findById(parseInt(req.getParameter("id")), user.getId());
                if (expense == null) {
                    resp.sendRedirect(req.getContextPath() + "/expenses");
                    return;
                }
                showForm(req, resp, expense);
            } else {
                showList(req, resp, user);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        String action = req.getParameter("action");
        try {
            if ("delete".equals(action)) {
                expenseDAO.delete(parseInt(req.getParameter("id")), user.getId());
                String month = req.getParameter("month") == null ? "" : req.getParameter("month");
                String category = req.getParameter("category") == null ? "" : req.getParameter("category");
                resp.sendRedirect(req.getContextPath() + "/expenses?month=" + enc(month)
                        + "&category=" + enc(category) + "&msg=deleted");
            } else if ("save".equals(action)) {
                save(req, resp, user);
            } else {
                resp.sendRedirect(req.getContextPath() + "/expenses");
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, User user)
            throws SQLException, ServletException, IOException {
        Expense e = new Expense();
        e.setId(parseInt(req.getParameter("id")));
        e.setUserId(user.getId());
        e.setTitle(nullToEmpty(req.getParameter("title")).trim());
        e.setCategory(nullToEmpty(req.getParameter("category")));
        String notes = nullToEmpty(req.getParameter("notes")).trim();
        e.setNotes(notes.isEmpty() ? null : notes);

        String error = null;

        try {
            e.setAmount(new BigDecimal(nullToEmpty(req.getParameter("amount")).trim()).setScale(2, RoundingMode.HALF_UP));
        } catch (NumberFormatException ex) {
            error = "Please enter a valid amount.";
        }
        try {
            e.setExpenseDate(LocalDate.parse(nullToEmpty(req.getParameter("expenseDate"))));
        } catch (DateTimeParseException ex) {
            if (error == null) {
                error = "Please choose a valid date.";
            }
        }

        if (error == null) {
            if (e.getTitle().isEmpty() || e.getTitle().length() > 150) {
                error = "Title is required (max 150 characters).";
            } else if (e.getAmount().signum() <= 0 || e.getAmount().compareTo(MAX_AMOUNT) > 0) {
                error = "Amount must be greater than 0.";
            } else if (!Categories.ALL.contains(e.getCategory())) {
                error = "Please choose a category.";
            } else if (notes.length() > 255) {
                error = "Notes can be at most 255 characters.";
            }
        }

        if (error != null) {
            req.setAttribute("error", error);
            showForm(req, resp, e);
            return;
        }

        if (e.getId() > 0) {
            expenseDAO.update(e);
        } else {
            expenseDAO.add(e);
        }
        String month = e.getExpenseDate().toString().substring(0, 7);
        resp.sendRedirect(req.getContextPath() + "/expenses?month=" + month + "&msg=saved");
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp, User user)
            throws SQLException, ServletException, IOException {
        // no "month" parameter = current month, empty "month" = all time
        String monthParam = req.getParameter("month");
        if (monthParam == null) {
            monthParam = YearMonth.now().toString();
        }
        YearMonth month = null;
        if (!monthParam.isBlank()) {
            try {
                month = YearMonth.parse(monthParam);
            } catch (DateTimeParseException ex) {
                monthParam = "";
            }
        }
        String category = nullToEmpty(req.getParameter("category"));
        if (!category.isEmpty() && !Categories.ALL.contains(category)) {
            category = "";
        }

        List<Expense> expenses = expenseDAO.findFiltered(user.getId(), month, category);
        BigDecimal total = BigDecimal.ZERO;
        for (Expense ex : expenses) {
            total = total.add(ex.getAmount());
        }

        req.setAttribute("expenses", expenses);
        req.setAttribute("total", total);
        req.setAttribute("month", monthParam);
        req.setAttribute("category", category);
        req.setAttribute("categories", Categories.ALL);
        req.getRequestDispatcher("/WEB-INF/views/expenses.jsp").forward(req, resp);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Expense expense)
            throws ServletException, IOException {
        req.setAttribute("expense", expense);
        req.setAttribute("categories", Categories.ALL);
        req.setAttribute("today", LocalDate.now().toString());
        req.getRequestDispatcher("/WEB-INF/views/expense-form.jsp").forward(req, resp);
    }

    private static User currentUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("user");
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
