<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'Register'} - JSP Project</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/styles.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container-fluid min-vh-100 py-4">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="card shadow">
                    <div class="card-body p-4">
                        <div class="text-center mb-4">
                            <i class="bi bi-person-plus-fill display-4 text-primary"></i>
                            <h2 class="mt-2">Create Account</h2>
                            <p class="text-muted">Join our community today</p>
                        </div>

                        <!-- Error Message -->
                        <c:if test="${error != null}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="bi bi-exclamation-triangle me-2"></i>
                                ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/register" novalidate>
                            <div class="mb-3">
                                <label for="username" class="form-label">
                                    <i class="bi bi-person me-1"></i>Username *
                                </label>
                                <input type="text" 
                                       class="form-control" 
                                       id="username" 
                                       name="username" 
                                       value="${username}" 
                                       required 
                                       minlength="3"
                                       maxlength="50"
                                       pattern="^[a-zA-Z0-9_]+$"
                                       placeholder="Enter your username"
                                       autocomplete="username">
                                <div class="form-text">
                                    3-50 characters, letters, numbers, and underscores only
                                </div>
                                <div class="invalid-feedback">
                                    Please enter a valid username (3-50 characters, letters, numbers, underscores only).
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="email" class="form-label">
                                    <i class="bi bi-envelope me-1"></i>Email Address *
                                </label>
                                <input type="email" 
                                       class="form-control" 
                                       id="email" 
                                       name="email" 
                                       value="${email}" 
                                       required 
                                       placeholder="Enter your email address"
                                       autocomplete="email">
                                <div class="invalid-feedback">
                                    Please enter a valid email address.
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="password" class="form-label">
                                    <i class="bi bi-lock me-1"></i>Password *
                                </label>
                                <div class="input-group">
                                    <input type="password" 
                                           class="form-control" 
                                           id="password" 
                                           name="password" 
                                           required 
                                           minlength="6"
                                           maxlength="100"
                                           placeholder="Enter your password"
                                           autocomplete="new-password">
                                    <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                                <div class="form-text">
                                    At least 6 characters long
                                </div>
                                <div class="invalid-feedback">
                                    Password must be at least 6 characters long.
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label">
                                    <i class="bi bi-lock-fill me-1"></i>Confirm Password *
                                </label>
                                <div class="input-group">
                                    <input type="password" 
                                           class="form-control" 
                                           id="confirmPassword" 
                                           name="confirmPassword" 
                                           required 
                                           placeholder="Confirm your password"
                                           autocomplete="new-password">
                                    <button class="btn btn-outline-secondary" type="button" id="toggleConfirmPassword">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                                <div class="invalid-feedback" id="confirmPasswordFeedback">
                                    Please confirm your password.
                                </div>
                            </div>

                            <div class="mb-3 form-check">
                                <input type="checkbox" class="form-check-input" id="agreeTerms" required>
                                <label class="form-check-label" for="agreeTerms">
                                    I agree to the <a href="#" class="text-decoration-none">Terms of Service</a> 
                                    and <a href="#" class="text-decoration-none">Privacy Policy</a> *
                                </label>
                                <div class="invalid-feedback">
                                    You must agree to the terms before registering.
                                </div>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary btn-lg">
                                    <i class="bi bi-person-plus me-2"></i>
                                    Create Account
                                </button>
                            </div>
                        </form>

                        <hr class="my-4">

                        <div class="text-center">
                            <p class="mb-0">Already have an account?</p>
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-secondary mt-2">
                                <i class="bi bi-box-arrow-in-right me-2"></i>
                                Sign In
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        (function() {
            'use strict';
            
            const form = document.querySelector('form');
            const password = document.querySelector('#password');
            const confirmPassword = document.querySelector('#confirmPassword');
            const confirmPasswordFeedback = document.querySelector('#confirmPasswordFeedback');

            // Password matching validation
            function validatePasswordMatch() {
                if (confirmPassword.value && password.value !== confirmPassword.value) {
                    confirmPassword.setCustomValidity('Passwords do not match');
                    confirmPasswordFeedback.textContent = 'Passwords do not match.';
                } else {
                    confirmPassword.setCustomValidity('');
                    confirmPasswordFeedback.textContent = 'Please confirm your password.';
                }
            }

            password.addEventListener('input', validatePasswordMatch);
            confirmPassword.addEventListener('input', validatePasswordMatch);

            // Form validation
            form.addEventListener('submit', function(event) {
                validatePasswordMatch();
                
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            });

            // Password toggles
            const togglePassword = document.querySelector('#togglePassword');
            const toggleConfirmPassword = document.querySelector('#toggleConfirmPassword');

            togglePassword.addEventListener('click', function() {
                const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
                password.setAttribute('type', type);
                
                const icon = this.querySelector('i');
                icon.classList.toggle('bi-eye');
                icon.classList.toggle('bi-eye-slash');
            });

            toggleConfirmPassword.addEventListener('click', function() {
                const type = confirmPassword.getAttribute('type') === 'password' ? 'text' : 'password';
                confirmPassword.setAttribute('type', type);
                
                const icon = this.querySelector('i');
                icon.classList.toggle('bi-eye');
                icon.classList.toggle('bi-eye-slash');
            });
        })();
    </script>
</body>
</html>