<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="isEdit" value="${not empty expense and expense.id > 0}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit Expense' : 'Add Expense'}"/>
<%@ include file="includes/header.jspf" %>

<div class="card auth-card" style="max-width: 560px; margin-top: 0;">
  <div class="card-body p-4">
    <h4 class="mb-3"><c:out value="${pageTitle}"/></h4>

    <c:if test="${not empty error}">
      <div class="alert alert-danger"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/expenses">
      <input type="hidden" name="action" value="save">
      <c:if test="${isEdit}">
        <input type="hidden" name="id" value="${expense.id}">
      </c:if>

      <div class="mb-3">
        <label class="form-label" for="title">Title</label>
        <input class="form-control" type="text" id="title" name="title" maxlength="150"
               value="<c:out value='${expense.title}'/>" required autofocus>
      </div>

      <div class="row">
        <div class="col-6 mb-3">
          <label class="form-label" for="amount">Amount (&#8377;)</label>
          <input class="form-control" type="number" id="amount" name="amount" step="0.01" min="0.01"
                 value="<c:out value='${expense.amount}'/>" required>
        </div>
        <div class="col-6 mb-3">
          <label class="form-label" for="expenseDate">Date</label>
          <input class="form-control" type="date" id="expenseDate" name="expenseDate"
                 value="${empty expense.expenseDate ? today : expense.expenseDate}" required>
        </div>
      </div>

      <div class="mb-3">
        <label class="form-label" for="category">Category</label>
        <select class="form-select" id="category" name="category" required>
          <option value="">Choose...</option>
          <c:forEach var="cat" items="${categories}">
            <option value="${cat}" ${cat == expense.category ? 'selected' : ''}>${cat}</option>
          </c:forEach>
        </select>
      </div>

      <div class="mb-3">
        <label class="form-label" for="notes">Notes (optional)</label>
        <textarea class="form-control" id="notes" name="notes" rows="2" maxlength="255"><c:out value="${expense.notes}"/></textarea>
      </div>

      <div class="d-flex gap-2">
        <button class="btn btn-primary" type="submit">Save</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/expenses">Cancel</a>
      </div>
    </form>
  </div>
</div>

<%@ include file="includes/footer.jspf" %>
