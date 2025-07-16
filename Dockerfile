# Use OpenJDK 21 as base image
FROM openjdk:21-jdk-slim

# Download and install Tomcat 10.1.26 (Updated Version)
RUN apt-get update && apt-get install -y wget && \
    wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.26/bin/apache-tomcat-10.1.26.tar.gz && \
    tar -xzf apache-tomcat-10.1.26.tar.gz && \
    mv apache-tomcat-10.1.26 /opt/tomcat && \
    rm apache-tomcat-10.1.26.tar.gz

# Copy the WAR file
COPY target/SecureComplaintBox.war /opt/tomcat/webapps/

# Expose port
EXPOSE 8080

# Start Tomcat
CMD ["/opt/tomcat/bin/catalina.sh", "run"]