<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chuyển đổi thành công</title>
    <link rel="stylesheet" href="style.css">
    <style>
        .success-container {
            max-width: 600px;
            margin: 50px auto;
            padding: 30px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
            text-align: center;
        }
        
        .success-icon {
            font-size: 72px;
            color: #28a745;
            margin-bottom: 20px;
        }
        
        .success-title {
            color: #28a745;
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 15px;
        }
        
        .success-message {
            color: #666;
            font-size: 16px;
            margin-bottom: 30px;
            line-height: 1.5;
        }
        
        .file-info {
            background: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 20px;
            margin: 20px 0;
            text-align: left;
        }
        
        .file-info h3 {
            color: #333;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
        }
        
        .file-info h3 i {
            margin-right: 10px;
        }
        
        .file-details {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
            margin-bottom: 20px;
        }
        
        .detail-item {
            display: flex;
            flex-direction: column;
        }
        
        .detail-label {
            font-weight: bold;
            color: #555;
            font-size: 12px;
            text-transform: uppercase;
            margin-bottom: 5px;
        }
        
        .detail-value {
            color: #333;
            font-size: 14px;
        }
        
        .download-section {
            background: linear-gradient(135deg, #007bff, #0056b3);
            color: white;
            padding: 25px;
            border-radius: 8px;
            margin: 20px 0;
        }
        
        .download-btn {
            display: inline-block;
            background: rgba(255,255,255,0.2);
            color: white;
            padding: 15px 30px;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            font-size: 16px;
            transition: background 0.3s;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255,255,255,0.3);
        }
        
        .download-btn:hover {
            background: rgba(255,255,255,0.3);
            transform: translateY(-2px);
        }
        
        .actions {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 30px;
            flex-wrap: wrap;
        }
        
        .action-btn {
            padding: 12px 25px;
            border: none;
            border-radius: 5px;
            font-weight: bold;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s;
        }
        
        .btn-primary {
            background: #007bff;
            color: white;
        }
        
        .btn-primary:hover {
            background: #0056b3;
            transform: translateY(-2px);
        }
        
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        
        .btn-secondary:hover {
            background: #545b62;
            transform: translateY(-2px);
        }
        
        .btn-outline {
            background: transparent;
            color: #007bff;
            border: 2px solid #007bff;
        }
        
        .btn-outline:hover {
            background: #007bff;
            color: white;
        }
        
        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
            gap: 15px;
            margin: 20px 0;
        }
        
        .stat-item {
            text-align: center;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
        }
        
        .stat-value {
            font-size: 20px;
            font-weight: bold;
            color: #007bff;
        }
        
        .stat-label {
            font-size: 12px;
            color: #666;
            text-transform: uppercase;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <div class="success-container">
        <!-- Success Icon and Message -->
        <div class="success-icon">✅</div>
        <h1 class="success-title">Chuyển đổi thành công!</h1>
        <p class="success-message">
            File của bạn đã được chuyển đổi sang PDF thành công. 
            Bạn có thể tải xuống ngay bây giờ hoặc quay lại để chuyển đổi file khác.
        </p>

        <!-- File Information -->
        <div class="file-info">
            <h3>📄 Thông tin file</h3>
            <div class="file-details">
                <div class="detail-item">
                    <span class="detail-label">File gốc</span>
                    <span class="detail-value">${originalFile}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">File PDF</span>
                    <span class="detail-value">${downloadFile}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Trạng thái</span>
                    <span class="detail-value" style="color: #28a745;">✓ Hoàn thành</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Thời gian</span>
                    <span class="detail-value" id="currentTime"></span>
                </div>
            </div>
        </div>

        <!-- Download Section -->
        <div class="download-section">
            <h3 style="margin-bottom: 15px;">📥 Tải xuống file PDF</h3>
            <p style="margin-bottom: 20px; opacity: 0.9;">
                File PDF đã sẵn sàng để tải xuống. Click vào nút bên dưới để lưu về máy.
            </p>
            <a href="download?file=${downloadFile}" class="download-btn">
                💾 Tải xuống PDF
            </a>
        </div>

        <!-- Quick Stats -->
        <div class="stats">
            <div class="stat-item">
                <div class="stat-value">PDF</div>
                <div class="stat-label">Định dạng</div>
            </div>
            <div class="stat-item">
                <div class="stat-value">100%</div>
                <div class="stat-label">Thành công</div>
            </div>
            <div class="stat-item">
                <div class="stat-value">&lt; 1s</div>
                <div class="stat-label">Thời gian</div>
            </div>
        </div>

        <!-- Action Buttons -->
        <div class="actions">
            <a href="convert" class="action-btn btn-primary">
                🔄 Chuyển đổi file khác
            </a>
            <a href="myJobs.jsp" class="action-btn btn-outline">
                📋 Lịch sử chuyển đổi
            </a>
            <a href="index.jsp" class="action-btn btn-secondary">
                🏠 Về trang chủ
            </a>
        </div>
    </div>

    <script>
        // Display current time
        function updateTime() {
            const now = new Date();
            const timeString = now.toLocaleString('vi-VN');
            document.getElementById('currentTime').textContent = timeString;
        }
        
        updateTime();
        
        // Auto refresh time every minute
        setInterval(updateTime, 60000);
        
        // Add some animation
        document.addEventListener('DOMContentLoaded', function() {
            const container = document.querySelector('.success-container');
            container.style.opacity = '0';
            container.style.transform = 'translateY(20px)';
            
            setTimeout(() => {
                container.style.transition = 'all 0.5s ease';
                container.style.opacity = '1';
                container.style.transform = 'translateY(0)';
            }, 100);
        });

        // Add click tracking for analytics (optional)
        document.querySelector('.download-btn').addEventListener('click', function() {
            console.log('PDF download initiated:', '${downloadFile}');
        });
    </script>
</body>
</html>