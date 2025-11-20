<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Convert File to PDF</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/submit.css">
</head>
<body>
    <div class="container">
        <h1>Convert to PDF</h1>
        <p class="subtitle">Convert your documents to PDF with Vietnamese font support</p>
        
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
        
        <div class="supported-formats">
            <h3>Supported Formats:</h3>
            <ul>
                <li><strong>DOCX</strong> - Microsoft Word Document</li>
                <li><strong>XLSX</strong> - Microsoft Excel Spreadsheet</li>
                <li><strong>TXT</strong> - Plain Text File</li>
            </ul>
        </div>
        
        <form action="convert" method="post" enctype="multipart/form-data" id="uploadForm">
            <div class="upload-area" id="uploadArea">
                <p><strong>Click to select file</strong></p>
                <p>or drag and drop file here</p>
                <p style="font-size: 12px; color: #999; margin-top: 10px;">Maximum size: 100MB</p>
                <input type="file" name="file" id="fileInput" class="file-input" 
                       accept=".docx,.xlsx,.txt" required>
                <div class="file-name" id="fileName"></div>
            </div>
            
            <button type="submit" class="btn btn-primary" id="submitBtn" disabled>
                Convert to PDF
            </button>
            
            <button type="button" class="btn btn-secondary" onclick="location.href='jobs'">
                View Conversion History
            </button>
        </form>
        
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/">Home</a>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>
    
    <script>
        const uploadArea = document.getElementById('uploadArea');
        const fileInput = document.getElementById('fileInput');
        const fileName = document.getElementById('fileName');
        const submitBtn = document.getElementById('submitBtn');
        const uploadForm = document.getElementById('uploadForm');
        
        // Click to upload
        uploadArea.addEventListener('click', () => {
            fileInput.click();
        });
        
        // File selected
        fileInput.addEventListener('change', (e) => {
            if (e.target.files.length > 0) {
                const file = e.target.files[0];
                fileName.textContent = file.name;
                fileName.style.display = 'block';
                submitBtn.disabled = false;
            }
        });
        
        // Drag and drop
        uploadArea.addEventListener('dragover', (e) => {
            e.preventDefault();
            uploadArea.style.borderColor = '#764ba2';
            uploadArea.style.background = '#e9ecef';
        });
        
        uploadArea.addEventListener('dragleave', () => {
            uploadArea.style.borderColor = '#667eea';
            uploadArea.style.background = '#f8f9fa';
        });
        
        uploadArea.addEventListener('drop', (e) => {
            e.preventDefault();
            uploadArea.style.borderColor = '#667eea';
            uploadArea.style.background = '#f8f9fa';
            
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                fileInput.files = files;
                fileName.textContent = files[0].name;
                fileName.style.display = 'block';
                submitBtn.disabled = false;
            }
        });
        
        // Handle form submission with AJAX (async)
        uploadForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const formData = new FormData(uploadForm);
            
            // Disable button and show loading
            submitBtn.disabled = true;
            submitBtn.textContent = 'Uploading...';
            
            try {
                const response = await fetch('convert', {
                    method: 'POST',
                    body: formData
                });
                
                const result = await response.json();
                
                if (result.status === 'queued') {
                    // Success - redirect to jobs page with message
                    window.location.href = 'jobs?message=' + encodeURIComponent(result.message);
                } else {
                    // Error
                    alert('Error: ' + (result.message || 'Could not create conversion job'));
                    submitBtn.disabled = false;
                    submitBtn.textContent = 'Convert to PDF';
                }
            } catch (error) {
                console.error('Upload error:', error);
                alert('Server connection error: ' + error.message);
                submitBtn.disabled = false;
                submitBtn.textContent = 'Convert to PDF';
            }
        });
    </script>
</body>
</html>
