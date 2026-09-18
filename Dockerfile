FROM tomcat:9.0-jdk8

# Remove default Tomcat apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy web application
COPY webapp/store /usr/local/tomcat/webapps/ROOT

# Copy all servlet source files
COPY *.java /usr/src/servlets/

# Copy the new authentication servlets
COPY webapp/store/RegisterServlet.java /usr/src/servlets/
COPY webapp/store/LoginServlet.java /usr/src/servlets/

# Compile all servlets
RUN javac -cp /usr/local/tomcat/lib/servlet-api.jar \
    -d /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
    /usr/src/servlets/*.java

EXPOSE 8080

CMD ["catalina.sh", "run"]
