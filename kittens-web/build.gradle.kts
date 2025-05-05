plugins {
    application
    id("org.springframework.boot") version "3.4.5"
}

application {
    mainClass.set("ru.kimvlry.kittens.web.Main")
}

dependencies {
    implementation(project(":kittens"))

    implementation("org.springframework.boot:spring-boot-starter-web:3.4.5")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.5")
    implementation("org.springframework.boot:spring-boot-starter-security:3.4.5")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.4.5")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

    testImplementation("org.springframework.security:spring-security-test:6.4.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.4.5")
    testImplementation("org.mockito:mockito-core:5.17.0")

    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("org.mapstruct:mapstruct:1.6.3")
    implementation("org.yaml:snakeyaml:2.4")
    implementation("com.auth0:java-jwt:4.5.0")

    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
}

configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-simple")
}