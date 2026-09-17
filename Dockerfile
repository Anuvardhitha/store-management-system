FROM tomcat:9.0-jdk8

# Remove default Tomcat apps to keep the image clean
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy your app (HTML, WEB-INF/lib, etc.) into Tomcat's webapps folder
COPY webapp/store /usr/local/tomcat/webapps/ROOT

# Copy the Java source files into a temporary build folder
COPY *.java /usr/src/servlets/

# Compile them against Tomcat's own servlet-api.jar, writing straight into WEB-INF/classes
RUN javac -cp /usr/local/tomcat/lib/servlet-api.jar -d /usr/local/tomcat/webapps/ROOT/WEB-INF/classes /usr/src/servlets/*.java

EXPOSE 8080

CMD ["catalina.sh", "run"]