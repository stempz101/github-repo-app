package com.atipera.github_repo.exception;

import com.atipera.github_repo.enums.GitHubResource;

public class GitHubResourceNotFoundException extends RuntimeException {

  private static final String MESSAGE = "GitHub resource not found";
  private static final String FORMAT_MESSAGE = "GitHub %s not found";

  public GitHubResourceNotFoundException() {
    super(MESSAGE);
  }

  public GitHubResourceNotFoundException(GitHubResource resource) {
    super(String.format(FORMAT_MESSAGE, resource.name().toLowerCase()));
  }
}
