package com.atipera.github_repo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

  @Value("${application.github.api.base-url}")
  private String gitHubApiBaseUrl;

  @Value("${application.github.api.accept-header}")
  private String gitHubApiAcceptHeader;

  @Value("${application.github.api.token}")
  private String gitHubApiToken;

  @Value("${application.github.api.version}")
  private String gitHubApiVersion;

  @Bean
  public WebClient gitHubClient() {

    return WebClient.builder()
        .baseUrl(gitHubApiBaseUrl)
        .defaultHeaders(headers -> {
          headers.add(HttpHeaders.ACCEPT, gitHubApiAcceptHeader);
          headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + gitHubApiToken);
          headers.add("X-GitHub-Api-Version", gitHubApiVersion);
        })
        .build();
  }
}
