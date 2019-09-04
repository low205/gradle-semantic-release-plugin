[![Build Status](https://travis-ci.org/low205/gradle-semantic-release-plugin.svg?branch=master)](https://travis-ci.org/low205/gradle-semantic-release-plugin)
[![Download](https://api.bintray.com/packages/low205/gradle-plugins/gradle-semantic-release-plugin/images/download.svg) ](https://bintray.com/low205/gradle-plugins/gradle-semantic-release-plugin/_latestVersion)
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

* apply plugin in your root project:    
    
        plugins {
            id("de.maltsev.gradle.semanticrelease") version "[version]"
        }
        
* configure your Travis build to have GITHUB_TOKEN, which is OAuth token with access to your repository
* remove from your gradle build code which sets `version`
* now your current version and next version will be inferred automatically by computing next version based on commits happened after latest release 
* plugin will add extension for Project, which will check if project has new version `Project.hasNewSemanticVersion()`
* plugin will not check for any deployment or publishing tasks, so please provide a check to such tasks:
    
        import de.maltsev.gradle.semanticrelease.hasNewSemanticVersion
        tasks {
            "bintrayUpload"(BintrayUploadTask::class) {
                onlyIf { hasNewSemanticVersion() }
            }
        }
* run task `semanticReleasePublish` to publish new release on GitHub.
    * Preferred way to use it is to add this task to after success in `tavis.yml`:      
        
            script:
              - ./gradlew build
            after_success:
              - ./gradlew semanticReleasePublish
                
* if you had no releases in github, first version will be 'v0.1.0'. You can manually create release with needed semantic version on master branch.

#### Writing commits

* Every patch change commit should be prefixed with `fix`. Examples: 
    * `fix: add check for null pointer exception`
    * `fix(payments): add check for null pointer exception`
    
* Every minor change commit should be prefixed with `feat`. Examples:
    * `feat: create new repository`
    * `feat(jpa): create new repository`
    
* Every major change commit should have `BREAKING CHANGE:` string. Examples:
    * ```
      feat: new version 2 api 
      
      BREAKING CHANGE: old version 1 api removed      
    * ```
       feat(api): new version 2 api 
       
       BREAKING CHANGE: old version 1 api removed

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
    
* Example for release note generated for breaking change with prefix with subType:

      ## v1.1.1 (2019-09-03)
      
      #### Breaking Changes
  
      * **api**: new version 2 api (commitHash)
          ```BREAKING CHANGE: old version 1 api removed```

#### Version infer

When build runs:
* Plugin will find your branch from TRAVIS_PULL_REQUEST_BRANCH or TRAVIS_BRANCH environment properties
* Then will try to find latest release from GitHub. 
* Then based on commit messages in changes will infer and set current project and subprojects version
* If version had changes with major, minor or patch commit plugin will allow `semanticReleasePublish` task to run.

#### Configuration

By default project configured with:

    import de.maltsev.gradle.semanticrelease.VersionInference
    import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup
    
    semanticRelease {
        targetBranch.set("master")
        inferVersion.set(VersionInference.ONLY_ON_TARGET)
        releaseChanges.set(VersionChangeGroup.values().asList())
    }

#### Not on master?
By default plugin infers version only on master. 

On other than `master` plugin can infer branched version with `<branchName>.<lastCommitHash>` pattern. 

You can enable such inference by setting inferVersion configuration to ALWAYS:
    
    import de.maltsev.gradle.semanticrelease.VersionInference
    semanticRelease {    
        inferVersion.set(VersionInference.ALWAYS)
    }

Of course `semanticReleasePublish` task will run only on `master`. 

Also plugin will allow to use extension to check if your are on `master` branch.
For example you would want to publish docker image from any branch, but publish artifacts only on `master`
    
    import de.maltsev.gradle.semanticrelease.isOnTargetBranch
    import de.maltsev.gradle.semanticrelease.hasNewSemanticVersion
    tasks {
         "dockerBuildImage" {
            onlyIf { hasNewSemanticVersion() }
         }
         "bintrayUpload"(BintrayUploadTask::class) {
             onlyIf { hasNewSemanticVersion() && isOnTargetBranch() }
         }        
    }
#### External deployment
You can use versionFile task when you deploying publishing outside of gradle:
        
    import de.maltsev.gradle.semanticrelease.hasNewSemanticVersion
    tasks {
        create("versionFile") {
            onlyIf { hasNewSemanticVersion() }
            doLast {
                file(".version").delete()
                file(".version").writeText(version.toString())
            }
        } 
    }

#### Feedback

Please drop an Issue if you have any feedback or feature wish or you have stumbled on unexpected behavior.
 
