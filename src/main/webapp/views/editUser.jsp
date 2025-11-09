<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa User - JSP Final Project</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .form-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
        }
        .header-section {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            border-radius: 10px 10px 0 0;
            padding: 2rem;
        }
        .form-control:focus {
            border-color: #f093fb;
            box-shadow: 0 0 0 0.2rem rgba(240, 147, 251, 0.25);
        }
        .btn-warning {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            border: none;
            color: white;
        }
        .btn-warning:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
            color: white;
        }
        .required-field::after {
            content: "*";
            color: red;
            margin-left: 3px;
        }
        .user-info-card {
            background: #f8f9fa;
            border-left: 4px solid #f093fb;
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
                            <i class="bi bi-pencil-square display-6"></i>
                        </h1>
                        <h2 class="h3 mt-3 mb-0">Chỉnh sửa User</h2>
                        <p class="mb-0 opacity-75">Cập nhật thông tin tài khoản người dùng</p>
                    </div>

                    <!-- Form Content -->
                    <div class="p-4">
                        <!-- Check if user exists -->
                        <c:choose>
                            <c:when test="${empty user}">
                                <div class="alert alert-danger text-center">
                                    <i class="bi bi-exclamation-triangle display-4"></i>
                                    <h4 class="mt-2">User không tồn tại!</h4>
                                    <p>User mà bạn muốn chỉnh sửa không tồn tại hoặc đã bị xóa.</p>
                                    <a href="${pageContext.request.contextPath}/users" class="btn btn-primary">
                                        <i class="bi bi-arrow-left"></i> Về danh sách Users
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <!-- User Info Card -->
                                <div class="user-info-card p-3 mb-4">
                                    <h6><i class="bi bi-info-circle"></i> Thông tin hiện tại:</h6>
                                    <div class="row">
                                        <div class="col-md-4">
                                            <small class="text-muted">ID:</small><br>
                                            <strong>${user.id}</strong>
                                        </div>
                                        <div class="col-md-4">
                                            <small class="text-muted">Username hiện tại:</small><br>
                                            <strong>${user.username}</strong>
                                        </div>
                                        <div class="col-md-4">
                                            <small class="text-muted">Email hiện tại:</small><br>
                                            <strong>${user.email}</strong>
                                        </div>
                                    </div>
                                </div>

                                <!-- Error Messages -->
                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        <i class="bi bi-exclamation-triangle"></i> 
                                        <strong>Lỗi!</strong> ${error}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                    </div>
                                </c:if>

                                <!-- Edit User Form -->
                                <form method="POST" action="${pageContext.request.contextPath}/users" novalidate>
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="id" value="${user.id}">
                                    
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
                                                   value="${not empty param.username ? param.username : user.username}"
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
                                                   value="${not empty param.email ? param.email : user.email}"
                                                   required 
                                                   placeholder="Nhập địa chỉ email">
                                            <div class="form-text">
                                                Địa chỉ email hợp lệ (vd: user@example.com)
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Password Section -->
                                    <div class="alert alert-warning">
                                        <h6><i class="bi bi-shield-lock"></i> Đổi mật khẩu (Tùy chọn):</h6>
                                        <p class="mb-0">Để giữ mật khẩu cũ, hãy để trống các trường mật khẩu bên dưới.</p>
                                    </div>

                                    <div class="row">
                                        <!-- Password Field -->
                                        <div class="col-md-6 mb-3">
                                            <label for="password" class="form-label">
                                                <i class="bi bi-lock"></i> Mật khẩu mới
                                            </label>
                                            <input type="password" 
                                                   class="form-control" 
                                                   id="password" 
                                                   name="password" 
                                                   minlength="6" 
                                                   maxlength="100"
                                                   placeholder="Để trống nếu không đổi mật khẩu">
                                            <div class="form-text">
                                                Để trống nếu không muốn đổi mật khẩu
                                            </div>
                                        </div>

                                        <!-- Confirm Password Field -->
                                        <div class="col-md-6 mb-3">
                                            <label for="confirmPassword" class="form-label">
                                                <i class="bi bi-lock-fill"></i> Xác nhận mật khẩu mới
                                            </label>
                                            <input type="password" 
                                                   class="form-control" 
                                                   id="confirmPassword" 
                                                   name="confirmPassword" 
                                                   placeholder="Xác nhận mật khẩu mới">
                                            <div class="form-text">
                                                Nhập lại mật khẩu mới để xác nhận
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Form Guidelines -->
                                    <div class="alert alert-info">
                                        <h6><i class="bi bi-info-circle"></i> Hướng dẫn:</h6>
                                        <ul class="mb-0">
                                            <li><strong>Username:</strong> 3-50 ký tự, chỉ chứa chữ cái, số và dấu gạch dưới</li>
                                            <li><strong>Email:</strong> Phải là địa chỉ email hợp lệ và chưa được dùng cho user khác</li>
                                            <li><strong>Mật khẩu:</strong> Để trống nếu không muốn thay đổi, hoặc nhập ít nhất 6 ký tự</li>
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
                                                <a href="${pageContext.request.contextPath}/users?action=view&id=${user.id}" 
                                                   class="btn btn-outline-info">
                                                    <i class="bi bi-eye"></i> Xem chi tiết
                                                </a>
                                                <button type="reset" class="btn btn-outline-warning">
                                                    <i class="bi bi-arrow-clockwise"></i> Reset
                                                </button>
                                                <button type="submit" class="btn btn-warning">
                                                    <i class="bi bi-check-circle"></i> Cập nhật
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </c:otherwise>
                        </c:choose>
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
    
    <!-- Simple form validation -->
    <script>
        // Simple password confirmation check on form submit
        document.querySelector('form').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            // Only check if user entered a password
            if (password.length > 0 || confirmPassword.length > 0) {
                if (password !== confirmPassword) {
                    e.preventDefault();
                    alert('Mật khẩu mới và xác nhận mật khẩu không khớp!');
                    document.getElementById('confirmPassword').focus();
                    return false;
                }
                
                if (password.length < 6) {
                    e.preventDefault();
                    alert('Mật khẩu mới phải có ít nhất 6 ký tự!');
                    document.getElementById('password').focus();
                    return false;
                }
            }
            
            return true;
        });
    </script>
</body>
</html>