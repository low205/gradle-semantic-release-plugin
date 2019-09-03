[![Build Status](https://travis-ci.org/low205/gradle-semantic-release-plugin.svg?branch=master)](https://travis-ci.org/low205/gradle-semantic-release-plugin)
[ ![Download](https://api.bintray.com/packages/low205/gradle-plugins/gradle-semantic-release-plugin/images/download.svg) ](https://bintray.com/low205/gradle-plugins/gradle-semantic-release-plugin/_latestVersion)
[![codecov](https://codecov.io/gh/low205/gradle-semantic-release-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/low205/gradle-semantic-release-plugin)

# gradle-semantic-release-plugin
This is a very simple semantic release plugin for projects which use *GitHub* and *Travis*.

##### Inspired by:
 * [Semantic Release](https://github.com/semantic-release/semantic-release)
 * [Standalone Go Semantic Release](https://github.com/go-semantic-release/semantic-release)

#### How does it work?
Instead of writing [meaningless commit messages](http://whatthecommit.com/), we can take our time to think about the changes in the codebase and write them down. Following the [AngularJS Commit Message Conventions](https://docs.google.com/document/d/1QrDFcIiPjSLDn3EL15IJygNPiHORgU1_OOAqWjiDU5Y/edit) it is then possible to generate a helpful changelog and to derive the next semantic version number from them.

When `semantic-release` is setup it will do that after every successful continuous integration build of your master branch (or any other branch you specify) and publish the new version for you. This way no human is directly involved in the release process and your releases are guaranteed to be [unromantic and unsentimental](http://sentimentalversioning.org/).

_Source: [semantic-release/semantic-release#how-does-it-work](https://github.com/semantic-release/semantic-release#how-does-it-work)_

#### How to use?

* apply plugin:
    ```
    plugins {
        id("de.maltsev.gradle.semanticrelease") version "[version]"
    }
    ```
* configure your Travis build to have GITHUB_TOKEN, which is OAuth token with access to your repository
* remove from your gradle build code which sets `version`
* now your current version and next version will be inferred automatically by computing next version based on commits happened after latest release 
* your version will be a class of type SemanticVersion, toString() of which will return semantic version without prefix
* version will be always inferred, you can check that you will have new version buy checking `project.hasProperty("semanticVersion")`
* run task `semanticReleasePublish` to publish new release on GitHub. 
* versions will start from 'v0.1.0' or just create first release manually with needed semantic version
* you can use these additional prefixes for release notes groups, all other commits and prefixes will land in `Others`:

    | Prefix   | Group name               |
    |----------|--------------------------|
    | perf     | Performance Improvements |
    | revert   | Reverts                  |
    | docs     | Documentation            |
    | style    | Style                    |
    | refactor | Code Refactoring         |
    | test     | Tests                    |
    | chore    | Chores                   |
