<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.bean.User" %>
<%
    User user = (User) request.getAttribute("user");
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
    <title>View User - Admin Panel</title>
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
                            <a class="nav-link" href="<%=request.getContextPath()%>/submit.jsp">
                                <i class="bi bi-file-earmark-pdf me-2"></i>Convert
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
                    <h1 class="h2">View User</h1>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <a href="<%=request.getContextPath()%>/admin/users" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left me-1"></i>Back to Users
                        </a>
                    </div>
                </div>

                <div class="row">
                    <div class="col-lg-8">
                        <div class="card">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">User Details</h5>
                                <div>
                                    <a href="<%=request.getContextPath()%>/admin/users?action=edit&id=<%=user.getId()%>" 
                                       class="btn btn-primary btn-sm">
                                        <i class="bi bi-pencil me-1"></i>Edit
                                    </a>
                                </div>
                            </div>
                            <div class="card-body">
                                <div class="row mb-3">
                                    <div class="col-sm-4">
                                        <strong>User ID:</strong>
                                    </div>
                                    <div class="col-sm-8">
                                        <%=user.getId()%>
                                    </div>
                                </div>

                                <div class="row mb-3">
                                    <div class="col-sm-4">
                                        <strong>Username:</strong>
                                    </div>
                                    <div class="col-sm-8">
                                        <%=user.getUsername()%>
                                    </div>
                                </div>

                                <div class="row mb-3">
                                    <div class="col-sm-4">
                                        <strong>Email:</strong>
                                    </div>
                                    <div class="col-sm-8">
                                        <%=user.getEmail()%>
                                    </div>
                                </div>

                                <div class="row mb-3">
                                    <div class="col-sm-4">
                                        <strong>Role:</strong>
                                    </div>
                                    <div class="col-sm-8">
                                        <% if (user.isAdmin()) { %>
                                            <span class="badge bg-danger">Administrator</span>
                                        <% } else { %>
                                            <span class="badge bg-primary">User</span>
                                        <% } %>
                                    </div>
                                </div>

                                <div class="row mb-3">
                                    <div class="col-sm-4">
                                        <strong>Created Date:</strong>
                                    </div>
                                    <div class="col-sm-8">
                                        <%=user.getCreatedAt() != null ? user.getCreatedAt().toString() : "Not available"%>
                                    </div>
                                </div>
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