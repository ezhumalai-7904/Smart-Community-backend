
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests
EXPOSE 8080
# Wildcard use pannu - automatic ah JAR name catch pannum
CMD ["java", "-jar", "target/*.jar"]