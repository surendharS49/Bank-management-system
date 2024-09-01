package bankApp;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class ATM {
    private int atmId;
    private Region region;
    private double balance;
    private String atmType;
    private String status;
    private Date lastServicedDate;
    private int capacity;


    public ATM(Region region, double balance, String atmType, String status, Date lastServicedDate, int capacity) {
        this.region = region;
        this.balance = balance;
        this.atmType = atmType;
        this.status = status;
        this.lastServicedDate = lastServicedDate;
        this.capacity = capacity;
    }

    public ATM(int atmId, Region region, double balance, String atmType, String status, Date lastServicedDate, int capacity) {
        this.atmId = atmId;
        this.region = region;
        this.balance = balance;
        this.atmType = atmType;
        this.status = status;
        this.lastServicedDate = lastServicedDate;
        this.capacity = capacity;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setAtmType(String atmType) {
        this.atmType = atmType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLastServicedDate(Date lastServicedDate) {
        this.lastServicedDate = lastServicedDate;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void createATM(Connection connection) throws SQLException {
        String sql = "INSERT INTO ATM (regionId, balance, atmType, status, lastServicedDate, capacity) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, region.getRegionId());
            pstmt.setDouble(2, balance);
            pstmt.setString(3, atmType);
            pstmt.setString(4, status);
            pstmt.setDate(5, lastServicedDate);
            pstmt.setInt(6, capacity);
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.atmId = generatedKeys.getInt(1);
                }
            }
            System.out.printf("ATM created successfully with ID %d.%n", atmId);
        }
    }
    public static ATM getATMByID(Connection connection, int atmId) throws SQLException {
        String sql = "SELECT * FROM ATM WHERE atmId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, atmId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Region region = Region.getRegionByID(connection, rs.getInt("regionId"));
                return new ATM(
                        rs.getInt("atmId"),
                        region,
                        rs.getDouble("balance"),
                        rs.getString("atmType"),
                        rs.getString("status"),
                        rs.getDate("lastServicedDate"),
                        rs.getInt("capacity")
                );
            } else {
                System.out.printf("ATM not found.%n");
                return null;
            }
        }
    }

    public void updateATM(Connection connection) throws SQLException {
        String sql = "UPDATE ATM SET regionId = ?, balance = ?, atmType = ?, status = ?, lastServicedDate = ?, capacity = ? WHERE atmId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, region.getRegionId());
            pstmt.setDouble(2, balance);
            pstmt.setString(3, atmType);
            pstmt.setString(4, status);
            pstmt.setDate(5, lastServicedDate);
            pstmt.setInt(6, capacity);
            pstmt.setInt(7, atmId);
            pstmt.executeUpdate();
            System.out.printf("ATM updated successfully.%n");
        }
    }

    public static void deleteATM(Connection connection, int atmId) throws SQLException {
        String sql = "DELETE FROM ATM WHERE atmId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, atmId);
            pstmt.executeUpdate();
            System.out.printf("ATM deleted successfully.%n");
        }
    }

    public boolean withdraw(Connection connection, int accountId, double amount) throws SQLException {
        if (amount <= 0) {
            System.out.printf("Invalid withdrawal amount.%n");
            return false;
        }

        if (amount > this.balance) {
            System.out.printf("ATM does not have sufficient balance for this withdrawal.%n");
            return false;
        }

        Account account = Account.getAccountByID(connection, accountId);
        if (account == null || account.getBalance() < amount) {
            System.out.printf("Account does not have sufficient funds.%n");
            return false;
        }

        account.setBalance(account.getBalance() - amount);
        account.updateAccount(connection);
        this.balance -= amount;
        this.updateATM(connection);
        System.out.printf("Withdrawal successful. New account balance: %.2f%n", account.getBalance());
        return true;
    }

    public boolean deposit(Connection connection, int accountId, double amount) throws SQLException {
        if (amount <= 0) {
            System.out.printf("Invalid deposit amount.%n");
            return false;
        }

        Account account = Account.getAccountByID(connection, accountId);
        if (account == null) {
            System.out.printf("Account not found.%n");
            return false;
        }

        account.setBalance(account.getBalance() + amount);
        account.updateAccount(connection);
        System.out.printf("Deposit successful. New account balance: %.2f%n", account.getBalance());
        return true;
    }
    public void displayATMDetails() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.printf("ATM ID   Region          Balance      Type     Last Serviced Date   Capacity%n");

        System.out.printf("%-8d %-14s %-12.2f %-8s %-19s %-8d%n",
                atmId,
                region.getRegionName(),
                balance,
                atmType,
                dateFormat.format(lastServicedDate),
                capacity);
    }


    public static void manageATMs(Scanner scanner, Connection connection) throws SQLException {
        boolean t = true;
        while (t) {
            System.out.printf("1. Create ATM%n");
            System.out.printf("2. Retrieve ATM%n");
            System.out.printf("3. Update ATM%n");
            System.out.printf("4. Delete ATM%n");
            System.out.printf("5. Withdraw from ATM%n");
            System.out.printf("6. Deposit into ATM%n");
            System.out.printf("7. Display ATM Details%n");
            System.out.println("8. Exit");
            System.out.printf("Please select an option: ");

            int choice = getValidInt(scanner);

            switch (choice) {
                case 1:
                    createATM(scanner, connection);
                    break;
                case 2:
                    retrieveATM(scanner, connection);
                    break;
                case 3:
                    updateATM(scanner, connection);
                    break;
                case 4:
                    deleteATM(scanner, connection);
                    break;
                case 5:
                    withdrawFromATM(scanner, connection);
                    break;
                case 6:
                    depositToATM(scanner, connection);
                    break;
                case 7:
                    displayATMDetails(scanner, connection);
                    break;
                case 8:
                    t = false;
                    break;
                default:
                    System.out.printf("Invalid choice.%n");
            }
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

    private static void createATM(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter region ID: ");
        int regionId = getValidInt(scanner);
        Region region = Region.getRegionByID(connection, regionId);

        System.out.printf("Enter balance: ");
        double balance = getValidDouble(scanner);

        System.out.printf("Enter ATM type: ");
        String atmType = scanner.next();

        System.out.printf("Enter status: ");
        String status = scanner.next();

        System.out.printf("Enter last serviced date (YYYY-MM-DD): ");
        Date lastServicedDate = Date.valueOf(scanner.next());

        System.out.printf("Enter capacity: ");
        int capacity = getValidInt(scanner);

        ATM newATM = new ATM(region, balance, atmType, status, lastServicedDate, capacity);
        newATM.createATM(connection);
    }

    private static void retrieveATM(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter ATM ID: ");
        int atmId = getValidInt(scanner);
        ATM atm = getATMByID(connection, atmId);
        if (atm != null) {
            atm.displayATMDetails();
        }
    }

    private static void updateATM(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter ATM ID: ");
        int atmId = getValidInt(scanner);
        ATM atm = getATMByID(connection, atmId);

        if (atm != null) {
            System.out.printf("Enter new region ID: ");
            int regionId = getValidInt(scanner);
            Region region = Region.getRegionByID(connection, regionId);

            System.out.printf("Enter new balance: ");
            double balance = getValidDouble(scanner);

            System.out.printf("Enter new ATM type: ");
            String atmType = scanner.next();

            System.out.printf("Enter new status: ");
            String status = scanner.next();

            System.out.printf("Enter new last serviced date (YYYY-MM-DD): ");
            Date lastServicedDate = Date.valueOf(scanner.next());

            System.out.printf("Enter new capacity: ");
            int capacity = getValidInt(scanner);

            atm.setRegion(region);
            atm.setBalance(balance);
            atm.setAtmType(atmType);
            atm.setStatus(status);
            atm.setLastServicedDate(lastServicedDate);
            atm.setCapacity(capacity);

            atm.updateATM(connection);
        }
    }

    private static void deleteATM(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter ATM ID: ");
        int atmId = getValidInt(scanner);
        deleteATM(connection, atmId);
    }

    private static void withdrawFromATM(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter ATM ID: ");
        int atmId = getValidInt(scanner);
        ATM atm = getATMByID(connection, atmId);

        if (atm != null) {
            System.out.printf("Enter account ID: ");
            int accountId = getValidInt(scanner);

            System.out.printf("Enter amount to withdraw: ");
            double amount = getValidDouble(scanner);

            atm.withdraw(connection, accountId, amount);
        }
    }

    private static void depositToATM(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter ATM ID: ");
        int atmId = getValidInt(scanner);
        ATM atm = getATMByID(connection, atmId);

        if (atm != null) {
            System.out.printf("Enter account ID: ");
            int accountId = getValidInt(scanner);

            System.out.printf("Enter amount to deposit: ");
            double amount = getValidDouble(scanner);

            atm.deposit(connection, accountId, amount);
        }
    }

    private static void displayATMDetails(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter ATM ID: ");
        int atmId = getValidInt(scanner);
        ATM atm = getATMByID(connection, atmId);

        if (atm != null) {
            atm.displayATMDetails();
        }
    }
}
