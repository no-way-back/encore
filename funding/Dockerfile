# Build stage
FROM amazoncorretto:21-alpine AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY funding/ funding/

RUN ./gradlew :funding:clean :funding:bootJar -x test --no-daemon

# Runtime stage
FROM amazoncorretto:21-alpine AS runtime

WORKDIR /app

COPY --from=build /app/funding/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 18082