<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    // Check if user is logged in
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    model.bean.User currentUser = (model.bean.User) session.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'Dashboard'} - JSP Project</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/styles.css" rel="stylesheet">
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
                <i class="bi bi-speedometer2 me-2"></i>Dashboard
            </a>
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/dashboard">
                            <i class="bi bi-house-door me-1"></i>Home
                        </a>
                    </li>
                    <% if (currentUser.isAdmin()) { %>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">
                            <i class="bi bi-people me-1"></i>User Management
                        </a>
                    </li>
                    <% } %>
                </ul>
                
                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" 
                           data-bs-toggle="dropdown">
                            <i class="bi bi-person-circle me-1"></i>
                            <%= currentUser.getDisplayName() %>
                            <span class="badge bg-<%= currentUser.isAdmin() ? "danger" : "primary" %> ms-1">
                                <%= currentUser.getRole().toUpperCase() %>
                            </span>
                        </a>
                        <ul class="dropdown-menu">
                            <li><h6 class="dropdown-header">Account</h6></li>
                            <li><a class="dropdown-item" href="#">
                                <i class="bi bi-person me-2"></i>Profile
                            </a></li>
                            <li><a class="dropdown-item" href="#">
                                <i class="bi bi-gear me-2"></i>Settings
                            </a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                <i class="bi bi-box-arrow-right me-2"></i>Logout
                            </a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container mt-4">
        <!-- Welcome Section -->
        <div class="row mb-4">
            <div class="col">
                <div class="card bg-primary text-white">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-grow-1">
                                <h2 class="card-title mb-1">
                                    <i class="bi bi-person-circle me-2"></i>
                                    Welcome, <%= currentUser.getDisplayName() %>!
                                </h2>
                                <p class="card-text mb-0">
                                    Role: <strong><%= currentUser.getRole().toUpperCase() %></strong> | 
                                    Last Login: <fmt:formatDate value="<%= new java.util.Date() %>" pattern="MMM dd, yyyy 'at' HH:mm" />
                                </p>
                            </div>
                            <div>
                                <i class="bi bi-speedometer2 display-4 opacity-50"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="row mb-4">
            <div class="col-md-12">
                <h3 class="mb-3">
                    <i class="bi bi-lightning-charge me-2"></i>Quick Actions
                </h3>
            </div>
            
            <% if (currentUser.isAdmin()) { %>
            <!-- Admin Actions -->
            <div class="col-md-4 mb-3">
                <div class="card h-100 border-primary">
                    <div class="card-body text-center">
                        <i class="bi bi-people-fill display-4 text-primary mb-3"></i>
                        <h5 class="card-title">Manage Users</h5>
                        <p class="card-text">View, add, edit, and delete user accounts</p>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">
                            <i class="bi bi-arrow-right me-1"></i>Go to Users
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4 mb-3">
                <div class="card h-100 border-info">
                    <div class="card-body text-center">
                        <i class="bi bi-bar-chart-fill display-4 text-info mb-3"></i>
                        <h5 class="card-title">System Reports</h5>
                        <p class="card-text">View system statistics and reports</p>
                        <a href="#" class="btn btn-info">
                            <i class="bi bi-arrow-right me-1"></i>View Reports
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4 mb-3">
                <div class="card h-100 border-warning">
                    <div class="card-body text-center">
                        <i class="bi bi-gear-fill display-4 text-warning mb-3"></i>
                        <h5 class="card-title">System Settings</h5>
                        <p class="card-text">Configure system preferences</p>
                        <a href="#" class="btn btn-warning">
                            <i class="bi bi-arrow-right me-1"></i>Settings
                        </a>
                    </div>
                </div>
            </div>
            <% } else { %>
            <!-- User Actions -->
            <div class="col-md-4 mb-3">
                <div class="card h-100 border-success">
                    <div class="card-body text-center">
                        <i class="bi bi-person-fill display-4 text-success mb-3"></i>
                        <h5 class="card-title">My Profile</h5>
                        <p class="card-text">View and update your personal information</p>
                        <a href="#" class="btn btn-success">
                            <i class="bi bi-arrow-right me-1"></i>View Profile
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4 mb-3">
                <div class="card h-100 border-info">
                    <div class="card-body text-center">
                        <i class="bi bi-clock-history display-4 text-info mb-3"></i>
                        <h5 class="card-title">Activity History</h5>
                        <p class="card-text">View your recent activity and login history</p>
                        <a href="#" class="btn btn-info">
                            <i class="bi bi-arrow-right me-1"></i>View History
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4 mb-3">
                <div class="card h-100 border-warning">
                    <div class="card-body text-center">
                        <i class="bi bi-gear-fill display-4 text-warning mb-3"></i>
                        <h5 class="card-title">Preferences</h5>
                        <p class="card-text">Manage your account preferences and settings</p>
                        <a href="#" class="btn btn-warning">
                            <i class="bi bi-arrow-right me-1"></i>Preferences
                        </a>
                    </div>
                </div>
            </div>
            <% } %>
        </div>

        <!-- Account Information -->
        <div class="row">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-info-circle me-2"></i>Account Information
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <strong>Username:</strong>
                                <p class="mb-2"><%= currentUser.getUsername() %></p>
                                
                                <strong>Email:</strong>
                                <p class="mb-2"><%= currentUser.getEmail() %></p>
                            </div>
                            <div class="col-md-6">
                                <strong>Role:</strong>
                                <p class="mb-2">
                                    <span class="badge bg-<%= currentUser.isAdmin() ? "danger" : "primary" %>">
                                        <%= currentUser.getRole().toUpperCase() %>
                                    </span>
                                </p>
                                
                                <strong>Member Since:</strong>
                                <p class="mb-2">
                                    <fmt:formatDate value="<%= currentUser.getCreatedAt() %>" pattern="MMM dd, yyyy" />
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-shield-check me-2"></i>Security
                        </h5>
                    </div>
                    <div class="card-body">
                        <p class="text-muted small">Your session is secure</p>
                        <div class="d-grid">
                            <a href="#" class="btn btn-outline-primary btn-sm mb-2">
                                <i class="bi bi-key me-1"></i>Change Password
                            </a>
                            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger btn-sm">
                                <i class="bi bi-box-arrow-right me-1"></i>Logout
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="mt-5 py-4 bg-light text-center text-muted">
        <div class="container">
            <p>&copy; 2025 JSP Final Project. All rights reserved.</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>