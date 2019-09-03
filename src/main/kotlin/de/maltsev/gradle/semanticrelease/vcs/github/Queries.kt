package de.maltsev.gradle.semanticrelease.vcs.github

internal val tagQuery: String = """query
        |(${'$'}owner:String!, ${'$'}repo:String!, ${'$'}prefix:String!) {
        |   repository(owner: ${'$'}owner, name: ${'$'}repo) {
        |       refs(refPrefix: ${'$'}prefix, first: 1, orderBy: {field: ALPHABETICAL, direction: DESC}) {
        |           edges {
        |               node {
        |                 name
        |                 target {
        |                   oid
        |                 }
        |               }
        |           }
        |       }
        |   }
        |}
    """.trimMargin()

internal val lastCommitQuery: String = """
        |query (${'$'}owner: String!, ${'$'}repo: String!, ${'$'}branchName: String!) {
        |  repository(owner: ${'$'}owner, name: ${'$'}repo) {
        |    ref(qualifiedName: ${'$'}branchName) {
        |      target {
        |        ... on Commit {
        |          history(first: 1) {
        |            pageInfo {
        |              hasNextPage
        |              startCursor
        |              endCursor
        |            }
        |            edges {
        |              node {
        |                ... on Commit {
        |                  id
        |                  oid
        |                  message
        |                  committedDate
        |                }
        |              }
        |            }
        |          }
        |        }
        |      }
        |    }
        |  }
        |}
    """.trimMargin()

internal val commitsQuery: String = """
        |query (${'$'}owner: String!, ${'$'}repo: String!, ${'$'}branchName: String!) {
        |  repository(owner: ${'$'}owner, name: ${'$'}repo) {
        |    ref(qualifiedName: ${'$'}branchName) {
        |      target {
        |        ... on Commit {
        |          history(first: 100) {
        |            pageInfo {
        |              hasNextPage
        |              startCursor
        |              endCursor
        |            }
        |            edges {
        |              node {
        |                ... on Commit {
        |                  id
        |                  oid
        |                  message
        |                  committedDate
        |                }
        |              }
        |            }
        |          }
        |        }
        |      }
        |    }
        |  }
        |}
    """.trimMargin()

internal val nextCommitsQuery = """
        |query (${'$'}owner: String!, ${'$'}repo: String!, ${'$'}branchName: String!, ${'$'}endCursor: String!) {
        |  repository(owner: ${'$'}owner, name: ${'$'}repo) {
        |    ref(qualifiedName: ${'$'}branchName) {
        |      target {
        |        ... on Commit {
        |          history(first: 100, after: ${'$'}endCursor) {
        |            pageInfo {
        |              hasNextPage
        |              startCursor
        |              endCursor
        |            }
        |            edges {
        |              node {
        |                ... on Commit {
        |                  id
        |                  oid
        |                  message
        |                  committedDate
        |                }
        |              }
        |            }
        |          }
        |        }
        |      }
        |    }
        |  }
        |}
        |
        |
    """.trimMargin()
