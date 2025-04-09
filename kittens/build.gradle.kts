plugins {
    application
    java
    id("org.hibernate.orm") version "6.6.11.Final"
    id("org.flywaydb.flyway") version "11.4.0"
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
    implementation("org.flywaydb:flyway-core:11.4.0")
    implementation("org.flywaydb:flyway-database-postgresql:11.4.0")
    implementation("org.jboss.logging:jboss-logging:3.5.3.Final")
}