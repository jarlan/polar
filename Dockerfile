# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring \
    && mkdir -p /app/data /app/generated-qr \
    && chown -R spring:spring /app/data /app/generated-qr
USER spring:spring
COPY --from=build /app/target/*.jar app.jar
VOLUME /app/data
VOLUME /app/generated-qr
EXPOSE 8443
ENTRYPOINT ["java", "-jar", "app.jar"]
