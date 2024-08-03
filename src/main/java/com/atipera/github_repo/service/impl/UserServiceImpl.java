package com.atipera.github_repo.service.impl;

import com.atipera.github_repo.dto.BranchDto;
import com.atipera.github_repo.dto.BranchInfoDto;
import com.atipera.github_repo.dto.ErrorMessageDto;
import com.atipera.github_repo.dto.RepositoryDto;
import com.atipera.github_repo.dto.RepositoryInfoDto;
import com.atipera.github_repo.enums.GitHubResource;
import com.atipera.github_repo.exception.GitHubResourceNotFoundException;
import com.atipera.github_repo.exception.RateLimitExceededException;
import com.atipera.github_repo.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

  private final WebClient gitHubClient;

  @Override
  public Mono<List<RepositoryInfoDto>> getUserRepositories(String username) {
    LOG.info("Fetching user's (username={}) repositories", username);

    return gitHubClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(String.format("/users/%s/repos", username))
            .queryParam("type", "all")
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
            response -> handle4xxClientError(response, GitHubResource.USER))
        .bodyToFlux(RepositoryDto.class)
        .filter(repo -> !repo.fork())
        .flatMap(this::buildRepositoryInfo)
        .collectList();
  }

  private Mono<RepositoryInfoDto> buildRepositoryInfo(RepositoryDto repo) {
    return fetchBranches(repo.owner().login(), repo.name())
        .map(branches -> new RepositoryInfoDto(repo.name(), repo.owner().login(), branches));
  }

  private Mono<List<BranchInfoDto>> fetchBranches(String owner, String repo) {
    LOG.debug("Fetching repository's (name={}) branches", repo);

    return gitHubClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(String.format("/repos/%s/%s/branches", owner, repo))
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
            response -> handle4xxClientError(response, GitHubResource.REPOSITORY))
        .bodyToFlux(BranchDto.class)
        .map(branch -> new BranchInfoDto(branch.name(), branch.commit().sha()))
        .collectList();
  }

  private Mono<? extends Throwable> handle4xxClientError(ClientResponse response,
      GitHubResource resource) {
    return switch (response.statusCode()) {
      case HttpStatus.FORBIDDEN -> Mono.error(new RateLimitExceededException());
      case HttpStatus.NOT_FOUND -> Mono.error(new GitHubResourceNotFoundException(resource));
      default -> response.bodyToMono(ErrorMessageDto.class)
          .flatMap(errorMessage -> Mono.error(new RuntimeException(errorMessage.message())));
    };
  }
}
