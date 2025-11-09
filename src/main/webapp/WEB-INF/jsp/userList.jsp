<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - JSP Final Project</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .table th {
            background-color: #007bff;
            color: white;
            border: none;
        }
        .btn-sm {
            padding: 0.25rem 0.5rem;
            font-size: 0.875rem;
        }
        .user-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(45deg, #007bff, #0056b3);
            color: white;
            font-weight: bold;
            margin-right: 10px;
        }
        .status-badge {
            font-size: 0.75rem;
        }
        .action-buttons {
            white-space: nowrap;
        }
        .stats-card {
            background: linear-gradient(45deg, #007bff, #0056b3);
            color: white;
            border-radius: 15px;
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                <i class="bi bi-database"></i> JSP Final Project
            </a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/">
                    <i class="bi bi-house"></i> Home
                </a>
                <a class="nav-link active" href="${pageContext.request.contextPath}/users">
                    <i class="bi bi-people"></i> Users
                </a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <!-- Page Header -->
        <div class="row mb-4">
            <div class="col-lg-8">
                <h2>
                    <i class="bi bi-people-fill"></i> User Management
                </h2>
                <p class="text-muted">Manage users in the system - view, create, edit, and delete user accounts</p>
            </div>
            <div class="col-lg-4">
                <div class="card stats-card">
                    <div class="card-body text-center">
                        <h3 class="card-title">
                            <i class="bi bi-person-plus"></i> ${userCount}
                        </h3>
                        <p class="card-text">Total Users</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle"></i> ${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Action Buttons -->
        <div class="row mb-3">
            <div class="col-lg-6">
                <button class="btn btn-success" onclick="createUser()">
                    <i class="bi bi-person-plus"></i> Add New User
                </button>
                <button class="btn btn-secondary" onclick="refreshPage()">
                    <i class="bi bi-arrow-clockwise"></i> Refresh
                </button>
            </div>
            <div class="col-lg-6">
                <div class="input-group">
                    <span class="input-group-text"><i class="bi bi-search"></i></span>
                    <input type="text" class="form-control" id="searchInput" placeholder="Search users...">
                </div>
            </div>
        </div>

        <!-- Users Table -->
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">
                    <i class="bi bi-list"></i> Users List
                    <small class="text-muted">(${userCount} users found)</small>
                </h5>
            </div>
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty users}">
                        <div class="text-center p-5">
                            <i class="bi bi-person-x" style="font-size: 3rem; color: #ccc;"></i>
                            <h4 class="text-muted mt-3">No Users Found</h4>
                            <p class="text-muted">Start by adding your first user to the system.</p>
                            <button class="btn btn-primary" onclick="createUser()">
                                <i class="bi bi-person-plus"></i> Add First User
                            </button>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover table-striped mb-0" id="usersTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>User</th>
                                        <th>Email</th>
                                        <th>Created</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="user" items="${users}" varStatus="status">
                                        <tr>
                                            <td>
                                                <span class="badge bg-secondary">#${user.id}</span>
                                            </td>
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <div class="user-avatar">
                                                        ${user.username.substring(0, 1).toUpperCase()}
                                                    </div>
                                                    <div>
                                                        <strong>${user.username}</strong>
                                                        <br>
                                                        <small class="text-muted">${user.displayName}</small>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <i class="bi bi-envelope"></i> ${user.email}
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${user.createdAt}" pattern="dd/MM/yyyy"/>
                                                <br>
                                                <small class="text-muted">
                                                    <fmt:formatDate value="${user.createdAt}" pattern="HH:mm"/>
                                                </small>
                                            </td>
                                            <td>
                                                <span class="badge bg-success status-badge">
                                                    <i class="bi bi-person-check"></i> Active
                                                </span>
                                            </td>
                                            <td class="action-buttons">
                                                <div class="btn-group" role="group">
                                                    <button type="button" class="btn btn-outline-primary btn-sm" 
                                                            onclick="viewUser(${user.id})" 
                                                            title="View Details">
                                                        <i class="bi bi-eye"></i>
                                                    </button>
                                                    <button type="button" class="btn btn-outline-secondary btn-sm" 
                                                            onclick="editUser(${user.id})" 
                                                            title="Edit User">
                                                        <i class="bi bi-pencil"></i>
                                                    </button>
                                                    <button type="button" class="btn btn-outline-danger btn-sm" 
                                                            onclick="deleteUser(${user.id}, '${user.username}')" 
                                                            title="Delete User">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <!-- Pagination (if needed in future) -->
                        <div class="card-footer">
                            <div class="d-flex justify-content-between align-items-center">
                                <small class="text-muted">
                                    Showing ${users.size()} of ${userCount} users
                                </small>
                                <small class="text-muted">
                                    Last updated: <fmt:formatDate value="<%= new java.util.Date() %>" pattern="dd/MM/yyyy HH:mm"/>
                                </small>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Custom JavaScript -->
    <script>
        // Search functionality
        document.getElementById('searchInput').addEventListener('keyup', function() {
            const searchValue = this.value.toLowerCase();
            const tableRows = document.querySelectorAll('#usersTable tbody tr');
            
            tableRows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(searchValue) ? '' : 'none';
            });
        });

        // User actions
        function viewUser(userId) {
            window.location.href = '${pageContext.request.contextPath}/users?action=view&id=' + userId;
        }

        function editUser(userId) {
            // For now, show alert (implement edit form later)
            alert('Edit user functionality will be implemented in next phase.\nUser ID: ' + userId);
        }

        function createUser() {
            // For now, show prompt (implement create form later)
            const username = prompt('Enter username:');
            const email = prompt('Enter email:');
            const password = prompt('Enter password:');
            
            if (username && email && password) {
                // Create form and submit
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/users?action=create';
                
                const addField = (name, value) => {
                    const input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = name;
                    input.value = value;
                    form.appendChild(input);
                };
                
                addField('username', username);
                addField('email', email);
                addField('password', password);
                
                document.body.appendChild(form);
                form.submit();
            }
        }

        function deleteUser(userId, username) {
            if (confirm('Are you sure you want to delete user "' + username + '"?\n\nThis action cannot be undone.')) {
                // Create form and submit
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/users?action=delete&id=' + userId;
                
                document.body.appendChild(form);
                form.submit();
            }
        }

        function refreshPage() {
            window.location.reload();
        }

        // Auto-dismiss alerts after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
    </script>
</body>
</html>