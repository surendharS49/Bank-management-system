package bankApp;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Transaction {
    private int transactionId;
    private int accountId;
    private double amount;
    private String transactionType;
    private Date date;
    private String transactionDescription;
    private Integer loanId;
    private Integer receiverAccountId;

    public Transaction(int accountId, double amount, String transactionType, Date date, String transactionDescription, Integer loanId, Integer receiverAccountId) {
        this.accountId = accountId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.date = date;
        this.transactionDescription = transactionDescription;
        this.loanId = loanId;
        this.receiverAccountId = receiverAccountId;
    }

    public Transaction(int transactionId, int accountId, double amount, String transactionType, Date date, String transactionDescription, Integer loanId, Integer receiverAccountId) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.date = date;
        this.transactionDescription = transactionDescription;
        this.loanId = loanId;
        this.receiverAccountId = receiverAccountId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public Integer getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(Integer receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public void createTransaction(Connection connection) throws SQLException {
        if (!doesAccountExist(connection, accountId)) {
            throw new SQLException("Account ID " + accountId + " does not exist. Cannot create transaction.");
        }

        String query = "INSERT INTO transactions (accountID, transactionType, amount, transactionDate, transactionDescription, loanID, ReceiverAccountID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, accountId);
            statement.setString(2, transactionType);
            statement.setDouble(3, amount);
            statement.setDate(4, date);
            statement.setString(5, transactionDescription);
            if (loanId != null) {
                statement.setInt(6, loanId);
            } else {
                statement.setNull(6, Types.INTEGER);
            }
            if (receiverAccountId != null) {
                statement.setInt(7, receiverAccountId);
            } else {
                statement.setNull(7, Types.INTEGER);
            }
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.transactionId = generatedKeys.getInt(1);
                }
            }

            updateAccountBalance(connection);
        }
    }

    private boolean doesAccountExist(Connection connection, int accountId) throws SQLException {
        String query = "SELECT COUNT(*) FROM account WHERE accountID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, accountId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    void updateAccountBalance(Connection connection) throws SQLException {
        Account account = Account.getAccountByID(connection, accountId);
        if (account != null) {
            double currentBalance = account.getBalance();
            double newBalance = currentBalance;

            if ("credit".equalsIgnoreCase(transactionType)) {
                newBalance += amount;
            } else if ("debit".equalsIgnoreCase(transactionType)) {
                newBalance -= amount;
            } else if ("transfer".equalsIgnoreCase(transactionType)) {
                newBalance -= amount;
                if (receiverAccountId != null) {
                    Account receiverAccount = Account.getAccountByID(connection, receiverAccountId);
                    if (receiverAccount != null) {
                        receiverAccount.setBalance(receiverAccount.getBalance() + amount);
                        receiverAccount.updateAccount(connection);
                    } else {
                        throw new SQLException("Receiver account not found for ID: " + receiverAccountId);
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
            }

            account.setBalance(newBalance);
            account.updateAccount(connection);
        } else {
            throw new SQLException("Account not found for ID: " + accountId);
        }
    }

    public static List<Transaction> getTransactionsByAccountID(Connection connection, int accountId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE accountID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, accountId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int transactionId = resultSet.getInt("transactionID");
                double amount = resultSet.getDouble("amount");
                String transactionType = resultSet.getString("transactionType");
                Date date = resultSet.getDate("transactionDate");
                String transactionDescription = resultSet.getString("transactionDescription");
                Integer loanId = resultSet.getObject("loanID", Integer.class);
                Integer receiverAccountId = resultSet.getObject("ReceiverAccountID", Integer.class);
                transactions.add(new Transaction(transactionId, accountId, amount, transactionType, date, transactionDescription, loanId, receiverAccountId));
            }
        }
        return transactions;
    }

    public void updateTransaction(Connection connection, double amount, String transactionType, Date date, String transactionDescription, Integer loanId, Integer receiverAccountId) throws SQLException {
        String query = "UPDATE transactions SET accountID = ?, amount = ?, transactionType = ?, transactionDate = ?, transactionDescription = ?, loanID = ?, ReceiverAccountID = ? WHERE transactionID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, accountId);
            statement.setDouble(2, amount);
            statement.setString(3, transactionType);
            statement.setDate(4, date);
            statement.setString(5, transactionDescription);
            if (loanId != null) {
                statement.setInt(6, loanId);
            } else {
                statement.setNull(6, Types.INTEGER);
            }
            if (receiverAccountId != null) {
                statement.setInt(7, receiverAccountId);
            } else {
                statement.setNull(7, Types.INTEGER);
            }
            statement.setInt(8, transactionId);
            statement.executeUpdate();

            updateAccountBalance(connection);
        }
    }

    public static void deleteTransaction(Connection connection, int transactionId) throws SQLException {
        String query = "DELETE FROM transactions WHERE transactionID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, transactionId);
            statement.executeUpdate();
        }
    }

    static void manageTransactions(Scanner scanner, Connection connection) throws SQLException {
        boolean k = true;
        while (k) {
            System.out.println("1. New Transaction");
            System.out.println("2. Show Transaction");
            System.out.println("3. Delete Transaction");
            System.out.println("4. Exit");
            System.out.print("Please select an option: ");

            int choice = Main.getValidInt(scanner);

            switch (choice) {
                case 1:
                    createTransaction(scanner, connection);
                    break;
                case 2:
                    retrieveTransaction(scanner, connection);
                    break;
                case 3:
                    deleteTransaction(scanner, connection);
                    break;
                case 4:
                    k = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    public static double getValidDouble(Scanner scanner) {
        double value;
        while (true) {
            try {
                value = Double.parseDouble(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return value;
    }
    private static void createTransaction(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Account ID: ");
        int accountId = Main.getValidInt(scanner);
        System.out.print("Enter Amount: ");
        double amount = getValidDouble(scanner);
        System.out.print("Enter Transaction Type (credit/debit/transfer): ");
        String transactionType = scanner.next();
        scanner.nextLine();
        System.out.print("Enter Transaction Date (YYYY-MM-DD): ");
        String dateString = scanner.nextLine();
        Date date = Date.valueOf(LocalDate.parse(dateString));
        System.out.print("Enter Transaction Description: ");
        String transactionDescription = scanner.nextLine();
        System.out.print("Enter Loan ID (if applicable, otherwise enter 0): ");
        int loanIdInput = Main.getValidInt(scanner);
        Integer loanId = loanIdInput != 0 ? loanIdInput : null;
        System.out.print("Enter Receiver Account ID (if applicable, otherwise enter 0): ");
        int receiverAccountIdInput = Main.getValidInt(scanner);
        Integer receiverAccountId = receiverAccountIdInput != 0 ? receiverAccountIdInput : null;

        Transaction transaction = new Transaction(accountId, amount, transactionType, date, transactionDescription, loanId, receiverAccountId);
        transaction.createTransaction(connection);
        System.out.println("Transaction created successfully with Transaction ID: " + transaction.getTransactionId());
    }
    public static void displayTransactionHistory(Connection connection, int accountId) throws SQLException {
        String query = "SELECT * FROM Transactions WHERE accountId = ? ORDER BY transactionDate DESC";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, accountId);
            ResultSet resultSet = statement.executeQuery();

            System.out.printf("%-15s %-15s %-10s %-15s %-30s\n", "Transaction ID", "Amount", "Type", "Date", "Description");
            System.out.println("----------------------------------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                int transactionId = resultSet.getInt("transactionId");
                double amount = resultSet.getDouble("amount");
                String transactionType = resultSet.getString("transactionType");
                Date transactionDate = resultSet.getDate("transactionDate");
                String description = resultSet.getString("transactionDescription");


                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = dateFormat.format(transactionDate);

                System.out.printf("%-15d %-15.2f %-10s %-15s %-30s\n",
                        transactionId, amount, transactionType, formattedDate, description);
            }
        }}

    private static void retrieveTransaction(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Account ID to retrieve transactions: ");
        int accountId = Main.getValidInt(scanner);
        List<Transaction> transactions = Transaction.getTransactionsByAccountID(connection, accountId);

        if (!transactions.isEmpty()) {
            System.out.printf("%-15s %-15s %-15s %-15s %-15s %-35s %-25s %-15s\n",
                    "Transaction ID", "Account ID", "Amount", "Type", "Date", "Description", "Loan ID", "Receiver ID");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println();
            for (Transaction transaction : transactions) {
                System.out.printf("%-15d %-15d %-15.2f %-15s %-15s %-35s %-25s %-15s\n",
                        transaction.getTransactionId(),
                        transaction.getAccountId(),
                        transaction.getAmount(),
                        transaction.getTransactionType(),
                        transaction.getDate(),
                        transaction.getTransactionDescription(),
                        transaction.getLoanId() != null ? transaction.getLoanId().toString() : "N/A",
                        transaction.getReceiverAccountId() != null ? transaction.getReceiverAccountId().toString() : "N/A");
            }
        } else {
            System.out.println("No transactions found for Account ID: " + accountId);
        }
    }

    private static void deleteTransaction(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Transaction ID to delete: ");
        int transactionId = Main.getValidInt(scanner);
        Transaction.deleteTransaction(connection, transactionId);
        System.out.println("Transaction deleted successfully.");
    }
}
