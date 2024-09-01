package bankApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Region {
    private String regionName;
    private int regionId;
    private String description;
    private String phoneNumber;
    private String email;

    public Region(int regionId, String regionName, String description, String phoneNumber, String email) {
        this.regionId = regionId;
        this.regionName = regionName;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
  public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void createRegion(Connection connection) throws SQLException {
        String sql = "INSERT INTO Region (regionId, name, description, phoneNumber, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, regionId);
            pstmt.setString(2, regionName);
            pstmt.setString(3, description);
            pstmt.setString(4, phoneNumber);
            pstmt.setString(5, email);
            pstmt.executeUpdate();
        }
    }

    public static void manageRegions(Scanner scanner, Connection connection) throws SQLException {
        boolean running = true;
        while (running) {
            System.out.println("1. Create Region");
            System.out.println("2. Retrieve Region");
            System.out.println("3. Update Region");
            System.out.println("4. Delete Region");
            System.out.println("5. Exit");
            System.out.print("Please select an option: ");

            int choice = Main.getValidInt(scanner);

            switch (choice) {
                case 1:
                    createRegion(scanner, connection);
                    break;
                case 2:
                    retrieveRegion(scanner, connection);
                    break;
                case 3:
                    updateRegion(scanner, connection);
                    break;
                case 4:
                    deleteRegion(scanner, connection);
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void createRegion(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Region ID: ");
        int regionId = Main.getValidInt(scanner);
        scanner.nextLine();
        System.out.print("Enter Region Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Description: ");
        String description = scanner.nextLine();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        Region region = new Region(regionId, name, description, phone, email);
        region.createRegion(connection);
        System.out.println("Region created successfully.");
    }

    private static void retrieveRegion(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Region ID to retrieve: ");
        int regionId = Main.getValidInt(scanner);
        Region region = Region.getRegionByID(connection, regionId);
        if (region != null) {
            region.printRegionDetails();
        } else {
            System.out.println("Region not found.");
        }
    }

    private static void updateRegion(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Region ID to update: ");
        int regionId = Main.getValidInt(scanner);
        Region region = Region.getRegionByID(connection, regionId);
        if (region != null) {
            System.out.print("Enter New Region Name: ");
            scanner.nextLine();
            String name = scanner.nextLine();
            System.out.print("Enter New Description: ");
            String description = scanner.nextLine();
            System.out.print("Enter New Phone Number: ");
            String phone = scanner.nextLine();
            System.out.print("Enter New Email: ");
            String email = scanner.nextLine();

            region.updateRegion(connection, name, description, phone, email);
            System.out.println("Region updated successfully.");
        } else {
            System.out.println("Region not found.");
        }
    }

    private static void deleteRegion(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter Region ID to delete: ");
        int regionId = Main.getValidInt(scanner);
        Region.deleteRegion(connection, regionId);
        System.out.println("Region deleted successfully.");
    }

    public static Region getRegionByID(Connection connection, int regionId) throws SQLException {
        String sql = "SELECT * FROM Region WHERE regionId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, regionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Region(
                        rs.getInt("regionId"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("phoneNumber"),
                        rs.getString("email"));
            } else {
                return null;
            }
        }
    }
    public void updateRegion(Connection connection, String name, String description, String phone, String email) throws SQLException {
        String sql = "UPDATE Region SET name = ?, description = ?, phoneNumber = ?, email = ? WHERE regionId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.setInt(5, this.regionId);
            pstmt.executeUpdate();
        }
    }

    public static void deleteRegion(Connection connection, int regionId) throws SQLException {
        String sql = "DELETE FROM Region WHERE regionId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, regionId);
            pstmt.executeUpdate();
        }
    }

    public void printRegionDetails() {
        System.out.printf("%-10s %-20s %-36s %-15s %-25s%n",
                "ID", "Name", "Description", "      Phone", "Email");
        System.out.printf("%-10d %-20s %-36s %-15s %-25s%n",
                this.regionId, this.regionName, this.description, this.phoneNumber, this.email);
    }
public String getName() {
        return regionName;
    }

    public void setName(String name) {
        this.regionName = name;
    }
}
