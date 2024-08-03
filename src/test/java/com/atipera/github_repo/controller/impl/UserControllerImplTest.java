package com.atipera.github_repo.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.atipera.github_repo.contoller.impl.UserControllerImpl;
import com.atipera.github_repo.dto.BranchInfoDto;
import com.atipera.github_repo.dto.ErrorMessageDto;
import com.atipera.github_repo.dto.RepositoryInfoDto;
import com.atipera.github_repo.enums.GitHubResource;
import com.atipera.github_repo.exception.GitHubResourceNotFoundException;
import com.atipera.github_repo.exception.RateLimitExceededException;
import com.atipera.github_repo.service.UserService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = UserControllerImpl.class)
public class UserControllerImplTest {

  @MockBean
  private UserService userService;

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void getUserRepositories_Success() throws Exception {
    // Given
    String username = "john_doe";
    List<RepositoryInfoDto> expectedResult = Arrays.asList(
        new RepositoryInfoDto("repo1", "john_doe", Arrays.asList(
            new BranchInfoDto("branch1", "f52j34h52gf345"),
            new BranchInfoDto("branch2", "gj73lj44573jgl")
        )),
        new RepositoryInfoDto("repo2", "john_doe", Arrays.asList(
            new BranchInfoDto("branch3", "6lkj6354l63gjc"),
            new BranchInfoDto("branch4", "6v3nm34jv6345h")
        ))
    );

    // When
    when(userService.getUserRepositories(username)).thenReturn(Mono.just(expectedResult));

    // Then
    webTestClient.get().uri(String.format("/users/%s/repos", username))
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(RepositoryInfoDto.class)
        .contains(expectedResult.get(0), expectedResult.get(1));
  }

  @Test
  void getUserRepositories_RateLimitExceeded_Failure() {
    // Given
    String username = "john_doe";
    ErrorMessageDto expectedResult = new ErrorMessageDto(403, "GitHub API rate limit exceeded");

    // When
    when(userService.getUserRepositories(username)).thenThrow(new RateLimitExceededException());

    // Then
    webTestClient.get().uri(String.format("/users/%s/repos", username))
        .exchange()
        .expectStatus().isForbidden()
        .expectBody(ErrorMessageDto.class)
        .consumeWith(result -> {
          assertNotNull(result.getResponseBody());
          assertEquals(expectedResult.status(), result.getResponseBody().status());
          assertEquals(expectedResult.message(), result.getResponseBody().message());
        });
  }

  @Test
  void getUserRepositories_UserNotFound_Failure() {
    // Given
    String username = "john_doe";
    ErrorMessageDto expectedResult = new ErrorMessageDto(404, "GitHub user not found");

    // When
    when(userService.getUserRepositories(username))
        .thenThrow(new GitHubResourceNotFoundException(GitHubResource.USER));

    // Then
    webTestClient.get().uri(String.format("/users/%s/repos", username))
        .exchange()
        .expectStatus().isNotFound()
        .expectBody(ErrorMessageDto.class)
        .consumeWith(result -> {
          assertNotNull(result.getResponseBody());
          assertEquals(expectedResult.status(), result.getResponseBody().status());
          assertEquals(expectedResult.message(), result.getResponseBody().message());
        });
  }

  @Test
  void getUserRepositories_RepositoryNotFound_Failure() {
    // Given
    String username = "john_doe";
    ErrorMessageDto expectedResult = new ErrorMessageDto(404, "GitHub repository not found");

    // When
    when(userService.getUserRepositories(username))
        .thenThrow(new GitHubResourceNotFoundException(GitHubResource.REPOSITORY));

    // Then
    webTestClient.get().uri(String.format("/users/%s/repos", username))
        .exchange()
        .expectStatus().isNotFound()
        .expectBody(ErrorMessageDto.class)
        .consumeWith(result -> {
          assertNotNull(result.getResponseBody());
          assertEquals(expectedResult.status(), result.getResponseBody().status());
          assertEquals(expectedResult.message(), result.getResponseBody().message());
        });
  }

  @Test
  void getUserRepositories_IfException_Failure() {
    // Given
    String username = "john_doe";
    ErrorMessageDto expectedResult = new ErrorMessageDto(500, "Internal error");

    // When
    when(userService.getUserRepositories(username)).thenThrow(new RuntimeException("Internal error"));

    // Then
    webTestClient.get().uri(String.format("/users/%s/repos", username))
        .exchange()
        .expectStatus().is5xxServerError()
        .expectBody(ErrorMessageDto.class)
        .consumeWith(result -> {
          assertNotNull(result.getResponseBody());
          assertEquals(expectedResult.status(), result.getResponseBody().status());
          assertEquals(expectedResult.message(), result.getResponseBody().message());
        });
  }
}
