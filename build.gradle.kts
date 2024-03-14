plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("jacoco")
}

tasks.processResources {
    expand(mapOf("version" to version))
}

group = "com.rudderstack"

repositories {
    mavenCentral()
}
val kafkaVersion = "3.7.0"
val slf4jApiVersion = "2.0.12"
val junitVersion = "5.10.2"
val jacksonVersion = "2.16.2"
val rudderAnalytics = "3.0.0"
val mockitoCore = "5.11.0"
var curatorTest = "5.6.0"

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
    testImplementation("org.apache.curator:curator-test:$curatorTest")

}

jacoco {
    toolVersion = "0.8.11"
    reportsDirectory = layout.buildDirectory.dir("coverage")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks {
    shadowJar {
        archiveBaseName.set("rudderstack-kafka-connector")
        archiveVersion.set("$version")
        archiveClassifier.set("")
    }
}
