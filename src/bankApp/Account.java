package bankApp;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Account {
    private int accountID;
    private int customerID;
    private String accountType;
    private double balance;
    private Date dateOpened;
    private String accountStatus;

    public Account(int customerID, String accountType, double balance, Date dateOpened, String accountStatus) {
        this.customerID = customerID;
        this.accountType = accountType;
        this.balance = balance;
        this.dateOpened = dateOpened != null ? dateOpened : new Date(System.currentTimeMillis());
        this.accountStatus = accountStatus;
    }

    public Account(int accountID, int customerID, String accountType, double balance, Date dateOpened, String accountStatus) {
        this.accountID = accountID;
        this.customerID = customerID;
        this.accountType = accountType;
        this.balance = balance;
        this.dateOpened = dateOpened != null ? dateOpened : new Date(System.currentTimeMillis());
        this.accountStatus = accountStatus;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Date getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(Date dateOpened) {
        this.dateOpened = dateOpened != null ? dateOpened : new Date(System.currentTimeMillis());
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public void createAccount(Connection connection) throws SQLException {
        String sql = "INSERT INTO bankapp.account (customerID, balance, accountType, accountStatus, dateOpened) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, customerID);
            pstmt.setDouble(2, balance);
            pstmt.setString(3, accountType);
            pstmt.setString(4, accountStatus);
            pstmt.setDate(5, new Date(System.currentTimeMillis()));
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.accountID = generatedKeys.getInt(1);
                }
            }
            System.out.printf("Account created successfully with ID %d.%n", accountID);
        }
    }

    public static Account getAccountByID(Connection connection, int accountID) throws SQLException {
        String sql = "SELECT * FROM bankapp.account WHERE accountID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getInt("accountID"),
                        rs.getInt("customerID"),
                        rs.getString("accountType"),
                        rs.getDouble("balance"),
                        rs.getDate("dateOpened"),
                        rs.getString("accountStatus")
                );
            } else {
                return null;
            }
        }
    }

    public void updateAccount(Connection connection) throws SQLException {
        String sql = "UPDATE bankapp.account SET customerID = ?, accountType = ?, balance = ?, dateOpened = ?, accountStatus = ? WHERE accountID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            pstmt.setString(2, accountType);
            pstmt.setDouble(3, balance);
            pstmt.setDate(4, dateOpened);
            pstmt.setString(5, accountStatus);
            pstmt.setInt(6, accountID);
            pstmt.executeUpdate();
            System.out.printf("Account updated successfully.%n");
        }
    }

    public void displayAccountDetails() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s%n",
                "Account ID", "Customer ID", "Account Type", "Balance", "Date Opened", "Account Status");

        System.out.printf("%-15d %-15d %-15s %-15.2f %-15s %-15s%n",
                accountID, customerID, accountType, balance,
                dateFormat.format(dateOpened), accountStatus);
    }

    public static void deleteAccount(Connection connection, int accountID) throws SQLException {
        String sql = "SET FOREIGN KEY=0 ;" +
                "DELETE FROM bankapp.account WHERE accountID = ?" +
                "SET FOREIGN KEY=1;"
                ;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountID);
            pstmt.executeUpdate();
            System.out.printf("Account deleted successfully.%n");
        }
    }


    public static void updateAccountBalance(Connection connection, int accountID, String transactionType, double amount) throws SQLException {
        String selectSql = "SELECT balance FROM bankapp.account WHERE accountID = ?";
        double currentBalance = 0.0;

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            selectStmt.setInt(1, accountID);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                currentBalance = rs.getDouble("balance");
            } else {
                System.out.printf("Account with ID %d not found.%n", accountID);
                return;
            }
        }

        double newBalance = currentBalance;
        if ("debit".equalsIgnoreCase(transactionType)) {
            if (amount > currentBalance) {
                System.out.printf("Insufficient funds. Cannot debit %.2f from account with ID %d.%n", amount, accountID);
                return;
            }
            newBalance -= amount;
        } else if ("credit".equalsIgnoreCase(transactionType)) {
            newBalance += amount;
        } else {
            System.out.printf("Invalid transaction type. Please use 'debit' or 'credit'.%n");
            return;
        }

        String updateSql = "UPDATE bankapp.account SET balance = ? WHERE accountID = ?";

        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
            updateStmt.setDouble(1, newBalance);
            updateStmt.setInt(2, accountID);
            updateStmt.executeUpdate();
            System.out.printf("Account balance updated successfully. New balance: %.2f%n", newBalance);
        }
    }

    public static void manageAccounts(Scanner scanner, Connection connection) throws SQLException {
        boolean k=true;
        while(k){
        System.out.printf("1. Create Account%n");
        System.out.printf("2. Retrieve Account%n");
        System.out.printf("3. Update Account%n");
        System.out.printf("4. Delete Account%n");
        System.out.printf("5. Display Account Details%n");
        System.out.printf("6. Exit");
            System.out.println();
        System.out.println("Please select an option: ");

        int choice = getValidInt(scanner);

        switch (choice) {
            case 1:
                createAccount(scanner, connection);
                break;
            case 2:
                retrieveAccount(scanner, connection);
                break;
            case 3:
                updateAccount(scanner, connection);
                break;
            case 4:
                deleteAccount(scanner, connection);
                break;
            case 5:
                displayAccountDetails(scanner, connection);
                break;
            case 6:
                k=false;
                break;
            default:
                System.out.printf("Invalid choice. Please try again.%n");
                manageAccounts(scanner, connection);
        }}
    }

    private static void createAccount(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Customer ID: ");
        int customerID = getValidInt(scanner);

        System.out.printf("Enter Account Type: ");
        String accountType = scanner.next();

        System.out.printf("Enter Initial Balance: ");
        double balance = getValidDouble(scanner);

        Date dateOpened = new Date(System.currentTimeMillis());

        System.out.printf("Enter Account Status: ");
        String accountStatus = scanner.next();

        Account newAccount = new Account(customerID, accountType, balance, dateOpened, accountStatus);
        newAccount.createAccount(connection);
    }

    private static void retrieveAccount(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Account ID: ");
        int accountID = getValidInt(scanner);

        Account account = Account.getAccountByID(connection, accountID);
        if (account != null) {
            account.displayAccountDetails();
        } else {
            System.out.printf("Account with ID %d not found.%n", accountID);
        }
    }

    private static void updateAccount(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Account ID: ");
        int accountID = getValidInt(scanner);

        Account account = Account.getAccountByID(connection, accountID);
        if (account == null) {
            System.out.printf("Account with ID %d not found.%n", accountID);
            return;
        }

        System.out.printf("Enter new Customer ID: ");
        int newCustomerID = getValidInt(scanner);

        System.out.printf("Enter new Account Type: ");
        String newAccountType = scanner.next();

        System.out.printf("Enter new Balance: ");
        double newBalance = getValidDouble(scanner);

        Date newDateOpened = new Date(System.currentTimeMillis());

        System.out.printf("Enter new Account Status: ");
        String newAccountStatus = scanner.next();

        account.setCustomerID(newCustomerID);
        account.setAccountType(newAccountType);
        account.setBalance(newBalance);
        account.setDateOpened(newDateOpened);
        account.setAccountStatus(newAccountStatus);

        account.updateAccount(connection);
    }

    private static void deleteAccount(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Account ID: ");
        int accountID = getValidInt(scanner);

        Account.deleteAccount(connection, accountID);
    }

    private static void displayAccountDetails(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Account ID: ");
        int accountID = getValidInt(scanner);

        Account account = Account.getAccountByID(connection, accountID);
        if (account != null) {
            account.displayAccountDetails();
        } else {
            System.out.printf("Account with ID %d not found.%n", accountID);
        }
    }

    private static int getValidInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.printf("Invalid input. Please enter a valid integer: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static double getValidDouble(Scanner scanner) {
        while (!scanner.hasNextDouble()) {
            System.out.printf("Invalid input. Please enter a valid number: ");
            scanner.next();
        }
        return scanner.nextDouble();
    }
}
