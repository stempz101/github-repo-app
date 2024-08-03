package com.atipera.github_repo.service;

import com.atipera.github_repo.dto.RepositoryInfoDto;
import java.util.List;
import reactor.core.publisher.Mono;

public interface UserService {

  Mono<List<RepositoryInfoDto>> getUserRepositories(String username);
}
