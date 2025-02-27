package ru.kimvlry.atmSimulator.operationResults;

/**
 * Business-logic checked exception thrown when the amount of money is invalid to be involved in operation
 */
public class InvaidAmountException extends Exception {
    public InvaidAmountException() {
        super("Amount of money to deposit or withdraw must be a positive number");
    }
}
