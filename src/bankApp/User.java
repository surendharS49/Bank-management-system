package bankApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int userId;
    private String username;
    private String password;
    private int branchId;

    public User(String username, String password,int branchId) {
        this.username = username;
        this.password = password;
        this.branchId=branchId;
    }

    public User(int userId, String username, String password,int branchId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.branchId=branchId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void createUser(Connection connection) throws SQLException {
        String sql = "INSERT INTO User (username, password,branchId) VALUES (?, ?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, branchId);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.userId = generatedKeys.getInt(1);
                }
            }
        }
    }

    public static User getUserByID(Connection connection, int userId) throws SQLException {
        String sql = "SELECT * FROM User WHERE userId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("userId"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("branchId")
                );
            } else {
                return null;
            }
        }
    }

    public void updateUser(Connection connection) throws SQLException {
        String sql = "UPDATE User SET username = ?, password = ? WHERE userId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
        }
    }

    public static void deleteUser(Connection connection, int userId) throws SQLException {
        String sql = "DELETE FROM User WHERE userId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    public static boolean validateUser(String username, String password, Connection connection) throws SQLException {
        String sql = "SELECT * FROM User WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }
}
