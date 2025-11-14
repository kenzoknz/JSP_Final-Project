<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="model.dao.JobDAO" %>
<%@ page import="model.bean.Job" %>
<%@ page import="model.bean.User" %>
<%@ page import="java.util.List" %>

<%
    // Check if user is logged in
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp?message=Please login to view your conversion history");
        return;
    }

    // Get user's jobs
    JobDAO jobDAO = new JobDAO();
    List<Job> jobs = jobDAO.getJobsByUserId(user.getId());
    request.setAttribute("jobs", jobs);
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch sử chuyển đổi</title>
    <link rel="stylesheet" href="style.css">
    <style>
        .jobs-container {
            max-width: 1000px;
            margin: 30px auto;
            padding: 20px;
        }
        
        .page-header {
            background: linear-gradient(135deg, #007bff, #0056b3);
            color: white;
            padding: 30px;
            border-radius: 10px;
            text-align: center;
            margin-bottom: 30px;
        }
        
        .page-header h1 {
            margin: 0;
            font-size: 28px;
        }
        
        .page-header p {
            margin: 10px 0 0 0;
            opacity: 0.9;
        }
        
        .filters {
            background: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .filter-row {
            display: flex;
            gap: 15px;
            align-items: center;
            flex-wrap: wrap;
        }
        
        .filter-item {
            display: flex;
            flex-direction: column;
        }
        
        .filter-item label {
            font-weight: bold;
            margin-bottom: 5px;
            font-size: 14px;
            color: #555;
        }
        
        .filter-item select,
        .filter-item input {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        
        .jobs-list {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .jobs-header {
            background: #f8f9fa;
            padding: 20px;
            border-bottom: 1px solid #dee2e6;
        }
        
        .jobs-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 20px;
            margin-bottom: 20px;
        }
        
        .stat-card {
            text-align: center;
            padding: 15px;
            background: white;
            border-radius: 6px;
            border: 1px solid #e9ecef;
        }
        
        .stat-value {
            font-size: 24px;
            font-weight: bold;
            color: #007bff;
        }
        
        .stat-label {
            font-size: 12px;
            color: #666;
            text-transform: uppercase;
            margin-top: 5px;
        }
        
        .job-item {
            padding: 20px;
            border-bottom: 1px solid #e9ecef;
            transition: background-color 0.3s;
        }
        
        .job-item:hover {
            background-color: #f8f9fa;
        }
        
        .job-item:last-child {
            border-bottom: none;
        }
        
        .job-header {
            display: flex;
            justify-content: between;
            align-items: flex-start;
            margin-bottom: 15px;
        }
        
        .job-info {
            flex: 1;
        }
        
        .job-title {
            font-size: 16px;
            font-weight: bold;
            color: #333;
            margin-bottom: 5px;
        }
        
        .job-meta {
            display: flex;
            gap: 15px;
            font-size: 14px;
            color: #666;
            margin-bottom: 10px;
            flex-wrap: wrap;
        }
        
        .meta-item {
            display: flex;
            align-items: center;
            gap: 5px;
        }
        
        .job-status {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .status-badge {
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
            text-transform: uppercase;
        }
        
        .status-pending {
            background: #fff3cd;
            color: #856404;
        }
        
        .status-in-progress {
            background: #d1ecf1;
            color: #0c5460;
        }
        
        .status-completed {
            background: #d4edda;
            color: #155724;
        }
        
        .status-failed {
            background: #f8d7da;
            color: #721c24;
        }
        
        .job-actions {
            display: flex;
            gap: 10px;
            margin-top: 15px;
        }
        
        .action-btn {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            font-size: 14px;
            font-weight: bold;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 5px;
            transition: all 0.3s;
        }
        
        .btn-download {
            background: #007bff;
            color: white;
        }
        
        .btn-download:hover {
            background: #0056b3;
        }
        
        .btn-retry {
            background: #ffc107;
            color: #212529;
        }
        
        .btn-retry:hover {
            background: #e0a800;
        }
        
        .btn-delete {
            background: #dc3545;
            color: white;
        }
        
        .btn-delete:hover {
            background: #c82333;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #666;
        }
        
        .empty-state img {
            width: 120px;
            height: 120px;
            opacity: 0.5;
            margin-bottom: 20px;
        }
        
        .navigation {
            text-align: center;
            margin-top: 30px;
        }
        
        .navigation a {
            color: #007bff;
            text-decoration: none;
            margin: 0 15px;
            font-weight: bold;
        }
        
        .navigation a:hover {
            text-decoration: underline;
        }
        
        .pagination {
            display: flex;
            justify-content: center;
            margin: 30px 0;
            gap: 10px;
        }
        
        .page-btn {
            padding: 8px 12px;
            border: 1px solid #ddd;
            background: white;
            color: #007bff;
            text-decoration: none;
            border-radius: 4px;
            transition: all 0.3s;
        }
        
        .page-btn:hover,
        .page-btn.active {
            background: #007bff;
            color: white;
            border-color: #007bff;
        }
    </style>
</head>
<body>
    <div class="jobs-container">
        <!-- Page Header -->
        <div class="page-header">
            <h1>📋 Lịch sử chuyển đổi</h1>
            <p>Quản lý và theo dõi các file đã chuyển đổi của bạn</p>
        </div>

        <!-- Filters -->
        <div class="filters">
            <div class="filter-row">
                <div class="filter-item">
                    <label>Trạng thái</label>
                    <select id="statusFilter">
                        <option value="">Tất cả</option>
                        <option value="COMPLETED">Hoàn thành</option>
                        <option value="PENDING">Đang chờ</option>
                        <option value="IN_PROGRESS">Đang xử lý</option>
                        <option value="FAILED">Thất bại</option>
                    </select>
                </div>
                <div class="filter-item">
                    <label>Loại file</label>
                    <select id="typeFilter">
                        <option value="">Tất cả</option>
                        <option value="DOCX">DOCX</option>
                        <option value="XLSX">XLSX</option>
                        <option value="TXT">TXT</option>
                    </select>
                </div>
                <div class="filter-item">
                    <label>Từ ngày</label>
                    <input type="date" id="fromDate">
                </div>
                <div class="filter-item">
                    <label>Đến ngày</label>
                    <input type="date" id="toDate">
                </div>
            </div>
        </div>

        <!-- Jobs List -->
        <div class="jobs-list">
            <div class="jobs-header">
                <div class="jobs-stats">
                    <%
                        int totalJobs = jobs.size();
                        int completedJobs = 0;
                        int failedJobs = 0;
                        int pendingJobs = 0;
                        
                        for (Job job : jobs) {
                            switch (job.getStatus()) {
                                case COMPLETED: completedJobs++; break;
                                case FAILED: failedJobs++; break;
                                case PENDING:
                                case IN_PROGRESS: pendingJobs++; break;
                            }
                        }
                    %>
                    <div class="stat-card">
                        <div class="stat-value"><%= totalJobs %></div>
                        <div class="stat-label">Tổng số</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value"><%= completedJobs %></div>
                        <div class="stat-label">Hoàn thành</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value"><%= pendingJobs %></div>
                        <div class="stat-label">Đang xử lý</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value"><%= failedJobs %></div>
                        <div class="stat-label">Thất bại</div>
                    </div>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty jobs}">
                    <div class="empty-state">
                        <div style="font-size: 72px; margin-bottom: 20px;">📄</div>
                        <h3>Chưa có file nào được chuyển đổi</h3>
                        <p>Bạn chưa thực hiện chuyển đổi file nào. Hãy bắt đầu ngay!</p>
                        <a href="convert" class="action-btn btn-download" style="margin-top: 20px;">
                            🔄 Chuyển đổi file đầu tiên
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="job" items="${jobs}">
                        <div class="job-item" data-status="${job.status}" data-type="${job.type}">
                            <div class="job-header">
                                <div class="job-info">
                                    <div class="job-title">
                                        📄 ${job.originalFilename != null ? job.originalFilename : 'Unnamed File'}
                                    </div>
                                    <div class="job-meta">
                                        <div class="meta-item">
                                            📅 <fmt:formatDate value="${job.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                        </div>
                                        <div class="meta-item">
                                            🔧 ${job.type}
                                        </div>
                                        <c:if test="${job.fileSize > 0}">
                                            <div class="meta-item">
                                                💾 ${job.getFormattedFileSize()}
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="job-status">
                                    <span class="status-badge status-${job.status.toString().toLowerCase().replace('_', '-')}">
                                        <c:choose>
                                            <c:when test="${job.status == 'PENDING'}">⏳ Đang chờ</c:when>
                                            <c:when test="${job.status == 'IN_PROGRESS'}">🔄 Đang xử lý</c:when>
                                            <c:when test="${job.status == 'COMPLETED'}">✅ Hoàn thành</c:when>
                                            <c:when test="${job.status == 'FAILED'}">❌ Thất bại</c:when>
                                            <c:otherwise>${job.getDisplayStatus()}</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>
                            
                            <c:if test="${not empty job.errorMessage}">
                                <div style="color: #dc3545; font-size: 14px; margin-top: 10px;">
                                    ⚠️ ${job.errorMessage}
                                </div>
                            </c:if>
                            
                            <div class="job-actions">
                                <c:if test="${job.status == 'COMPLETED'}">
                                    <a href="download?file=${job.outputPath.substring(job.outputPath.lastIndexOf('/') + 1)}" 
                                       class="action-btn btn-download">
                                        💾 Tải xuống PDF
                                    </a>
                                </c:if>
                                
                                <c:if test="${job.status == 'FAILED'}">
                                    <a href="convert" class="action-btn btn-retry">
                                        🔄 Thử lại
                                    </a>
                                </c:if>
                                
                                <a href="#" onclick="deleteJob(${job.id})" class="action-btn btn-delete">
                                    🗑️ Xóa
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Navigation -->
        <div class="navigation">
            <a href="convert">🔄 Chuyển đổi file mới</a>
            <a href="index.jsp">🏠 Trang chủ</a>
            <a href="profile.jsp">👤 Hồ sơ</a>
        </div>
    </div>

    <script>
        // Filter functionality
        function applyFilters() {
            const statusFilter = document.getElementById('statusFilter').value;
            const typeFilter = document.getElementById('typeFilter').value;
            const fromDate = document.getElementById('fromDate').value;
            const toDate = document.getElementById('toDate').value;
            
            const jobItems = document.querySelectorAll('.job-item');
            
            jobItems.forEach(item => {
                let show = true;
                
                // Status filter
                if (statusFilter && !item.dataset.status.includes(statusFilter)) {
                    show = false;
                }
                
                // Type filter
                if (typeFilter && !item.dataset.type.includes(typeFilter)) {
                    show = false;
                }
                
                // Date filters would need additional data attributes
                // For now, we'll skip date filtering in the demo
                
                item.style.display = show ? 'block' : 'none';
            });
        }
        
        // Add event listeners for filters
        document.getElementById('statusFilter').addEventListener('change', applyFilters);
        document.getElementById('typeFilter').addEventListener('change', applyFilters);
        document.getElementById('fromDate').addEventListener('change', applyFilters);
        document.getElementById('toDate').addEventListener('change', applyFilters);
        
        // Delete job function
        function deleteJob(jobId) {
            if (confirm('Bạn có chắc chắn muốn xóa job này?')) {
                // In a real application, this would send an AJAX request to delete the job
                alert('Tính năng xóa job sẽ được triển khai sau.');
            }
        }
        
        // Auto refresh every 30 seconds for pending jobs
        setInterval(function() {
            const pendingJobs = document.querySelectorAll('[data-status*="PENDING"], [data-status*="IN_PROGRESS"]');
            if (pendingJobs.length > 0) {
                // In a real application, this would refresh the job status
                console.log('Checking for job updates...');
            }
        }, 30000);
    </script>
</body>
</html>