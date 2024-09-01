//package bankApp;
//
//import java.sql.Connection;
//import java.sql.Date;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class Payment {
//    private int paymentID;
//    private int cardID;
//    private double amount;
//    private Date paymentDate;
//    private String merchant;
//
//    // Constructor with all fields
//    public Payment( double amount, Date paymentDate, String merchant) {
////        this.paymentID = paymentID;
////        this.cardID = cardID;
//        this.amount = amount;
//        this.paymentDate = paymentDate;
//        this.merchant = merchant;
//    }
//
//    // Getters and Setters
//    public int getPaymentID() {
//        return paymentID;
//    }
//
//    public void setPaymentID(int paymentID) {
//        this.paymentID = paymentID;
//    }
//
//    public int getCardID() {
//        return cardID;
//    }
//
//    public void setCardID(int cardID) {
//        this.cardID = cardID;
//    }
//
//    public double getAmount() {
//        return amount;
//    }
//
//    public void setAmount(double amount) {
//        this.amount = amount;
//    }
//
//    public Date getPaymentDate() {
//        return paymentDate;
//    }
//
//    public void setPaymentDate(Date paymentDate) {
//        this.paymentDate = paymentDate;
//    }
//
//    public String getMerchant() {
//        return merchant;
//    }
//
//    public void setMerchant(String merchant) {
//        this.merchant = merchant;
//    }
//
//    // Create Payment
//    public void createPayment(Connection connection) throws SQLException {
//        String sql = "INSERT INTO Payment (paymentID, cardID, amount, paymentDate, merchant) VALUES (?, ?, ?, ?, ?)";
//        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//            pstmt.setInt(1, paymentID);
//            pstmt.setInt(2, cardID);
//            pstmt.setDouble(3, amount);
//            pstmt.setDate(4, paymentDate);
//            pstmt.setString(5, merchant);
//            pstmt.executeUpdate();
//        }
//    }
//
//    // Retrieve Payment
//    public static Payment getPaymentByID(Connection connection, int paymentID) throws SQLException {
//        String sql = "SELECT * FROM Payment WHERE paymentID = ?";
//        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//            pstmt.setInt(1, paymentID);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return new Payment(
//                        rs.getInt("paymentID"),
//                        rs.getInt("cardID"),
//                        rs.getDouble("amount"),
//                        rs.getDate("paymentDate"),
//                        rs.getString("merchant")
//                );
//            } else {
//                return null;
//            }
//        }
//    }
//
//    // Update Payment
//    public void updatePayment(Connection connection, double amount, Date paymentDate, String merchant) throws SQLException {
//        String sql = "UPDATE Payment SET cardID = ?, amount = ?, paymentDate = ?, merchant = ? WHERE paymentID = ?";
//        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//            pstmt.setInt(1, cardID);
//            pstmt.setDouble(2, amount);
//            pstmt.setDate(3, paymentDate);
//            pstmt.setString(4, merchant);
//            pstmt.setInt(5, paymentID);
//            pstmt.executeUpdate();
//        }
//    }
//
//    // Delete Payment
//    public static void deletePayment(Connection connection, int paymentID) throws SQLException {
//        String sql = "DELETE FROM Payment WHERE paymentID = ?";
//        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//            pstmt.setInt(1, paymentID);
//            pstmt.executeUpdate();
//        }
//    }
//
//    @Override
//    public String toString() {
//        return "PaymentID: " + paymentID + ", CardID: " + cardID + ", Amount: " + amount +
//                ", PaymentDate: " + paymentDate + ", Merchant: " + merchant;
//    }
//}
