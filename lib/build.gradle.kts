import com.vanniktech.maven.publish.SonatypeHost

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
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
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
version = "0.0.2"

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