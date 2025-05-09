package ru.kimvlry.kittens.socials;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EntityScan("ru.kimvlry.kittens")
@ComponentScan(basePackages = {"ru.kimvlry.kittens", "ru.kimvlry.kittens.socials"})
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}