<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    // Check if user is admin
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    model.bean.User currentUser = (model.bean.User) session.getAttribute("user");
    if (!currentUser.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/submit.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'User Management'} - JSP Project</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/styles.css" rel="stylesheet">
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/submit.jsp">
                <i class="bi bi-shield-check me-2"></i>Admin Panel
            </a>
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/admin/users">
                            <i class="bi bi-people me-1"></i>User Management
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/submit.jsp">
                            <i class="bi bi-file-earmark-pdf me-1"></i>Convert
                        </a>
                    </li>
                </ul>
                
                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" 
                           data-bs-toggle="dropdown">
                            <i class="bi bi-person-circle me-1"></i>
                            <%= currentUser.getDisplayName() %>
                            <span class="badge bg-danger ms-1">ADMIN</span>
                        </a>
                        <ul class="dropdown-menu">
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
        <!-- Header -->
        <div class="row mb-4">
            <div class="col">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1 class="h2 mb-1">
                            <i class="bi bi-people-fill me-2 text-primary"></i>User Management
                        </h1>
                        <p class="text-muted mb-0">Manage system users and their roles</p>
                    </div>
                    <div>
                        <a href="${pageContext.request.contextPath}/admin/users?action=add" class="btn btn-primary">
                            <i class="bi bi-person-plus me-2"></i>Add New User
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Messages -->
        <c:if test="${param.message != null}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle me-2"></i>
                ${param.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${param.error != null}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle me-2"></i>
                ${param.error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${error != null}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle me-2"></i>
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Statistics Cards -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card bg-primary text-white">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-grow-1">
                                <h5 class="card-title mb-0">Total Users</h5>
                                <h2 class="mb-0">${userCount}</h2>
                            </div>
                            <div>
                                <i class="bi bi-people display-4 opacity-75"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3">
                <div class="card bg-success text-white">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-grow-1">
                                <h5 class="card-title mb-0">Active Users</h5>
                                <h2 class="mb-0">
                                    <c:set var="activeCount" value="0" />
                                    <c:forEach var="user" items="${users}">
                                        <c:if test="${user.role == 'user'}">
                                            <c:set var="activeCount" value="${activeCount + 1}" />
                                        </c:if>
                                    </c:forEach>
                                    ${activeCount}
                                </h2>
                            </div>
                            <div>
                                <i class="bi bi-person-check display-4 opacity-75"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3">
                <div class="card bg-danger text-white">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-grow-1">
                                <h5 class="card-title mb-0">Admins</h5>
                                <h2 class="mb-0">
                                    <c:set var="adminCount" value="0" />
                                    <c:forEach var="user" items="${users}">
                                        <c:if test="${user.role == 'admin'}">
                                            <c:set var="adminCount" value="${adminCount + 1}" />
                                        </c:if>
                                    </c:forEach>
                                    ${adminCount}
                                </h2>
                            </div>
                            <div>
                                <i class="bi bi-shield-check display-4 opacity-75"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3">
                <div class="card bg-info text-white">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-grow-1">
                                <h5 class="card-title mb-0">This Month</h5>
                                <h2 class="mb-0">+${userCount > 0 ? 1 : 0}</h2>
                            </div>
                            <div>
                                <i class="bi bi-graph-up display-4 opacity-75"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Users Table -->
        <div class="card">
            <div class="card-header">
                <div class="row align-items-center">
                    <div class="col">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-table me-2"></i>All Users
                        </h5>
                    </div>
                    <div class="col-auto">
                        <div class="input-group input-group-sm">
                            <input type="text" class="form-control" placeholder="Search users..." id="userSearch">
                            <button class="btn btn-outline-secondary" type="button">
                                <i class="bi bi-search"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty users}">
                        <div class="text-center py-5">
                            <i class="bi bi-people display-1 text-muted"></i>
                            <h4 class="text-muted mt-3">No Users Found</h4>
                            <p class="text-muted">There are no users in the system.</p>
                            <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">
                                <i class="bi bi-person-plus me-2"></i>Add First User
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th><i class="bi bi-hash"></i> ID</th>
                                        <th><i class="bi bi-person"></i> Username</th>
                                        <th><i class="bi bi-envelope"></i> Email</th>
                                        <th><i class="bi bi-shield"></i> Role</th>
                                        <th><i class="bi bi-calendar"></i> Created</th>
                                        <th><i class="bi bi-calendar-check"></i> Updated</th>
                                        <th><i class="bi bi-gear"></i> Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="user" items="${users}">
                                        <tr>
                                            <td>
                                                <strong>${user.id}</strong>
                                            </td>
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <i class="bi bi-person-circle text-muted me-2"></i>
                                                    <div>
                                                        <strong>${user.username}</strong>
                                                        <c:if test="${user.id == sessionScope.user.id}">
                                                            <span class="badge bg-info ms-1">You</span>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <a href="mailto:${user.email}" class="text-decoration-none">
                                                    ${user.email}
                                                </a>
                                            </td>
                                            <td>
                                                <span class="badge bg-${user.role == 'admin' ? 'danger' : 'primary'}">
                                                    ${user.role.toUpperCase()}
                                                </span>
                                            </td>
                                            <td>
                                                <small class="text-muted">
                                                    <fmt:formatDate value="${user.createdAt}" pattern="MMM dd, yyyy" />
                                                </small>
                                            </td>
                                            <td>
                                                <small class="text-muted">
                                                    <fmt:formatDate value="${user.updatedAt}" pattern="MMM dd, yyyy" />
                                                </small>
                                            </td>
                                            <td>
                                                <div class="btn-group btn-group-sm" role="group">
                                                    <a href="${pageContext.request.contextPath}/admin/users?action=view&id=${user.id}" 
                                                       class="btn btn-outline-info" title="View">
                                                        <i class="bi bi-eye"></i>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/admin/users?action=edit&id=${user.id}" 
                                                       class="btn btn-outline-warning" title="Edit">
                                                        <i class="bi bi-pencil"></i>
                                                    </a>
                                                    <c:if test="${user.id != sessionScope.user.id}">
                                                        <button type="button" class="btn btn-outline-danger" 
                                                                onclick="confirmDelete('${user.id}', '${user.username}')" title="Delete">
                                                            <i class="bi bi-trash"></i>
                                                        </button>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="bi bi-exclamation-triangle text-danger me-2"></i>
                        Confirm Deletion
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to delete the user <strong id="deleteUserName"></strong>?</p>
                    <p class="text-muted mb-0">
                        <i class="bi bi-info-circle me-1"></i>
                        This action cannot be undone.
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form id="deleteForm" method="post" style="display: inline;">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" id="deleteUserId">
                        <button type="submit" class="btn btn-danger">
                            <i class="bi bi-trash me-2"></i>Delete User
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="mt-5 py-4 bg-light text-center text-muted">
        <div class="container">
            <p>&copy; 2025 JSP Final Project - Admin Panel. All rights reserved.</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Search functionality
        document.getElementById('userSearch').addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            const rows = document.querySelectorAll('tbody tr');
            
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(searchTerm) ? '' : 'none';
            });
        });

        // Delete confirmation
        function confirmDelete(userId, username) {
            document.getElementById('deleteUserId').value = userId;
            document.getElementById('deleteUserName').textContent = username;
            document.getElementById('deleteForm').action = '${pageContext.request.contextPath}/admin/users';
            
            new bootstrap.Modal(document.getElementById('deleteModal')).show();
        }
    </script>
</body>
</html>