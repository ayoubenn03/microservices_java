FROM eclipse-temurin:26-jdk AS build
WORKDIR /workspace
COPY . .
RUN chmod +x mvnw && ./mvnw -q -pl eureka-server -am package -DskipTests

FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /workspace/eureka-server/target/eureka-server-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
