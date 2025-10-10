plugins {
    id("io.micronaut.application") version "4.5.4"
    id("com.gradleup.shadow") version "9.2.2"
}

version = "0.1"
group = "micronaut.documentation.search"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut.opensearch:micronaut-opensearch-httpclient5")

    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")

    implementation("io.micronaut.views:micronaut-views-thymeleaf")
    implementation("io.micronaut.opensearch:micronaut-opensearch")
    implementation("org.apache.httpcomponents.client5:httpclient5")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-management")
    // HTML -> Markdown converter
    implementation("com.vladsch.flexmark:flexmark-html2md-converter:0.64.8")
    implementation("io.micronaut.mcp:micronaut-mcp-server-java-sdk:0.0.12")
    testImplementation("io.micronaut.mcp:micronaut-mcp-client-java-sdk:0.0.12")
    annotationProcessor("io.micronaut.jsonschema:micronaut-json-schema-processor:1.7.2")
    implementation("io.micronaut.jsonschema:micronaut-json-schema-annotations:1.7.2")
    implementation("io.micronaut.cache:micronaut-cache-caffeine")
    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    testImplementation("io.micronaut:micronaut-http-client")
}
tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
    options.compilerArgs.add("-Amicronaut.jsonschema.baseUri=https://micronaut.fun/schemas")
}
application {
    mainClass = "fun.micronaut.Application"
}
java {
    sourceCompatibility = JavaVersion.toVersion("21")
    targetCompatibility = JavaVersion.toVersion("21")
}


graalvmNative.toolchainDetection = false

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("micronaut.documentation.search.*")
    }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
}
