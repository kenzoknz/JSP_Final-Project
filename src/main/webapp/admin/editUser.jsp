<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.bean.User" %>
<%
    User user = (User) request.getAttribute("user");
    String error = (String) request.getAttribute("error");
    
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/admin/users");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit User - Admin Panel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <nav class="col-md-3 col-lg-2 d-md-block bg-light sidebar">
                <div class="position-sticky pt-3">
                    <h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
                        <span>Admin Panel</span>
                    </h6>
                    <ul class="nav flex-column">
                        <li class="nav-item">
                            <a class="nav-link" href="<%=request.getContextPath()%>/dashboard">
                                <i class="bi bi-house-door me-2"></i>Dashboard
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link active" href="<%=request.getContextPath()%>/admin/users">
                                <i class="bi bi-people me-2"></i>User Management
                            </a>
                        </li>
                    </ul>
                </div>
            </nav>

            <!-- Main content -->
            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                    <h1 class="h2">Edit User</h1>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <a href="<%=request.getContextPath()%>/admin/users" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left me-1"></i>Back to Users
                        </a>
                    </div>
                </div>

                <% if (error != null && !error.trim().isEmpty()) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i><%=error%>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <div class="row">
                    <div class="col-lg-8">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="mb-0">Edit User: <%=user.getUsername()%></h5>
                            </div>
                            <div class="card-body">
                                <form action="<%=request.getContextPath()%>/admin/users" method="post">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="id" value="<%=user.getId()%>">

                                    <div class="mb-3">
                                        <label for="username" class="form-label">Username *</label>
                                        <input type="text" class="form-control" id="username" name="username" 
                                               value="<%=user.getUsername()%>" required>
                                    </div>

                                    <div class="mb-3">
                                        <label for="email" class="form-label">Email *</label>
                                        <input type="email" class="form-control" id="email" name="email" 
                                               value="<%=user.getEmail()%>" required>
                                    </div>

                                    <div class="mb-3">
                                        <label for="password" class="form-label">New Password</label>
                                        <input type="password" class="form-control" id="password" name="password" 
                                               placeholder="Leave blank to keep current password">
                                        <div class="form-text">Only fill this field if you want to change the password.</div>
                                    </div>

                                    <div class="mb-3">
                                        <label for="role" class="form-label">Role *</label>
                                        <select class="form-select" id="role" name="role" required>
                                            <option value="user" <%=user.getRole().equals("user") ? "selected" : ""%>>User</option>
                                            <option value="admin" <%=user.getRole().equals("admin") ? "selected" : ""%>>Administrator</option>
                                        </select>
                                    </div>

                                    <div class="d-flex gap-2">
                                        <button type="submit" class="btn btn-primary">
                                            <i class="bi bi-check-lg me-1"></i>Update User
                                        </button>
                                        <a href="<%=request.getContextPath()%>/admin/users?action=view&id=<%=user.getId()%>" 
                                           class="btn btn-outline-secondary">
                                            <i class="bi bi-eye me-1"></i>View User
                                        </a>
                                        <a href="<%=request.getContextPath()%>/admin/users" class="btn btn-outline-secondary">
                                            Cancel
                                        </a>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>