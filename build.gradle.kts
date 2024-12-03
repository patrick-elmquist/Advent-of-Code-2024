import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.21"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    implementation("io.ktor:ktor-client-java:3.0.1")
    implementation("io.ktor:ktor-client-logging-jvm:3.0.1")
    implementation("ch.qos.logback:logback-classic:1.5.12")
    implementation("tools.aqua:z3-turnkey:4.12.2.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
    }
}
