package com.atipera.github_repo.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.atipera.github_repo.dto.BranchDto;
import com.atipera.github_repo.dto.RepositoryDto;
import com.atipera.github_repo.dto.RepositoryInfoDto;
import com.atipera.github_repo.enums.GitHubResource;
import com.atipera.github_repo.exception.GitHubResourceNotFoundException;
import com.atipera.github_repo.exception.RateLimitExceededException;
import com.atipera.github_repo.test.utils.RepositoryTestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

@SpringBootTest
public class UserServiceImplTest {

  private static MockWebServer mockWebServer;

  @Autowired
  private UserServiceImpl userService;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeAll
  static void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
  }

  @DynamicPropertySource
  private static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("application.github.api.base-url",
        () -> String.format("http://localhost:%s", mockWebServer.getPort()));
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void getUserRepositories_Success()
      throws JsonProcessingException, InterruptedException {
    // Given
    String username = "johnDoe";
    List<RepositoryDto> repositories = List.of(
        RepositoryTestUtil.getRepositoryDto1(),
        RepositoryTestUtil.getRepositoryDto2()
    );
    List<BranchDto> branchesOfRepo1 = RepositoryTestUtil.getRepo1Branches();
    List<BranchDto> branchesOfRepo2 = RepositoryTestUtil.getRepo2Branches();

    String reposJson = objectMapper.writeValueAsString(repositories);
    String branchesOfRepo1Json = objectMapper.writeValueAsString(branchesOfRepo1);
    String branchesOfRepo2Json = objectMapper.writeValueAsString(branchesOfRepo2);

    List<RepositoryInfoDto> expectedResult = List.of(
        RepositoryTestUtil.getRepoInfoDto1(),
        RepositoryTestUtil.getRepoInfoDto2()
    );

    // When
    mockWebServer.enqueue(
        new MockResponse().setBody(reposJson).addHeader("Content-Type", "application/json"));
    mockWebServer.enqueue(
        new MockResponse().setBody(branchesOfRepo1Json).addHeader("Content-Type", "application/json"));
    mockWebServer.enqueue(
        new MockResponse().setBody(branchesOfRepo2Json).addHeader("Content-Type", "application/json"));

    Mono<List<RepositoryInfoDto>> result = userService.getUserRepositories(username);

    // Then
    StepVerifier.create(result, StepVerifierOptions.create())
        .expectNextMatches(
            repos -> repos.size() == expectedResult.size() &&
                repos.contains(expectedResult.getFirst()) &&
                repos.contains(expectedResult.get(1)))
        .verifyComplete();

    RecordedRequest reposRequest = mockWebServer.takeRequest();
    assertEquals("GET", reposRequest.getMethod());
    assertEquals(String.format("/users/%s/repos?type=all", username), reposRequest.getPath());

    RecordedRequest branchesRequest1 = mockWebServer.takeRequest();
    assertEquals("GET", branchesRequest1.getMethod());
    assertEquals(
        String.format("/repos/%s/%s/branches", expectedResult.getFirst().owner(), expectedResult.getFirst().name()),
        branchesRequest1.getPath()
    );

    RecordedRequest branchesRequest2 = mockWebServer.takeRequest();
    assertEquals("GET", branchesRequest2.getMethod());
    assertEquals(
        String.format("/repos/%s/%s/branches", expectedResult.get(1).owner(), expectedResult.get(1).name()),
        branchesRequest2.getPath()
    );
  }

  @Test
  void getUserRepositories_IfForkExists_Success()
      throws JsonProcessingException, InterruptedException {
    // Given
    String username = "johnDoe";
    List<RepositoryDto> repositories = List.of(
        RepositoryTestUtil.getRepositoryDto1(),
        RepositoryTestUtil.getRepositoryDto3(), // Fork repo
        RepositoryTestUtil.getRepositoryDto2()
    );
    List<BranchDto> branchesOfRepo1 = RepositoryTestUtil.getRepo1Branches();
    List<BranchDto> branchesOfRepo2 = RepositoryTestUtil.getRepo2Branches();

    String reposJson = objectMapper.writeValueAsString(repositories);
    String branchesOfRepo1Json = objectMapper.writeValueAsString(branchesOfRepo1);
    String branchesOfRepo2Json = objectMapper.writeValueAsString(branchesOfRepo2);

    List<RepositoryInfoDto> expectedResult = List.of(
        RepositoryTestUtil.getRepoInfoDto1(),
        RepositoryTestUtil.getRepoInfoDto2()
    );

    // When
    mockWebServer.enqueue(
        new MockResponse().setBody(reposJson).addHeader("Content-Type", "application/json"));
    mockWebServer.enqueue(
        new MockResponse().setBody(branchesOfRepo1Json).addHeader("Content-Type", "application/json"));
    mockWebServer.enqueue(
        new MockResponse().setBody(branchesOfRepo2Json).addHeader("Content-Type", "application/json"));

    Mono<List<RepositoryInfoDto>> result = userService.getUserRepositories(username);

    // Then
    StepVerifier.create(result)
        .expectNextMatches(
            repos -> repos.size() == expectedResult.size() &&
                repos.contains(expectedResult.getFirst()) &&
                repos.contains(expectedResult.get(1)))
        .verifyComplete();

    RecordedRequest reposRequest = mockWebServer.takeRequest();
    assertEquals("GET", reposRequest.getMethod());
    assertEquals(String.format("/users/%s/repos?type=all", username), reposRequest.getPath());

    RecordedRequest branchesRequest1 = mockWebServer.takeRequest();
    assertEquals("GET", branchesRequest1.getMethod());
    assertEquals(
        String.format("/repos/%s/%s/branches", expectedResult.getFirst().owner(), expectedResult.getFirst().name()),
        branchesRequest1.getPath()
    );

    RecordedRequest branchesRequest2 = mockWebServer.takeRequest();
    assertEquals("GET", branchesRequest2.getMethod());
    assertEquals(
        String.format("/repos/%s/%s/branches", expectedResult.get(1).owner(), expectedResult.get(1).name()),
        branchesRequest2.getPath()
    );
  }

  @Test
  void getUserRepositories_FetchingRepos_RateLimitExceeded_Failure() throws InterruptedException {
    // Given
    String username = "johnDoe";
    String expectedErrorMessage = "GitHub API rate limit exceeded";

    // When
    mockWebServer.enqueue(
        new MockResponse().setResponseCode(403).addHeader("Content-Type", "application/json"));

    Mono<List<RepositoryInfoDto>> result = userService.getUserRepositories(username);

    // Then
    StepVerifier.create(result)
        .expectErrorMatches(throwable -> throwable instanceof RateLimitExceededException &&
            throwable.getMessage().equals(expectedErrorMessage))
        .verify();

    RecordedRequest reposRequest = mockWebServer.takeRequest();
    assertEquals("GET", reposRequest.getMethod());
    assertEquals(String.format("/users/%s/repos?type=all", username), reposRequest.getPath());
  }

  @Test
  void getUserRepositories_FetchingRepos_UserNotFound_Failure() throws InterruptedException {
    // Given
    String username = "johnDoe";
    String expectedErrorMessage = String.format("GitHub %s not found",
        GitHubResource.USER.name().toLowerCase());

    // When
    mockWebServer.enqueue(
        new MockResponse().setResponseCode(404).addHeader("Content-Type", "application/json"));

    Mono<List<RepositoryInfoDto>> result = userService.getUserRepositories(username);

    // Then
    StepVerifier.create(result)
        .expectErrorMatches(throwable -> throwable instanceof GitHubResourceNotFoundException &&
            throwable.getMessage().equals(expectedErrorMessage))
        .verify();

    RecordedRequest reposRequest = mockWebServer.takeRequest();
    assertEquals("GET", reposRequest.getMethod());
    assertEquals(String.format("/users/%s/repos?type=all", username), reposRequest.getPath());
  }

  @Test
  void getUserRepositories_FetchingBranches_RateLimitExceeded_Failure()
      throws JsonProcessingException, InterruptedException {
    // Given
    String username = "johnDoe";
    List<RepositoryDto> repositories = List.of(RepositoryTestUtil.getRepositoryDto1());

    String reposJson = objectMapper.writeValueAsString(repositories);

    String expectedErrorMessage = "GitHub API rate limit exceeded";

    // When
    mockWebServer.enqueue(
        new MockResponse().setBody(reposJson).addHeader("Content-Type", "application/json"));
    mockWebServer.enqueue(
        new MockResponse().setResponseCode(403).addHeader("Content-Type", "application/json"));

    Mono<List<RepositoryInfoDto>> result = userService.getUserRepositories(username);

    // Then
    StepVerifier.create(result)
        .expectErrorMatches(throwable -> throwable instanceof RateLimitExceededException &&
            throwable.getMessage().equals(expectedErrorMessage))
        .verify();

    RecordedRequest reposRequest = mockWebServer.takeRequest();
    assertEquals("GET", reposRequest.getMethod());
    assertEquals(String.format("/users/%s/repos?type=all", username), reposRequest.getPath());

    RecordedRequest branchesRequest1 = mockWebServer.takeRequest();
    assertEquals("GET", branchesRequest1.getMethod());
    assertEquals(
        String.format("/repos/%s/%s/branches", repositories.getFirst().owner().login(), repositories.getFirst().name()),
        branchesRequest1.getPath()
    );
  }

  @Test
  void getUserRepositories_FetchingBranches_RepoNotFound_Failure()
      throws JsonProcessingException, InterruptedException {
    // Given
    String username = "johnDoe";
    List<RepositoryDto> repositories = List.of(RepositoryTestUtil.getRepositoryDto1());

    String reposJson = objectMapper.writeValueAsString(repositories);

    String expectedErrorMessage = String.format("GitHub %s not found",
        GitHubResource.REPOSITORY.name().toLowerCase());

    // When
    mockWebServer.enqueue(
        new MockResponse().setBody(reposJson).addHeader("Content-Type", "application/json"));
    mockWebServer.enqueue(
        new MockResponse().setResponseCode(404).addHeader("Content-Type", "application/json"));

    Mono<List<RepositoryInfoDto>> result = userService.getUserRepositories(username);

    // Then
    StepVerifier.create(result)
        .expectErrorMatches(throwable -> throwable instanceof GitHubResourceNotFoundException &&
            throwable.getMessage().equals(expectedErrorMessage))
        .verify();

    RecordedRequest reposRequest = mockWebServer.takeRequest();
    assertEquals("GET", reposRequest.getMethod());
    assertEquals(String.format("/users/%s/repos?type=all", username), reposRequest.getPath());

    RecordedRequest branchesRequest1 = mockWebServer.takeRequest();
    assertEquals("GET", branchesRequest1.getMethod());
    assertEquals(
        String.format("/repos/%s/%s/branches", repositories.getFirst().owner().login(), repositories.getFirst().name()),
        branchesRequest1.getPath()
    );
  }
}
