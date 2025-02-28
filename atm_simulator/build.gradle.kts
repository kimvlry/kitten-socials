plugins {
    java
    application
}

tasks.register<JavaExec>("runApp") {
    description = "starts interactive console interface"
    mainClass.set("ru.kimvlry.atmSimulator.Main")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
}