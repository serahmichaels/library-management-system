import javax.swing.*;
import java.sql.*;
import java.util.Random;
import java.time.LocalDate;
import java.sql.Date;

/**
 * the purpose of this class is to manage library operations,
 * including registering a new user, adding/editing new titles,
 * login/logout, viewing the catalog, and reserving titles.
 */
public class LibraryOperations {
    private static int loggedInUser = -1;

    public static void addBook(JTextArea outputArea) {
        outputArea.setText("");
        String title = JOptionPane.showInputDialog("Enter title:");
        String genre = JOptionPane.showInputDialog("Enter genre:");
        String author = JOptionPane.showInputDialog("Enter author:");
        String isbn = JOptionPane.showInputDialog("Enter ISBN:");

        try (Connection conn = LibraryDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO books (title, genre, author, isbn) VALUES (?, ?, ?, ?)")) {

            stmt.setString(1, title);
            stmt.setString(2, genre);
            stmt.setString(3, author);
            stmt.setString(4, isbn);
            stmt.executeUpdate();
            outputArea.append("Book added successfully.\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewBooks(JTextArea outputArea) {
        try (Connection conn = LibraryDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

            outputArea.setText("");
            while (rs.next()) {
                outputArea.append(rs.getInt("id") + ". " + rs.getString("title") + " - " + rs.getString("author") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void editBook(JTextArea outputArea) {
        outputArea.setText("");
        int bookId = Integer.parseInt(JOptionPane.showInputDialog("Enter book ID to edit:"));
        String title = JOptionPane.showInputDialog("Enter new title:");
        String genre = JOptionPane.showInputDialog("Enter new genre:");
        String author = JOptionPane.showInputDialog("Enter new author:");

        try (Connection conn = LibraryDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE books SET title=?, genre=?, author=? WHERE id=?")) {

            stmt.setString(1, title);
            stmt.setString(2, genre);
            stmt.setString(3, author);
            stmt.setInt(4, bookId);
            stmt.executeUpdate();
            outputArea.append("Book updated successfully.\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void registerUser(JTextArea outputArea) {
        outputArea.setText("");
        String name = JOptionPane.showInputDialog("Enter your name:");
        int cardNumber = new Random().nextInt(10000);

        try (Connection conn = LibraryDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name, card_number) VALUES (?, ?)")) {

            stmt.setString(1, name);
            stmt.setInt(2, cardNumber);
            stmt.executeUpdate();
            outputArea.append("Account created. Your card number: " + cardNumber + "\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loginUser(JTextArea outputArea) {
        outputArea.setText("");
        int cardNumber = Integer.parseInt(JOptionPane.showInputDialog("Enter your card number:"));

        try (Connection conn = LibraryDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE card_number = ?")) {

            stmt.setInt(1, cardNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                loggedInUser = rs.getInt("id");
                outputArea.append("Login successful.\n");
            } else {
                outputArea.append("Invalid card number.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void reserveBook(JTextArea outputArea) {
        outputArea.setText("");
        if (loggedInUser == -1) {
            outputArea.append("You must be logged in to reserve a book.\n");
            return;
        }

        int bookId = Integer.parseInt(JOptionPane.showInputDialog("Enter book ID to reserve:"));
        LocalDate dueDate = LocalDate.now().plusWeeks(2);

        try (Connection conn = LibraryDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE books SET reserved_by=?, due_date=? WHERE id=?")) {

            stmt.setInt(1, loggedInUser);
            stmt.setDate(2, Date.valueOf(dueDate));
            stmt.setInt(3, bookId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                outputArea.append("Book reserved successfully! Due date: " + dueDate + "\n");
            } else {
                outputArea.append("Book reservation failed. Check the ID.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void logoutUser(JTextArea outputArea) {
        outputArea.setText("");
        loggedInUser = -1;
        outputArea.append("Logged out.\n");
    }
}

/**
 * functions to add:
 * add search functionality
 * add view current holds
 * add admin user and function
 */
