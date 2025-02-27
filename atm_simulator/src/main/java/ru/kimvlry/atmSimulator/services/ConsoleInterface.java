package ru.kimvlry.atmSimulator.services;

import ru.kimvlry.atmSimulator.entities.User;
import ru.kimvlry.atmSimulator.operationResults.AccountNotFoundException;
import ru.kimvlry.atmSimulator.operationResults.InsufficientFundsException;
import ru.kimvlry.atmSimulator.operationResults.InvaidAmountException;
import ru.kimvlry.atmSimulator.operationResults.NoAccountSelectedException;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Provides an interactive console interface for the app.
 * Allows User to operate their bank accounts
 */
public class ConsoleInterface {
    private final User user;
    private final Scanner scanner;

    /**
     * Initializes the console interface with a new User instance and input scanner.
     */
    public ConsoleInterface() {
        this.user = new User();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the ATM simulation. Reacts to User's input and until the User chooses to exit.
     */
    public void start() {
        showOptions();
        while (true) {
            System.out.print("\nChoose an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> createAccount();
                case "2" -> switchAccount();
                case "3" -> checkBalance();
                case "4" -> depositMoney();
                case "5" -> withdrawMoney();
                case "6" -> showTransactionHistory();
                case "0" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> showOptions();
            }
        }
    }

    /**
     * Shows the list of options available in the simulator.
     */
    private void showOptions() {
        System.out.println("\nATM Simulator is now running! Choose an option:");
        System.out.println("1. Create new bank account");
        System.out.println("2. Switch account");
        System.out.println("3. View balance");
        System.out.println("4. Deposit money");
        System.out.println("5. Withdraw money");
        System.out.println("6. View transaction history");
        System.out.println("0. Exit");
    }

    /**
     * Handles user input to create a new bank account.
     * Delegates the creation process to the {@link User} class
     * and displays the generated account ID.
     */
    private void createAccount() {
        String accountId = user.createAccount();
        System.out.println("Created an account with ID: " + accountId);
    }

    /**
     * Handles user input for switching accounts.
     * Delegates the operation to the {@link User} class
     * and prints either success or an error message.
     */
    private void switchAccount() {
        System.out.print("Enter account ID to switch to: ");
        String accountNumber = scanner.nextLine();
        try {
            user.switchAccount(accountNumber);
            System.out.println("Switched to account: " + accountNumber);
        }
        catch (AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Handles user input to check the balance of the selected account.
     * Delegates the request to the {@link User} class and displays either
     * the balance or an error message.
     */
    private void checkBalance() {
        try {
            BigDecimal balance = user.getBalance();
            System.out.println("Current balance: " + balance);
        }
        catch (NoAccountSelectedException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Handles user input to deposit money into the selected account.
     * Delegates the operation to the {@link User} class and displays either
     * the updated balance or an error message.
     */
    private void depositMoney() {
        System.out.print("Enter amount to deposit: ");
        try {
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            BigDecimal after = user.deposit(amount);
            System.out.println("Deposit successful! Current balance: " + after);
        }
        catch (NoAccountSelectedException | InvaidAmountException e) {
            System.out.println("Error: " + e.getMessage());
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
        }
    }

    /**
     * Handles user input to withdraw money from the selected account.
     * Delegates the operation to the {@link User} class and displays either
     * the updated balance or an error message.
     */
    private void withdrawMoney() {
        System.out.print("Enter amount to withdraw: ");
        try {
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            BigDecimal after = user.withdraw(amount);
            System.out.println("Withdrawal successful! Current balance: " + after);
        }
        catch (NoAccountSelectedException | InsufficientFundsException | InvaidAmountException e) {
            System.out.println("Error: " + e.getMessage());
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
        }
    }

    /**
     * Handles user input to display the transaction history of the selected account.
     * Delegates the request to the {@link User} class and prints either
     * the transaction history or an error message.
     */
    private void showTransactionHistory() {
        try {
            System.out.println(user.getTransactionHistory());
        }
        catch (NoAccountSelectedException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
