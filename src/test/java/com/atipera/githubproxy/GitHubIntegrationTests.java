package com.atipera.githubproxy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GitHubIntegrationTests {

    private static WireMockServer wireMockServer;

    @LocalServerPort
    int port;

    private RestTemplate restTemplate = new RestTemplate();

    @BeforeAll
    static void setupWireMock() {
        wireMockServer = new WireMockServer(8089); // можна інший вільний порт
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void resetWireMock() {
        WireMock.reset();
    }

    @Test
    void testGetRepositories_success() {
        stubFor(get(urlEqualTo("/users/testuser/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  {
                                    "name": "repo1",
                                    "fork": false,
                                    "owner": { "login": "testuser" }
                                  }
                                ]
                                """)));

        stubFor(get(urlEqualTo("/repos/testuser/repo1/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  {
                                    "name": "main",
                                    "commit": { "sha": "abcd1234" }
                                  }
                                ]
                                """)));

        GitHubClient client = new GitHubClient() {
            @Override
            List<Map<String, Object>> getUserRepositories(String username) {
                ResponseEntity<List> response = new RestTemplate().getForEntity(
                        "http://localhost:8089/users/{username}/repos",
                        List.class,
                        username
                );
                return response.getBody();
            }

            @Override
            List<Map<String, Object>> getBranches(String owner, String repo) {
                ResponseEntity<List> response = new RestTemplate().getForEntity(
                        "http://localhost:8089/repos/{owner}/{repo}/branches",
                        List.class,
                        owner,
                        repo
                );
                return response.getBody();
            }
        };

        GitHubService service = new GitHubService(client);
        GitHubController controller = new GitHubController(service);

        List<RepositoryResponse> repos = controller.getRepositories("testuser");

        assertEquals(1, repos.size());
        assertEquals("repo1", repos.get(0).repositoryName());
        assertEquals("testuser", repos.get(0).ownerLogin());
        assertEquals(1, repos.get(0).branches().size());
        assertEquals("main", repos.get(0).branches().get(0).name());
    }

    @Test
    void testGetRepositories_userNotFound() {
        stubFor(get(urlEqualTo("/users/unknownuser/repos"))
                .willReturn(aResponse().withStatus(404)));

        GitHubClient client = new GitHubClient() {
            @Override
            List<Map<String, Object>> getUserRepositories(String username) {
                try {
                    ResponseEntity<List> response = new RestTemplate().getForEntity(
                            "http://localhost:8089/users/{username}/repos",
                            List.class,
                            username
                    );
                    return response.getBody();
                } catch (Exception e) {
                    throw new GithubUserNotFoundException("User " + username + " not found");
                }
            }

            @Override
            List<Map<String, Object>> getBranches(String owner, String repo) {
                return List.of();
            }
        };

        GitHubService service = new GitHubService(client);
        GitHubController controller = new GitHubController(service);

        GithubUserNotFoundException ex = assertThrows(GithubUserNotFoundException.class,
                () -> controller.getRepositories("unknownuser"));

        assertEquals("User unknownuser not found", ex.getMessage());
    }
}