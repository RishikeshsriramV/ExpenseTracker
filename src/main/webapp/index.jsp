<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%
    boolean loggedIn = session.getAttribute("user") != null;
    response.sendRedirect(request.getContextPath() + (loggedIn ? "/dashboard" : "/login"));
%>
