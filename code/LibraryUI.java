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
        buttonPanel.setLayout(new GridLayout(4, 2, 10, 10)); // 4 rows, 2 columns with spacing


        JButton registerButton = new JButton("Register");
        JButton addBookButton = new JButton("Add Book");
        JButton loginButton = new JButton("Login");
        JButton viewBooksButton = new JButton("View Catalog");
        JButton exitButton = new JButton("Exit");
        JButton reserveBookButton = new JButton("Reserve Book");
        JButton logoutButton = new JButton("Logout");
        JButton editBookButton = new JButton("Edit Book");


        buttonPanel.add(registerButton);
        buttonPanel.add(addBookButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(editBookButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(viewBooksButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(reserveBookButton);


        registerButton.addActionListener(e -> LibraryOperations.registerUser(outputArea));
        loginButton.addActionListener(e -> LibraryOperations.loginUser(outputArea));
        viewBooksButton.addActionListener(e -> LibraryOperations.viewBooks(outputArea));
        addBookButton.addActionListener(e -> LibraryOperations.addBook(outputArea));
        editBookButton.addActionListener(e -> LibraryOperations.editBook(outputArea));
        reserveBookButton.addActionListener(e -> LibraryOperations.reserveBook(outputArea));
        logoutButton.addActionListener(e -> LibraryOperations.logoutUser(outputArea));
        exitButton.addActionListener(e -> System.exit(0));  // Closes the application


        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);


        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        frame.setVisible(true);
    }


    private static void addButton(JPanel panel, String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        panel.add(button);
    }
}


