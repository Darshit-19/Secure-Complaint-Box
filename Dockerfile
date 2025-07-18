# Use slim OpenJDK 21 as base image
FROM openjdk:21-jdk-slim

# Set environment variables for Tomcat
ENV TOMCAT_VERSION=10.1.43
ENV TOMCAT_DIR=/opt/tomcat

# Install required tools, download and extract Tomcat
RUN apt-get update && \
    apt-get install -y wget && \
    wget https://dlcdn.apache.org/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    tar -xzf apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    mv apache-tomcat-${TOMCAT_VERSION} ${TOMCAT_DIR} && \
    rm apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Copy your WAR file into the Tomcat webapps directory
COPY target/SecureComplaintBox.war ${TOMCAT_DIR}/webapps/

# Expose the default Tomcat port
EXPOSE 8080

# Set the default command to run Tomcat
CMD ["sh", "-c", "${TOMCAT_DIR}/bin/catalina.sh run"]
