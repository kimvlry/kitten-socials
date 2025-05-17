plugins {
    java
}

subprojects {
    apply(plugin = "java")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    repositories {
        mavenCentral()
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
}