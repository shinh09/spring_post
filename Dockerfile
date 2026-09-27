FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

COPY src src

RUN ./gradlew bootJar --no-daemon \
    && find build/libs -name "*.jar" ! -name "*-plain.jar" \
       -exec cp {} app.jar \;

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]