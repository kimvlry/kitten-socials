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
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
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