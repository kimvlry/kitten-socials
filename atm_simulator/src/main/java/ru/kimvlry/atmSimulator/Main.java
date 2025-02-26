package ru.kimvlry.atmSimulator;

import ru.kimvlry.atmSimulator.services.ConsoleInterface;

public class Main {
    public static void main(String[] args) {
        ConsoleInterface atm = new ConsoleInterface();
        atm.start();
    }
}