// build.gradle.kts (Project)
plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}