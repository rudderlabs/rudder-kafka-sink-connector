plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("jacoco")
    id("org.sonarqube") version "4.4.1.3373"
}

group = "com.rudderstack"

repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven/")
}
// x-release-please-start-version
val version="0.4.1"
// x-release-please-end

project.version = version

// Set Java compatibility to JDK 21
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

val kafkaVersion = "3.8.0"
val slf4jApiVersion = "2.0.16"
val junitVersion = "5.10.2"
val jacksonVersion = "2.18.0"
val rudderAnalytics = "3.1.2"
val mockitoCore = "5.14.1"
val avroVersion = "1.12.0"
val confluentVersion = "7.7.1"
val okhttpVersion = "4.12.0"

dependencies {
    implementation("org.apache.kafka:connect-api:$kafkaVersion")
    implementation("org.apache.kafka:connect-json:$kafkaVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")

    implementation("org.slf4j:slf4j-api:$slf4jApiVersion")
    implementation("org.slf4j:slf4j-log4j12:$slf4jApiVersion")
    implementation("com.rudderstack.sdk.java.analytics:analytics:$rudderAnalytics")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("org.apache.avro:avro:$avroVersion")
    implementation("io.confluent:kafka-connect-avro-converter:$confluentVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.mockito:mockito-core:$mockitoCore")
    testImplementation("com.squareup.okhttp3:mockwebserver:$okhttpVersion")
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory = layout.buildDirectory.dir("coverage")
}
sonarqube {
  properties {
    property("sonar.projectKey", "rudderlabs_rudder-kafka-sink-connector")
    property("sonar.organization", "rudderlabs")
    property("sonar.host.url", "https://sonarcloud.io")
    property("sonar.gradle.skipCompile", "true")
  }
}

sonar {
  properties {
    property("sonar.projectKey", "rudderlabs_rudder-kafka-sink-connector")
    property("sonar.organization", "rudderlabs")
    property("sonar.host.url", "https://sonarcloud.io")
    property("sonar.gradle.skipCompile", "true")
  }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
}

tasks {
    shadowJar {
        archiveBaseName.set("rudderstack-kafka-connector")
        archiveVersion.set(version)
        archiveClassifier.set("")
    }
}



