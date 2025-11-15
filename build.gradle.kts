// build.gradle.kts (Project: GymVance)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // AÑADE ESTO: PLUGIN CON VERSIÓN
    id("com.google.gms.google-services") version "4.4.2" apply false
}
