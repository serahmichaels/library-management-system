import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * this class is intended to develop the user interface for the
 * Library Management System.
 */
public class LibraryUI {
    private static JFrame frame;
    private static JTextArea outputArea = new JTextArea(10, 30);

    public static void createUI() {
        frame = new JFrame("Library Management System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Library Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 2, 10, 10));

        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");
        JButton viewBooksButton = new JButton("View Catalog");
        JButton exitButton = new JButton("Exit");
        JButton reserveBookButton = new JButton("Reserve Book");
        JButton logoutButton = new JButton("Logout");
        JButton searchBookButton = new JButton("Search Book");
        JButton viewProfileButton = new JButton("View Profile");
        JButton adminButton = new JButton("Admin");
        JButton returnButton = new JButton("Return Book");

        buttonPanel.add(registerButton);
        buttonPanel.add(viewBooksButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(searchBookButton);
        buttonPanel.add(viewProfileButton);
        buttonPanel.add(reserveBookButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(adminButton);

        registerButton.addActionListener(e -> LibraryOperations.registerUser(outputArea));
        loginButton.addActionListener(e -> LibraryOperations.loginUser(outputArea));
        viewBooksButton.addActionListener(e -> LibraryOperations.viewBooks(outputArea));
        reserveBookButton.addActionListener(e -> LibraryOperations.reserveBook(outputArea));
        logoutButton.addActionListener(e -> LibraryOperations.logoutUser(outputArea));
        searchBookButton.addActionListener(e -> LibraryOperations.searchBooks(outputArea));
        viewProfileButton.addActionListener(e -> LibraryOperations.viewProfile(outputArea));
        returnButton.addActionListener(e -> LibraryOperations.returnBook(outputArea));
        adminButton.addActionListener(e -> {
            if (LibraryOperations.isAdminUser(outputArea)) {
                showAdminPanel();
            }
        });

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(new JScrollPane(outputArea), BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static void showAdminPanel() {
        JFrame adminFrame = new JFrame("Admin Panel");
        adminFrame.setSize(400, 300);
        adminFrame.setLayout(new GridLayout(4, 1, 10, 10));

        JButton addBookButton = new JButton("Add Book");
        JButton editBookButton = new JButton("Edit Book");
        JButton removeBookButton = new JButton("Remove Book");
        JButton exitAdminButton = new JButton("Exit Admin");

        addBookButton.addActionListener(e -> LibraryOperations.addBook(outputArea));
        editBookButton.addActionListener(e -> LibraryOperations.editBook(outputArea));
        removeBookButton.addActionListener(e -> LibraryOperations.removeBook(outputArea));
        exitAdminButton.addActionListener(e -> adminFrame.dispose());

        adminFrame.add(addBookButton);
        adminFrame.add(editBookButton);
        adminFrame.add(removeBookButton);
        adminFrame.add(exitAdminButton);

        adminFrame.setVisible(true);
    }

    private static void addButton(JPanel panel, String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        panel.add(button);
    }
}