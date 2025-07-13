# Deployment Guide - Secure Complaint Box

## 🔒 Security Configuration

### Environment Variables Required

Before deploying to production, set these environment variables:

```bash
# Environment
ENVIRONMENT=production

# Database Configuration
DB_URL=jdbc:mysql://your-production-db-host:3306/secure_complaint_box
DB_USER=your_production_db_username
DB_PASSWORD=your_secure_production_password

# Email Configuration (Gmail App Password)
EMAIL_USER=your-production-email@gmail.com
EMAIL_PASSWORD=your_production_gmail_app_password

# SMTP Configuration
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587

# Encryption Configuration (Must be exactly 16 characters)
ENCRYPTION_SECRET=YourProductionSecret16
```

## 🚀 Deployment Methods

### Method 1: Tomcat with Environment Variables

#### Linux/Mac (setenv.sh)
```bash
#!/bin/bash
export ENVIRONMENT=production
export DB_URL=jdbc:mysql://localhost:3306/secure_complaint_box
export DB_USER=your_db_username
export DB_PASSWORD=your_db_password
export EMAIL_USER=your-email@gmail.com
export EMAIL_PASSWORD=your_gmail_app_password
export SMTP_HOST=smtp.gmail.com
export SMTP_PORT=587
export ENCRYPTION_SECRET=YourSecretKey16
```

#### Windows (setenv.bat)
```batch
set ENVIRONMENT=production
set DB_URL=jdbc:mysql://localhost:3306/secure_complaint_box
set DB_USER=your_db_username
set DB_PASSWORD=your_db_password
set EMAIL_USER=your-email@gmail.com
set EMAIL_PASSWORD=your_gmail_app_password
set SMTP_HOST=smtp.gmail.com
set SMTP_PORT=587
set ENCRYPTION_SECRET=YourSecretKey16
```

### Method 2: Docker Deployment

```yaml
version: '3.8'
services:
  app:
    image: tomcat:10.1
    ports:
      - "8080:8080"
    environment:
      - ENVIRONMENT=production
      - DB_URL=jdbc:mysql://db:3306/secure_complaint_box
      - DB_USER=your_user
      - DB_PASSWORD=your_password
      - EMAIL_USER=your-email@gmail.com
      - EMAIL_PASSWORD=your_app_password
      - SMTP_HOST=smtp.gmail.com
      - SMTP_PORT=587
      - ENCRYPTION_SECRET=YourSecretKey16
    volumes:
      - ./target/SecureComplaintBox.war:/usr/local/tomcat/webapps/SecureComplaintBox.war
    depends_on:
      - db
  
  db:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=your_root_password
      - MYSQL_DATABASE=secure_complaint_box
      - MYSQL_USER=your_user
      - MYSQL_PASSWORD=your_password
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database/init.sql:/docker-entrypoint-initdb.d/init.sql

volumes:
  mysql_data:
```

### Method 3: Cloud Platform Deployment

#### Heroku
```bash
heroku config:set ENVIRONMENT=production
heroku config:set DB_URL=jdbc:mysql://your-db-host:3306/secure_complaint_box
heroku config:set DB_USER=your_db_username
heroku config:set DB_PASSWORD=your_db_password
heroku config:set EMAIL_USER=your-email@gmail.com
heroku config:set EMAIL_PASSWORD=your_gmail_app_password
heroku config:set SMTP_HOST=smtp.gmail.com
heroku config:set SMTP_PORT=587
heroku config:set ENCRYPTION_SECRET=YourSecretKey16
```

#### AWS Elastic Beanstalk
Create `.ebextensions/environment.config`:
```yaml
option_settings:
  aws:elasticbeanstalk:application:environment:
    ENVIRONMENT: production
    DB_URL: jdbc:mysql://your-db-host:3306/secure_complaint_box
    DB_USER: your_db_username
    DB_PASSWORD: your_db_password
    EMAIL_USER: your-email@gmail.com
    EMAIL_PASSWORD: your_gmail_app_password
    SMTP_HOST: smtp.gmail.com
    SMTP_PORT: 587
    ENCRYPTION_SECRET: YourSecretKey16
```

## 🔐 Security Best Practices

### 1. Database Security
- Use dedicated database user with minimal privileges
- Enable SSL/TLS for database connections
- Regularly rotate database passwords
- Use strong, unique passwords

### 2. Email Security
- Use Gmail App Passwords (not regular passwords)
- Enable 2-Factor Authentication on Gmail account
- Regularly rotate app passwords
- Monitor email sending logs

### 3. Encryption Security
- Generate a strong 16-character encryption secret
- Never reuse encryption secrets across environments
- Store encryption secrets securely
- Regularly rotate encryption secrets

### 4. Application Security
- Use HTTPS in production
- Set secure session timeouts
- Implement rate limiting
- Regular security updates

## 📋 Pre-Deployment Checklist

- [ ] Environment variables configured
- [ ] Database created and accessible
- [ ] Email configuration tested
- [ ] Encryption secret generated
- [ ] HTTPS certificate installed
- [ ] Firewall rules configured
- [ ] Backup strategy implemented
- [ ] Monitoring setup
- [ ] Security testing completed

## 🧪 Testing Configuration

Add this to your application startup to verify configuration:

```java
// In your main servlet or filter
ConfigUtil.printConfiguration();
```

This will output:
```
=== Configuration Debug ===
Environment: PRODUCTION
Database URL: jdbc:mysql://your-db-host:3306/secure_complaint_box
Database User: your_db_username
Email User: your-email@gmail.com
SMTP Host: smtp.gmail.com
SMTP Port: 587
Encryption Secret Length: 16
==========================
```

## 🆘 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check DB_URL, DB_USER, DB_PASSWORD
   - Verify database is running and accessible
   - Check firewall rules

2. **Email Not Sending**
   - Verify EMAIL_USER and EMAIL_PASSWORD
   - Check Gmail App Password is correct
   - Ensure 2FA is enabled on Gmail

3. **Encryption Errors**
   - Verify ENCRYPTION_SECRET is exactly 16 characters
   - Check for special characters in secret
   - Ensure consistent secret across deployments

4. **Environment Variables Not Loading**
   - Restart Tomcat after setting environment variables
   - Check variable names are correct
   - Verify no spaces in variable values 