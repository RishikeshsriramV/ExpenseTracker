package com.expensetracker.dao;

import com.expensetracker.model.CategoryTotal;
import com.expensetracker.model.Expense;
import com.expensetracker.util.DBUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    private static final String COLUMNS = "id, user_id, title, amount, category, expense_date, notes";

    public void add(Expense e) throws SQLException {
        String sql = "INSERT INTO expenses (user_id, title, amount, category, expense_date, notes) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getUserId());
            ps.setString(2, e.getTitle());
            ps.setBigDecimal(3, e.getAmount());
            ps.setString(4, e.getCategory());
            ps.setDate(5, Date.valueOf(e.getExpenseDate()));
            ps.setString(6, e.getNotes());
            ps.executeUpdate();
        }
    }

    public boolean update(Expense e) throws SQLException {
        String sql = "UPDATE expenses SET title=?, amount=?, category=?, expense_date=?, notes=? WHERE id=? AND user_id=?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getTitle());
            ps.setBigDecimal(2, e.getAmount());
            ps.setString(3, e.getCategory());
            ps.setDate(4, Date.valueOf(e.getExpenseDate()));
            ps.setString(5, e.getNotes());
            ps.setInt(6, e.getId());
            ps.setInt(7, e.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id, int userId) throws SQLException {
        String sql = "DELETE FROM expenses WHERE id=? AND user_id=?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public Expense findById(int id, int userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM expenses WHERE id=? AND user_id=?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** month and category are optional filters (null / blank = no filter). */
    public List<Expense> findFiltered(int userId, YearMonth month, String category) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNS + " FROM expenses WHERE user_id=?");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        if (month != null) {
            sql.append(" AND expense_date >= ? AND expense_date < ?");
            params.add(Date.valueOf(month.atDay(1)));
            params.add(Date.valueOf(month.plusMonths(1).atDay(1)));
        }
        if (category != null && !category.isBlank()) {
            sql.append(" AND category=?");
            params.add(category);
        }
        sql.append(" ORDER BY expense_date DESC, id DESC");

        List<Expense> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Expense> recent(int userId, int limit) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM expenses WHERE user_id=? ORDER BY expense_date DESC, id DESC LIMIT ?";
        List<Expense> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public BigDecimal totalAll(int userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM expenses WHERE user_id=?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        }
    }

    public BigDecimal totalBetween(int userId, LocalDate from, LocalDate toExclusive) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM expenses WHERE user_id=? AND expense_date >= ? AND expense_date < ?";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(toExclusive));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        }
    }

    public List<CategoryTotal> categoryTotals(int userId, LocalDate from, LocalDate toExclusive, BigDecimal grandTotal)
            throws SQLException {
        String sql = "SELECT category, SUM(amount) AS total FROM expenses "
                + "WHERE user_id=? AND expense_date >= ? AND expense_date < ? "
                + "GROUP BY category ORDER BY total DESC";
        List<CategoryTotal> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(toExclusive));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    int percent = 0;
                    if (grandTotal.signum() > 0) {
                        percent = total.multiply(BigDecimal.valueOf(100))
                                .divide(grandTotal, 0, RoundingMode.HALF_UP).intValue();
                    }
                    list.add(new CategoryTotal(rs.getString("category"), total, percent));
                }
            }
        }
        return list;
    }

    private Expense map(ResultSet rs) throws SQLException {
        Expense e = new Expense();
        e.setId(rs.getInt("id"));
        e.setUserId(rs.getInt("user_id"));
        e.setTitle(rs.getString("title"));
        e.setAmount(rs.getBigDecimal("amount"));
        e.setCategory(rs.getString("category"));
        e.setExpenseDate(rs.getDate("expense_date").toLocalDate());
        e.setNotes(rs.getString("notes"));
        return e;
    }
}
