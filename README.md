[![Build Status](https://travis-ci.org/low205/gradle-semantic-release-plugin.svg?branch=master)](https://travis-ci.org/low205/gradle-semantic-release-plugin)
[![Download](https://api.bintray.com/packages/low205/gradle-plugins/gradle-semantic-release-plugin/images/download.svg?version=0.1.0) ](https://bintray.com/low205/gradle-plugins/gradle-semantic-release-plugin/0.1.0/link)
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
        id("de.maltsev.gradle.semanticrelease") version "0.1.0"
    }
    ```
* configure your Travis build to have GITHUB_TOKEN, which is OAuth token with access to your repository
* remove from your gradle build code which sets `version`
* run `semanticReleasePublish` task. 
    * It will run `semanticReleaseVersion` task. It will read your last release from GitHub and will prepare list of changes based on commit messages happened after you last release.
    * If you on master it will create release and publish it to GitHub
    * If you on any other branch it will only infer version with `-<branchName>.<commitNumber>` suffix. 
    * If version did not changed, version will not be set
    * If version was not found, it will create release from all commits with `v0.1.0` tag name.
    * If you want to start from some specific version, just create release manually with proper semantic version
