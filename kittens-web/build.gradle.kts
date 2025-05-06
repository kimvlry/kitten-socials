plugins {
    application
    id("org.flywaydb.flyway") version ("11.8.0")
    id("org.springframework.boot") version "3.4.5"
}

application {
    mainClass.set("ru.kimvlry.kittens.web.Main")
}

flyway {
    url = "jdbc:postgresql://localhost:5432/kittens_db"
    user = "postgres"
    password = "postgres"
    locations = arrayOf("classpath:db/migration")
    baselineOnMigrate = true
}

dependencies {
    implementation("org.flywaydb:flyway-core:11.8.0")
    implementation("org.flywaydb:flyway-database-postgresql:11.8.0")
    implementation("org.jboss.logging:jboss-logging:3.6.1.Final")

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
    implementation("com.auth0:java-jwt:4.5.0")

    implementation("org.yaml:snakeyaml:2.3")
    implementation("com.github.javafaker:javafaker:1.0.2") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }

    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
}

configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-simple")
}