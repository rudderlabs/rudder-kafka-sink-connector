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
val kafkaVersion = "2.8.0"
val slf4japiVersion = "2.0.12"
val junitVersion = "5.10.2"

dependencies {
    compileOnly("org.apache.kafka:connect-api:$kafkaVersion")
    compileOnly("org.apache.kafka:connect-json:$kafkaVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")

    implementation("org.slf4j:slf4j-api:$slf4japiVersion")
    implementation("com.rudderstack.sdk.java.analytics:analytics:3.0.0")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
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
