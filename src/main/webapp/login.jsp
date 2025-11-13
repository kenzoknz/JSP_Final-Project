<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'Login'} - JSP Project</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/styles.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container-fluid vh-100">
        <div class="row justify-content-center align-items-center vh-100">
            <div class="col-md-6 col-lg-4">
                <div class="card shadow">
                    <div class="card-body p-4">
                        <div class="text-center mb-4">
                            <i class="bi bi-person-circle display-4 text-primary"></i>
                            <h2 class="mt-2">Login</h2>
                            <p class="text-muted">Sign in to your account</p>
                        </div>

                        <!-- Success Message -->
                        <c:if test="${param.message != null}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="bi bi-check-circle me-2"></i>
                                ${param.message}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <!-- Error Message -->
                        <c:if test="${error != null}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="bi bi-exclamation-triangle me-2"></i>
                                ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/login" novalidate>
                            <div class="mb-3">
                                <label for="usernameOrEmail" class="form-label">
                                    <i class="bi bi-person me-1"></i>Username or Email
                                </label>
                                <input type="text" 
                                       class="form-control" 
                                       id="usernameOrEmail" 
                                       name="usernameOrEmail" 
                                       value="${usernameOrEmail}" 
                                       required 
                                       placeholder="Enter your username or email"
                                       autocomplete="username">
                                <div class="invalid-feedback">
                                    Please enter your username or email.
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="password" class="form-label">
                                    <i class="bi bi-lock me-1"></i>Password
                                </label>
                                <div class="input-group">
                                    <input type="password" 
                                           class="form-control" 
                                           id="password" 
                                           name="password" 
                                           required 
                                           placeholder="Enter your password"
                                           autocomplete="current-password">
                                    <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                                <div class="invalid-feedback">
                                    Please enter your password.
                                </div>
                            </div>

                            <div class="mb-3 form-check">
                                <input type="checkbox" class="form-check-input" id="rememberMe" name="rememberMe">
                                <label class="form-check-label" for="rememberMe">
                                    Remember me
                                </label>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary btn-lg">
                                    <i class="bi bi-box-arrow-in-right me-2"></i>
                                    Sign In
                                </button>
                            </div>
                        </form>

                        <hr class="my-4">

                        <div class="text-center">
                            <p class="mb-0">Don't have an account?</p>
                            <a href="${pageContext.request.contextPath}/register" class="btn btn-outline-secondary mt-2">
                                <i class="bi bi-person-plus me-2"></i>
                                Create Account
                            </a>
                        </div>

                        <div class="text-center mt-3">
                            <small class="text-muted">
                                Demo accounts: admin/admin123 | john_doe/password123
                            </small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Form validation
        (function() {
            'use strict';
            
            const form = document.querySelector('form');
            
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            });

            // Password toggle
            const togglePassword = document.querySelector('#togglePassword');
            const password = document.querySelector('#password');

            togglePassword.addEventListener('click', function() {
                const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
                password.setAttribute('type', type);
                
                const icon = this.querySelector('i');
                icon.classList.toggle('bi-eye');
                icon.classList.toggle('bi-eye-slash');
            });
        })();
    </script>
</body>
</html>