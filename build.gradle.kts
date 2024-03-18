plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("jacoco")
    id("org.sonarqube") version "4.4.1.3373"
}

group = "com.rudderstack"

repositories {
    mavenCentral()
}
// x-release-please-start-version
val version="0.1.0"
// x-release-please-end

project.version = version

val kafkaVersion = "3.7.0"
val slf4jApiVersion = "2.0.12"
val junitVersion = "5.10.2"
val jacksonVersion = "2.16.2"
val rudderAnalytics = "3.0.0"
val mockitoCore = "5.11.0"

dependencies {
    implementation("org.apache.kafka:connect-api:$kafkaVersion")
    implementation("org.apache.kafka:connect-json:$kafkaVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")

    implementation("org.slf4j:slf4j-api:$slf4jApiVersion")
    implementation("com.rudderstack.sdk.java.analytics:analytics:$rudderAnalytics")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.mockito:mockito-core:$mockitoCore")
}

jacoco {
    toolVersion = "0.8.11"
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
        archiveVersion.set("$version")
        archiveClassifier.set("")
    }
}



