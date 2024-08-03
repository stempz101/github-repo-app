package com.atipera.github_repo.dto;

import java.util.List;

public record RepositoryInfoDto(
    String name,
    String owner,
    List<BranchInfoDto> branches
) {

}
