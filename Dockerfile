FROM anapsix/alpine-java:8

COPY target/pricing-app-0.0.1-SNAPSHOT.jar /pricing-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/pricing-app.jar"]
