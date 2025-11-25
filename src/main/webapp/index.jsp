<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Check if user is already logged in
    if (session.getAttribute("user") != null) {
        // User is logged in, redirect to convert page
        response.sendRedirect(request.getContextPath() + "/submit.jsp");
    } else {
        // User not logged in, redirect to login page
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
%>
