package ru.kimvlry.atmSimulator.operationResults;

/**
 * Business-logic checked exception thrown when the operation is attempted before any account is selected to operate
 */
public class NoAccountSelectedException extends Exception {
    public NoAccountSelectedException() {
        super("There's no 'current account'. Select an account to operate in the first place");
    }
}
