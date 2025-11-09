# Giai đoạn 2 - Hướng Dẫn Setup và Test Database MVC

## 📋 Tổng quan

Giai đoạn 2 đã hoàn thành việc xây dựng hệ thống MVC với database MySQL bao gồm:

### ✅ Deliverables Hoàn Thành:

1. **Database Setup**: Cơ sở dữ liệu `projectdb` với bảng `users`
2. **Model Layer**: User POJO, UserDAO với CRUD operations
3. **View Layer**: JSP pages với Bootstrap UI
4. **Controller Layer**: UserController servlet xử lý HTTP requests
5. **Database Connection**: DBConnection singleton với HikariCP connection pooling

## 🛠️ Cấu Trúc Project

```
jsp-servlet-basic/
├── src/main/java/com/example/
│   ├── config/
│   │   └── DBConnection.java          # Database connection manager
│   ├── model/
│   │   └── User.java                  # User POJO model
│   ├── dao/
│   │   └── UserDAO.java               # Data Access Object
│   └── servlet/
│       ├── HelloServlet.java          # Original servlet
│       └── UserController.java        # User management controller
├── src/main/webapp/
│   ├── index.jsp                      # Home page with navigation
│   └── WEB-INF/
│       ├── web.xml                    # Servlet configuration
│       └── jsp/
│           ├── userList.jsp           # User listing page
│           └── error.jsp              # Error handling page
├── database.sql                       # Database setup script
└── pom.xml                           # Maven dependencies
```

## 🔧 Setup Instructions

### Bước 1: Cài Đặt và Cấu Hình MySQL

1. **Cài đặt MySQL Server** (nếu chưa có):
   - Download từ: https://dev.mysql.com/downloads/mysql/
   - Hoặc dùng XAMPP/WAMP

2. **Khởi động MySQL Server**:
   ```bash
   # Windows (với XAMPP)
   Start XAMPP Control Panel → Start MySQL
   
   # Hoặc Windows Service
   net start mysql
   ```

3. **Kết nối MySQL**:
   ```bash
   mysql -u root -p
   ```

### Bước 2: Tạo Database và Tables

1. **Chạy Database Script**:
   ```bash
   mysql -u root -p < database.sql
   ```

2. **Hoặc copy-paste trong MySQL Command Line**:
   ```sql
   source /path/to/database.sql
   ```

3. **Verify Database**:
   ```sql
   USE projectdb;
   SHOW TABLES;
   SELECT * FROM users;
   ```

### Bước 3: Cấu Hình Database Connection

1. **Mở file DBConnection.java**:
   ```java
   // Dòng 44-47: Cấu hình database
   private static final String DB_URL = "jdbc:mysql://localhost:3306/projectdb?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
   private static final String DB_USERNAME = "root";
   private static final String DB_PASSWORD = ""; // Đổi thành password MySQL của bạn
   ```

2. **Cập nhật password** theo cài đặt MySQL của bạn

### Bước 4: Build và Deploy

1. **Build Project**:
   ```bash
   mvn clean package
   ```

2. **Chạy với Embedded Tomcat**:
   ```bash
   mvn tomcat7:run
   ```

3. **Hoặc chạy với Jetty**:
   ```bash
   mvn jetty:run
   ```

## 🧪 Testing Instructions

### Test 1: Kiểm Tra Database Connection

1. **Truy cập**: http://localhost:8080/basic-project/users
2. **Kết quả mong đợi**:
   - Hiển thị danh sách 4 users mẫu
   - Không có lỗi database connection

### Test 2: CRUD Operations

#### Create User:
1. Click "Add New User" button
2. Nhập thông tin trong prompt boxes:
   - Username: `test_user_new`
   - Email: `newuser@example.com`
   - Password: `password123`
3. **Kết quả**: User mới xuất hiện trong danh sách

#### Read/List Users:
1. **Truy cập**: http://localhost:8080/basic-project/users
2. **Kiểm tra**: Danh sách hiển thị tất cả users
3. **Kiểm tra**: Search functionality hoạt động

#### Delete User:
1. Click nút delete (trash icon) của user
2. Confirm deletion
3. **Kết quả**: User bị xóa khỏi danh sách

### Test 3: MVC Architecture

#### Model Test:
```java
// UserDAO methods đã implement:
- insert(User user)          ✅
- findByEmail(String email)  ✅  
- login(String email, String passwordHash) ✅
- listAll()                  ✅
```

#### View Test:
- **JSP hiển thị data** từ database ✅
- **Bootstrap styling** responsive ✅
- **JSTL tags** hoạt động ✅

#### Controller Test:
- **GET /users** → hiển thị danh sách ✅
- **POST /users?action=create** → tạo user ✅
- **POST /users?action=delete** → xóa user ✅

## 🔍 Troubleshooting

### Lỗi Database Connection:

1. **MySQL not running**:
   ```bash
   # Khởi động MySQL
   net start mysql
   ```

2. **Wrong password**:
   - Cập nhật `DB_PASSWORD` trong `DBConnection.java`

3. **Database không tồn tại**:
   ```sql
   CREATE DATABASE projectdb;
   USE projectdb;
   source database.sql;
   ```

### Lỗi Build:

1. **Dependencies not found**:
   ```bash
   mvn clean install
   ```

2. **Java version mismatch**:
   - Kiểm tra Java 8+ được cài đặt

### Lỗi Runtime:

1. **ClassNotFoundException**:
   - Kiểm tra MySQL Connector trong dependencies

2. **500 Internal Server Error**:
   - Check server logs trong terminal
   - Verify database connection

## 📊 Database Schema

### Bảng `users`:

| Column | Type | Constraints |
|--------|------|-------------|
| id | INT | PRIMARY KEY AUTO_INCREMENT |
| username | VARCHAR(50) | NOT NULL UNIQUE |
| email | VARCHAR(100) | NOT NULL UNIQUE |
| password_hash | VARCHAR(255) | NOT NULL |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE |

### Sample Data:

| ID | Username | Email | Password (Hashed) |
|----|----------|-------|------------------|
| 1 | admin | admin@example.com | SHA256('admin123') |
| 2 | john_doe | john@example.com | SHA256('password123') |
| 3 | jane_smith | jane@example.com | SHA256('mypassword') |
| 4 | test_user | test@example.com | SHA256('test123') |

## 🎯 URLs để Test

| URL | Method | Description |
|-----|--------|-------------|
| http://localhost:8080/basic-project/ | GET | Home page |
| http://localhost:8080/basic-project/users | GET | User list |
| http://localhost:8080/basic-project/users?action=create | POST | Create user |
| http://localhost:8080/basic-project/users?action=delete&id=X | POST | Delete user |

## 📝 Các Tính Năng Đã Implement

### ✅ Required Features:
- [x] Database `projectdb` với bảng `users`
- [x] DBConnection.java singleton pattern
- [x] User.java POJO model
- [x] UserDAO.java với insert(), findByEmail(), login(), listAll()
- [x] JSP hiển thị danh sách users
- [x] MVC pattern rõ ràng (Model-View-Controller)

### ✅ Bonus Features:
- [x] HikariCP connection pooling
- [x] Bootstrap responsive UI
- [x] Search functionality
- [x] Error handling
- [x] Logging với SLF4J
- [x] Input validation
- [x] Password hashing (SHA-256)

## 🚀 Next Steps (Giai đoạn 3)

1. **User Authentication System**
2. **Session Management**
3. **Form-based CRUD**
4. **Advanced UI Features**
5. **Security Enhancements**

---

**🎉 Giai đoạn 2 hoàn thành thành công!** 

Database và MVC architecture đã được thiết lập và test thành công.