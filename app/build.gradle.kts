plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.byteduo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.byteduo"
        minSdk = 26
        targetSdk = 33
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
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //new library
    implementation ("com.jpardogo.googleprogressbar:library:1.2.0")

    //firebase lib
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-storage-common:17.0.0")
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")




    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Navigation componenet
    val nav_version = "2.7.5"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")


    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.firebaseui:firebase-ui-database:8.0.2")

    //hashing password
    implementation ("at.favre.lib:bcrypt:0.9.0")
    //in use
    implementation ("org.mindrot:jbcrypt:0.4")

    //glide to round the images

    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation ("androidx.recyclerview:recyclerview:1.2.1")


}
