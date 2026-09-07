FROM eclipse-temurin:26-jdk AS build
WORKDIR /workspace
COPY . .
RUN chmod +x mvnw && ./mvnw -q -pl book-service -am package -DskipTests

FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /workspace/book-service/target/book-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "app.jar"]
