FROM eclipse-temurin:25-jdk-alpine

WORKDIR /app

COPY target/rica-polyglot-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=production

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
