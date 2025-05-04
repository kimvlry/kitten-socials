plugins {
    application
    id("org.flywaydb.flyway") version ("11.8.0")
}

application {
    mainClass.set("ru.kimvlry.kittens.Main")
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
}