# Secure Complaint Box

A secure web application for organizations to manage and track complaints with encryption, email verification, and admin dashboard.

## 🚀 Features

- **🔐 Secure Authentication**: OTP-based admin login with email verification
- **🔒 Data Encryption**: AES encryption for sensitive complaint data
- **📧 Email Integration**: Gmail SMTP for OTP delivery
- **📊 Admin Dashboard**: Real-time complaint tracking and management
- **🎨 Modern UI**: Responsive design with Tailwind CSS
- **🏢 Multi-Organization**: Support for multiple organizations

## 🛠️ Technology Stack

- **Backend**: Java Servlets (Jakarta EE)
- **Frontend**: HTML, JavaScript, Tailwind CSS
- **Database**: MySQL
- **Security**: AES encryption, BCrypt password hashing
- **Email**: Jakarta Mail (Gmail SMTP)

## 📋 Prerequisites

- Java 21 or higher
- Apache Tomcat 10.1+
- MySQL 8.0+
- Node.js 16+ (for Tailwind CSS build)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/secure-complaint-box.git
cd secure-complaint-box
```

### 2. Set Up Configuration
```bash
# Windows
setup-config.bat

# Or manually copy the example file
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

### 3. Edit Configuration
Edit `src/main/resources/application.properties` with your values:
```properties
# Database
db.url=jdbc:mysql://localhost:3306/secure_complaint_box
db.user=your_db_username
db.password=your_db_password

# Email (Gmail App Password)
email.user=your-email@gmail.com
email.password=your_gmail_app_password

# SMTP
smtp.host=smtp.gmail.com
smtp.port=587

# Encryption (16 characters)
encryption.secret=YourSecretKey16
```

### 4. Build CSS
```bash
# Install dependencies
npm install

# Build CSS for development
npm run build:css

# Build CSS for production
npm run build:css:prod
```

### 5. Set Up Database
```sql
CREATE DATABASE secure_complaint_box;
USE secure_complaint_box;

-- Organizations table
CREATE TABLE organizations (
    org_id VARCHAR(50) PRIMARY KEY,
    org_name VARCHAR(255) NOT NULL,
    admin_email VARCHAR(255) NOT NULL UNIQUE,
    admin_pass_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Complaints table
CREATE TABLE complaints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    org_id VARCHAR(50) NOT NULL,
    reference_code VARCHAR(20) NOT NULL UNIQUE,
    type VARCHAR(100) NOT NULL,
    description_enc TEXT NOT NULL,
    contact_enc TEXT,
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (org_id) REFERENCES organizations(org_id)
);
```

### 6. Deploy to Tomcat
1. Build the project in your IDE
2. Deploy the WAR file to Tomcat
3. Access at `http://localhost:8080/SecureComplaintBox`

## 🔧 Configuration

### Environment Variables (Production)
Set these environment variables on your production server:

```bash
# Database
export DB_URL=jdbc:mysql://your-db-host:3306/secure_complaint_box
export DB_USER=your_production_username
export DB_PASSWORD=your_production_password

# Email
export EMAIL_USER=your-email@gmail.com
export EMAIL_PASSWORD=your_gmail_app_password

# SMTP
export SMTP_HOST=smtp.gmail.com
export SMTP_PORT=587

# Encryption
export ENCRYPTION_SECRET=YourSecretKey16

# Environment
export ENVIRONMENT=production
```

### Gmail App Password Setup
1. Enable 2-Factor Authentication on your Gmail account
2. Go to Google Account Settings > Security > App Passwords
3. Generate an app password for "Mail"
4. Use this password in the `email.password` configuration

## 📁 Project Structure

```
SecureComplaintBox/
├── src/main/java/com/securecomplaintbox/
│   ├── servlets/          # HTTP request handlers
│   ├── util/              # Utility classes
│   └── filter/            # Servlet filters
├── src/main/webapp/
│   ├── public/            # Static web resources
│   │   ├── css/           # Tailwind CSS
│   │   ├── js/            # JavaScript files
│   │   └── *.html         # Web pages
│   └── WEB-INF/           # Web application configuration
├── src/main/resources/
│   ├── application.properties.example  # Configuration template
│   └── application.properties          # Your config (ignored by git)
└── package.json           # Node.js dependencies for Tailwind CSS
```

## 🔒 Security Features

- **AES Encryption**: All sensitive complaint data is encrypted
- **BCrypt Hashing**: Secure password storage
- **OTP Verification**: Email-based two-factor authentication
- **Session Management**: Secure session handling
- **Input Validation**: Comprehensive input sanitization
- **SQL Injection Protection**: Prepared statements
- **Environment Variables**: Secure configuration management

## 🚀 Deployment

### Tomcat Deployment
1. Build the project
2. Deploy WAR file to Tomcat
3. Set environment variables in `setenv.sh`/`setenv.bat`

### Docker Deployment
```yaml
version: '3.8'
services:
  app:
    image: tomcat:10.1
    ports:
      - "8080:8080"
    environment:
      - DB_URL=jdbc:mysql://db:3306/secure_complaint_box
      - DB_USER=your_user
      - DB_PASSWORD=your_password
      - EMAIL_USER=your-email@gmail.com
      - EMAIL_PASSWORD=your_app_password
      - ENCRYPTION_SECRET=YourSecretKey16
    volumes:
      - ./target/SecureComplaintBox.war:/usr/local/tomcat/webapps/SecureComplaintBox.war
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

If you encounter any issues:
1. Check the configuration
2. Verify database connectivity
3. Ensure email settings are correct
4. Check Tomcat logs for errors

## 🔄 Version History

- **v1.0.0**: Initial release with basic complaint management
- **v1.1.0**: Added encryption and security features
- **v1.2.0**: Environment variable support and deployment improvements 