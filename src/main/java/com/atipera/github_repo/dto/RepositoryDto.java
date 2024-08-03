package com.atipera.github_repo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RepositoryDto(
    String name,
    Owner owner,
    boolean fork
) {

  public record Owner(
      String login
  ) {

  }
}
