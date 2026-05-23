FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY build/libs/gateway-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]
