import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * this class connects the SQL database to the java program.
 */
public class LibraryDatabase {
    private static Connection conn;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "smichaels", "1203");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
