import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    id("maven-publish")
    id("com.vanniktech.maven.publish") version "0.30.0"
}

kotlin {
    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            // todo: check if I can use this dependency for
            //  androidx.compose.material3.Text
            //  androidx.compose.material3.Icon
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(compose.uiTooling)
        }
        jvmTest.dependencies {
            implementation(compose.desktop.uiTestJUnit4)
            implementation("org.mockito.kotlin:mockito-kotlin:6.0.0")
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "com.thedroiddiv.menu"
    generateResClass = auto
}

group = "io.github.thedroiddiv"
version = "0.0.1"

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(
        group.toString(),
        "corntex",
        version.toString()
    )

    pom {
        name = "Corntex"
        description = "A flexible and hierarchical context menu library for Compose Desktop."
        inceptionYear = "2025"
        url = "https://github.com/thedroiddiv/Corntex"
        licenses {
            license {
                name.set("GNU General Public License v3.0")
                url.set("https://opensource.org/license/gpl-3-0")
            }
        }

        developers {
            developer {
                id.set("thedroiddiv@gmail.com")
                name.set("Divyansh Kushwaha")
                email.set("thedroiddiv@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:ssh://git@github.com/thedroiddiv/Corntex.git")
            developerConnection.set("scm:git:ssh://git@github.com/thedroiddiv/Corntex.git")
            url.set("https://github.com/thedroiddiv/Corntex.git")
        }
    }
}