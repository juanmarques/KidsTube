pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "KidsTube"

include(":app")
include(":core:core-ui")
include(":core:core-common")
include(":core:core-data")
include(":core:core-database")
include(":core:core-network")
include(":core:core-domain")
include(":core:core-datastore")
include(":feature:feature-home")
include(":feature:feature-search")
include(":feature:feature-player")
include(":feature:feature-parental")
include(":feature:feature-onboarding")
