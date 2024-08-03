package com.atipera.github_repo.contoller;

import com.atipera.github_repo.dto.ErrorMessageDto;
import com.atipera.github_repo.dto.RepositoryInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@RequestMapping("/users")
@Tag(name = "Users", description = "User management API")
public interface UserController {

  @GetMapping("/{username}/repos")
  @Operation(summary = "Fetching user's repositories", tags = "Users", responses = {
      @ApiResponse(responseCode = "200", description = "User's repository information successfully returned",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = RepositoryInfoDto.class)
          )
      ),
      @ApiResponse(responseCode = "403", description = "GitHub API rate limit exceeded",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorMessageDto.class)
          )
      ),
      @ApiResponse(responseCode = "404", description = "GitHub user or repository not found",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorMessageDto.class)
          )
      ),
      @ApiResponse(responseCode = "500", description = "Application or GitHub API failed to process the request",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorMessageDto.class)
          )
      )
  })
  Mono<List<RepositoryInfoDto>> getUserRepositories(@PathVariable String username);
}
