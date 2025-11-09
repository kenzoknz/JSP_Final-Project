<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Users - JSP Final Project</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .header-actions {
            border-bottom: 3px solid #0d6efd;
            padding-bottom: 1rem;
        }
        .user-card {
            transition: transform 0.2s;
            border-left: 4px solid #0d6efd;
        }
        .user-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .user-actions .btn {
            margin: 0 2px;
        }
        .stats-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        .search-card {
            background-color: #f8f9fa;
            border: 1px solid #dee2e6;
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
                <a class="nav-link" href="${pageContext.request.contextPath}/">
                    <i class="bi bi-house"></i> Trang chủ
                </a>
            </div>
        </div>
    </nav>

    <div class="container my-4">
        <!-- Header Section -->
        <div class="row header-actions mb-4">
            <div class="col-md-8">
                <h1 class="h2 text-primary mb-0">
                    <i class="bi bi-people"></i> Danh sách Users
                </h1>
                <p class="text-muted mb-0">Quản lý thông tin người dùng trong hệ thống</p>
            </div>
            <div class="col-md-4 d-flex align-items-center justify-content-end">
                <a href="${pageContext.request.contextPath}/users?action=add" 
                   class="btn btn-success btn-lg">
                    <i class="bi bi-person-plus"></i> Thêm User Mới
                </a>
            </div>
        </div>

        <!-- Statistics Card -->
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card stats-card">
                    <div class="card-body text-center">
                        <i class="bi bi-people display-4"></i>
                        <h3 class="card-title mt-2">
                            <c:choose>
                                <c:when test="${not empty users}">
                                    ${users.size()}
                                </c:when>
                                <c:otherwise>
                                    0
                                </c:otherwise>
                            </c:choose>
                        </h3>
                        <p class="card-text">Tổng số Users</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Search Section -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card search-card">
                    <div class="card-body">
                        <form method="GET" action="${pageContext.request.contextPath}/users">
                            <div class="row g-3 align-items-end">
                                <div class="col-md-4">
                                    <label for="searchEmail" class="form-label">
                                        <i class="bi bi-envelope"></i> Email
                                    </label>
                                    <input type="text" class="form-control" id="searchEmail" name="searchEmail" 
                                           value="${param.searchEmail}" placeholder="Nhập email để tìm kiếm">
                                </div>
                                <div class="col-md-4">
                                    <label for="searchUsername" class="form-label">
                                        <i class="bi bi-person"></i> Username
                                    </label>
                                    <input type="text" class="form-control" id="searchUsername" name="searchUsername" 
                                           value="${param.searchUsername}" placeholder="Nhập username để tìm kiếm">
                                </div>
                                <div class="col-md-4">
                                    <button type="submit" class="btn btn-primary me-2">
                                        <i class="bi bi-search"></i> Tìm kiếm
                                    </button>
                                    <a href="${pageContext.request.contextPath}/users" class="btn btn-outline-secondary">
                                        <i class="bi bi-arrow-clockwise"></i> Reset
                                    </a>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty message}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle"></i> ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Users List -->
        <c:choose>
            <c:when test="${empty users}">
                <!-- No Users Found -->
                <div class="row">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body text-center py-5">
                                <i class="bi bi-person-x display-1 text-muted mb-3"></i>
                                <h4 class="card-title text-muted">Không tìm thấy user nào</h4>
                                <p class="card-text text-muted">
                                    <c:choose>
                                        <c:when test="${not empty param.searchEmail or not empty param.searchUsername}">
                                            Không có user nào phù hợp với tiêu chí tìm kiếm.
                                        </c:when>
                                        <c:otherwise>
                                            Hiện tại chưa có user nào trong hệ thống.
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                                <a href="${pageContext.request.contextPath}/users?action=add" 
                                   class="btn btn-primary">
                                    <i class="bi bi-person-plus"></i> Thêm User Đầu Tiên
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Users Table -->
                <div class="row">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-header bg-primary text-white">
                                <h5 class="card-title mb-0">
                                    <i class="bi bi-table"></i> Danh sách Users 
                                    <small class="text-light">(${users.size()} user)</small>
                                </h5>
                            </div>
                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table table-hover mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th scope="col" class="text-center" style="width: 80px;">
                                                    <i class="bi bi-hash"></i> ID
                                                </th>
                                                <th scope="col">
                                                    <i class="bi bi-person"></i> Username
                                                </th>
                                                <th scope="col">
                                                    <i class="bi bi-envelope"></i> Email
                                                </th>
                                                <th scope="col" class="text-center">
                                                    <i class="bi bi-calendar"></i> Ngày tạo
                                                </th>
                                                <th scope="col" class="text-center" style="width: 200px;">
                                                    <i class="bi bi-gear"></i> Thao tác
                                                </th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="user" items="${users}">
                                                <tr>
                                                    <td class="text-center">
                                                        <span class="badge bg-secondary">${user.id}</span>
                                                    </td>
                                                    <td>
                                                        <strong>${user.username}</strong>
                                                    </td>
                                                    <td>
                                                        <i class="bi bi-envelope text-muted"></i> ${user.email}
                                                    </td>
                                                    <td class="text-center">
                                                        <small class="text-muted">
                                                            <fmt:formatDate value="${user.createdAt}" pattern="dd/MM/yyyy"/>
                                                        </small>
                                                    </td>
                                                    <td class="text-center user-actions">
                                                        <!-- View Button -->
                                                        <a href="${pageContext.request.contextPath}/users?action=view&id=${user.id}" 
                                                           class="btn btn-sm btn-outline-info"
                                                           title="Xem chi tiết">
                                                            <i class="bi bi-eye"></i>
                                                        </a>
                                                        
                                                        <!-- Edit Button -->
                                                        <a href="${pageContext.request.contextPath}/users?action=edit&id=${user.id}" 
                                                           class="btn btn-sm btn-outline-warning"
                                                           title="Chỉnh sửa">
                                                            <i class="bi bi-pencil"></i>
                                                        </a>
                                                        
                                                        <!-- Delete Button -->
                                                        <form method="POST" action="${pageContext.request.contextPath}/users" 
                                                              style="display: inline-block;"
                                                              onsubmit="return confirm('Bạn có chắc chắn muốn xóa user ${user.username}?');">
                                                            <input type="hidden" name="action" value="delete">
                                                            <input type="hidden" name="id" value="${user.id}">
                                                            <button type="submit" class="btn btn-sm btn-outline-danger"
                                                                    title="Xóa">
                                                                <i class="bi bi-trash"></i>
                                                            </button>
                                                        </form>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
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
                        <i class="bi bi-clock"></i> 
                        <fmt:formatDate value="<%= new java.util.Date() %>" pattern="dd/MM/yyyy HH:mm"/>
                    </small>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>