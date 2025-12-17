package com.atipera.githubproxy;

public record BranchResponse(
        String name,
        String lastCommitSha
) {}