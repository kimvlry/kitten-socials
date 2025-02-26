package ru.kimvlry.atmSimulator.services;

import ru.kimvlry.atmSimulator.entities.User;
import ru.kimvlry.atmSimulator.operationResults.AccountNotFoundException;
import ru.kimvlry.atmSimulator.operationResults.InsufficientFundsException;
import ru.kimvlry.atmSimulator.operationResults.NoAccountSelectedException;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleInterface {
    private final User user;
    private final Scanner scanner;

    public ConsoleInterface() {
        this.user = new User();
        this.scanner = new Scanner(System.in);
    }

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

    private void createAccount() {
        String accountId = user.createAccount();
        System.out.println("Created an account with ID: " + accountId);
    }

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

    private void checkBalance() {
        try {
            BigDecimal balance = user.getBalance();
            System.out.println("Current balance: " + balance);
        }
        catch (NoAccountSelectedException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void depositMoney() {
        System.out.print("Enter amount to deposit: ");
        try {
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            BigDecimal after = user.deposit(amount);
            System.out.println("Deposit successful! Current balance: " + after);
        }
        catch (NoAccountSelectedException e) {
            System.out.println("Error: " + e.getMessage());
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
        }
    }

    private void withdrawMoney() {
        System.out.print("Enter amount to withdraw: ");
        try {
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            BigDecimal after = user.withdraw(amount);
            System.out.println("Withdrawal successful! Current balance: " + after);
        }
        catch (NoAccountSelectedException | InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
        }
    }

    private void showTransactionHistory() {
        try {
            System.out.println(user.getTransactionHistory());
        }
        catch (NoAccountSelectedException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
