import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
}
android {
    namespace = "com.orhanobut.dialogplus"
    compileSdk = 37
    defaultConfig {
        minSdk = 15
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // New build tooling should not force consumers to compile against API 37.
        aarMetadata { minCompileSdk = 28 }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
kotlin {
    jvmToolchain(21)
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8) }
}
dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.android.test.runner)
}
dokka {
    dokkaSourceSets.configureEach {
        includes.from("Module.md")
    }
}
mavenPublishing {
    coordinates("com.orhanobut", "dialogplus", providers.gradleProperty("VERSION_NAME").get())
    configure(AndroidSingleVariantLibrary(
        variant = "release",
        sourcesJar = SourcesJar.Sources(),
        javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
    ))
    publishToMavenCentral(automaticRelease = false)
    signAllPublications()
    pom {
        name.set("DialogPlus")
        description.set("Customizable Android dialogs with list, grid and custom view content")
        url.set("https://github.com/orhanobut/dialogplus")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("orhanobut")
                name.set("Orhan Obut")
            }
        }
        scm {
            url.set("https://github.com/orhanobut/dialogplus")
            connection.set("scm:git:https://github.com/orhanobut/dialogplus.git")
            developerConnection.set("scm:git:ssh://git@github.com/orhanobut/dialogplus.git")
        }
    }
}
// Local verification needs no private key. Central publication still requires signing.
configure<SigningExtension> {
    isRequired = !providers.gradleProperty("localPublication").isPresent
}

// The current Android test runner requires API 21; published release artifacts stay at API 15.
androidComponents {
    beforeVariants(selector().withBuildType("debug")) { it.minSdk = 21 }
}
