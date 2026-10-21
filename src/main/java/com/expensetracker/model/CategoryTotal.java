package com.expensetracker.model;

import java.math.BigDecimal;

public class CategoryTotal {
    private final String category;
    private final BigDecimal total;
    private final int percent;

    public CategoryTotal(String category, BigDecimal total, int percent) {
        this.category = category;
        this.total = total;
        this.percent = percent;
    }

    public String getCategory() { return category; }
    public BigDecimal getTotal() { return total; }
    public int getPercent() { return percent; }
}
