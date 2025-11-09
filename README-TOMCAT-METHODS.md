# Các Cách Chạy Tomcat cho Dự Án JSP + Servlet

## 1. Triển Khai WAR trên Tomcat Standalone (Phương pháp truyền thống)

### Bước 1: Build project
```bash
mvn clean package
```

### Bước 2: Copy WAR file
```bash
# Copy file WAR đến thư mục webapps của Tomcat
copy target\basic-project.war D:\apache-tomcat-9.0.111\webapps\
```

### Bước 3: Khởi động Tomcat
```bash
# Khởi động Tomcat
D:\apache-tomcat-9.0.111\bin\startup.bat

# Truy cập ứng dụng
http://localhost:8080/basic-project/
```

### Ưu điểm:
- Production-ready
- Hiệu suất cao
- Cấu hình chi tiết

### Nhược điểm:
- Cần cài đặt Tomcat riêng
- Phải build và copy WAR mỗi lần thay đổi
- Chậm trong quá trình phát triển

## 2. Embedded Tomcat qua Maven Plugin (Phương pháp phát triển)

### Cấu hình trong pom.xml:
```xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <port>8080</port>
        <contextPath>/basic-project</contextPath>
    </configuration>
</plugin>
```

### Chạy:
```bash
mvn tomcat7:run
```

### Truy cập:
```
http://localhost:8080/basic-project/
```

### Ưu điểm:
- Không cần cài đặt Tomcat
- Tự động hot-reload
- Nhanh chóng cho phát triển
- Tích hợp với Maven

### Nhược điểm:
- Chỉ phù hợp cho development
- Hiệu suất thấp hơn standalone

## 3. Embedded Jetty qua Maven Plugin (Phương pháp thay thế)

### Cấu hình trong pom.xml:
```xml
<plugin>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-maven-plugin</artifactId>
    <version>9.4.53.v20231009</version>
    <configuration>
        <httpConnector>
            <port>8081</port>
        </httpConnector>
        <webApp>
            <contextPath>/basic-project</contextPath>
        </webApp>
    </configuration>
</plugin>
```

### Chạy:
```bash
mvn jetty:run
```

### Truy cập:
```
http://localhost:8081/basic-project/
```

### Ưu điểm:
- Nhẹ và nhanh
- Hot-reload tốt
- Tích hợp Maven
- Dễ cấu hình

### Nhược điểm:
- Khác biệt nhỏ với Tomcat
- Cần học thêm về Jetty

## 4. Spring Boot Embedded Tomcat (Phương pháp hiện đại)

### Cấu hình:
- Chuyển packaging từ `war` sang `jar`
- Thêm Spring Boot starter
- Tự động embedded Tomcat

### Ưu điểm:
- Microservice ready
- Packaging đơn giản
- Production ready
- Tự quản lý dependencies

### Nhược điểm:
- Cần học Spring Boot
- Thay đổi cấu trúc project

## So Sánh và Khuyến Nghị

| Phương pháp | Development | Production | Học tập | Performance |
|-------------|-------------|------------|---------|-------------|
| WAR + Standalone | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| Maven Tomcat Plugin | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| Maven Jetty Plugin | ⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| Spring Boot | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐ |

## Khuyến Nghị Sử Dụng

### Cho Học Tập & Phát Triển:
1. **Maven Tomcat Plugin** - Sử dụng `mvn tomcat7:run`
2. **Maven Jetty Plugin** - Sử dụng `mvn jetty:run` (port 8081)

### Cho Production:
1. **WAR + Standalone Tomcat** - Build và deploy WAR file

### Workflow Phát Triển Khuyến Nghị:
```bash
# 1. Phát triển với embedded server
mvn tomcat7:run

# 2. Test thay đổi real-time
# (Server tự động reload khi thay đổi file)

# 3. Build cho production
mvn clean package

# 4. Deploy WAR file lên production server
```

## Lệnh Hữu Ích

### Dừng server đang chạy:
```bash
# Dừng tất cả Java processes
taskkill /f /im java.exe

# Hoặc Ctrl+C trong terminal đang chạy server
```

### Kiểm tra port đang sử dụng:
```bash
netstat -ano | findstr :8080
```

### Thay đổi port:
- Tomcat plugin: Sửa `<port>8080</port>` trong pom.xml
- Jetty plugin: Sửa `<port>8081</port>` trong pom.xml