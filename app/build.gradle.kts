plugins { alias(libs.plugins.android.application) }
android {
    namespace = "com.orhanobut.android.dialogplussample"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.orhanobut.dialogplussample"
        minSdk = 15
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
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
dependencies { implementation(project(":dialogplus")) }

// The current Android test runner requires API 21; published release artifacts stay at API 15.
androidComponents {
    beforeVariants(selector().withBuildType("debug")) { it.minSdk = 21 }
}
