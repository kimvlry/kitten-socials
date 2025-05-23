plugins {
    java
    application
    id("org.flywaydb.flyway") version ("11.8.0")
    id("org.springframework.boot") version "3.4.5"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("ru.kimvlry.kittens.socials.Main")
}

flyway {
    url = "jdbc:postgresql://localhost:5432/kittens_db"
    user = "postgres"
    password = "postgres"
    locations = arrayOf("classpath:db/migration")
    baselineOnMigrate = true
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.1")
    implementation("org.hibernate:hibernate-core:6.6.13.Final")
    implementation("org.postgresql:postgresql:42.7.5")

    testImplementation("org.testcontainers:testcontainers:1.19.8")
    testImplementation("org.testcontainers:junit-jupiter:1.19.8")
    testImplementation("org.testcontainers:postgresql:1.19.8")
    testImplementation("org.slf4j:slf4j-simple:2.0.16")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

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

tasks.test {
    useJUnitPlatform()
}

tasks.register<Javadoc>("generateJavaDoc") {
    source = sourceSets["main"].allJava
    classpath = sourceSets["main"].compileClasspath
    setDestinationDir(file("javadoc"))
}
tasks.build {
    dependsOn("generateJavaDoc")
}

configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-simple")
}