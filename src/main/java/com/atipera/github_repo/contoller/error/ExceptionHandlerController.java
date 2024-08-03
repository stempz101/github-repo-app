package com.atipera.github_repo.contoller.error;

import com.atipera.github_repo.dto.ErrorMessageDto;
import com.atipera.github_repo.exception.GitHubResourceNotFoundException;
import com.atipera.github_repo.exception.RateLimitExceededException;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {

  private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerController.class);

  @Hidden
  @ExceptionHandler(RateLimitExceededException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorMessageDto handleForbiddenException(Exception ex) {
    LOG.error("handleForbiddenException: {}", ex.getMessage(), ex);
    return new ErrorMessageDto(HttpStatus.FORBIDDEN.value(), ex.getMessage());
  }

  @Hidden
  @ExceptionHandler(GitHubResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorMessageDto handleNotFoundException(Exception ex) {
    LOG.error("handleNotFoundException: {}", ex.getMessage(), ex);
    return new ErrorMessageDto(HttpStatus.NOT_FOUND.value(), ex.getMessage());
  }

  @Hidden
  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorMessageDto handleException(RuntimeException ex) {
    LOG.error("handleException: {}", ex.getMessage(), ex);
    return new ErrorMessageDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
  }
}
