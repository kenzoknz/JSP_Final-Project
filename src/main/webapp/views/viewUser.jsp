<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết User - JSP Final Project</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
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
                <!-- Success/Error Messages -->
                <c:if test="${not empty message}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle"></i> 
                        <strong>Thành công!</strong> ${message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- Error Messages -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-triangle"></i> 
                        <strong>Lỗi!</strong> ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- Check if user exists -->
                <c:choose>
                    <c:when test="${empty user}">
                        <div class="card user-card">
                            <div class="card-body text-center py-5">
                                <i class="bi bi-person-x display-1 text-muted mb-3"></i>
                                <h4 class="card-title text-muted">User không tồn tại!</h4>
                                <p class="card-text text-muted">
                                    User mà bạn muốn xem không tồn tại hoặc đã bị xóa.
                                </p>
                                <a href="${pageContext.request.contextPath}/users" class="btn btn-primary">
                                    <i class="bi bi-arrow-left"></i> Về danh sách Users
                                </a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- User Details Card -->
                        <div class="user-card">
                            <!-- Header Section -->
                            <div class="header-section text-center">
                                <div class="user-avatar">
                                    <i class="bi bi-person-circle"></i>
                                </div>
                                <h1 class="h2 mb-2">${user.username}</h1>
                                <p class="mb-0 opacity-75">
                                    <i class="bi bi-envelope"></i> ${user.email}
                                </p>
                                <span class="status-badge bg-success mt-3 d-inline-block">
                                    <i class="bi bi-check-circle"></i> Active User
                                </span>
                            </div>

                            <!-- Information Section -->
                            <div class="info-section">
                                <h5 class="mb-4">
                                    <i class="bi bi-info-circle text-primary"></i> Thông tin chi tiết
                                </h5>
                                
                                <div class="row">
                                    <!-- Username -->
                                    <div class="col-md-6 mb-3">
                                        <div class="info-item">
                                            <div class="info-label">
                                                <i class="bi bi-person"></i> Username
                                            </div>
                                            <div class="info-value">
                                                ${user.username}
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Email -->
                                    <div class="col-md-6 mb-3">
                                        <div class="info-item">
                                            <div class="info-label">
                                                <i class="bi bi-envelope"></i> Địa chỉ Email
                                            </div>
                                            <div class="info-value">
                                                <a href="mailto:${user.email}" class="text-decoration-none">
                                                    ${user.email}
                                                </a>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Created Date -->
                                    <div class="col-md-6 mb-3">
                                        <div class="info-item">
                                            <div class="info-label">
                                                <i class="bi bi-calendar-plus"></i> Ngày tạo
                                            </div>
                                            <div class="info-value">
                                                <c:choose>
                                                    <c:when test="${not empty user.createdAt}">
                                                        <fmt:formatDate value="${user.createdAt}" 
                                                                      pattern="dd/MM/yyyy HH:mm:ss"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Không có thông tin</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Last Updated -->
                                    <div class="col-md-6 mb-3">
                                        <div class="info-item">
                                            <div class="info-label">
                                                <i class="bi bi-calendar-check"></i> Cập nhật lần cuối
                                            </div>
                                            <div class="info-value">
                                                <c:choose>
                                                    <c:when test="${not empty user.updatedAt}">
                                                        <fmt:formatDate value="${user.updatedAt}" 
                                                                      pattern="dd/MM/yyyy HH:mm:ss"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Chưa cập nhật</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Additional Info -->
                                <div class="alert alert-info">
                                    <h6><i class="bi bi-shield-lock"></i> Bảo mật:</h6>
                                    <ul class="mb-0">
                                        <li>Mật khẩu được mã hóa bằng SHA-256</li>
                                        <li>Thông tin cá nhân được bảo vệ theo chính sách bảo mật</li>
                                        <li>Chỉ có quản trị viên mới có thể xem thông tin này</li>
                                    </ul>
                                </div>
                            </div>

                            <!-- Actions Section -->
                            <div class="actions-section text-center">
                                <h6 class="mb-3">
                                    <i class="bi bi-gear-fill text-primary"></i> Thao tác
                                </h6>
                                
                                <div class="btn-group" role="group">
                                    <!-- Edit Button -->
                                    <a href="${pageContext.request.contextPath}/users?action=edit&id=${user.id}" 
                                       class="btn btn-warning">
                                        <i class="bi bi-pencil"></i> Chỉnh sửa
                                    </a>
                                    
                                    <!-- Delete Button -->
                                    <form method="POST" action="${pageContext.request.contextPath}/users" 
                                          class="view-user-display-inline"
                                          onsubmit="return confirm('⚠️ CẢNH BÁO!\n\nBạn có chắc chắn muốn xóa user \'${user.username}\'?\n\nHành động này không thể hoàn tác!');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${user.id}">
                                        <button type="submit" class="btn btn-danger">
                                            <i class="bi bi-trash"></i> Xóa User
                                        </button>
                                    </form>
                                </div>

                                <div class="mt-3">
                                    <a href="${pageContext.request.contextPath}/users" 
                                       class="btn btn-outline-secondary">
                                        <i class="bi bi-list"></i> Về danh sách Users
                                    </a>
                                    
                                    <a href="${pageContext.request.contextPath}/users?action=add" 
                                       class="btn btn-outline-success">
                                        <i class="bi bi-person-plus"></i> Thêm User mới
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
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
                        <i class="bi bi-eye"></i> Xem chi tiết User - ${user.username}
                    </small>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>