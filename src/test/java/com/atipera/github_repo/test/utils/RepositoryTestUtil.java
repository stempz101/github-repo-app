package com.atipera.github_repo.test.utils;

import com.atipera.github_repo.dto.BranchDto;
import com.atipera.github_repo.dto.BranchDto.Commit;
import com.atipera.github_repo.dto.BranchInfoDto;
import com.atipera.github_repo.dto.RepositoryDto;
import com.atipera.github_repo.dto.RepositoryDto.Owner;
import com.atipera.github_repo.dto.RepositoryInfoDto;
import java.util.Arrays;
import java.util.List;

public class RepositoryTestUtil {

  public static final String GITHUB_REPO_1_NAME = "repo1";
  public static final String GITHUB_REPO_1_OWNER = "johnDoe";
  public static final String GITHUB_REPO_1_BRANCH_1_NAME = "branch1";
  public static final String GITHUB_REPO_1_BRANCH_1_LAST_COMMIT = "f52j34h52gf345";
  public static final String GITHUB_REPO_1_BRANCH_2_NAME = "branch2";
  public static final String GITHUB_REPO_1_BRANCH_2_LAST_COMMIT = "gj73lj44573jgl";
  public static final boolean GITHUB_REPO_1_FORK = false;

  public static final String GITHUB_REPO_2_NAME = "repo2";
  public static final String GITHUB_REPO_2_OWNER = "johnDoe";
  public static final String GITHUB_REPO_2_BRANCH_1_NAME = "branch3";
  public static final String GITHUB_REPO_2_BRANCH_1_LAST_COMMIT = "6lkj6354l63gjc";
  public static final String GITHUB_REPO_2_BRANCH_2_NAME = "branch4";
  public static final String GITHUB_REPO_2_BRANCH_2_LAST_COMMIT = "6v3nm34jv6345h";
  public static final boolean GITHUB_REPO_2_FORK = false;

  public static final String GITHUB_REPO_3_NAME = "repo3";
  public static final String GITHUB_REPO_3_OWNER = "alexPeres";
  public static final boolean GITHUB_REPO_3_FORK = true;

  public static RepositoryDto getRepositoryDto1() {
    return new RepositoryDto(
        GITHUB_REPO_1_NAME,
        new Owner(GITHUB_REPO_1_OWNER),
        GITHUB_REPO_1_FORK
    );
  }

  public static RepositoryDto getRepositoryDto2() {
    return new RepositoryDto(
        GITHUB_REPO_2_NAME,
        new Owner(GITHUB_REPO_2_OWNER),
        GITHUB_REPO_2_FORK
    );
  }

  public static RepositoryDto getRepositoryDto3() {
    return new RepositoryDto(
        GITHUB_REPO_3_NAME,
        new Owner(GITHUB_REPO_3_OWNER),
        GITHUB_REPO_3_FORK
    );
  }

  public static List<BranchDto> getRepo1Branches() {
    return Arrays.asList(
        new BranchDto(GITHUB_REPO_1_BRANCH_1_NAME, new Commit(GITHUB_REPO_1_BRANCH_1_LAST_COMMIT)),
        new BranchDto(GITHUB_REPO_1_BRANCH_2_NAME, new Commit(GITHUB_REPO_1_BRANCH_2_LAST_COMMIT))
    );
  }

  public static List<BranchDto> getRepo2Branches() {
    return Arrays.asList(
        new BranchDto(GITHUB_REPO_2_BRANCH_1_NAME, new Commit(GITHUB_REPO_2_BRANCH_1_LAST_COMMIT)),
        new BranchDto(GITHUB_REPO_2_BRANCH_2_NAME, new Commit(GITHUB_REPO_2_BRANCH_2_LAST_COMMIT))
    );
  }

  public static RepositoryInfoDto getRepoInfoDto1() {
    return new RepositoryInfoDto(GITHUB_REPO_1_NAME, GITHUB_REPO_1_OWNER,
        Arrays.asList(
            new BranchInfoDto(GITHUB_REPO_1_BRANCH_1_NAME, GITHUB_REPO_1_BRANCH_1_LAST_COMMIT),
            new BranchInfoDto(GITHUB_REPO_1_BRANCH_2_NAME, GITHUB_REPO_1_BRANCH_2_LAST_COMMIT)
        )
    );
  }

  public static RepositoryInfoDto getRepoInfoDto2() {
    return new RepositoryInfoDto(GITHUB_REPO_2_NAME, GITHUB_REPO_2_OWNER,
        Arrays.asList(
            new BranchInfoDto(GITHUB_REPO_2_BRANCH_1_NAME, GITHUB_REPO_2_BRANCH_1_LAST_COMMIT),
            new BranchInfoDto(GITHUB_REPO_2_BRANCH_2_NAME, GITHUB_REPO_2_BRANCH_2_LAST_COMMIT)
        )
    );
  }
}
