plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

tasks.processResources {
    expand(mapOf("version" to version))
}

group = "com.rudderstack"

repositories {
    mavenCentral()
}
val kafkaVersion = "3.7.0"
val slf4japiVersion = "2.0.12"
val junitVersion = "5.10.2"
val jacksonVersion = "2.16.2"

dependencies {
    implementation("org.apache.kafka:connect-api:$kafkaVersion")
    implementation("org.apache.kafka:connect-json:$kafkaVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")

    implementation("org.slf4j:slf4j-api:$slf4japiVersion")
    implementation("com.rudderstack.sdk.java.analytics:analytics:3.0.0")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.mockito:mockito-core:5.11.0")
}

tasks.test {
    useJUnitPlatform()
}
tasks {
    shadowJar {
        archiveBaseName.set("rudderstack-kafka-connector")
        archiveVersion.set("$version")
        archiveClassifier.set("")
    }
}
