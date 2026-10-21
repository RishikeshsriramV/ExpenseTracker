<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Register"/>
<%@ include file="includes/header.jspf" %>

<div class="card auth-card">
  <div class="card-body p-4">
    <h4 class="mb-3">Create account</h4>

    <c:if test="${not empty error}">
      <div class="alert alert-danger"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/register">
      <div class="mb-3">
        <label class="form-label" for="name">Name</label>
        <input class="form-control" type="text" id="name" name="name" maxlength="100" value="<c:out value='${name}'/>" required autofocus>
      </div>
      <div class="mb-3">
        <label class="form-label" for="email">Email</label>
        <input class="form-control" type="email" id="email" name="email" maxlength="150" value="<c:out value='${email}'/>" required>
      </div>
      <div class="mb-3">
        <label class="form-label" for="password">Password (min 6 characters)</label>
        <input class="form-control" type="password" id="password" name="password" minlength="6" required>
      </div>
      <div class="mb-3">
        <label class="form-label" for="confirmPassword">Confirm password</label>
        <input class="form-control" type="password" id="confirmPassword" name="confirmPassword" minlength="6" required>
      </div>
      <button class="btn btn-primary w-100" type="submit">Register</button>
    </form>
    <p class="text-center mt-3 mb-0">Already have an account? <a href="${pageContext.request.contextPath}/login">Login</a></p>
  </div>
</div>

<%@ include file="includes/footer.jspf" %>
