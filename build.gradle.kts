// build.gradle.kts (Project)
plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id ("org.jetbrains.kotlin.kapt") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}

