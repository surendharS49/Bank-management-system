package bankApp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    static  String URL = "jdbc:mysql://127.0.0.1:3306/bankapp";
     static  String USER = "root";
     static  String PASSWORD = "BENstokes@55";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
