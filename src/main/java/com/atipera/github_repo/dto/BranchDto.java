package com.atipera.github_repo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BranchDto(
    String name,
    Commit commit
) {

  public record Commit(
      String sha
  ) {

  }
}
