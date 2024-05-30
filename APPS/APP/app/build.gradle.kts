plugins {
    alias(libs.plugins.androidApplication)

    id("com.google.gms.google-services") version "4.4.1"

}


android {
    namespace = "com.example.firebasedemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.firebasedemo"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.common)
    implementation(files("C:\\Users\\marti\\Desktop\\APPS\\app\\libs\\activation.jar"))
    implementation(files("C:\\Users\\marti\\Desktop\\APPS\\app\\libs\\additionnal.jar"))
    implementation(files("C:\\Users\\marti\\Desktop\\APPS\\app\\libs\\mail.jar"))
    implementation(files("C:\\Users\\marti\\Desktop\\APPS\\zaloPay\\zpdk-release-v3.1.aar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation ("com.google.firebase:firebase-database:21.0.0")
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.google.zxing:core:3.4.1")
    //zalo pay
    implementation("commons-codec:commons-codec:1.14")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")


}
