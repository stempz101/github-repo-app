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

![image](https://github.com/user-attachments/assets/09db6222-c6c3-4074-8ac2-25d9fb2f9411)

To test an endpoint, you should follow the next steps:
1. Click on the endpoint
![image](https://github.com/user-attachments/assets/549e0c19-2084-45fa-8e8b-2120e2631323)
2. Click on the `Try it out` button
![image](https://github.com/user-attachments/assets/7353e15d-450a-4fa0-b0e1-e09c60d8f79d)
3. Fill in the required field and click on the `Execute` button
![image](https://github.com/user-attachments/assets/67c584e2-1214-4df6-8373-52a9511a9d25)
4. Result of the endpoint execution will be displayed in the `Server response` section below
![image](https://github.com/user-attachments/assets/69dfe5ec-1f78-4685-9f14-954ff3bdbe6a)



