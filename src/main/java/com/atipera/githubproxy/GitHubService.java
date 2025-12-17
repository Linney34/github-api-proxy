package com.atipera.githubproxy;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
class GitHubService {
    private final GitHubClient githubClient;

    GitHubService(GitHubClient githubClient) {
        this.githubClient = githubClient;
    }

    List<RepositoryResponse> getRepositories(String username) {
        List<Map<String, Object>> repos = githubClient.getUserRepositories(username);

        return repos.stream()
                .filter(repo -> !(Boolean) repo.get("fork"))
                .map(repo -> mapRepository(username, repo))
                .collect(Collectors.toList());
    }

    private RepositoryResponse mapRepository(String username, Map<String, Object> repo) {
        String repoName = (String) repo.get("name");
        String ownerLogin = (String) ((Map<String, Object>) repo.get("owner")).get("login");

        List<BranchResponse> branches = githubClient
                .getBranches(username, repoName)
                .stream()
                .map(branch -> {
                    Map<String, Object> commit = (Map<String, Object>) branch.get("commit");
                    return new BranchResponse(
                            (String) branch.get("name"),
                            (String) commit.get("sha")
                    );
                })
                .toList();

        return new RepositoryResponse(repoName, ownerLogin, branches);
    }
}