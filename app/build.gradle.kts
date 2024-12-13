plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "com.c242_ps044.artoz"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.c242_ps044.artoz"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  viewBinding {
    enable = true
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.preference.ktx)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  implementation(libs.retrofit)
  implementation(libs.converter.gson)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.androidx.lifecycle.viewmodel.ktx)
  implementation(libs.androidx.lifecycle.livedata.ktx)
  implementation(libs.androidx.swiperefreshlayout)
  implementation(libs.okhttp)
}