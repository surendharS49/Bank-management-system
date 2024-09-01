package bankApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CardIssuer {
    private int id;
    private String cardIssuerName;
    private String cardType;

    public CardIssuer(String cardIssuerName, String cardType) {
        this.cardIssuerName = cardIssuerName;
        this.cardType = cardType;
    }

    public CardIssuer(int id, String cardIssuerName, String cardType) {
        this.id = id;
        this.cardIssuerName = cardIssuerName;
        this.cardType = cardType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardIssuerName() {
        return cardIssuerName;
    }

    public void setCardIssuerName(String cardIssuerName) {
        this.cardIssuerName = cardIssuerName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void createCardIssuer(Connection connection) throws SQLException {
        String sql = "INSERT INTO CardIssuer (cardIssuerName, cardType) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, cardIssuerName);
            pstmt.setString(2, cardType);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                }
            }
            System.out.printf("CardIssuer created successfully with ID %d.%n", id);
        }
    }

    public static CardIssuer getCardIssuerById(Connection connection, int id) throws SQLException {
        String sql = "SELECT * FROM CardIssuer WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new CardIssuer(
                        rs.getInt("id"),
                        rs.getString("cardIssuerName"),
                        rs.getString("cardType")
                );
            } else {
                System.out.printf("CardIssuer not found.%n");
                return null;
            }
        }
    }

    public void updateCardIssuer(Connection connection) throws SQLException {
        String sql = "UPDATE CardIssuer SET cardIssuerName = ?, cardType = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cardIssuerName);
            pstmt.setString(2, cardType);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            System.out.printf("CardIssuer updated successfully.%n");
        }
    }

    public static void deleteCardIssuer(Connection connection, int id) throws SQLException {
        String sql = "DELETE FROM CardIssuer WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.printf("CardIssuer deleted successfully.%n");
        }
    }

    public void displayCardIssuerDetails() {

        System.out.printf("| ID | Card Issuer Name   | Card Type        |%n");

        System.out.printf("| %-2d | %-18s | %-16s |%n", id, cardIssuerName, cardType);
    }

    public static void manageCardIssuers(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("1. Create CardIssuer%n");
        System.out.printf("2. Retrieve CardIssuer%n");
        System.out.printf("3. Update CardIssuer%n");
        System.out.printf("4. Delete CardIssuer%n");
        System.out.printf("5. Display CardIssuer Details%n");
        System.out.printf("Please select an option: ");

        int choice = getValidInt(scanner);

        switch (choice) {
            case 1:
                createCardIssuer(scanner, connection);
                break;
            case 2:
                retrieveCardIssuer(scanner, connection);
                break;
            case 3:
                updateCardIssuer(scanner, connection);
                break;
            case 4:
                deleteCardIssuer(scanner, connection);
                break;
            case 5:
                displayCardIssuerDetails(scanner, connection);
                break;
            default:
                System.out.printf("Invalid choice.%n");
        }
    }

    private static int getValidInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.printf("Invalid input. Please enter a valid integer: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static void createCardIssuer(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter card issuer name: ");
        String cardIssuerName = scanner.next();

        System.out.printf("Enter card type: ");
        String cardType = scanner.next();

        CardIssuer newCardIssuer = new CardIssuer(cardIssuerName, cardType);
        newCardIssuer.createCardIssuer(connection);
    }

    private static void retrieveCardIssuer(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter CardIssuer ID: ");
        int id = getValidInt(scanner);
        CardIssuer cardIssuer = getCardIssuerById(connection, id);
        if (cardIssuer != null) {
            cardIssuer.displayCardIssuerDetails();
        }
    }

    private static void updateCardIssuer(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter CardIssuer ID: ");
        int id = getValidInt(scanner);
        CardIssuer cardIssuer = getCardIssuerById(connection, id);

        if (cardIssuer != null) {
            System.out.printf("Enter new card issuer name: ");
            String cardIssuerName = scanner.next();

            System.out.printf("Enter new card type: ");
            String cardType = scanner.next();

            cardIssuer.setCardIssuerName(cardIssuerName);
            cardIssuer.setCardType(cardType);

            cardIssuer.updateCardIssuer(connection);
        }
    }

    private static void deleteCardIssuer(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter CardIssuer ID: ");
        int id = getValidInt(scanner);
        deleteCardIssuer(connection, id);
    }

    private static void displayCardIssuerDetails(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter CardIssuer ID: ");
        int id = getValidInt(scanner);
        CardIssuer cardIssuer = getCardIssuerById(connection, id);

        if (cardIssuer != null) {
            cardIssuer.displayCardIssuerDetails();
        }
    }
}

