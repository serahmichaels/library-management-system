import javax.swing.*;
import java.sql.*;
import java.util.Random;
import java.time.LocalDate;

/**
 * the purpose of this class is to manage library operations,
 * including registering a new user, administrative features
 * such as adding/editing/removing titles,login/logout, 
 * viewing the catalog, searching the catalog,
 * and reserving titles.
 */
public class LibraryOperations {
    private static int loggedInUser = -1;
    static boolean isAdmin = false;

    //------------------------------------------------------------------------------------------

    public static void addBook(JTextArea outputArea) {
        if (!isAdmin) {
            JOptionPane.showMessageDialog(null, "You do not have access");
            return;
        }
        outputArea.setText("");
        String title = JOptionPane.showInputDialog("Enter title:");
        String genre = JOptionPane.showInputDialog("Enter genre:");
        String author = JOptionPane.showInputDialog("Enter author:");
        String isbn = JOptionPane.showInputDialog("Enter ISBN:");
        int copies = Integer.parseInt(JOptionPane.showInputDialog("Enter number of copies:"));

        try (Connection conn = LibraryDatabase.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO books (title, genre, author, isbn, copies) VALUES (?, ?, ?, ?, ?)");) {
            stmt.setString(1, title);
            stmt.setString(2, genre);
            stmt.setString(3, author);
            stmt.setString(4, isbn);
            stmt.setInt(5, copies);
            stmt.executeUpdate();
            outputArea.append("Book added successfully.\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------

    public static void viewBooks(JTextArea outputArea) {
        try (Connection conn = LibraryDatabase.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT id, title, author, copies FROM books")) {

            outputArea.setText("");

            while (rs.next()) {
                int bookId = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int copies = rs.getInt("copies");

                outputArea.append(bookId + ". " + title + " - " + author + " || (Available Copies: " + copies + ")\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------

    public static void editBook(JTextArea outputArea) {
        outputArea.setText("");
        if (!isAdmin) {
            JOptionPane.showMessageDialog(null, "You do not have access.");
            return;
        }
        outputArea.setText("");
        int bookId = Integer.parseInt(JOptionPane.showInputDialog("Enter book ID to edit:"));
        String title = JOptionPane.showInputDialog("Enter new title:");
        String genre = JOptionPane.showInputDialog("Enter new genre:");
        String author = JOptionPane.showInputDialog("Enter new author:");

        try (Connection conn = LibraryDatabase.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE books SET title=?, genre=?, author=? WHERE id=?")) {

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

    //------------------------------------------------------------------------------------------

    public static void removeBook(JTextArea outputArea) {
        outputArea.setText("");
        if (!isAdmin) {
            JOptionPane.showMessageDialog(null, "You do not have access.");
            return;
        }
        outputArea.setText("");
        int bookId = Integer.parseInt(JOptionPane.showInputDialog("Enter book ID to remove:"));
        try (Connection conn = LibraryDatabase.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE id=?")) {
            stmt.setInt(1, bookId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Book removed successfully.");
                outputArea.append("Book removed successfully.\n");
            } else {
                outputArea.append("Book not found. Check the ID.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------

    public static void registerUser(JTextArea outputArea) {
        outputArea.setText("");
        String name = JOptionPane.showInputDialog("Enter your name:");
        int cardNumber = new Random().nextInt(10000);

        try (Connection conn = LibraryDatabase.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name, card_number) VALUES (?, ?)")) {

            stmt.setString(1, name);
            stmt.setInt(2, cardNumber);
            stmt.executeUpdate();
            outputArea.append("Account created. Your card number: " + cardNumber + "\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------

    public static void loginUser(JTextArea outputArea) {
        outputArea.setText("");
        int cardNumber = Integer.parseInt(JOptionPane.showInputDialog("Enter your card number:"));

        try (Connection conn = LibraryDatabase.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT id, is_admin FROM users WHERE card_number = ?")) {
            stmt.setInt(1, cardNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                loggedInUser = rs.getInt("id");
                isAdmin = rs.getBoolean("is_admin");
                outputArea.append("Login successful.\n");
            } else {
                outputArea.append("Invalid card number.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------

    public static void searchBooks(JTextArea outputArea) {
        outputArea.setText("");
        String title = JOptionPane.showInputDialog("Enter book title to search:");
        try (Connection conn = LibraryDatabase.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE title LIKE ?")) {

            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();

            outputArea.setText("");
            while (rs.next()) {
                outputArea.append(rs.getInt("id") + ". " + rs.getString("title") + " - " + rs.getString("author") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------

    public static void reserveBook(JTextArea outputArea) {
        outputArea.setText("");
        if (loggedInUser == -1) {
            outputArea.append("You must be logged in to reserve a book.\n");
            return;
        }
        int bookId = Integer.parseInt(JOptionPane.showInputDialog("Enter book ID to reserve:"));
        LocalDate dueDate = LocalDate.now().plusWeeks(2);

        try (Connection conn = LibraryDatabase.getConnection(); PreparedStatement copiesStmt = conn.prepareStatement("SELECT copies FROM books WHERE id = ?"); PreparedStatement reserveStmt = conn.prepareStatement("UPDATE books SET reserved_by=?, due_date=?, copies=copies-1 WHERE id=? AND copies > 0");) {
            copiesStmt.setInt(1, bookId);
            ResultSet rs = copiesStmt.executeQuery();
            if (rs.next()) {
                int availableCopies = rs.getInt("copies");
                if (availableCopies > 0) {
                    reserveStmt.setInt(1, loggedInUser);
                    reserveStmt.setDate(2, Date.valueOf(dueDate));
                    reserveStmt.setInt(3, bookId);

                    int rowsAffected = reserveStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Book reserved successfully! Due date: " + dueDate);
                        outputArea.append("Book reserved successfully! Due date: " + dueDate + "\n");
                    } else {
                        outputArea.append("Book reservation failed. Check the ID.\n");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No copies available for this book. Please check back later.");
                    outputArea.append("No copies available for this book. Please check back later.\n");
                }
            } else {
                outputArea.append("Book not found. Check the ID.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------

    public static void returnBook(JTextArea outputArea) {
        outputArea.setText("");
        if (loggedInUser == -1) {
            outputArea.append("You must be logged in to return a book.\n");
            return;
        }
        int bookId = Integer.parseInt(JOptionPane.showInputDialog("Enter book ID to return:"));

        try (Connection conn = LibraryDatabase.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE books SET copies = copies + 1, reserved_by = NULL, due_date = NULL WHERE id = ? AND reserved_by = ?");) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, loggedInUser);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Book returned successfully.");
                outputArea.append("Book returned successfully.\n");
            } else {
                JOptionPane.showMessageDialog(null, "You do not have this book reserved.");
                outputArea.append("You do not have this book reserved.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------

    public static void viewProfile(JTextArea outputArea) {
        outputArea.setText("");
        if (loggedInUser == -1) {
            outputArea.append("You must be logged in to view your profile.\n");
            return;
        }
        try (Connection conn = LibraryDatabase.getConnection(); PreparedStatement userStmt = conn.prepareStatement("SELECT name, card_number FROM users WHERE id = ?"); PreparedStatement holdsStmt = conn.prepareStatement("SELECT title, author, due_date FROM books WHERE reserved_by = ?")) {
            userStmt.setInt(1, loggedInUser);
            ResultSet userRs = userStmt.executeQuery();
            if (userRs.next()) {
                String name = userRs.getString("name");
                int cardNumber = userRs.getInt("card_number");
                outputArea.append("Name: " + name + "\n");
                outputArea.append("Card Number: " + cardNumber + "\n");
            }
            holdsStmt.setInt(1, loggedInUser);
            ResultSet holdsRs = holdsStmt.executeQuery();
            outputArea.append("\nYour Current Holds:\n");
            while (holdsRs.next()) {
                String title = holdsRs.getString("title");
                String author = holdsRs.getString("author");
                Date dueDate = holdsRs.getDate("due_date");

                if (dueDate != null) {
                    outputArea.append(title + " - " + author + " (Due: " + dueDate + ")\n");
                } else {
                    outputArea.append(title + " - " + author + "\n");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //------------------------------------------------------------------------------------------
    public static boolean isAdminUser(JTextArea outputArea) {
        outputArea.setText("");
        if (!isAdmin) {
            outputArea.append("Access denied. Please contact library staff for more information.\n");
            return false;
        }

        return true;
    }

    //------------------------------------------------------------------------------------------

    public static void logoutUser(JTextArea outputArea) {
        outputArea.setText("");
        loggedInUser = -1;
        isAdmin = false;
        outputArea.append("Logged out.\n");
    }
}