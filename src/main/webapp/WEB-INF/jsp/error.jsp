<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - JSP Final Project</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            min-height: 100vh;
            display: flex;
            align-items: center;
        }
        .error-container {
            text-align: center;
        }
        .error-icon {
            font-size: 5rem;
            color: #dc3545;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-6">
                <div class="card border-0 shadow">
                    <div class="card-body p-5 error-container">
                        <i class="bi bi-exclamation-triangle error-icon"></i>
                        <h2 class="text-danger mt-3">Oops! Something went wrong</h2>
                        <p class="text-muted mt-3">
                            <%= request.getAttribute("error") != null ? request.getAttribute("error") : "An unexpected error occurred." %>
                        </p>
                        
                        <div class="mt-4">
                            <a href="<%= request.getContextPath() %>/" class="btn btn-primary me-2">
                                <i class="bi bi-house"></i> Go Home
                            </a>
                            <button onclick="history.back()" class="btn btn-secondary">
                                <i class="bi bi-arrow-left"></i> Go Back
                            </button>
                        </div>
                        
                        <hr class="my-4">
                        
                        <div class="text-start">
                            <h6>Technical Details:</h6>
                            <ul class="list-unstyled small text-muted">
                                <li><strong>Time:</strong> <%= new java.util.Date() %></li>
                                <li><strong>Request URI:</strong> <%= request.getRequestURI() %></li>
                                <% if (exception != null) { %>
                                <li><strong>Exception:</strong> <%= exception.getClass().getSimpleName() %></li>
                                <li><strong>Message:</strong> <%= exception.getMessage() %></li>
                                <% } %>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>