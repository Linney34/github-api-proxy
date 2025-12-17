# GitHub Proxy API

A Spring Boot service that exposes GitHub repository information through a simplified API.  
It retrieves repositories and their branches for a given GitHub user, filtering out forked repositories.

---

## Features

- List all **non-fork repositories** of a GitHub user.
- For each repository, return:
    - Repository name
    - Owner login
    - Branches with name and last commit SHA
- Returns **404** in a custom JSON format if the GitHub user does not exist:

```json
{
  "status": 404,
  "message": "User <username> not found"
}
```
- Uses GitHub REST API v3: https://developer.github.com/v3

## API Endpoints

### GET `/users/{username}/repositories`

Retrieve all non-fork repositories for a user with their branches.

**Request Example:**

```bash
GET http://localhost:8080/users/octocat/repositories
```

**Response Example:**
```json
[
  {
    "repositoryName": "Hello-World",
    "ownerLogin": "octocat",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "a1b2c3d4e5f6..."
      },
      {
        "name": "develop",
        "lastCommitSha": "f6e5d4c3b2a1..."
      }
    ]
  }
]
```
**Error Response Example (User Not Found):**
```json
{
"status": 404,
"message": "User unknownuser not found"
}
```
## Technologies
- Java 25

- Spring Boot 4

- RestTemplate for GitHub API integration

- Maven for dependency management

## How to Run

1. Clone the repository:
```bash
git clone https://github.com/yourusername/github-proxy.git
cd github-proxy
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The service will start at: http://localhost:8080

## Notes

- Forked repositories are excluded from the response.

- If the user has no repositories, an empty list [] is returned.

- The service handles only public repositories as GitHub API requires authentication for private repos.