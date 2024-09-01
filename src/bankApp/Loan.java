package bankApp;

import java.sql.*;
import java.util.Scanner;

public class Loan {
        private int loanId;
        private int accountId;
        private double loanAmount;
        private double interestRate;
        private int loanTerm;
        private String loanStatus;
        private Date startDate;
        private Date endDate;
        private String loanType;
        private double outstandingBalance;
        private String paymentSchedule;
        private double lateFee;
        private String purpose;


        public Loan(int loanId, int accountId, double loanAmount, double interestRate, int loanTerm,
                    String loanStatus, Date startDate, Date endDate, String loanType,
                    double outstandingBalance, String paymentSchedule, double lateFee, String purpose) {
                this.loanId = loanId;
                this.accountId = accountId;
                this.loanAmount = loanAmount;
                this.interestRate = interestRate;
                this.loanTerm = loanTerm;
                this.loanStatus = loanStatus;
                this.startDate = startDate;
                this.endDate = endDate;
                this.loanType = loanType;
                this.outstandingBalance = outstandingBalance;
                this.paymentSchedule = paymentSchedule;
                this.lateFee = lateFee;
                this.purpose = purpose;
        }

        public Loan(Connection connection, int accountId, int loanTerm, String loanStatus, Date startDate, Date endDate,
                    String loanType, String paymentSchedule, double lateFee, String purpose) throws SQLException {
                this.loanId = 0;
                this.accountId = accountId;
                this.loanTerm = loanTerm;
                this.loanStatus = loanStatus;
                this.startDate = startDate;
                this.endDate = endDate;
                this.loanType = loanType;
                this.paymentSchedule = paymentSchedule;
                this.lateFee = lateFee;
                this.purpose = purpose;
                this.interestRate = fetchInterestRate(loanType);
                this.loanAmount = fetchAccountBalance(connection, accountId);
                this.outstandingBalance = calculateOutstandingBalance();
        }

        private double fetchInterestRate(String loanType) {
                double rate = 0.0;
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankapp", "root", "BENstokes@55");
                     PreparedStatement statement = connection.prepareStatement("SELECT interest_rate FROM Interest WHERE loan_type = ?")) {
                        statement.setString(1, loanType);
                        ResultSet resultSet = statement.executeQuery();
                        if (resultSet.next()) {
                                rate = resultSet.getDouble("interest_rate");
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }
                return rate;
        }
        private double fetchAccountBalance(Connection connection, int accountId) throws SQLException {
                double balance = 0.0;
                String query = "SELECT balance FROM Account WHERE accountID = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setInt(1, accountId);
                        ResultSet resultSet = statement.executeQuery();
                        if (resultSet.next()) {
                                balance = resultSet.getDouble("balance");
                        }
                }
                return balance;
        }

       private double calculateOutstandingBalance() {
                   return loanAmount;
        }

        public void createLoan(Connection connection) throws SQLException {
                String query = "INSERT INTO Loan (accountId, loanAmount, interestRate, loanTerm, loanStatus, startDate, endDate, loanType, outstandingBalance, paymentSchedule, lateFee, purpose) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                        statement.setInt(1, accountId);
                        statement.setDouble(2, loanAmount);
                        statement.setDouble(3, interestRate);
                        statement.setInt(4, loanTerm);
                        statement.setString(5, loanStatus);
                        statement.setDate(6, startDate);
                        statement.setDate(7, endDate);
                        statement.setString(8, loanType);
                        statement.setDouble(9, outstandingBalance);
                        statement.setString(10, paymentSchedule);
                        statement.setDouble(11, lateFee);
                        statement.setString(12, purpose);
                        statement.executeUpdate();

                        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                        this.loanId = generatedKeys.getInt(1);
                                }
                        }
                }
        }

         public static Loan getLoanByID(Connection connection, int loanId) throws SQLException {
                String query = "SELECT * FROM Loan WHERE loanId = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setInt(1, loanId);
                        ResultSet resultSet = statement.executeQuery();
                        if (resultSet.next()) {
                                return new Loan(
                                        resultSet.getInt("loanId"),
                                        resultSet.getInt("accountId"),
                                        resultSet.getDouble("loanAmount"),
                                        resultSet.getDouble("interestRate"),
                                        resultSet.getInt("loanTerm"),
                                        resultSet.getString("loanStatus"),
                                        resultSet.getDate("startDate"),
                                        resultSet.getDate("endDate"),
                                        resultSet.getString("loanType"),
                                        resultSet.getDouble("outstandingBalance"),
                                        resultSet.getString("paymentSchedule"),
                                        resultSet.getDouble("lateFee"),
                                        resultSet.getString("purpose")
                                );
                        }
                }
                return null;
        }

      public void updateLoan(Connection connection, double loanAmount, int loanTerm, String loanStatus,
                               Date startDate, Date endDate, String loanType, String paymentSchedule,
                               double lateFee, String purpose) throws SQLException {
                this.interestRate = fetchInterestRate(loanType);
                this.outstandingBalance = calculateOutstandingBalance();

                String query = "UPDATE Loan SET loanAmount = ?, interestRate = ?, loanTerm = ?, loanStatus = ?, startDate = ?, endDate = ?, loanType = ?, outstandingBalance = ?, paymentSchedule = ?, lateFee = ?, purpose = ? WHERE loanId = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setDouble(1, loanAmount);
                        statement.setDouble(2, interestRate);
                        statement.setInt(3, loanTerm);
                        statement.setString(4, loanStatus);
                        statement.setDate(5, startDate);
                        statement.setDate(6, endDate);
                        statement.setString(7, loanType);
                        statement.setDouble(8, outstandingBalance);
                        statement.setString(9, paymentSchedule);
                        statement.setDouble(10, lateFee);
                        statement.setString(11, purpose);
                        statement.setInt(12, loanId);
                        statement.executeUpdate();
                }
        }

         public static void deleteLoan(Connection connection, int loanId) throws SQLException {
                String query = "DELETE FROM Loan WHERE loanId = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setInt(1, loanId);
                        statement.executeUpdate();
                }
        }

        public void processLoanInstallment(Connection connection, double paymentAmount) throws SQLException {
                if (paymentAmount <= 0) {
                        throw new IllegalArgumentException("Payment amount must be greater than zero.");
                }

                this.outstandingBalance -= paymentAmount;
                if (this.outstandingBalance < 0) {
                        this.outstandingBalance = 0;
                }

                String query = "UPDATE Loan SET outstandingBalance = ? WHERE loanId = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setDouble(1, this.outstandingBalance);
                        statement.setInt(2, this.loanId);
                        statement.executeUpdate();
                }

                createLoanInstallment(connection, paymentAmount);
        }

         public void createLoanInstallment(Connection connection, double paymentAmount) throws SQLException {
                String query = "INSERT INTO LoanInstallment (loanId, paymentAmount, paymentDate, remainingBalance) VALUES (?, ?, ?, ?)";
                Date paymentDate = new Date(System.currentTimeMillis());

                try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setInt(1, this.loanId);
                        statement.setDouble(2, paymentAmount);
                        statement.setDate(3, paymentDate);
                        statement.setDouble(4, this.outstandingBalance);

                        statement.executeUpdate();
                }

               String updateLoanQuery = "UPDATE Loan SET outstandingBalance = ? WHERE loanId = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateLoanQuery)) {
                        updateStatement.setDouble(1, this.outstandingBalance);
                        updateStatement.setInt(2, this.loanId);

                        updateStatement.executeUpdate();
                }

                System.out.println("Loan installment created successfully. Remaining balance: " + this.outstandingBalance);
        }

       public void viewLoanInstallments(Connection connection) throws SQLException {
                String query = "SELECT * FROM LoanInstallment WHERE loanId = ? ORDER BY paymentDate ASC";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setInt(1, this.loanId);
                        ResultSet resultSet = statement.executeQuery();

                        System.out.println("Installments for Loan ID: " + this.loanId);
                        while (resultSet.next()) {
                                int installmentId = resultSet.getInt("installmentId");
                                double paymentAmount = resultSet.getDouble("paymentAmount");
                                Date paymentDate = resultSet.getDate("paymentDate");
                                double remainingBalance = resultSet.getDouble("remainingBalance");

                                System.out.println("Installment ID: " + installmentId);
                                System.out.println("Payment Amount: " + paymentAmount);
                                System.out.println("Payment Date: " + paymentDate);
                                System.out.println("Remaining Balance: " + remainingBalance);
                                System.out.println("---------");
                        }
                }
        }
  public static void manageLoans(Scanner scanner,Connection connection ) throws SQLException {
          System.out.println("Loan Management");
          System.out.println("1. Create Loan");
          System.out.println("2. View Loan");
          System.out.println("3. Update Loan");
          System.out.println("4. Delete Loan");
          System.out.println("5. Make Installment Payment");
          System.out.println("6. View Installments");
          System.out.println("7. EXit");

          int choice = scanner.nextInt();
          int k = 0;
          while (k == 0) {
                  switch (choice) {
                          case 1:
                                  System.out.println("Enter accountId:");
                                  int accountId = scanner.nextInt();
                                  System.out.println("Enter loanTerm (in months):");
                                  int loanTerm = scanner.nextInt();
                                  System.out.println("Enter loanStatus:");
                                  String loanStatus = scanner.next();
                                  System.out.println("Enter startDate (yyyy-mm-dd):");
                                  Date startDate = Date.valueOf(scanner.next());
                                  System.out.println("Enter endDate (yyyy-mm-dd):");
                                  Date endDate = Date.valueOf(scanner.next());
                                  System.out.println("Enter loanType:");
                                  String loanType = scanner.next();
                                  System.out.println("Enter paymentSchedule:");
                                  String paymentSchedule = scanner.next();
                                  System.out.println("Enter lateFee:");
                                  double lateFee = scanner.nextDouble();
                                  System.out.println("Enter purpose:");
                                  String purpose = scanner.next();

                                  Loan newLoan = new Loan(connection, accountId, loanTerm, loanStatus, startDate, endDate, loanType, paymentSchedule, lateFee, purpose);
                                  newLoan.createLoan(connection);
                                  System.out.println("Loan created successfully with ID: " + newLoan.loanId);
                                  break;

                          case 2:
                                  System.out.println("Enter loanId:");
                                  int loanId = scanner.nextInt();
                                  Loan retrievedLoan = Loan.getLoanByID(connection, loanId);
                                  if (retrievedLoan != null) {
                                          System.out.println("Loan details: " + retrievedLoan.toString());
                                  } else {
                                          System.out.println("Loan not found.");
                                  }
                                  break;

                          case 3:
                                  System.out.println("Enter loanId:");
                                  loanId = scanner.nextInt();
                                  Loan loanToUpdate = Loan.getLoanByID(connection, loanId);
                                  if (loanToUpdate != null) {
                                          System.out.println("Enter new loanAmount:");
                                          double loanAmount = scanner.nextDouble();
                                          System.out.println("Enter new loanTerm:");
                                          loanTerm = scanner.nextInt();
                                          System.out.println("Enter new loanStatus:");
                                          loanStatus = scanner.next();
                                          System.out.println("Enter new startDate (yyyy-mm-dd):");
                                          startDate = Date.valueOf(scanner.next());
                                          System.out.println("Enter new endDate (yyyy-mm-dd):");
                                          endDate = Date.valueOf(scanner.next());
                                          System.out.println("Enter new loanType:");
                                          loanType = scanner.next();
                                          System.out.println("Enter new paymentSchedule:");
                                          paymentSchedule = scanner.next();
                                          System.out.println("Enter new lateFee:");
                                          lateFee = scanner.nextDouble();
                                          System.out.println("Enter new purpose:");
                                          purpose = scanner.next();

                                          loanToUpdate.updateLoan(connection, loanAmount, loanTerm, loanStatus, startDate, endDate, loanType, paymentSchedule, lateFee, purpose);
                                          System.out.println("Loan updated successfully.");
                                  } else {
                                          System.out.println("Loan not found.");
                                          break;
                                  }
                                  break;

                          case 4:
                                  System.out.println("Enter loanId:");
                                  loanId = scanner.nextInt();
                                  Loan.deleteLoan(connection, loanId);
                                  System.out.println("Loan deleted successfully.");
                                  break;

                          case 5:
                                  System.out.println("Enter loanId:");
                                  loanId = scanner.nextInt();
                                  Loan loanToPay = Loan.getLoanByID(connection, loanId);
                                  if (loanToPay != null) {
                                          System.out.println("Enter paymentAmount:");
                                          double paymentAmount = scanner.nextDouble();
                                          loanToPay.processLoanInstallment(connection, paymentAmount);
                                  } else {
                                          System.out.println("Loan not found.");
                                  }
                                  break;

                          case 6:
                                  System.out.println("Enter loanId:");
                                  loanId = scanner.nextInt();
                                  Loan loanForInstallments = Loan.getLoanByID(connection, loanId);
                                  if (loanForInstallments != null) {
                                          loanForInstallments.viewLoanInstallments(connection);
                                  } else {
                                          System.out.println("Loan not found.");
                                  }
                                  break;

                          case 7:
                                  k=1;
                                  break;
                          default:
                                  System.out.println("Invalid choice.");
                                  break;
                  }
          }
  }


        @Override

        public String toString() {
        return String.format(
                "%-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s %-15s\n" +
                        "%-15d %-15d %-15.2f %-15.2f %-15d %-15s %-15s %-15s %-15s %-15.2f %-15s %-15.2f %-15s",
                "Loan ID", "Account ID", "Loan Amount", "Interest Rate", "Loan Term", "Loan Status",
                "Start Date", "End Date", "Loan Type", "Outstanding Balance", "Payment Schedule",
                "Late Fee", "Purpose",
                loanId, accountId, loanAmount, interestRate, loanTerm, loanStatus,
                startDate.toString(), endDate.toString(), loanType, outstandingBalance,
                paymentSchedule, lateFee, purpose
        );
}
}
