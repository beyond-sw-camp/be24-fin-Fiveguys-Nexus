FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache nodejs npm

WORKDIR /app

COPY mcp/naver-news /app/mcp
RUN cd /app/mcp && npm install --omit=dev

COPY build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]