# GitHub Repo Application

## Navigation

- [Description](#Description)
- [Technologies](#Technologies)
- [How to run](#How-to-run)

# Description

This application allows you to view a list of a particular user's GitHub repositories, including
information about each branch in it.

# Technologies

- Java 21
- Spring (Boot, WebFlux)
- OpenAPI 3
- JUnit 5
- Mockito
- Docker

# How to run

Create
your [GitHub personal access token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic)
and paste it into `<github_api_token>`.

   ```bash
   echo "GITHUB_API_TOKEN=<github_api_token>" > .env
   docker compose up -d
   ```

Once the application is running, you can access the API documentation and use the provided endpoints
already there. Here is the link: http://localhost:8080/swagger-ui/index.html 
