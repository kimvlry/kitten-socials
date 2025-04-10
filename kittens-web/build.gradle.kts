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

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$rootDir/kitten-api/openapi.yaml")
    outputDir.set("${layout.buildDirectory}/generated")
    apiPackage.set("ru.kimvlry.api")
    modelPackage.set("ru.kimvlry.dto")
    invokerPackage.set("ru.kimvlry.invoker")
    configOptions.set(
        mapOf(
            "interfaceOnly" to "true",
            "useSpringBoot3" to "true",
            "dateLibrary" to "java8"
        )
    )
}

sourceSets["main"].java.srcDirs(
    "$projectDir/build/generated/src/main/java"
)

tasks.compileJava {
    dependsOn("openApiGenerate")
}
