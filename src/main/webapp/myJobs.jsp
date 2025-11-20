<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Conversion Jobs</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/jobs.css">
</head>
<body>
    <div class="container">
        <h1>Conversion History</h1>
        <p class="subtitle">Manage your converted PDF files</p>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ${error}
            </div>
        </c:if>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                ${success}
            </div>
        </c:if>
        
        <div class="action-bar">
            <div>
                <strong>Total: ${not empty jobs ? jobs.size() : 0} job(s)</strong>
            </div>
            <div>
                <a href="convert" class="btn btn-primary">+ Convert New File</a>
                <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">Home</a>
            </div>
        </div>
        
        <c:choose>
            <c:when test="${empty jobs}">
                <div class="empty-state">
                    <i>📂</i>
                    <h2>No jobs yet</h2>
                    <p>You haven't converted any files yet. Try converting your first file!</p>
                    <br>
                    <a href="convert" class="btn btn-primary">Convert Now</a>
                </div>
            </c:when>
            <c:otherwise>
                <table class="jobs-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>File Name</th>
                            <th>Type</th>
                            <th>Size</th>
                            <th>Status</th>
                            <th>Created At</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="job" items="${jobs}">
                            <tr>
                                <td>#${job.id}</td>
                                <td>
                                    <div class="file-info">
                                        <div>
                                            <div><strong>${job.originalFilename}</strong></div>
                                            <c:if test="${job.status == 'FAILED' and not empty job.errorMessage}">
                                                <div class="error-message">${job.errorMessage}</div>
                                            </c:if>
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <span class="file-type">${job.type}</span>
                                </td>
                                <td>
                                    <fmt:formatNumber value="${job.fileSize / 1024}" maxFractionDigits="2"/> KB
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${job.status == 'PENDING'}">
                                            <span class="status-badge status-pending">Pending</span>
                                        </c:when>
                                        <c:when test="${job.status == 'IN_PROGRESS'}">
                                            <span class="status-badge status-in-progress">In Progress</span>
                                        </c:when>
                                        <c:when test="${job.status == 'COMPLETED'}">
                                            <span class="status-badge status-completed">Completed</span>
                                        </c:when>
                                        <c:when test="${job.status == 'FAILED'}">
                                            <span class="status-badge status-failed">Failed</span>
                                        </c:when>
                                    </c:choose>
                                </td>
                                <td>
                                    <fmt:formatDate value="${job.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </td>
                                <td>
                                    <c:if test="${job.status == 'COMPLETED'}">
                                        <a href="download?jobId=${job.id}" class="btn btn-download">Download</a>
                                    </c:if>
                                    <c:if test="${job.status == 'PENDING'}">
                                        <form method="post" action="jobs" style="display:inline;">
                                            <input type="hidden" name="action" value="cancel">
                                            <input type="hidden" name="jobId" value="${job.id}">
                                            <button type="submit" class="btn btn-cancel" 
                                                    onclick="return confirm('Are you sure you want to cancel this job?');">
                                                Cancel
                                            </button>
                                        </form>
                                    </c:if>
                                    <form method="post" action="jobs" style="display:inline;">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="jobId" value="${job.id}">
                                        <button type="submit" class="btn btn-danger" 
                                                onclick="return confirm('Are you sure you want to delete this job?');">
                                            Delete
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
    
    <script>
        // Auto-refresh for active jobs
        let hasActiveJobs = false;
        
        // Check for active jobs
        <c:forEach var="job" items="${jobs}">
            <c:if test="${job.status == 'PENDING' or job.status == 'IN_PROGRESS'}">
                hasActiveJobs = true;
            </c:if>
        </c:forEach>
        
        // Refresh every 5s if active
        if (hasActiveJobs) {
            console.log('Active jobs detected - auto-refresh enabled');
            setTimeout(() => {
                window.location.reload();
            }, 5000);
        }
        
        // AJAX polling alternative (commented out)
        /*
        // Poll job status via AJAX
        */
    </script>
</body>
</html>

