<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PDF Manager - Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .navbar {
            background-color: #2c3e50;
        }
        
        .navbar-brand {
            font-weight: 600;
            color: white !important;
        }
        
        .hero-section {
            padding: 80px 0;
            background: #007bff;
            color: white;
        }
        
        footer {
            background: #2c3e50;
            color: white;
            padding: 30px 0;
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="#">
                <i class="bi bi-file-pdf me-2"></i>PDF Manager
            </a>
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="#">Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">About</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">Contact</a>
                    </li>
                </ul>
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-light btn-sm me-2">
                            <i class="bi bi-box-arrow-in-right me-1"></i>Login
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-light btn-sm">
                            <i class="bi bi-person-plus me-1"></i>Register
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero-section">
        <div class="container">
            <div class="row">
                <div class="col-lg-8 mx-auto text-center">
                    <h1 class="display-4 fw-bold mb-4">PDF Manager</h1>
                    <p class="lead mb-4">
                        A simple and efficient document management system for your PDF files.
                        Upload, organize, and manage your documents with ease.
                    </p>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-light btn-lg">
                        <i class="bi bi-rocket-takeoff me-2"></i>Get Started
                    </a>
                </div>
            </div>
        </div>
    </section>

    <!-- About Section -->
    <section class="py-5 bg-light">
        <div class="container">
            <div class="row text-center">
                <div class="col-lg-8 mx-auto">
                    <h2 class="h2 fw-bold mb-3">About This Project</h2>
                    <p class="text-muted mb-4">
                        This is a Java-based web application built with JSP and Servlet technology. 
                        It demonstrates user authentication, role-based access control, and basic document management concepts.
                    </p>
                    <div class="row">
                        <div class="col-md-4">
                            <i class="bi bi-people display-4 text-primary mb-3"></i>
                            <h5>User Management</h5>
                            <p class="text-muted">Registration, login, and user profiles with admin controls.</p>
                        </div>
                        <div class="col-md-4">
                            <i class="bi bi-shield-check display-4 text-success mb-3"></i>
                            <h5>Security</h5>
                            <p class="text-muted">Secure authentication with password hashing and session management.</p>
                        </div>
                        <div class="col-md-4">
                            <i class="bi bi-gear display-4 text-info mb-3"></i>
                            <h5>Admin Panel</h5>
                            <p class="text-muted">Administrative interface for managing users and system settings.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer>
        <div class="container">
            <div class="row">
                <div class="col-lg-6">
                    <h5>PDF Manager</h5>
                    <p class="text-light">JSP Final Project - A simple document management system.</p>
                </div>
                <div class="col-lg-6 text-lg-end">
                    <p class="text-light">&copy; 2025 PDF Manager. All rights reserved.</p>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>