# Glm-Common [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A repository for commonly used code across different Glm server side implementations.

## Building
**Note:** If you do not have Gradle installed then use ./gradlew for Unix systems or Git Bash and gradlew.bat for Windows 
systems in place of any 'gradle' command.

In order to build Glm-Common just run the `gradle build` command. Once that is finished you will find library, sources, and 
javadoc .jars exported into the `./build/libs` folder and the will be labeled like the following.
```
GlmApi-x.x.x.jar
GlmApi-x.x.x-javadoc.jar
GlmApi-x.x.x-sources.jar
```

**Alternatively** you can include Glm-Common in your build.gradle file by using the following.
```
repositories {
    maven {
        name = 'reallifegames'
        url = 'https://reallifegames.net/artifactory/gradle-release-local'
    }
}

dependencies {
    compile 'net.reallifegames:GlmCommon:x.x.x' // For compile time.
    runtime 'net.reallifegames:GlmCommon:x.x.x' // For usage in a runtime application.
}
```