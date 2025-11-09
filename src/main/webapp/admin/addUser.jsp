<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add User - Admin Panel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="bg-light">
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard.jsp">
                <i class="bi bi-shield-check"></i> Admin Panel
            </a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">
                    <i class="bi bi-arrow-left"></i> Back to Users
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                    <i class="bi bi-box-arrow-right"></i> Logout
                </a>
            </div>
        </div>
    </nav>

    <div class="container my-5">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="form-container">
                    <!-- Header -->
                    <div class="header-section add-user-header text-center">
                        <h1 class="h2 mb-0">
                            <i class="bi bi-person-plus display-6 text-primary"></i>
                        </h1>
                        <h2 class="h3 mt-3 mb-0">Add New User</h2>
                        <p class="mb-0 text-muted">Fill in the information to create a new user account</p>
                    </div>

                    <!-- Form Content -->
                    <div class="p-4">
                        <!-- Error Messages -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="bi bi-exclamation-triangle"></i> 
                                <strong>Error!</strong> ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <!-- Success Messages -->
                        <c:if test="${not empty message}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="bi bi-check-circle"></i> ${message}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <!-- Add User Form -->
                        <form method="POST" action="${pageContext.request.contextPath}/admin/users" novalidate>
                            <input type="hidden" name="action" value="create">
                            
                            <div class="row">
                                <!-- Username Field -->
                                <div class="col-md-6 mb-3">
                                    <label for="username" class="form-label required-field">
                                        <i class="bi bi-person"></i> Username
                                    </label>
                                    <input type="text" 
                                           class="form-control" 
                                           id="username" 
                                           name="username" 
                                           value="${param.username}"
                                           required 
                                           minlength="3" 
                                           maxlength="50"
                                           placeholder="Enter username (3-50 characters)">
                                    <div class="form-text">
                                        Username can only contain letters, numbers, and underscores
                                    </div>
                                </div>

                                <!-- Email Field -->
                                <div class="col-md-6 mb-3">
                                    <label for="email" class="form-label required-field">
                                        <i class="bi bi-envelope"></i> Email
                                    </label>
                                    <input type="email" 
                                           class="form-control" 
                                           id="email" 
                                           name="email" 
                                           value="${param.email}"
                                           required 
                                           placeholder="Enter email address">
                                    <div class="form-text">
                                        Must be a valid email address (e.g. user@example.com)
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <!-- Password Field -->
                                <div class="col-md-6 mb-3">
                                    <label for="password" class="form-label required-field">
                                        <i class="bi bi-lock"></i> Password
                                    </label>
                                    <input type="password" 
                                           class="form-control" 
                                           id="password" 
                                           name="password" 
                                           required 
                                           minlength="6" 
                                           maxlength="100"
                                           placeholder="Enter password (minimum 6 characters)">
                                    <div class="form-text">
                                        Password must be at least 6 characters
                                    </div>
                                </div>

                                <!-- Confirm Password Field -->
                                <div class="col-md-6 mb-3">
                                    <label for="confirmPassword" class="form-label required-field">
                                        <i class="bi bi-lock-fill"></i> Confirm Password
                                    </label>
                                    <input type="password" 
                                           class="form-control" 
                                           id="confirmPassword" 
                                           name="confirmPassword" 
                                           required 
                                           placeholder="Re-enter password">
                                    <div class="form-text">
                                        Re-enter password to confirm
                                    </div>
                                </div>
                            </div>

                            <!-- Role Field -->
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="role" class="form-label">
                                        <i class="bi bi-shield"></i> User Role
                                    </label>
                                    <select class="form-select" id="role" name="role">
                                        <option value="user" ${param.role == 'user' ? 'selected' : ''}>Regular User</option>
                                        <option value="admin" ${param.role == 'admin' ? 'selected' : ''}>Administrator</option>
                                    </select>
                                    <div class="form-text">
                                        Select the appropriate role for this user
                                    </div>
                                </div>
                            </div>

                            <!-- Form Guidelines -->
                            <div class="alert alert-info">
                                <h6><i class="bi bi-info-circle"></i> Guidelines:</h6>
                                <ul class="mb-0">
                                    <li><strong>Username:</strong> 3-50 characters, letters, numbers and underscores only</li>
                                    <li><strong>Email:</strong> Must be a valid email address and not already in use</li>
                                    <li><strong>Password:</strong> Minimum 6 characters for security</li>
                                    <li><strong>Role:</strong> Regular users have basic access, admins have full control</li>
                                    <li><strong>Fields marked with (*) are required</strong></li>
                                </ul>
                            </div>

                            <!-- Form Actions -->
                            <div class="row">
                                <div class="col-12">
                                    <hr>
                                    <div class="d-flex gap-2 justify-content-end">
                                        <a href="${pageContext.request.contextPath}/admin/users" 
                                           class="btn btn-outline-secondary">
                                            <i class="bi bi-x-circle"></i> Cancel
                                        </a>
                                        <button type="reset" class="btn btn-outline-warning">
                                            <i class="bi bi-arrow-clockwise"></i> Reset Form
                                        </button>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="bi bi-check-circle"></i> Create User
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <footer class="bg-dark text-light py-4 mt-5">
        <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <h6><i class="bi bi-code-square"></i> JSP Final Project</h6>
                    <p class="mb-0">User Management System - Admin Panel</p>
                </div>
                <div class="col-md-6 text-md-end">
                    <small class="text-muted">
                        <i class="bi bi-shield-lock"></i> All information is encrypted and secure
                    </small>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Form validation script -->
    <script>
        // Password confirmation check on form submit
        document.querySelector('form').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Password and confirm password do not match!');
                document.getElementById('confirmPassword').focus();
                return false;
            }
            
            return true;
        });

        // Real-time password confirmation validation
        document.getElementById('confirmPassword').addEventListener('input', function() {
            const password = document.getElementById('password').value;
            const confirmPassword = this.value;
            
            if (confirmPassword && password !== confirmPassword) {
                this.setCustomValidity('Passwords do not match');
                this.classList.add('is-invalid');
            } else {
                this.setCustomValidity('');
                this.classList.remove('is-invalid');
            }
        });
    </script>
</body>
</html>