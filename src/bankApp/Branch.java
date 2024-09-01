package bankApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class Branch {
    private int branchId;
    private String name;
    private String address;
    private long phoneNumber;
    private Integer managerId;
    private Integer regionId;

    public Branch(int branchId, String name, String address, long phoneNumber, Integer managerId, Integer regionId) {
        this.branchId = branchId;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.managerId = managerId;
        this.regionId = regionId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
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

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public void createBranch(Connection connection) throws SQLException {
        String sql = "INSERT INTO branch (branchId, name, address, phoneNumber, managerId, regionId) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, branchId);
            pstmt.setString(2, name);
            pstmt.setString(3, address);
            pstmt.setLong(4, phoneNumber);
            if (managerId != null) {
                pstmt.setInt(5, managerId);
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }
            if (regionId != null) {
                pstmt.setInt(6, regionId);
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            pstmt.executeUpdate();
        }
    }

    public void displayBranchDetails() {
        System.out.printf("| Branch ID | Name                | Address             | Phone Number | Manager ID         | Region ID   |%n");
        System.out.printf("| %-9d | %-19s | %-19s | %-12d | %-18s | %-11s |%n",
                branchId,
                name,
                address,
                phoneNumber,
                (managerId != null) ? managerId : "NULL",
                (regionId != null) ? regionId : "NULL");
    }


    public static void manageBranches(Scanner scanner, Connection connection) throws SQLException {
        boolean t = true;
        while (t) {
            System.out.printf("1. Create Branch%n");
            System.out.printf("2. Retrieve Branch%n");
            System.out.printf("3. Update Branch%n");
            System.out.printf("4. Delete Branch%n");
            System.out.println("5. Exit");
            System.out.printf("Please select an option: ");

            int choice = Main.getValidInt(scanner);

            switch (choice) {
                case 1:
                    createBranch(scanner, connection);
                    break;
                case 2:
                    retrieveBranch(scanner, connection);
                    break;
                case 3:
                    updateBranch(scanner, connection);
                    break;
                case 4:
                    deleteBranch(scanner, connection);
                    break;
                case 5:
                    t = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void createBranch(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Branch ID: ");
        int branchId = Main.getValidInt(scanner);
        System.out.printf("Enter Branch Name: ");
        String name = scanner.next();
        System.out.printf("Enter Branch Address: ");
        String address = scanner.next();
        System.out.printf("Enter Phone Number: ");
        long phoneNumber = Employee.getValidLong(scanner);
        System.out.printf("Enter Manager ID (or press Enter for NULL): ");
        String managerIdInput = scanner.next();
        Integer managerId = managerIdInput.isEmpty() ? null : Integer.parseInt(managerIdInput);
        System.out.printf("Enter Region ID (or press Enter for NULL): ");
        String regionIdInput = scanner.next();
        Integer regionId = regionIdInput.isEmpty() ? null : Integer.parseInt(regionIdInput);

        Branch branch = new Branch(branchId, name, address, phoneNumber, managerId, regionId);
        branch.createBranch(connection);
        System.out.printf("Branch created successfully.%n");
    }

    private static void retrieveBranch(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Branch ID to retrieve: ");
        int branchId = Main.getValidInt(scanner);
        Branch branch = Branch.getBranchByID(connection, branchId);
        if (branch != null) {
            branch.displayBranchDetails();
        } else {
            System.out.printf("Branch not found.%n");
        }
    }

    private static void updateBranch(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Branch ID to update: ");
        int branchId = Main.getValidInt(scanner);
        Branch branch = Branch.getBranchByID(connection, branchId);
        if (branch != null) {
            System.out.printf("Enter New Branch Name: ");
            String name = scanner.next();
            System.out.printf("Enter New Branch Address: ");
            String address = scanner.next();
            System.out.printf("Enter New Phone Number: ");
            long phoneNumber = Employee.getValidLong(scanner);
            System.out.printf("Enter New Manager ID (or press Enter for NULL): ");
            String managerIdInput = scanner.next();
            Integer managerId = managerIdInput.isEmpty() ? null : Integer.parseInt(managerIdInput);
            System.out.printf("Enter New Region ID (or press Enter for NULL): ");
            String regionIdInput = scanner.next();
            Integer regionId = regionIdInput.isEmpty() ? null : Integer.parseInt(regionIdInput);

            branch.setName(name);
            branch.setAddress(address);
            branch.setPhoneNumber(phoneNumber);
            branch.setManagerId(managerId);
            branch.setRegionId(regionId);
            branch.updateBranch(connection);
            System.out.printf("Branch updated successfully.%n");
        } else {
            System.out.printf("Branch not found.%n");
        }
    }

    private static void deleteBranch(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Branch ID to delete: ");
        int branchId = Main.getValidInt(scanner);
        Branch.deleteBranch(connection, branchId);
        System.out.printf("Branch deleted successfully.%n");
    }

    public static Branch getBranchByID(Connection connection, int branchId) throws SQLException {
        String sql = "SELECT * FROM branch WHERE branchId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, branchId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Branch(
                        rs.getInt("branchId"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getLong("phoneNumber"),
                        rs.getInt("managerId") == 0 ? null : rs.getInt("managerId"),
                        rs.getInt("regionId") == 0 ? null : rs.getInt("regionId")
                );
            } else {
                return null;
            }
        }
    }

    public void updateBranch(Connection connection) throws SQLException {
        String sql = "UPDATE branch SET name = ?, address = ?, phoneNumber = ?, managerId = ?, regionId = ? WHERE branchId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setLong(3, phoneNumber);
            if (managerId != null) {
                pstmt.setInt(4, managerId);
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            if (regionId != null) {
                pstmt.setInt(5, regionId);
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }
            pstmt.setInt(6, branchId);
            pstmt.executeUpdate();
        }
    }

    public static void deleteBranch(Connection connection, int branchId) throws SQLException {
        String sql = "DELETE FROM branch WHERE branchId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, branchId);
            pstmt.executeUpdate();
        }
    }
}
