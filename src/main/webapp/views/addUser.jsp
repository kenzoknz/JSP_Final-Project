<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm User Mới - JSP Final Project</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .form-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
        }
        .header-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 10px 10px 0 0;
            padding: 2rem;
        }
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
        }
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        }
        .required-field::after {
            content: "*";
            color: red;
            margin-left: 3px;
        }
    </style>
</head>
<body class="bg-light">
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                <i class="bi bi-people-fill"></i> User Management
            </a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/users">
                    <i class="bi bi-arrow-left"></i> Quay lại
                </a>
            </div>
        </div>
    </nav>

    <div class="container my-5">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="form-container">
                    <!-- Header -->
                    <div class="header-section text-center">
                        <h1 class="h2 mb-0">
                            <i class="bi bi-person-plus display-6"></i>
                        </h1>
                        <h2 class="h3 mt-3 mb-0">Thêm User Mới</h2>
                        <p class="mb-0 opacity-75">Điền thông tin để tạo tài khoản người dùng mới</p>
                    </div>

                    <!-- Form Content -->
                    <div class="p-4">
                        <!-- Error Messages -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="bi bi-exclamation-triangle"></i> 
                                <strong>Lỗi!</strong> ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <!-- Add User Form -->
                        <form method="POST" action="${pageContext.request.contextPath}/users" novalidate>
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
                                           placeholder="Nhập username (3-50 ký tự)">
                                    <div class="form-text">
                                        Username chỉ chứa chữ cái, số và dấu gạch dưới
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
                                           placeholder="Nhập địa chỉ email">
                                    <div class="form-text">
                                        Địa chỉ email hợp lệ (vd: user@example.com)
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <!-- Password Field -->
                                <div class="col-md-6 mb-3">
                                    <label for="password" class="form-label required-field">
                                        <i class="bi bi-lock"></i> Mật khẩu
                                    </label>
                                    <input type="password" 
                                           class="form-control" 
                                           id="password" 
                                           name="password" 
                                           required 
                                           minlength="6" 
                                           maxlength="100"
                                           placeholder="Nhập mật khẩu (tối thiểu 6 ký tự)">
                                    <div class="form-text">
                                        Mật khẩu phải có ít nhất 6 ký tự
                                    </div>
                                </div>

                                <!-- Confirm Password Field -->
                                <div class="col-md-6 mb-3">
                                    <label for="confirmPassword" class="form-label required-field">
                                        <i class="bi bi-lock-fill"></i> Xác nhận mật khẩu
                                    </label>
                                    <input type="password" 
                                           class="form-control" 
                                           id="confirmPassword" 
                                           name="confirmPassword" 
                                           required 
                                           placeholder="Nhập lại mật khẩu">
                                    <div class="form-text">
                                        Nhập lại mật khẩu để xác nhận
                                    </div>
                                </div>
                            </div>

                            <!-- Form Guidelines -->
                            <div class="alert alert-info">
                                <h6><i class="bi bi-info-circle"></i> Hướng dẫn:</h6>
                                <ul class="mb-0">
                                    <li><strong>Username:</strong> 3-50 ký tự, chỉ chứa chữ cái, số và dấu gạch dưới</li>
                                    <li><strong>Email:</strong> Phải là địa chỉ email hợp lệ và chưa được sử dụng</li>
                                    <li><strong>Mật khẩu:</strong> Tối thiểu 6 ký tự để đảm bảo bảo mật</li>
                                    <li><strong>Các trường có dấu (*) là bắt buộc</strong></li>
                                </ul>
                            </div>

                            <!-- Form Actions -->
                            <div class="row">
                                <div class="col-12">
                                    <hr>
                                    <div class="d-flex gap-2 justify-content-end">
                                        <a href="${pageContext.request.contextPath}/users" 
                                           class="btn btn-outline-secondary">
                                            <i class="bi bi-x-circle"></i> Hủy
                                        </a>
                                        <button type="reset" class="btn btn-outline-warning">
                                            <i class="bi bi-arrow-clockwise"></i> Xóa form
                                        </button>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="bi bi-check-circle"></i> Tạo User
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
                    <p class="mb-0">User Management System với JSP & Servlet</p>
                </div>
                <div class="col-md-6 text-md-end">
                    <small class="text-muted">
                        <i class="bi bi-shield-lock"></i> Tất cả thông tin được bảo mật
                    </small>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Simple form validation (no complex JavaScript) -->
    <script>
        // Simple password confirmation check on form submit
        document.querySelector('form').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Mật khẩu và xác nhận mật khẩu không khớp!');
                document.getElementById('confirmPassword').focus();
                return false;
            }
            
            return true;
        });
    </script>
</body>
</html>