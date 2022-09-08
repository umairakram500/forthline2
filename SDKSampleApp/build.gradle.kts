import java.net.URI

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = URI.create("https://maven.pkg.github.com/Fourthline-com/FourthlineSDK-Android")
            credentials {
                username = ""
                password = getLocalProperty(propertyName = "FourthlineSdkGithubToken")
            }
        }
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}