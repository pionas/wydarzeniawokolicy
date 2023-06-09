import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.yaml.snakeyaml.Yaml

buildscript {
    dependencies {
        classpath("org.yaml:snakeyaml:2.0")
    }
}

plugins {
    id("org.springframework.boot") version "3.0.7"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.vaadin") version "24.0.5"
    kotlin("jvm") version "1.8.21"
    kotlin("kapt") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"
    kotlin("plugin.jpa") version "1.8.21"
    id("jacoco")
}

initAppProperties()
group = "pl.wydarzeniawokolicy"
version = extra["appVersion"]!!

java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val vaadinVersion = "24.0.5"
val mapstructVersion = "1.5.5.Final"
val mockitoVersion = "4.1.0"
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.vaadin:vaadin-spring-boot-starter")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoVersion")
    testImplementation("com.github.mvysny.kaributesting:karibu-testing-v24:2.0.2")
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:$vaadinVersion")
    }
}

tasks.withType<JacocoReport> {
    reports {
        xml.required.set(false)
        csv.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

fun initAppProperties() {
    fun getAppDetails(): Map<String, String> {
        val yaml = Yaml()
        val file = File("src/main/resources/application.yaml")
        val data = yaml.load<Map<String, Any>>(file.inputStream())
        return (data["info"] as Map<*, *>).get("app") as Map<String, String>
    }

    val appName by extra { getAppDetails()["name"] }
    val appVersion by extra { getAppDetails()["version"] }
}
