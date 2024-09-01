package bankApp;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

public class Card {
    private int cardId;
    private String cardNumber;
    private String pin;
    private String cardholderName;
    private Date expiryDate;
    private int accountId;
    private String status;
    private CardIssuer cardIssuer;
    public Card(String cardNumber, String pin, String cardholderName, Date expiryDate, int accountId,
                String status, CardIssuer cardIssuer) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.cardholderName = cardholderName;
        this.expiryDate = expiryDate;
        this.accountId = accountId;
        this.status = status;
        this.cardIssuer = cardIssuer;
    }
    public Card(int cardId, String cardNumber, String pin, String cardholderName, Date expiryDate,
                int accountId, String status, CardIssuer cardIssuer) {
        this.cardId = cardId;
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.cardholderName = cardholderName;
        this.expiryDate = expiryDate;
        this.accountId = accountId;
        this.status = status;
        this.cardIssuer = cardIssuer;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public static CardIssuer getCardIssuerByType(Connection connection, String cardType) throws SQLException {
        String sql = "SELECT * FROM CardIssuer WHERE cardType = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cardType);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new CardIssuer(
                        rs.getInt("id"),
                        rs.getString("cardIssuerName"),
                        rs.getString("cardType")
                );
            } else {
                return null;
            }
        }
    }
    public static void requestNewCard(Connection connection, int customerId, String cardType) throws SQLException {
        if (!isCustomerValid(connection, customerId)) {
            System.out.printf("Invalid customer ID. Card cannot be issued.%n");
            return;
        }
        CardIssuer cardIssuer = getCardIssuerByType(connection, cardType);
        if (cardIssuer == null) {
            System.out.printf("Invalid card type. Card cannot be issued.%n");
            return;
        }
        String cardNumber = generateCardNumber();
        String pin = generateDefaultPin();
        String cardholderName = getCustomerName(connection, customerId);
        Date expiryDate = new Date(System.currentTimeMillis() + 31536000000L);
        Card newCard = new Card(
                cardNumber,
                pin,
                cardholderName,
                expiryDate,
                customerId,
                "Active",
                cardIssuer
        );

        newCard.createCard(connection);
        System.out.printf("New card issued successfully for customer ID %d.%n", customerId);
    }
    private static boolean isCustomerValid(Connection connection, int customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Customer WHERE customerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    private static String generateDefaultPin() {
        return "1234";
    }
    private static String getCustomerName(Connection connection, int customerId) throws SQLException {
        String sql = "SELECT name FROM Customer WHERE customerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return "Unknown";
    }
    public void setPin(String pin) {
        this.pin = pin;
    }
    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }
    private static String generateCardNumber() {
        Random random = new Random();
        int number = 1000 + random.nextInt(9000);
        return String.valueOf(number);
    }
    public void createCard(Connection connection) throws SQLException {
        String sql = "INSERT INTO Card (cardNumber, pin, cardholderName, expiryDate, accountId, status, cardIssuerId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, cardNumber);
            pstmt.setString(2, pin);
            pstmt.setString(3, cardholderName);
            pstmt.setDate(4, expiryDate);
            pstmt.setInt(5, accountId);
            pstmt.setString(6, status);
            pstmt.setInt(7, cardIssuer.getId());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.cardId = generatedKeys.getInt(1);
                }
            }
            System.out.printf("Card created successfully with ID %d.%n", cardId);
        }
    }
    public static Card getCardByID(Connection connection, int cardId) throws SQLException {
        String sql = "SELECT c.*, ci.cardIssuerName, ci.cardType FROM Card c JOIN CardIssuer ci ON c.cardIssuerId = ci.id WHERE c.cardId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, cardId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                CardIssuer issuer = new CardIssuer(
                        rs.getInt("cardIssuerId"),
                        rs.getString("cardIssuerName"),
                        rs.getString("cardType")
                );
                return new Card(
                        rs.getInt("cardId"),
                        rs.getString("cardNumber"),
                        rs.getString("pin"),
                        rs.getString("cardholderName"),
                        rs.getDate("expiryDate"),
                        rs.getInt("accountId"),
                        rs.getString("status"),
                        issuer
                );
            } else {
                System.out.printf("Card not found.%n");
                return null;
            }
        }
    }
    public void updateCard(Connection connection) throws SQLException {
        String sql = "UPDATE Card SET cardNumber = ?, pin = ?, cardholderName = ?, expiryDate = ?, accountId = ?, status = ?, cardIssuerId = ? WHERE cardId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            pstmt.setString(2, pin);
            pstmt.setString(3, cardholderName);
            pstmt.setDate(4, expiryDate);
            pstmt.setInt(5, accountId);
            pstmt.setString(6, status);
            pstmt.setInt(7, cardIssuer.getId());
            pstmt.setInt(8, cardId);
            pstmt.executeUpdate();
            System.out.printf("Card updated successfully.%n");
        }
    }
    public static void deleteCard(Connection connection, int cardId) throws SQLException {
        String sql = "DELETE FROM Card WHERE cardId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, cardId);
            pstmt.executeUpdate();
            System.out.printf("Card deleted successfully.%n");
        }
    }
    public void displayCardDetails() {
        System.out.printf("Card ID       Card Number          PIN          Cardholder Name         Expiry Date       Account ID       Status       Card Issuer Name%n");
        System.out.printf("%-12d %-20s %-12s %-22s %-16s %-15d %-10s %-20s%n",
                cardId,
                cardNumber,
                pin,
                cardholderName,
                expiryDate,
                accountId,
                status,
                cardIssuer.getCardIssuerName());
    }
    public static void manageCards(Scanner scanner, Connection connection) throws SQLException {
        boolean t = true;
        while (t) {
            System.out.printf("1. Create Card%n");
            System.out.printf("2. Retrieve Card%n");
            System.out.printf("3. Update Card%n");
            System.out.printf("4. Delete Card%n");
            System.out.printf("5. Display Card Details%n");
            System.out.println("6. Exit");
            System.out.printf("Please select an option: ");

            int choice = getValidInt(scanner);

            switch (choice) {
                case 1:
                    createCard(scanner, connection);
                    break;
                case 2:
                    retrieveCard(scanner, connection);
                    break;
                case 3:
                    updateCard(scanner, connection);
                    break;
                case 4:
                    deleteCard(scanner, connection);
                    break;
                case 5:
                    displayCardDetails(scanner, connection);
                    break;
                case 6:
                    t = false;
                    break;
                default:
                    System.out.printf("Invalid choice. Please try again.%n");
                    manageCards(scanner, connection);
            }
        }
    }
    private static void createCard(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Account ID: ");
        int accountId = getValidInt(scanner);

        Account account = Account.getAccountByID(connection, accountId);
        if (account == null) {
            System.out.printf("Account not found. Please try again.%n");
            return;
        }

        System.out.printf("Enter Cardholder Name: ");
        String cardholderName = scanner.next();

        Date expiryDate = new Date(System.currentTimeMillis() + 31536000000L);
        CardIssuer cardIssuer = CardIssuer.getCardIssuerById(connection, 1);

        Card card = new Card(
                generateCardNumber(),
                "1234",
                cardholderName,
                expiryDate,
                accountId,
                "Active",
                cardIssuer
        );
        card.createCard(connection);
    }
    private static void retrieveCard(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Card ID: ");
        int cardId = getValidInt(scanner);

        Card card = getCardByID(connection, cardId);
        if (card != null) {
            card.displayCardDetails();
        }
    }
    private static void updateCard(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Card ID: ");
        int cardId = getValidInt(scanner);
        Card card = getCardByID(connection, cardId);
        if (card == null) {
            System.out.printf("Card not found. Please try again.%n");
            return;
        }
        System.out.printf("Enter New Card Number (leave empty to keep existing): ");
        String cardNumber = scanner.next();
        if (!cardNumber.isEmpty()) {
            card.setCardNumber(cardNumber);
        }
        System.out.printf("Enter New PIN (leave empty to keep existing): ");
        String pin = scanner.next();
        if (!pin.isEmpty()) {
            card.setPin(pin);
        }
        System.out.printf("Enter New Cardholder Name (leave empty to keep existing): ");
        String cardholderName = scanner.next();
        if (!cardholderName.isEmpty()) {
            card.setCardholderName(cardholderName);
        }

        card.updateCard(connection);
    }
    private static void deleteCard(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Card ID: ");
        int cardId = getValidInt(scanner);

        deleteCard(connection, cardId);
    }
    private static void displayCardDetails(Scanner scanner, Connection connection) throws SQLException {
        System.out.printf("Enter Card ID: ");
        int cardId = getValidInt(scanner);

        Card card = getCardByID(connection, cardId);
        if (card != null) {
            card.displayCardDetails();
        }
    }
    private static int getValidInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.printf("Invalid input. Please enter a number: ");
            scanner.next();
        }
        return scanner.nextInt();
    }
}
