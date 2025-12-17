package com.atipera.githubproxy;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
class GitHubController {

    private final GitHubService githubService;

    GitHubController(GitHubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/{username}/repositories")
    List<RepositoryResponse> getRepositories(@PathVariable String username) {
        return githubService.getRepositories(username);
    }
}