# Face Recognition SDK-Library (DEMO)
A Demo-Library for face recognize and validation using Deepface

## Requirements
* Android Gradle Plugin (AGP) 7.3.1

## Preparation
### 1. Generate GitHub Access Token
* Login to GitHub
* Go to Setting > Developer Settings > Personal Access Tokens > Generate new token
* Make sure you select the following scopes:

![Access Token](https://github.com/NeoRevolt/drawimage-demo-gitpack-publish/blob/master/acces_scope_github_token.PNG?raw=true)

### 2. Create ``github.properties`` file within the root project
```properties
    gpr.usr=YOUR_USERNAME //Change with your username
    gpr.key=YOUR_KEY  //Change with your personal access token before
```
Then add this properties to ``.gitignore``

## Usage / Implementation

### Gradle (settings.gradle)

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // Set to PREFER_SETTINGS
    repositories {
        google()
        mavenCentral()
        
        // Set target packages repo
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/NeoRevolt/FaceApp")
            credentials {
                username = 'gpr.usr'
                password = 'gpr.key'
            }
        }
    }
}
```

### Gradle (build.gradle/App)

```groovy
plugins {
    id 'kotlin-kapt'
}

android {
    compileSdk 33
    
    defaultConfig {
        targetSdk 33
    }
}

dependencies {
    implementation 'com.github.neorevolt:face-sdk:1.0.0'
}
```

# Sample Code
#### Save Token - Validate and Send Action to API:

```kotlin
// Initialize
private lateinit var faceVerification: FaceVerification 

public override fun onCreate(savedInstanceState: Bundle?) {
   ....
    
    // Get Instance
     faceVerification = FaceVerification()
    
    binding.apply {
            btnVerify.setOnClickListener {
            faceVerification.verify(imgFile1, imgFile2) // Verify image between two images
     }      
}
```
