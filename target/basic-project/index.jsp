<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Basic JSP Servlet Project</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f8f9fa;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .container {
            background-color: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            text-align: center;
            max-width: 500px;
        }
        h1 {
            color: #007bff;
            margin-bottom: 20px;
        }
        p {
            color: #666;
            margin-bottom: 30px;
            font-size: 16px;
        }
        .btn {
            display: inline-block;
            background-color: #007bff;
            color: white;
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 5px;
            font-size: 16px;
            transition: background-color 0.3s;
            margin: 10px;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .info {
            background-color: #e9f7ff;
            padding: 20px;
            border-radius: 5px;
            margin-top: 30px;
            border-left: 4px solid #007bff;
        }
        .time {
            color: #999;
            font-size: 14px;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Welcome to JSP Servlet Project!</h1>
        <p>This is a basic Maven project with JSP and Servlet integration.</p>
        
        <div>
            <a href="<%= request.getContextPath() %>/hello" class="btn">Go to Hello Servlet</a>
        </div>
        
        <div class="info">
            <h3>Project Information</h3>
            <p><strong>Context Path:</strong> <%= request.getContextPath() %></p>
            <p><strong>Server Info:</strong> <%= application.getServerInfo() %></p>
            <p><strong>JSP Version:</strong> <%= JspFactory.getDefaultFactory().getEngineInfo().getSpecificationVersion() %></p>
        </div>
        
        <div class="time">
            <p>Current time: <%= new java.util.Date() %></p>
        </div>
    </div>
</body>
</html>