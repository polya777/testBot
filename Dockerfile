FROM maven:3.9.6-eclipse-temurin-11 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=builder /app/target/telegram-meme-bot-1.0.jar app.jar
CMD ["java", "-jar", "app.jar"]