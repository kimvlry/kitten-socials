plugins {
    id("java")
    id("org.hibernate.orm") version "6.6.11.Final"
    id("org.flywaydb.flyway") version "11.4.0"
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
}