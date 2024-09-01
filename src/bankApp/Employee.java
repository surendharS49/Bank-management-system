package bankApp;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class Employee {
    private int employeeId;
    private String name;
    private int branchId;
    private String address;
    private long contactNo;
    private String email;
    private String position;
    private String department;
    private int salary;
    private Date dateOfHired;

    public Employee(String name, int branchId, String address, long contactNo, String email,
                    String position, String department, int salary, Date dateOfHired) {
        this.name = name;
        this.branchId = branchId;
        this.address = address;
        this.contactNo = contactNo;
        this.email = email;
        this.position = position;
        this.department = department;
        this.salary = salary;
        this.dateOfHired = dateOfHired;
    }

    public Employee(int employeeId, String name, int branchId, String address, long contactNo, String email,
                    String position, String department, int salary, Date dateOfHired) {
        this.employeeId = employeeId;
        this.name = name;
        this.branchId = branchId;
        this.address = address;
        this.contactNo = contactNo;
        this.email = email;
        this.position = position;
        this.department = department;
        this.salary = salary;
        this.dateOfHired = dateOfHired;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getContactNo() {
        return contactNo;
    }

    public void setContactNo(long contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public Date getDateOfHired() {
        return dateOfHired;
    }

    public void setDateOfHired(Date dateOfHired) {
        this.dateOfHired = dateOfHired;
    }

    public void createEmployee(Connection connection) throws SQLException {
        String sql = "INSERT INTO Employee (name, branchId, address, contactNo, email, position, department, salary, dateOfHired) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, branchId);
            pstmt.setString(3, address);
            pstmt.setLong(4, contactNo);
            pstmt.setString(5, email);
            pstmt.setString(6, position);
            pstmt.setString(7, department);
            pstmt.setInt(8, salary);
            pstmt.setDate(9, dateOfHired);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.employeeId = generatedKeys.getInt(1);
                }
            }
        }
    }

    public static Employee getEmployeeByID(Connection connection, int employeeId) throws SQLException {
        String sql = "SELECT * FROM Employee WHERE employeeId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Employee(
                        rs.getInt("employeeId"),
                        rs.getString("name"),
                        rs.getInt("branchId"),
                        rs.getString("address"),
                        rs.getLong("contactNo"),
                        rs.getString("email"),
                        rs.getString("position"),
                        rs.getString("department"),
                        rs.getInt("salary"),
                        rs.getDate("dateOfHired")
                );
            } else {
                return null;
            }
        }
    }

    public void updateEmployee(Connection connection, String name, String position, int salary, Date dateOfHired) throws SQLException {
        String sql = "UPDATE Employee SET name = ?, position = ?, salary = ?, dateOfHired = ? WHERE employeeId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, position);
            pstmt.setInt(3, salary);
            pstmt.setDate(4, dateOfHired);
            pstmt.setInt(5, employeeId);
            pstmt.executeUpdate();
        }
    }

    public static void deleteEmployee(Connection connection, int employeeId) throws SQLException {
        String sql = "DELETE FROM Employee WHERE employeeId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        }
    }

    public void displayEmployeeDetails() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        System.out.printf("| Employee ID  | Name           | Branch ID   | Address           | Contact No        | Email            |%n");

        System.out.printf("| %-12d | %-14s | %-11d | %-17s | %-17d | %-16s |%n",
                employeeId, name, branchId, address, contactNo, email);

        System.out.println();
        System.out.printf("| Position     | Department     | Salary      | Date of Hired     |           |%n");

        System.out.printf("| %-12s | %-14s | %-11d | %-17s |            %n",
                position, department, salary, dateFormat.format(dateOfHired));
    }


    public static void manageEmployees(Scanner scanner, Connection connection) throws SQLException {
        boolean t = true;
        while (t) {
            System.out.printf("%n1. Create Employee%n");
            System.out.printf("2. Retrieve Employee%n");
            System.out.printf("3. Update Employee%n");
            System.out.printf("4. Delete Employee%n");
            System.out.println("5. Exit");
            System.out.printf("Please select an option: ");

            int choice = Main.getValidInt(scanner);

            switch (choice) {
                case 1:
                    createEmployee(scanner, connection);
                    break;
                case 2:
                    retrieveEmployee(scanner, connection);
                    break;
                case 3:
                    updateEmployee(scanner, connection);
                    break;
                case 4:
                    deleteEmployee(scanner, connection);
                    break;
                case 5:
                    t = false;
                    break;
                default:
                    System.out.printf("Invalid choice.%n");
            }
        }
    }

    private static void createEmployee(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Employee Name: ");
        String name = scanner.next();
        System.out.printf("Enter Branch ID: ");
        int branchId = Main.getValidInt(scanner);
        System.out.printf("Enter Employee Address: ");
        String address = scanner.next();
        System.out.printf("Enter Employee Contact Number: ");
        long contactNo = getValidLong(scanner);
        System.out.printf("Enter Employee Email: ");
        String email = scanner.next();
        System.out.printf("Enter Employee Position: ");
        String position = scanner.next();
        System.out.printf("Enter Employee Department: ");
        String department = scanner.next();
        System.out.printf("Enter Employee Salary: ");
        int salary = Main.getValidInt(scanner);
        System.out.printf("Enter Employee Date of Hire (yyyy-mm-dd): ");
        String dateOfHire = scanner.next();

        Employee employee = new Employee(name, branchId, address, contactNo, email, position, department, salary, Date.valueOf(dateOfHire));
        employee.createEmployee(connection);
        System.out.printf("Employee created successfully with ID: %d%n", employee.getEmployeeId());
    }

    private static void retrieveEmployee(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Employee ID to retrieve: ");
        int employeeId = Main.getValidInt(scanner);

        Employee employee = Employee.getEmployeeByID(connection, employeeId);
        if (employee != null) {
            employee.displayEmployeeDetails();
        } else {
            System.out.printf("Employee with ID %d not found.%n", employeeId);
        }
    }

    private static void updateEmployee(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Employee ID to update: ");
        int employeeId = Main.getValidInt(scanner);

        Employee employee = Employee.getEmployeeByID(connection, employeeId);
        if (employee != null) {
            System.out.printf("Enter new Name: ");
            String name = scanner.next();
            System.out.printf("Enter new Position: ");
            String position = scanner.next();
            System.out.printf("Enter new Salary: ");
            int salary = Main.getValidInt(scanner);
            System.out.printf("Enter new Date of Hire (yyyy-mm-dd): ");
            String dateOfHire = scanner.next();

            employee.updateEmployee(connection, name, position, salary, Date.valueOf(dateOfHire));
            System.out.printf("Employee with ID %d updated successfully.%n", employeeId);
        } else {
            System.out.printf("Employee with ID %d not found.%n", employeeId);
        }
    }

    private static void deleteEmployee(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Employee ID to delete: ");
        int employeeId = Main.getValidInt(scanner);

        Employee.deleteEmployee(connection, employeeId);
        System.out.printf("Employee with ID %d deleted successfully.%n", employeeId);
    }

    public static long getValidLong(Scanner scanner) {
        while (!scanner.hasNextLong()) {
            System.out.printf("Invalid input. Please enter a valid long number: ");
            scanner.next();
        }
        return scanner.nextLong();
    }
}
