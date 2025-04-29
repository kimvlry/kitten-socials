plugins {
    application
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("java")
    id("org.openapi.generator") version "7.6.0"
}

application {
    mainClass.set("ru.kimvlry.kittens.web.main")
}

dependencies {
    implementation(project(":kittens"))

    implementation("org.flywaydb:flyway-core:11.4.0")
    implementation("org.flywaydb:flyway-database-postgresql:11.4.0")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("com.github.javafaker:javafaker:0.14") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    implementation("org.yaml:snakeyaml:1.17")
}

configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-simple")
}