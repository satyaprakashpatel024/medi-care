# Build stage: Compile code & generate custom minimal JRE
FROM maven:3.9.8-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml and resolve dependencies first for Docker layer caching
COPY pom.xml ./
RUN mvn dependency:go-offline -B || true

# Copy source code and build application JAR cleanly
COPY src ./src
RUN mvn clean package -DskipTests

# Create a stripped minimal JRE using jlink (~45MB JRE vs 212MB standard JRE)
RUN $JAVA_HOME/bin/jlink \
    --add-modules java.base,java.compiler,java.desktop,java.instrument,java.management,java.naming,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.security.sasl,java.sql,java.transaction.xa,java.xml,jdk.unsupported,jdk.crypto.ec \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /javaruntime

# Runtime stage: Ultra-lightweight Alpine Linux (~9MB base image)
FROM alpine:3.20

WORKDIR /app

# Install curl for container health check & tzdata for timezones
RUN apk add --no-cache curl tzdata

# Set up custom minimal Java runtime environment
COPY --from=builder /javaruntime /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Create non-root system group and user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Create the logs directory and assign ownership to your app user
RUN mkdir -p /app/logs && chown -R spring:spring /app/logs

# Switch to non-root user for security
USER spring:spring

# Copy built application artifact dynamically with spring user ownership
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

# Expose HTTP port
EXPOSE 8080

# Actuator Health Check probe
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health/liveness || exit 1

# Production JVM flags for containerized environment
ENTRYPOINT ["java", \
  "-XX:+UseG1GC", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
