package bankApp;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankapp";
    private static final String USER = "root";
    private static final String PASSWORD = "BENstokes@55";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            System.out.println("Welcome to Bank application");
            while(true) {
            System.out.println("Press 1 for Bank Login ");
            System.out.println("Press 2 for customer login ");
            int k=scanner.nextInt();

                switch (k) {
                    case 1:
                        bank(scanner, connection);
                        break;
                    case 2:
                        Customerlogin(scanner, connection);
                        break;
                    default:
                        System.out.println("Invalid option");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void Customerlogin(Scanner scanner, Connection connection) throws SQLException {
        boolean customerAuthenticated = false;
        customerAuthenticated = Customer.retrieveCustomer(scanner, connection, customerAuthenticated);

        if (customerAuthenticated) {
            return;
        }
        boolean continueSession = true;
        while (continueSession) {
            System.out.println("What would you like to do..!");
            System.out.println("1. Show balance");
            System.out.println("2. Withdraw amount");
            System.out.println("3. Deposit amount");
            System.out.println("4. Pay loan amount");
            System.out.println("5. View transaction history");
            System.out.println("6. Update personal information");
            System.out.println("7. Request new card");
            //System.out.println("8. Close account");
            System.out.println("8. Exit");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:

                    System.out.println("Enter account ID to check balance:");
                    int accountId = scanner.nextInt();
                    Account account = Account.getAccountByID(connection, accountId);
                    if (account != null) {
                        System.out.println("Current balance: " + account.getBalance());
                    } else {
                        System.out.println("Account not found.");
                    }
                    break;

                case 2:
                    System.out.println("Enter account ID to withdraw from:");
                    accountId = scanner.nextInt();
                    System.out.println("Enter amount to withdraw:");
                    double withdrawAmount = scanner.nextDouble();
                    account = Account.getAccountByID(connection, accountId);
                    if (account != null) {
                        if (account.getBalance() >= withdrawAmount) {
                            Account.updateAccountBalance(connection, accountId, "debit", withdrawAmount);
                            System.out.println("Withdrawal successful. New balance: " + account.getBalance());
                        } else {
                            System.out.println("Insufficient balance.");
                        }
                    } else {
                        System.out.println("Account not found.");
                    }
                    break;

                case 3:
                    System.out.println("Enter account ID to deposit to:");
                    accountId = scanner.nextInt();
                    System.out.println("Enter amount to deposit:");
                    double depositAmount = scanner.nextDouble();
                    account = Account.getAccountByID(connection, accountId);
                    if (account != null) {
                        Account.updateAccountBalance(connection, accountId, "credit", depositAmount);
                        System.out.println("Deposit successful. New balance: " + account.getBalance());
                    } else {
                        System.out.println("Account not found.");
                    }
                    break;

                case 4:
                    System.out.println("Enter loan ID to make a payment:");
                    int loanId = scanner.nextInt();
                    System.out.println("Enter amount to pay:");
                    double paymentAmount = scanner.nextDouble();
                    Loan loan = Loan.getLoanByID(connection, loanId);
                    if (loan != null) {
                        loan.processLoanInstallment(connection, paymentAmount);
                    } else {
                        System.out.println("Loan not found.");
                    }
                    break;

                case 5:
                    System.out.println("Enter account ID to view transaction history:");
                    accountId = scanner.nextInt();
                    Transaction.displayTransactionHistory(connection, accountId);
                    break;

                case 6:
                    System.out.println("Enter your customer ID:");
                    int customerId = scanner.nextInt();
                    System.out.println("Enter new address:");
                    String newAddress = scanner.next();
                    System.out.println("Enter new contact number:");
                    String newContact = scanner.next();
                    Customer.updateCustomerInformation(connection, customerId, newAddress, Long.parseLong(newContact));
                    System.out.println("Personal information updated successfully.");
                    break;

                case 7:
                    System.out.println("Enter your customer ID:");
                    customerId = scanner.nextInt();
                    System.out.println("Enter card type (debit/credit):");
                    String cardType = scanner.next();
                    Card.requestNewCard(connection, customerId, cardType);
                    System.out.println("New card requested successfully.");
                    break;

                case 8:
                    continueSession = false;
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid option. Please choose a valid operation.");
                    break;
            }
        }
    }

    private static void bank(Scanner scanner,Connection connection) throws SQLException {
        System.out.println("Enter your username ");
        String username=scanner.next();
        System.out.println("Enter your password :");
        String password =scanner.next();

        if(User.validateUser(username,password,connection)){
        while (true) {

            printMenu();
            int choice = getValidInt(scanner);

            switch (choice) {
                case 1:
                    Customer.manageCustomers(scanner, connection);
                    break;
                case 2:
                    Transaction.manageTransactions(scanner, connection);
                    break;
                case 3:
                    Region.manageRegions(scanner, connection);
                    break;
                case 4:
                    Branch.manageBranches(scanner, connection);
                    break;
                case 5:
                    Loan.manageLoans(scanner, connection);
                    break;
                case 6:
                    Interest.manageInterests(scanner, connection);
                    break;
                case 7:
                    Employee.manageEmployees(scanner, connection);
                    break;
                case 8:
                    ATM.manageATMs(scanner,connection);
                    break;
                case 9:
                    Card.manageCards(scanner,connection);
                    break;
                case 10:
                    Account.manageAccounts(scanner,connection);
                    break;

                case 11:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }}else{
            System.out.println("!!..Enter correct username and password ");
        }
    }

    private static void printMenu() {
        System.out.println("*******************************");
        System.out.println("Bank Application Menu");
        System.out.println("1. Manage Customers");
        System.out.println("2. Manage Transactions");
        System.out.println("3. Manage Regions");
        System.out.println("4. Manage Branch");
        System.out.println("5. Manage Loans");
        System.out.println("6. Manage Interest Rates");
        System.out.println("7. Manage Employees");
        System.out.println("8. Manage ATM");
        System.out.println("9. Manage Card");
        System.out.println("10.Manage Account");
        System.out.println("11.Exit");
        System.out.print("Please select an option: ");
        System.out.println();
    }

    public static int getValidInt(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a number: ");
                scanner.next();
            }
        }
    }
}
