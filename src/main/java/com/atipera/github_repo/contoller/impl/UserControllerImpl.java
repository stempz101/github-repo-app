package com.atipera.github_repo.contoller.impl;

import com.atipera.github_repo.contoller.UserController;
import com.atipera.github_repo.dto.RepositoryInfoDto;
import com.atipera.github_repo.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

  private final UserService userService;

  @Override
  public Mono<List<RepositoryInfoDto>> getUserRepositories(@PathVariable String username) {
    return userService.getUserRepositories(username);
  }
}
