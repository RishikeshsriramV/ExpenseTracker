<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Expenses"/>
<%@ include file="includes/header.jspf" %>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h3 class="mb-0">Expenses</h3>
  <a class="btn btn-primary" href="${pageContext.request.contextPath}/expenses?action=new">+ Add Expense</a>
</div>

<c:if test="${param.msg == 'saved'}"><div class="alert alert-success">Expense saved.</div></c:if>
<c:if test="${param.msg == 'deleted'}"><div class="alert alert-warning">Expense deleted.</div></c:if>

<form class="row g-2 align-items-end mb-3" method="get" action="${pageContext.request.contextPath}/expenses">
  <div class="col-sm-4 col-md-3">
    <label class="form-label mb-1" for="month">Month</label>
    <input class="form-control" type="month" id="month" name="month" value="<c:out value='${month}'/>">
  </div>
  <div class="col-sm-4 col-md-3">
    <label class="form-label mb-1" for="category">Category</label>
    <select class="form-select" id="category" name="category">
      <option value="">All</option>
      <c:forEach var="cat" items="${categories}">
        <option value="${cat}" ${cat == category ? 'selected' : ''}>${cat}</option>
      </c:forEach>
    </select>
  </div>
  <div class="col-auto">
    <button class="btn btn-outline-primary" type="submit">Filter</button>
    <a class="btn btn-link" href="${pageContext.request.contextPath}/expenses?month=">Show all</a>
  </div>
</form>

<div class="card stat-card">
  <div class="card-body">
    <c:choose>
      <c:when test="${empty expenses}">
        <p class="text-muted mb-0">No expenses found for this filter.</p>
      </c:when>
      <c:otherwise>
        <div class="table-responsive">
          <table class="table table-hover mb-0">
            <thead>
              <tr><th>Date</th><th>Title</th><th>Category</th><th>Notes</th><th class="text-end">Amount</th><th class="text-end">Actions</th></tr>
            </thead>
            <tbody>
              <c:forEach var="e" items="${expenses}">
                <tr>
                  <td>${e.expenseDate}</td>
                  <td><c:out value="${e.title}"/></td>
                  <td><span class="badge text-bg-secondary"><c:out value="${e.category}"/></span></td>
                  <td class="text-muted small"><c:out value="${e.notes}"/></td>
                  <td class="text-end amount">&#8377; <fmt:formatNumber value="${e.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                  <td class="text-end">
                    <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/expenses?action=edit&id=${e.id}">Edit</a>
                    <form class="d-inline" method="post" action="${pageContext.request.contextPath}/expenses"
                          onsubmit="return confirm('Delete this expense?');">
                      <input type="hidden" name="action" value="delete">
                      <input type="hidden" name="id" value="${e.id}">
                      <input type="hidden" name="month" value="<c:out value='${month}'/>">
                      <input type="hidden" name="category" value="<c:out value='${category}'/>">
                      <button class="btn btn-sm btn-outline-danger" type="submit">Delete</button>
                    </form>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
            <tfoot>
              <tr>
                <th colspan="4" class="text-end">Total</th>
                <th class="text-end amount">&#8377; <fmt:formatNumber value="${total}" minFractionDigits="2" maxFractionDigits="2"/></th>
                <th></th>
              </tr>
            </tfoot>
          </table>
        </div>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<%@ include file="includes/footer.jspf" %>
