pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "TrailBook"
include(":app")
include(":core:common")
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:design")
include(":feature:authentication")
include(":feature:home")
include(":feature:explore")
include(":feature:experience")
include(":feature:profile")
