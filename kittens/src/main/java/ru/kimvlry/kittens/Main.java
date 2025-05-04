package ru.kimvlry.kittens;

import org.flywaydb.core.Flyway;

public class Main {
    public static void main(String[] args) {

        String jdbcUrl = "jdbc:postgresql://localhost:5432/kittens_demo";
        String username = "postgres";
        String password = "postgres";

        Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .cleanDisabled(false)
                .load();

        flyway.clean();
        flyway.migrate();
    }
}
