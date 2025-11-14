<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chuyển đổi file sang PDF</title>
    <link rel="stylesheet" href="style.css">
    <style>
        .upload-container {
            max-width: 600px;
            margin: 50px auto;
            padding: 30px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
        }
        
        .upload-header {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .upload-header h1 {
            color: #333;
            margin-bottom: 10px;
        }
        
        .upload-header p {
            color: #666;
            font-size: 14px;
        }
        
        .upload-form {
            margin-bottom: 30px;
        }
        
        .file-input-container {
            position: relative;
            margin: 20px 0;
        }
        
        .file-input {
            width: 100%;
            padding: 15px;
            border: 2px dashed #ddd;
            border-radius: 5px;
            background: #f9f9f9;
            transition: border-color 0.3s;
        }
        
        .file-input:hover {
            border-color: #007bff;
        }
        
        .file-info {
            margin: 15px 0;
            padding: 10px;
            background: #f8f9fa;
            border-radius: 5px;
            font-size: 14px;
        }
        
        .file-info ul {
            margin: 10px 0;
            padding-left: 20px;
        }
        
        .submit-btn {
            width: 100%;
            padding: 15px;
            background: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        
        .submit-btn:hover {
            background: #0056b3;
        }
        
        .submit-btn:disabled {
            background: #ccc;
            cursor: not-allowed;
        }
        
        .alert {
            padding: 15px;
            margin: 20px 0;
            border-radius: 5px;
            font-weight: bold;
        }
        
        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
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
        
        .progress-container {
            display: none;
            margin: 20px 0;
        }
        
        .progress-bar {
            width: 100%;
            height: 20px;
            background: #e9ecef;
            border-radius: 10px;
            overflow: hidden;
        }
        
        .progress-fill {
            height: 100%;
            background: #007bff;
            width: 0%;
            transition: width 0.3s ease;
        }
    </style>
</head>
<body>
    <div class="upload-container">
        <div class="upload-header">
            <h1>🔄 Chuyển đổi File sang PDF</h1>
            <p>Hỗ trợ các định dạng: DOCX, XLSX, TXT</p>
        </div>

        <!-- Error/Success Messages -->
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ❌ ${error}
            </div>
        </c:if>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                ✅ ${success}
            </div>
        </c:if>

        <!-- Upload Form -->
        <form action="convert" method="post" enctype="multipart/form-data" class="upload-form" id="uploadForm">
            <div class="file-input-container">
                <input type="file" 
                       id="fileInput" 
                       name="file" 
                       class="file-input" 
                       accept=".docx,.xlsx,.txt"
                       required>
            </div>

            <div class="file-info">
                <strong>📋 Thông tin:</strong>
                <ul>
                    <li><strong>Định dạng hỗ trợ:</strong> DOCX, XLSX, TXT</li>
                    <li><strong>Kích thước tối đa:</strong> 5 MB</li>
                    <li><strong>Đầu ra:</strong> File PDF</li>
                </ul>
            </div>

            <div class="progress-container" id="progressContainer">
                <div class="progress-bar">
                    <div class="progress-fill" id="progressFill"></div>
                </div>
                <p style="text-align: center; margin-top: 10px;">Đang xử lý...</p>
            </div>

            <button type="submit" class="submit-btn" id="submitBtn">
                🚀 Chuyển đổi sang PDF
            </button>
        </form>

        <div class="navigation">
            <a href="index.jsp">🏠 Trang chủ</a>
            <a href="myJobs.jsp">📄 Lịch sử chuyển đổi</a>
            <a href="profile.jsp">👤 Hồ sơ</a>
        </div>
    </div>

    <script>
        document.getElementById('uploadForm').addEventListener('submit', function(e) {
            const fileInput = document.getElementById('fileInput');
            const file = fileInput.files[0];
            
            if (!file) {
                e.preventDefault();
                alert('Vui lòng chọn file để chuyển đổi!');
                return;
            }
            
            // Check file size (5MB limit)
            if (file.size > 5 * 1024 * 1024) {
                e.preventDefault();
                alert('File quá lớn! Kích thước tối đa là 5MB.');
                return;
            }
            
            // Check file type
            const allowedTypes = ['.docx', '.xlsx', '.txt'];
            const fileName = file.name.toLowerCase();
            const isValidType = allowedTypes.some(type => fileName.endsWith(type));
            
            if (!isValidType) {
                e.preventDefault();
                alert('Định dạng file không được hỗ trợ! Chỉ chấp nhận DOCX, XLSX, TXT.');
                return;
            }
            
            // Show progress
            document.getElementById('progressContainer').style.display = 'block';
            document.getElementById('submitBtn').disabled = true;
            document.getElementById('submitBtn').textContent = '⏳ Đang xử lý...';
            
            // Simulate progress (since we don't have real progress tracking)
            let progress = 0;
            const progressInterval = setInterval(() => {
                progress += Math.random() * 15;
                if (progress > 90) progress = 90;
                document.getElementById('progressFill').style.width = progress + '%';
            }, 200);
            
            // Clear interval after form submission
            setTimeout(() => clearInterval(progressInterval), 1000);
        });
        
        // File input change handler
        document.getElementById('fileInput').addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const fileSize = (file.size / 1024 / 1024).toFixed(2);
                const fileType = file.name.split('.').pop().toUpperCase();
                
                // Update file info
                const fileInfo = document.querySelector('.file-info');
                fileInfo.innerHTML = `
                    <strong>📄 File đã chọn:</strong><br>
                    <strong>Tên:</strong> ${file.name}<br>
                    <strong>Loại:</strong> ${fileType}<br>
                    <strong>Kích thước:</strong> ${fileSize} MB<br>
                    <strong>Trạng thái:</strong> <span style="color: green;">✓ Sẵn sàng chuyển đổi</span>
                `;
            }
        });
    </script>
</body>
</html>