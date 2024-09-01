package bankApp;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Customer {
    private static int authenticatedCustomerId;
    private int customerId;
    private String name;
    private String address;
    private long contactNumber;
    private String email;
    private Date dateOfBirth;
    private String occupation;
    private String accountStatus;
    private String aadhaarNumber;
    String gender;

    public Customer(String name, String address, long contactNumber, String email, Date dateOfBirth,
                    String occupation, String accountStatus, String aadhaarNumber) {
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.occupation = occupation;
        this.accountStatus = accountStatus;
        this.aadhaarNumber = aadhaarNumber;
    }
    public static void manageCustomers(Scanner scanner, Connection connection) throws SQLException {
        boolean t=true;

        while(t) {
            System.out.println();
            System.out.println("1. Create Customer");
            System.out.println("2. Retrieve Customer");
            System.out.println("3. Update Customer");
            System.out.println("4. Delete Customer");
            System.out.println("5. Exit");
            System.out.print("Please select an option: ");

            int choice = Main.getValidInt(scanner);

            switch (choice) {

                case 1:
                    createCustomer(scanner, connection);
                    break;
                case 2:
                    retrieveCustomer(scanner, connection);
                    break;
                case 3:
                    updateCustomer(scanner, connection);
                    break;
                case 4:
                    deleteCustomer(scanner, connection);
                    break;
                case 5:
                    t=false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }

        }
    }
    private static void createCustomer(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Enter Name: ");
        String name = scanner.next();
        System.out.println("Enter Address: ");
        String address = scanner.next();
        System.out.println("Enter Contact Number: ");
        long contactNumber = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Enter Email: ");
        String email = scanner.next();
        System.out.println("Enter Date of Birth (yyyy-mm-dd): ");
        String dob = scanner.next();
        System.out.println("Enter Occupation: ");
        String occupation = scanner.next();
        System.out.println("Enter Account Status: ");
        String accountStatus = scanner.next();
        System.out.println("Enter Aadhaar Number: ");
        String aadhaarNumber = scanner.next();
        System.out.println("Enter gender");
        String gen=scanner.next();
        System.out.print("Enter Initial Balance: ");
        double initialBalance = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Account Type: ");
        String accountType = scanner.nextLine();

        Customer customer = new Customer(name, address, contactNumber, email, Date.valueOf(dob), occupation, accountStatus, aadhaarNumber);
        customer.createCustomerWithAccount(connection, initialBalance, accountType);
        System.out.println("Customer created successfully.");

        retrieveCustomer(scanner, connection, customer.getCustomerId());
    }

    private static void retrieveCustomer(Scanner scanner, Connection connection, int customerId) throws SQLException {
        Customer customer = Customer.getCustomerByID(connection, customerId);

        if (customer != null ) {
            System.out.printf("| Customer ID | Name                 | Address                           | Contact Number      | Email                | Date of Birth        |%n");
            System.out.printf("| %-11d   %-20s   %-20s   %-18d   %-20s   %-20s  %n",
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getAddress(),
                    customer.getContactNumber(),
                    customer.getEmail(),
                    customer.getDateOfBirth());
            System.out.println();
            System.out.printf("| Occupation     | Account Status         | Aadhaar Number        |%n");
            System.out.printf("| %-20s  %-20s  %-20s       %n",
                    customer.getOccupation(),
                    customer.getAccountStatus(),
                    customer.getAadhaarNumber());
            System.out.println();
            System.out.println("Account Details");

            List<Account> accounts = customer.getAccounts(connection);
            if (!accounts.isEmpty()) {
                System.out.printf("| Account ID  | Balance              | Account Type         | Account Status       |    %n");
                for (Account account : accounts) {
                    System.out.printf("| %-11d | %-20.2f | %-20s | %-20s |                    %n",
                            account.getAccountID(),
                            account.getBalance(),
                            account.getAccountType(),
                            account.getAccountStatus());
                }
            } else {
                System.out.println("No accounts found for this customer.");
            }
        } else {
            System.out.println("Customer not found.");
        }
    }

    public static void updateCustomerInformation(Connection connection, int customerId, String newAddress, long newContact) throws SQLException {
        String sql = "UPDATE bankapp.customer SET address = ?, contactNumber = ? WHERE customerId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newAddress);
            pstmt.setLong(2, newContact);
            pstmt.setInt(3, customerId);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Customer information updated successfully.");
            } else {
                System.out.println("Customer not found or no changes made.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating customer information: " + e.getMessage());
            throw e;
        }
    }
    private static void retrieveCustomer(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Customer ID to retrieve: ");
        int customerId = Main.getValidInt(scanner);
        Customer customer = Customer.getCustomerByID(connection, customerId);

        if (customer != null ) {
            System.out.printf("| Customer ID | Name                 | Address                           | Contact Number      | Email                | Date of Birth        |%n");
            System.out.printf("| %-11d   %-20s   %-20s   %-18d   %-20s   %-20s  %n",
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getAddress(),
                    customer.getContactNumber(),
                    customer.getEmail(),
                    customer.getDateOfBirth());
            System.out.println();
            System.out.printf("| Occupation     | Account Status         | Aadhaar Number        |%n");
            System.out.printf("| %-21s  %-20s  %-20s       %n",
                    customer.getOccupation(),
                    customer.getAccountStatus(),
                    customer.getAadhaarNumber());
            System.out.println();
            System.out.println("Account Details");

            List<Account> accounts = customer.getAccounts(connection);
            if (!accounts.isEmpty()) {
                System.out.printf("| Account ID  | Balance              | Account Type         | Account Status       |    %n");
                for (Account account : accounts) {
                    System.out.printf("| %-11d | %-20.2f | %-20s | %-20s |                    %n",
                            account.getAccountID(),
                            account.getBalance(),
                            account.getAccountType(),
                            account.getAccountStatus());
                }
            } else {
                System.out.println("No accounts found for this customer.");
            }
        } else {
            System.out.println("Customer not found.");
        }
    }

    public static boolean retrieveCustomer(Scanner scanner, Connection connection, boolean k) throws SQLException {
        System.out.print("Enter Customer ID to retrieve: ");
        int customerId = Main.getValidInt(scanner);
        Customer customer = Customer.getCustomerByID(connection, customerId);
        if (customer == null) {
            k = true;
        }
        System.out.println("Please enter your date of birth (yyyy-mm-dd) for validation:");
        String enteredDob = scanner.next();
        int a=0;
        String query = "SELECT dateOfBirth FROM customer WHERE customerId = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            customerId = Customer.getAuthenticatedCustomerId();
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedDob = resultSet.getString("dateOfBirth");
                if (!enteredDob.equals(storedDob)) {
                    a=1;
                    System.out.println("Date of birth does not match our records. Exiting...");
                }
            }
        }

        if (customer != null && a==0) {
            System.out.printf("| Customer ID | Name                 | Address                           | Contact Number      | Email                | Date of Birth        |%n");
            System.out.printf("| %-11d   %-20s   %-20s   %-18d   %-20s   %-20s  %n",
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getAddress(),
                    customer.getContactNumber(),
                    customer.getEmail(),
                    customer.getDateOfBirth());
            System.out.println();
            System.out.printf("| Occupation     | Account Status         | Aadhaar Number        |%n");
            System.out.printf("| %-11s  %-20s  %-20s       %n",
                    customer.getOccupation(),
                    customer.getAccountStatus(),
                    customer.getAadhaarNumber());
            System.out.println();
            System.out.println("Account Details");

            List<Account> accounts = customer.getAccounts(connection);
            if (!accounts.isEmpty()) {
                System.out.printf("| Account ID      Balance                Account Type          Account Status        %n");
                for (Account account : accounts) {
                    System.out.printf("| %-11d | %-20.2f | %-20s | %-20s |                   %n",
                            account.getAccountID(),
                            account.getBalance(),
                            account.getAccountType(),
                            account.getAccountStatus());
                }
            } else {
                System.out.println("No accounts found for this customer.");
            }
        } else {
            System.out.println("Customer not found.");
        }
        return k;
    }


    private static void updateCustomer(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Customer ID to update: ");
        int customerId = Main.getValidInt(scanner);
        Customer customer = Customer.getCustomerByID(connection, customerId);

        if (customer != null) {
            System.out.print("Enter New Name: ");
            scanner.nextLine();
            String name = scanner.nextLine();
            System.out.print("Enter New Address: ");
            String address = scanner.nextLine();
            System.out.print("Enter New Contact Number: ");
            long contactNumber = scanner.nextLong();
            scanner.nextLine();
            System.out.print("Enter New Email: ");
            String email = scanner.nextLine();
            System.out.print("Enter New Date of Birth (yyyy-mm-dd): ");
            String dob = scanner.nextLine();
            System.out.print("Enter New Occupation: ");
            String occupation = scanner.nextLine();
            System.out.print("Enter New Account Status: ");
            String accountStatus = scanner.nextLine();
            System.out.println("Enter gender");
            String gen=scanner.next();
            System.out.print("Enter New Aadhaar Number: ");
            String aadhaarNumber = scanner.nextLine();

            customer = new Customer(customerId, name, address, contactNumber, email, Date.valueOf(dob), occupation, accountStatus, aadhaarNumber);
            customer.updateCustomer(connection);
            System.out.println("Customer updated successfully.");
        } else {
            System.out.println("Customer not found.");
        }
    }

    private static void deleteCustomer(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Customer ID to delete: ");
        int customerId = Main.getValidInt(scanner);
        Customer.deleteCustomer(connection, customerId);
        System.out.println("Customer deleted successfully.");
    }
    public static long getValidLong(Scanner scanner) {
        while (!scanner.hasNextLong()) {
            System.out.printf("Invalid input. Please enter a valid number: ");
            scanner.next();
        }
        long result = scanner.nextLong();
        scanner.nextLine();
        return result;
    }

    public Customer(int customerId, String name, String address, long contactNumber, String email,
                    Date dateOfBirth, String occupation, String accountStatus, String aadhaarNumber) {
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.occupation = occupation;
        this.accountStatus = accountStatus;
        this.aadhaarNumber = aadhaarNumber;
    }

    public static int getAuthenticatedCustomerId() {
        return authenticatedCustomerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(long contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public void createCustomerWithAccount(Connection connection, double initialBalance, String accountType) throws SQLException {
        String customerSql = "INSERT INTO bankapp.customer (name, address, contactNumber, email, dateOfBirth, occupation, accountStatus, aadhaarNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String accountSql = "INSERT INTO bankapp.account (customerID, balance, accountType, accountStatus) VALUES (?, ?, ?, ?)";

        try (PreparedStatement customerPstmt = connection.prepareStatement(customerSql, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement accountPstmt = connection.prepareStatement(accountSql)) {

            connection.setAutoCommit(false);

            customerPstmt.setString(1, name);
            customerPstmt.setString(2, address);
            customerPstmt.setLong(3, contactNumber);
            customerPstmt.setString(4, email);
            customerPstmt.setDate(5, dateOfBirth);
            customerPstmt.setString(6, occupation);
            customerPstmt.setString(7, accountStatus);
            customerPstmt.setString(8, aadhaarNumber);
            customerPstmt.executeUpdate();

            try (ResultSet generatedKeys = customerPstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.customerId = generatedKeys.getInt(1);

                    accountPstmt.setInt(1, this.customerId);
                    accountPstmt.setDouble(2, initialBalance);
                    accountPstmt.setString(3, accountType);
                    accountPstmt.setString(4, accountStatus);
                    accountPstmt.executeUpdate();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public static Customer getCustomerByID(Connection connection, int customerId) throws SQLException {
        String sql = "SELECT * FROM bankapp.customer WHERE customerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("customerId"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getLong("contactNumber"),
                        rs.getString("email"),
                        rs.getDate("dateOfBirth"),
                        rs.getString("occupation"),
                        rs.getString("accountStatus"),
                        rs.getString("aadhaarNumber")
                );
            } else {
                return null;
            }
        }
    }

    public List<Account> getAccounts(Connection connection) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM bankapp.account WHERE customerID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                accounts.add(new Account(
                        rs.getInt("accountID"),
                        rs.getInt("customerID"),
                        rs.getString("accountType"),
                        rs.getDouble("balance"),

                        rs.getDate("dateOpened"),
                        rs.getString("accountStatus")
                ));
            }
        }
        return accounts;
    }
    public void updateCustomer(Connection connection) throws SQLException {
        String sql = "UPDATE bankapp.customer SET name = ?, address = ?, contactNumber = ?, email = ?, dateOfBirth = ?, occupation = ?, accountStatus = ?, aadhaarNumber = ? WHERE customerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setLong(3, contactNumber);
            pstmt.setString(4, email);
            pstmt.setDate(5, dateOfBirth);
            pstmt.setString(6, occupation);
            pstmt.setString(7, accountStatus);
            pstmt.setString(8, aadhaarNumber);
            pstmt.setInt(9, customerId);
            pstmt.executeUpdate();
        }
    }

    public static void deleteCustomer(Connection connection, int customerId) throws SQLException {
        String deleteAccountsSql = "DELETE FROM bankapp.account WHERE customerID = ?";
        String deleteCustomerSql = "DELETE FROM bankapp.customer WHERE customerId = ?";

        try (PreparedStatement deleteAccountsPstmt = connection.prepareStatement(deleteAccountsSql);
             PreparedStatement deleteCustomerPstmt = connection.prepareStatement(deleteCustomerSql)) {

            connection.setAutoCommit(false);

            deleteAccountsPstmt.setInt(1, customerId);
            deleteAccountsPstmt.executeUpdate();

            deleteCustomerPstmt.setInt(1, customerId);
            deleteCustomerPstmt.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public String toString() {
        return "Customer ID: " + customerId + "\n" +
                "Name: " + name + "\n" +
                "Address: " + address + "\n" +
                "Contact Number: " + contactNumber + "\n" +
                "Email: " + email + "\n" +
                "Date of Birth: " + dateOfBirth + "\n" +
                "Occupation: " + occupation + "\n" +
                "Account Status: " + accountStatus + "\n" +
                "Aadhaar Number: " + aadhaarNumber;
    }
}