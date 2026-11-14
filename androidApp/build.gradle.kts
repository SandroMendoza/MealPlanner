import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    debugImplementation(libs.compose.uiTooling)
}

// Release signing values come from local.properties (local builds) or environment
// variables (CI). When any is missing, release builds are produced unsigned.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

fun signingValue(name: String): String? =
    localProperties.getProperty(name) ?: System.getenv(name)

val releaseStoreFile = signingValue("RELEASE_STORE_FILE")
val releaseStorePassword = signingValue("RELEASE_STORE_PASSWORD")
val releaseKeyAlias = signingValue("RELEASE_KEY_ALIAS")
val releaseKeyPassword = signingValue("RELEASE_KEY_PASSWORD")
val hasReleaseSigning = listOf(releaseStoreFile, releaseStorePassword, releaseKeyAlias, releaseKeyPassword)
    .all { !it.isNullOrBlank() }

// Google's published sample IDs, which only ever serve test ads. Debug builds always use
// them; release builds use the real AdMob IDs from local.properties or environment
// variables, and fall back to these when the real ones are missing.
val admobTestAppId = "ca-app-pub-3940256099942544~3347511713"
val admobTestBannerId = "ca-app-pub-3940256099942544/9214589741"

val admobAppId = signingValue("ADMOB_APP_ID")
val admobBannerHomeId = signingValue("ADMOB_BANNER_HOME_ID")
val admobBannerMealPlanId = signingValue("ADMOB_BANNER_MEAL_PLAN_ID")
val hasAdmobIds = listOf(admobAppId, admobBannerHomeId, admobBannerMealPlanId)
    .all { !it.isNullOrBlank() }
if (!hasAdmobIds) {
    logger.warn("AdMob IDs missing from local.properties; release builds will serve test ads only.")
}

android {
    namespace = "com.meal.planner"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "sandragon.inc.mealplanner"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(releaseStoreFile!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            manifestPlaceholders["admobAppId"] = admobTestAppId
            manifestPlaceholders["admobBannerHomeId"] = admobTestBannerId
            manifestPlaceholders["admobBannerMealPlanId"] = admobTestBannerId
        }
        getByName("release") {
            manifestPlaceholders["admobAppId"] = if (hasAdmobIds) admobAppId!! else admobTestAppId
            manifestPlaceholders["admobBannerHomeId"] = if (hasAdmobIds) admobBannerHomeId!! else admobTestBannerId
            manifestPlaceholders["admobBannerMealPlanId"] = if (hasAdmobIds) admobBannerMealPlanId!! else admobTestBannerId
            isMinifyEnabled = true
            isShrinkResources = true
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}