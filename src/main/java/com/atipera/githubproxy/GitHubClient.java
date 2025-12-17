package com.atipera.githubproxy;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
class GitHubClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://api.github.com";

    List<Map<String, Object>> getUserRepositories(String username) {
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(
                    BASE_URL + "/users/{username}/repos",
                    List.class,
                    username
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new GithubUserNotFoundException();
        }
    }

    List<Map<String, Object>> getBranches(String owner, String repo) {
        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL + "/repos/{owner}/{repo}/branches",
                List.class,
                owner,
                repo
        );
        return response.getBody();
    }
}