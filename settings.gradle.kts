pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AdaptiveDesignTokens"
include(":app")
include(":core:design-system")
include(":feature:profile:domain")
include(":feature:profile:data")
include(":feature:profile:presentation")
include(":feature:ready_to_type:domain")
include(":feature:ready_to_type:presentation")
include(":feature:editing_status:presentation")
include(":feature:cloud_photo_upload")
include(":feature:guided_tour")
