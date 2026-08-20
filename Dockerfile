FROM tomcat:9.0-jdk8

# Remove default Tomcat apps to keep the image clean
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy your app into Tomcat's webapps folder
COPY webapp/store /usr/local/tomcat/webapps/ROOT

EXPOSE 8080

CMD ["catalina.sh", "run"]