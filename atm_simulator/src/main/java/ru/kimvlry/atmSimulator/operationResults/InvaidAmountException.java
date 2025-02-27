package ru.kimvlry.atmSimulator.operationResults;

/**
 *
 */
public class InvaidAmountException extends Exception {
    public InvaidAmountException() {
        super("Amount of money to deposit or withdraw must be a positive number");
    }
}
