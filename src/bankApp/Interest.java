package bankApp;

import java.sql.*;
import java.util.Scanner;

public class Interest {
    private int interestId;
    private String loanType;
    private double interestRate;
    private Date effectiveDate;
    private String description;
    private String compoundFrequency;
    private double minimumBalance;

    public Interest(int interestId, String loanType, double interestRate, Date effectiveDate, String description, String compoundFrequency, double minimumBalance) {
        this.interestId = interestId;
        this.loanType = loanType;
        this.interestRate = interestRate;
        this.effectiveDate = effectiveDate;
        this.description = description;
        this.compoundFrequency = compoundFrequency;
        this.minimumBalance = minimumBalance;
    }

    public static void manageInterests(Scanner scanner, Connection connection) throws SQLException {
        boolean t = true;
        while (t) {
            System.out.println("1. Set Interest Rate");
            System.out.println("2. Get Interest Rate");
            System.out.println("3. Update Interest Rate");
            System.out.println("4. Delete Interest Rate");
            System.out.println("5. Exit");
            System.out.print("Please select an option: ");

            int choice = Main.getValidInt(scanner);

            switch (choice) {
                case 1:
                    setInterestRate(scanner, connection);
                    break;
                case 2:
                    getInterestRate(scanner, connection);
                    break;
                case 3:
                    updateInterestRate(scanner, connection);
                    break;
                case 4:
                    deleteInterestRate(scanner, connection);
                    break;
                case 5:
                    t = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void deleteInterestRate(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Interest ID to delete: ");
        int interestId = Main.getValidInt(scanner);

        deleteInterest(connection, interestId);
        System.out.println("Interest rate deleted successfully.");
    }

    private static void updateInterestRate(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Interest ID to update: ");
        int interestId = Main.getValidInt(scanner);

        System.out.print("Enter New Loan Type: ");
        String loanType = scanner.next();
        scanner.nextLine();

        System.out.print("Enter New Interest Rate: ");
        double interestRate = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter New Effective Date (yyyy-mm-dd): ");
        String effectiveDateString = scanner.nextLine();
        Date effectiveDate = Date.valueOf(effectiveDateString);

        System.out.print("Enter New Description: ");
        String description = scanner.nextLine();

        System.out.print("Enter New Compound Frequency (e.g., Monthly, Quarterly): ");
        String compoundFrequency = scanner.nextLine();

        System.out.print("Enter New Minimum Balance: ");
        double minimumBalance = scanner.nextDouble();
        scanner.nextLine();

        Interest interest = new Interest(interestId, loanType, interestRate, effectiveDate, description, compoundFrequency, minimumBalance);
        interest.updateInterest(connection);
        System.out.println("Interest rate updated successfully.");
    }

    private static void setInterestRate(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Loan Type: ");
        String loanType = scanner.next();
        scanner.nextLine();
        System.out.print("Enter Interest Rate: ");
        double interestRate = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter Effective Date (yyyy-mm-dd): ");
        String effectiveDateString = scanner.nextLine();
        Date effectiveDate = Date.valueOf(effectiveDateString);

        System.out.print("Enter Description: ");
        String description = scanner.nextLine();

        System.out.print("Enter Compound Frequency (e.g., Monthly, Quarterly): ");
        String compoundFrequency = scanner.nextLine();

        System.out.print("Enter Minimum Balance: ");
        double minimumBalance = scanner.nextDouble();
        scanner.nextLine();
        Interest interest = new Interest(0, loanType, interestRate, effectiveDate, description, compoundFrequency, minimumBalance);
        interest.createInterest(connection);
        System.out.println("Interest rate set successfully.");
    }
    private static void getInterestRate(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Interest ID to get interest rate: ");
        int interestId = Main.getValidInt(scanner);

        Interest interest = getInterestByID(connection, interestId);
        if (interest != null) {
            printInterestDetails(interest);
        } else {
            System.out.println("Interest not found.");
        }
    }

    public void createInterest(Connection connection) throws SQLException {
        String query = "INSERT INTO Interest (interest_id, loan_type, interest_rate, effective_date, description, compound_frequency, minimum_balance) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, interestId);
            statement.setString(2, loanType);
            statement.setDouble(3, interestRate);
            statement.setDate(4, effectiveDate);
            statement.setString(5, description);
            statement.setString(6, compoundFrequency);
            statement.setDouble(7, minimumBalance);
            statement.executeUpdate();
        }
    }
    public static Interest getInterestByID(Connection connection, int interestId) throws SQLException {
        String query = "SELECT * FROM Interest WHERE interest_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, interestId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String loanType = resultSet.getString("loan_type");
                double interestRate = resultSet.getDouble("interest_rate");
                Date effectiveDate = resultSet.getDate("effective_date");
                String description = resultSet.getString("description");
                String compoundFrequency = resultSet.getString("compound_frequency");
                double minimumBalance = resultSet.getDouble("minimum_balance");
                return new Interest(interestId, loanType, interestRate, effectiveDate, description, compoundFrequency, minimumBalance);
            }
        }
        return null;
    }

    public void updateInterest(Connection connection) throws SQLException {
        String query = "UPDATE Interest SET loan_type = ?, interest_rate = ?, effective_date = ?, description = ?, compound_frequency = ?, minimum_balance = ? WHERE interest_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, loanType);
            statement.setDouble(2, interestRate);
            statement.setDate(3, effectiveDate);
            statement.setString(4, description);
            statement.setString(5, compoundFrequency);
            statement.setDouble(6, minimumBalance);
            statement.setInt(7, interestId);
            statement.executeUpdate();
        }
    }

    public static void deleteInterest(Connection connection, int interestId) throws SQLException {
        String query = "DELETE FROM Interest WHERE interest_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, interestId);
            statement.executeUpdate();
        }
    }

    private static void printInterestDetails(Interest interest) {

        System.out.printf("| Interest ID | Loan Type            | Interest Rate        | Effective Date       | Description          | Compound Frequency   | Minimum Balance      |%n");

        System.out.printf("| %-11d | %-20s | %-20.2f | %-20s | %-20s | %-20s | %-20.2f |%n",
                interest.getInterestId(),
                interest.getLoanType(),
                interest.getInterestRate(),
                interest.getEffectiveDate().toString(),
                interest.getDescription(),
                interest.getCompoundFrequency(),
                interest.getMinimumBalance());
    }


    @Override
    public String toString() {
        return "Interest ID: " + interestId +
                ", Loan Type: " + loanType +
                ", Interest Rate: " + interestRate +
                ", Effective Date: " + effectiveDate +
                ", Description: " + description +
                ", Compound Frequency: " + compoundFrequency +
                ", Minimum Balance: " + minimumBalance;
    }

    public int getInterestId() {
        return interestId;
    }

    public void setInterestId(int interestId) {
        this.interestId = interestId;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompoundFrequency() {
        return compoundFrequency;
    }

    public void setCompoundFrequency(String compoundFrequency) {
        this.compoundFrequency = compoundFrequency;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }
}
