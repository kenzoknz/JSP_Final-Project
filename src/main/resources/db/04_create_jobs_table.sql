-- Tạo bảng jobs để lưu trữ lịch sử xử lý file của người dùng
CREATE TABLE IF NOT EXISTS jobs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type VARCHAR(50) NOT NULL COMMENT 'Loại file: DOCX, XLSX, TXT',
    status ENUM('PENDING','IN_PROGRESS','COMPLETED','FAILED') DEFAULT 'PENDING' COMMENT 'Trạng thái xử lý',
    input_path VARCHAR(255) NOT NULL COMMENT 'Đường dẫn file gốc',
    output_path VARCHAR(255) NULL COMMENT 'Đường dẫn file PDF đã convert',
    original_filename VARCHAR(255) NOT NULL COMMENT 'Tên file gốc do user upload',
    file_size BIGINT DEFAULT 0 COMMENT 'Kích thước file (bytes)',
    error_message TEXT NULL COMMENT 'Thông báo lỗi nếu có',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo job',
    started_at TIMESTAMP NULL COMMENT 'Thời gian bắt đầu xử lý',
    finished_at TIMESTAMP NULL COMMENT 'Thời gian hoàn thành',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_user_created (user_id, created_at DESC),
    INDEX idx_status (status),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Bảng lưu trữ lịch sử xử lý convert file sang PDF';