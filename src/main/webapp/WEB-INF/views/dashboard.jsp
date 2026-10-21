<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Dashboard"/>
<%@ include file="includes/header.jspf" %>

<div class="d-flex justify-content-between align-items-center mb-4">
  <h3 class="mb-0">Dashboard</h3>
  <a class="btn btn-primary" href="${pageContext.request.contextPath}/expenses?action=new">+ Add Expense</a>
</div>

<div class="row g-3 mb-4">
  <div class="col-md-6">
    <div class="card stat-card"><div class="card-body">
      <div class="text-muted">Spent in <c:out value="${monthLabel}"/></div>
      <div class="stat-value text-primary amount">&#8377; <fmt:formatNumber value="${monthTotal}" minFractionDigits="2" maxFractionDigits="2"/></div>
    </div></div>
  </div>
  <div class="col-md-6">
    <div class="card stat-card"><div class="card-body">
      <div class="text-muted">All-time total</div>
      <div class="stat-value amount">&#8377; <fmt:formatNumber value="${allTotal}" minFractionDigits="2" maxFractionDigits="2"/></div>
    </div></div>
  </div>
</div>

<div class="row g-3">
  <div class="col-lg-5">
    <div class="card stat-card h-100"><div class="card-body">
      <h5 class="mb-3">This month by category</h5>
      <c:choose>
        <c:when test="${empty categoryTotals}">
          <p class="text-muted mb-0">No expenses this month yet.</p>
        </c:when>
        <c:otherwise>
          <c:forEach var="ct" items="${categoryTotals}">
            <div class="d-flex justify-content-between small">
              <span><c:out value="${ct.category}"/></span>
              <span class="amount">&#8377; <fmt:formatNumber value="${ct.total}" minFractionDigits="2" maxFractionDigits="2"/> (${ct.percent}%)</span>
            </div>
            <div class="progress mb-3" style="height: 8px;">
              <div class="progress-bar" role="progressbar" style="width: ${ct.percent}%"></div>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div></div>
  </div>

  <div class="col-lg-7">
    <div class="card stat-card h-100"><div class="card-body">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="mb-0">Recent expenses</h5>
        <a href="${pageContext.request.contextPath}/expenses?month=">View all</a>
      </div>
      <c:choose>
        <c:when test="${empty recent}">
          <p class="text-muted mb-0">Nothing here yet. Add your first expense!</p>
        </c:when>
        <c:otherwise>
          <table class="table table-sm mb-0">
            <thead><tr><th>Date</th><th>Title</th><th>Category</th><th class="text-end">Amount</th></tr></thead>
            <tbody>
              <c:forEach var="e" items="${recent}">
                <tr>
                  <td>${e.expenseDate}</td>
                  <td><c:out value="${e.title}"/></td>
                  <td><span class="badge text-bg-secondary"><c:out value="${e.category}"/></span></td>
                  <td class="text-end amount">&#8377; <fmt:formatNumber value="${e.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </c:otherwise>
      </c:choose>
    </div></div>
  </div>
</div>

<%@ include file="includes/footer.jspf" %>
