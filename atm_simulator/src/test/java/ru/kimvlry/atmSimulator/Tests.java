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
    void NonPositiveAmountToDepositShouldFail() {
        BankAccount bankAccount = new BankAccount("777");
        assertThrows(InvalidAmountException.class, () -> bankAccount.deposit(BigDecimal.valueOf(-100)));
    }

    @Test
    void NonPositiveAmountToWithdrawShouldFail() {
        BankAccount bankAccount = new BankAccount("777");
        assertDoesNotThrow(() -> bankAccount.deposit(BigDecimal.valueOf(101)));
        assertThrows(InvalidAmountException.class, () -> bankAccount.withdraw(BigDecimal.valueOf(-100)));
    }

    @Test
    void PositiveAmountToWithdrawShouldPassWithSufficientFunds() {
        BankAccount bankAccount = new BankAccount("777");
        assertDoesNotThrow(() -> bankAccount.deposit(BigDecimal.valueOf(101)));
        assertDoesNotThrow(() -> bankAccount.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void PositiveAmountToWithdrawShouldFailWithInsufficientFunds() {
        BankAccount bankAccount = new BankAccount("777");
        assertDoesNotThrow(() -> bankAccount.deposit(BigDecimal.valueOf(101)));
        assertThrows(InsufficientFundsException.class, () -> bankAccount.withdraw(BigDecimal.valueOf(102)));
    }
}


class UserTest {
    @Test
    void SwitchingToInvalidAccountIDShouldFail() {
        User user = new User();
        String first_account = user.createAccount();
        String invalidID = first_account + "invalid";
        assertThrows(AccountNotFoundException.class, () -> user.switchAccount(invalidID));
    }

    @Test
    void SwitchingToValidAccountIDShouldPass() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
    }

    @Test
    void GetBalanceOperationWithNoAccountSelectedShouldFail() {
        User user = new User();
        assertThrows(NoAccountSelectedException.class, () -> user.getBalance());
    }

    @Test
    void GetBalanceOperationWithCurrentAccountSelectedShouldPass() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertDoesNotThrow(() -> user.getBalance());
    }

    @Test
    void DepositWithNoAccountSelectedShouldFail() {
        User user = new User();
        assertThrows(NoAccountSelectedException.class, () -> user.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void NegativeDepositWithCurrentAccountSelectedShouldFail() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertThrows(InvalidAmountException.class, () -> user.deposit(BigDecimal.valueOf(-100)));
    }

    @Test
    void NonNegativeDepositWithCurrentAccountSelectedShouldPass() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertDoesNotThrow(() -> user.deposit(BigDecimal.valueOf(100)));
    }

    @Test
    void WithdrawWithNoAccountSelectedShouldFail() {
        User user = new User();
        String first_account = user.createAccount();
        assertThrows(NoAccountSelectedException.class, () -> user.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void NegativeAmountWithdrawShouldFail() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertThrows(InvalidAmountException.class, () -> user.withdraw(BigDecimal.valueOf(-100)));
    }

    @Test
    void WithdrawWithInsufficientFundsShouldFail() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertDoesNotThrow(() -> user.deposit(BigDecimal.valueOf(100)));
        assertThrows(InsufficientFundsException.class, () -> user.withdraw(BigDecimal.valueOf(101)));
    }

    @Test
    void NonNegativeAmountWithdrawWithSufficientFundsAndAccountSelectedShouldPass() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertDoesNotThrow(() -> user.deposit(BigDecimal.valueOf(100)));
        assertDoesNotThrow(() -> user.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void GetTransactionHistoryWithNoAccountSelectedShouldFail() {
        User user = new User();
        assertThrows(NoAccountSelectedException.class, () -> user.getTransactionHistory());
    }

    @Test
    void GetTransactionHistoryWithCurrentAccountSelectedShouldPass() {
        User user = new User();
        String first_account = user.createAccount();
        assertDoesNotThrow(() -> user.switchAccount(first_account));
        assertDoesNotThrow(() -> user.getTransactionHistory());
    }
}
