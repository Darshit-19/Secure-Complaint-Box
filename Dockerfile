# Use OpenJDK 21 as base image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Install necessary packages
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    nodejs \
    npm \
    && rm -rf /var/lib/apt/lists/*

# Download and install Tomcat 10.1.43 (latest as of now)
RUN wget https://downloads.apache.org/tomcat/tomcat-10/v10.1.43/bin/apache-tomcat-10.1.43.tar.gz \
    && tar -xzf apache-tomcat-10.1.43.tar.gz \
    && mv apache-tomcat-10.1.43 /opt/tomcat \
    && rm apache-tomcat-10.1.43.tar.gz

# Copy application files
COPY src/ ./src/
COPY package.json ./
COPY tailwind.config.js ./

# Build Tailwind CSS
RUN npm install
RUN npm run build:css:prod

# Download required JAR files to Tomcat lib
RUN wget -O /opt/tomcat/lib/mysql-connector-j-9.3.0.jar \
    https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.3.0/mysql-connector-j-9.3.0.jar

RUN wget -O /opt/tomcat/lib/jakarta.mail-2.0.1.jar \
    https://repo1.maven.org/maven2/org/eclipse/angus/jakarta.mail/2.0.1/jakarta.mail-2.0.1.jar

RUN wget -O /opt/tomcat/lib/jakarta.activation-2.0.1.jar \
    https://repo1.maven.org/maven2/jakarta/activation/jakarta.activation-api/2.0.1/jakarta.activation-api-2.0.1.jar

RUN wget -O /opt/tomcat/lib/jbcrypt-0.4.jar \
    https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar

# Copy web resources to Tomcat
RUN cp -r src/main/webapp/public /opt/tomcat/webapps/SecureComplaintBox/
RUN mkdir -p /opt/tomcat/webapps/SecureComplaintBox/WEB-INF/
RUN cp src/main/webapp/WEB-INF/web.xml /opt/tomcat/webapps/SecureComplaintBox/WEB-INF/

# Copy JAR files to WEB-INF/lib
RUN mkdir -p /opt/tomcat/webapps/SecureComplaintBox/WEB-INF/lib
RUN cp /opt/tomcat/lib/*.jar /opt/tomcat/webapps/SecureComplaintBox/WEB-INF/lib/

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV CATALINA_OPTS="-Djava.security.egd=file:/dev/./urandom"

# Expose port
EXPOSE 8080

# Start Tomcat
CMD ["/opt/tomcat/bin/catalina.sh", "run"] 