package ru.kimvlry.atmSimulator;

import org.junit.jupiter.api.Test;
import ru.kimvlry.atmSimulator.entities.BankAccount;
import ru.kimvlry.atmSimulator.entities.User;
import ru.kimvlry.atmSimulator.operationResults.AccountNotFoundException;
import ru.kimvlry.atmSimulator.operationResults.InsufficientFundsException;
import ru.kimvlry.atmSimulator.operationResults.InvalidAmountException;
import ru.kimvlry.atmSimulator.operationResults.NoAccountSelectedException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankAccountTests {
    @Test
    void nonPositiveAmountToDepositShouldFail() {
        BankAccount bankAccount = new BankAccount("777");
        assertThrows(InvalidAmountException.class, () -> bankAccount.deposit(BigDecimal.valueOf(-100)));
    }

    @Test
    void nonPositiveAmountToWithdrawShouldFail() {
        BankAccount bankAccount = new BankAccount("777");
        assertDoesNotThrow(() -> bankAccount.deposit(BigDecimal.valueOf(101)));
        assertThrows(InvalidAmountException.class, () -> bankAccount.withdraw(BigDecimal.valueOf(-100)));
    }

    @Test
    void positiveAmountToWithdrawShouldPassWithSufficientFunds() {
        BankAccount bankAccount = new BankAccount("777");
        assertDoesNotThrow(() -> bankAccount.deposit(BigDecimal.valueOf(101)));
        assertDoesNotThrow(() -> bankAccount.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void positiveAmountToWithdrawShouldFailWithInsufficientFunds() {
        BankAccount bankAccount = new BankAccount("777");
        assertDoesNotThrow(() -> bankAccount.deposit(BigDecimal.valueOf(101)));
        assertThrows(InsufficientFundsException.class, () -> bankAccount.withdraw(BigDecimal.valueOf(102)));
    }
}


class UserTest {
    @Test
    void switchingToInvalidAccountIDShouldFail() {
        User user = new User();
        String first_account = user.createAccount();
        String invalidID = first_account + "invalid";
        assertThrows(AccountNotFoundException.class, () -> user.switchAccount(invalidID));
    }

    @Test
    void switchingToValidAccountIDShouldPass() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
    }

    @Test
    void getBalanceOperationWithNoAccountSelectedShouldFail() {
        User user = new User();
        assertThrows(NoAccountSelectedException.class, () -> user.getBalance());
    }

    @Test
    void getBalanceOperationWithCurrentAccountSelectedShouldPass() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertDoesNotThrow(() -> user.getBalance());
    }

    @Test
    void depositWithNoAccountSelectedShouldFail() {
        User user = new User();
        assertThrows(NoAccountSelectedException.class, () -> user.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void negativeDepositWithCurrentAccountSelectedShouldFail() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertThrows(InvalidAmountException.class, () -> user.deposit(BigDecimal.valueOf(-100)));
    }

    @Test
    void nonNegativeDepositWithCurrentAccountSelectedShouldPass() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertDoesNotThrow(() -> user.deposit(BigDecimal.valueOf(100)));
    }

    @Test
    void withdrawWithNoAccountSelectedShouldFail() {
        User user = new User();
        String first_account = user.createAccount();
        assertThrows(NoAccountSelectedException.class, () -> user.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void negativeAmountWithdrawShouldFail() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertThrows(InvalidAmountException.class, () -> user.withdraw(BigDecimal.valueOf(-100)));
    }

    @Test
    void withdrawWithInsufficientFundsShouldFail() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertDoesNotThrow(() -> user.deposit(BigDecimal.valueOf(100)));
        assertThrows(InsufficientFundsException.class, () -> user.withdraw(BigDecimal.valueOf(101)));
    }

    @Test
    void nonNegativeAmountWithdrawWithSufficientFundsAndAccountSelectedShouldPass() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertDoesNotThrow(() -> user.deposit(BigDecimal.valueOf(100)));
        assertDoesNotThrow(() -> user.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void getTransactionHistoryWithNoAccountSelectedShouldFail() {
        User user = new User();
        assertThrows(NoAccountSelectedException.class, () -> user.getTransactionHistory());
    }

    @Test
    void getTransactionHistoryWithCurrentAccountSelectedShouldPass() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertDoesNotThrow(() -> user.getTransactionHistory());
    }
}
