<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Basic JSP Servlet Project</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
</head>
<body class="home-body">
    <div class="container home-container">
        <h1>Welcome to JSP Servlet Project!</h1>
        <p>This is a basic Maven project with JSP and Servlet integration.</p>
        
        <div>
            <a href="<%= request.getContextPath() %>/hello" class="btn home-btn">Go to Hello Servlet</a>
            <a href="<%= request.getContextPath() %>/users" class="btn home-btn">User Management</a>
        </div>
        
        <div class="info home-info">
            <h3>Project Information</h3>
            <p><strong>Context Path:</strong> <%= request.getContextPath() %></p>
            <p><strong>Server Info:</strong> <%= application.getServerInfo() %></p>
            <p><strong>JSP Version:</strong> <%= JspFactory.getDefaultFactory().getEngineInfo().getSpecificationVersion() %></p>
        </div>
        
        <div class="time home-time">
            <p>Current time: <%= new java.util.Date() %></p>
        </div>
    </div>
</body>
</html>