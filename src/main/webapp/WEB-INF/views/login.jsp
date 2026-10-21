<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login"/>
<%@ include file="includes/header.jspf" %>

<div class="card auth-card">
  <div class="card-body p-4">
    <h4 class="mb-3">Login</h4>

    <c:if test="${param.registered == '1'}">
      <div class="alert alert-success">Account created. Please log in.</div>
    </c:if>
    <c:if test="${not empty error}">
      <div class="alert alert-danger"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
      <div class="mb-3">
        <label class="form-label" for="email">Email</label>
        <input class="form-control" type="email" id="email" name="email" value="<c:out value='${email}'/>" required autofocus>
      </div>
      <div class="mb-3">
        <label class="form-label" for="password">Password</label>
        <input class="form-control" type="password" id="password" name="password" required>
      </div>
      <button class="btn btn-primary w-100" type="submit">Login</button>
    </form>
    <p class="text-center mt-3 mb-0">New here? <a href="${pageContext.request.contextPath}/register">Create an account</a></p>
  </div>
</div>

<%@ include file="includes/footer.jspf" %>
