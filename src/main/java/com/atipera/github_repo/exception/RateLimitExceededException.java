package com.atipera.github_repo.exception;

public class RateLimitExceededException extends RuntimeException {

  private static final String MESSAGE = "GitHub API rate limit exceeded";

  public RateLimitExceededException() {
    super(MESSAGE);
  }
}
