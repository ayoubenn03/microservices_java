FROM eclipse-temurin:26-jdk AS build
WORKDIR /workspace
COPY . .
RUN chmod +x mvnw && ./mvnw -q -pl config-server -am package -DskipTests

FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /workspace/config-server/target/config-server-1.0.0-SNAPSHOT.jar app.jar
COPY config-server/config-repo ./config-repo
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
