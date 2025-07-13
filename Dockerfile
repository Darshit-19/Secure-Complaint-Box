# Use OpenJDK 21 as base image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Install necessary packages
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Download and install Tomcat 10.1
RUN wget https://downloads.apache.org/tomcat/tomcat-10/v10.1.17/bin/apache-tomcat-10.1.17.tar.gz \
    && tar -xzf apache-tomcat-10.1.17.tar.gz \
    && mv apache-tomcat-10.1.17 /opt/tomcat \
    && rm apache-tomcat-10.1.17.tar.gz

# Copy your application files
COPY src/ /app/src/
COPY package.json /app/
COPY tailwind.config.js /app/

# Install Node.js for Tailwind CSS build
RUN apt-get update && apt-get install -y nodejs npm

# Build Tailwind CSS
RUN npm install
RUN npm run build:css:prod

# Build the Java application (you'll need to build this locally first)
# For now, we'll assume the WAR file is copied
COPY target/SecureComplaintBox.war /opt/tomcat/webapps/

# Copy MySQL connector
COPY src/main/webapp/WEB-INF/lib/mysql-connector-j-9.3.0.jar /opt/tomcat/lib/
COPY src/main/webapp/WEB-INF/lib/jakarta.mail-2.0.1.jar /opt/tomcat/lib/
COPY src/main/webapp/WEB-INF/lib/jakarta.activation-2.0.1.jar /opt/tomcat/lib/
COPY src/main/webapp/WEB-INF/lib/jbcrypt-0.4.jar /opt/tomcat/lib/

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV CATALINA_OPTS="-Djava.security.egd=file:/dev/./urandom"

# Expose port
EXPOSE 8080

# Start Tomcat
CMD ["/opt/tomcat/bin/catalina.sh", "run"] 