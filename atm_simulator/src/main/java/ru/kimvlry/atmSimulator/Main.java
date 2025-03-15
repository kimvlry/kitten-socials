package ru.kimvlry.atmSimulator;

import ru.kimvlry.atmSimulator.services.ConsoleInterface;

/**
 * Application entry point.
 * Starts the interactive console interface.
 */
public class Main {
    public static void main(String[] args) {
        ConsoleInterface atm = new ConsoleInterface();
        atm.start();
    }
}