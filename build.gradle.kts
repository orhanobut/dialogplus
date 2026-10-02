buildscript {
    dependencies {
        // Upgrade AGP's built-in Kotlin compiler; no separate Kotlin Android plugin.
        classpath(libs.kotlin.gradle.plugin)
    }
}
plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.maven.publish) apply false
}
