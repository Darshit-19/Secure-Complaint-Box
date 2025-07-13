# Railway Deployment Guide

## 🚂 Deploying Secure Complaint Box to Railway

### Prerequisites
- GitHub repository with your code
- Railway account (free tier available)
- MySQL database (Railway provides this)

## 📋 Step-by-Step Deployment

### Step 1: Prepare Your Project

1. **Build your Java project in Eclipse:**
   - Right-click project → Export → WAR file
   - Save as `target/SecureComplaintBox.war`

2. **Build Tailwind CSS:**
   ```bash
   npm run build:css:prod
   ```

3. **Run the build script:**
   ```bash
   .\build-for-railway.bat
   ```

### Step 2: Push to GitHub

```bash
git add .
git commit -m "Add Railway deployment configuration"
git push
```

### Step 3: Set Up Railway

1. **Go to [Railway.app](https://railway.app)**
2. **Sign in with GitHub**
3. **Click "New Project"**
4. **Select "Deploy from GitHub repo"**
5. **Choose your repository**: `Darshit-19/Secure-Complaint-Box`

### Step 4: Add MySQL Database

1. **In your Railway project, click "New"**
2. **Select "Database" → "MySQL"**
3. **Wait for database to be created**
4. **Copy the database connection details**

### Step 5: Configure Environment Variables

In your Railway project, go to **Variables** tab and add:

```bash
# Environment
ENVIRONMENT=production

# Database (use Railway MySQL connection details)
DB_URL=jdbc:mysql://your-railway-mysql-host:3306/railway
DB_USER=root
DB_PASSWORD=your-railway-mysql-password

# Email (Gmail App Password)
EMAIL_USER=your-email@gmail.com
EMAIL_PASSWORD=your_gmail_app_password

# SMTP
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587

# Encryption (16 characters)
ENCRYPTION_SECRET=YourRailwaySecret16
```

### Step 6: Deploy

1. **Railway will automatically detect the Dockerfile**
2. **Click "Deploy"**
3. **Wait for build to complete**
4. **Your app will be available at the provided URL**

## 🔧 Railway-Specific Configuration

### Database Setup
Railway provides MySQL databases. Use these connection details:
- **Host**: Provided by Railway
- **Port**: 3306
- **Database**: `railway`
- **Username**: `root`
- **Password**: Provided by Railway

### Environment Variables Priority
Railway environment variables override all other configuration:
1. Railway Environment Variables
2. Properties File
3. Hardcoded Defaults

## 🚀 Deployment Checklist

- [ ] Java project built (WAR file created)
- [ ] Tailwind CSS built
- [ ] Code pushed to GitHub
- [ ] Railway project created
- [ ] MySQL database added
- [ ] Environment variables configured
- [ ] Deployment successful
- [ ] Application accessible

## 🔍 Troubleshooting

### Build Issues
- **Docker build fails**: Check Dockerfile syntax
- **WAR file missing**: Build project in Eclipse first
- **Dependencies missing**: Ensure all JAR files are in WEB-INF/lib/

### Runtime Issues
- **Database connection fails**: Check Railway MySQL credentials
- **Email not working**: Verify Gmail App Password
- **Application not starting**: Check Railway logs

### Common Railway Commands
```bash
# View logs
railway logs

# Check status
railway status

# Redeploy
railway up
```

## 📊 Monitoring

Railway provides:
- **Real-time logs**
- **Performance metrics**
- **Uptime monitoring**
- **Automatic restarts**

## 🔒 Security Notes

- **Never commit** `application.properties` to GitHub
- **Use Railway environment variables** for production secrets
- **Rotate encryption secrets** for production
- **Use strong passwords** for database and email

## 💰 Cost Optimization

Railway Free Tier includes:
- **500 hours/month** of compute
- **1GB RAM** per service
- **1GB storage**
- **MySQL database** included

## 🎯 Success Indicators

Your deployment is successful when:
- ✅ Application builds without errors
- ✅ Database connection established
- ✅ Email functionality works
- ✅ Application accessible via Railway URL
- ✅ All features working (registration, login, complaints)

## 📞 Support

If you encounter issues:
1. Check Railway logs
2. Verify environment variables
3. Test database connectivity
4. Review application logs
5. Contact Railway support if needed 