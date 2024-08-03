FROM eclipse-temurin:21
WORKDIR /app
COPY target/github-repo-*.jar github-repo.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "github-repo.jar"]
