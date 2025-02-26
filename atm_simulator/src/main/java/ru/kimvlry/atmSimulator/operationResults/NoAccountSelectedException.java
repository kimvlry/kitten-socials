package ru.kimvlry.atmSimulator.operationResults;

public class NoAccountSelectedException extends Exception {
    public NoAccountSelectedException() {
        super("There's no 'current account'. Select an account to operate in the first place");
    }
}
