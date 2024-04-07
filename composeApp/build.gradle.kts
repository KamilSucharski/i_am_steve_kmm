plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.coroutines.android)
        }

        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(libs.coroutines.core)
            implementation(libs.koin)
            implementation(libs.kotlinxIO)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktorfit.lib)
            implementation(libs.multiplatformSettings)
            implementation(libs.napier)
            implementation(libs.sonner)
            implementation(libs.voyager.koin)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
        }
    }
}

android {
    namespace = "com.iamsteve"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.iamsteve.android"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        val versionMajor = libs.versions.app.major.get().toInt()
        val versionMinor = libs.versions.app.minor.get().toInt()
        val versionPatch = libs.versions.app.patch.get().toInt()

        versionName = String.format("%s.%s.%s", versionMajor, versionMinor, versionPatch)
        versionCode = versionMajor * 1000000 + versionMinor * 1000 + versionPatch
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
        }

        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }

}

dependencies {
    add("kspCommonMainMetadata", libs.ktorfit.ksp)
    add("kspAndroid", libs.ktorfit.ksp)
    add("kspIosArm64", libs.ktorfit.ksp)
    add("kspIosSimulatorArm64", libs.ktorfit.ksp)
    add("kspIosX64", libs.ktorfit.ksp)
}

// Hack from https://github.com/Foso/Ktorfit/issues/512 to fix https://github.com/google/ksp/issues/929
project.afterEvaluate {
    tasks.named("kspKotlinIosArm64") {
        dependsOn("generateMRiosArm64Main")
    }
    tasks.named("kspKotlinIosSimulatorArm64") {
        dependsOn("generateMRiosSimulatorArm64Main")
    }
    tasks.named("kspKotlinIosX64") {
        dependsOn("generateMRiosX64Main")
    }
}