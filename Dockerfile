# Use OpenJDK 21 as a slim and secure base image
FROM openjdk:21-jdk-slim

# Set the Tomcat version as a variable for easy updates
ARG TOMCAT_VERSION=10.1.26

# Download and install Tomcat, including a checksum verification step for security and stability
RUN apt-get update && \
    apt-get install -y wget && \
    wget https://archive.apache.org/dist/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    wget https://archive.apache.org/dist/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz.sha512 && \
    echo "$(cat apache-tomcat-${TOMCAT_VERSION}.tar.gz.sha512) apache-tomcat-${TOMCAT_VERSION}.tar.gz" | sha512sum -c - && \
    tar -xzf apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    mv apache-tomcat-${TOMCAT_VERSION} /opt/tomcat && \
    rm apache-tomcat-${TOMCAT_VERSION}.tar.gz apache-tomcat-${TOMCAT_VERSION}.tar.gz.sha512 && \
    apt-get clean

# Copy your application's WAR file into Tomcat's webapps directory
COPY target/SecureComplaintBox.war /opt/tomcat/webapps/

# Expose the port Tomcat runs on
EXPOSE 8080

# The command to run when the container starts
CMD ["/opt/tomcat/bin/catalina.sh", "run"]