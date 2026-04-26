package viewAndControl;

import data.GameDatabase;
import data.UserDatabase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Displays the account creation screen for the ABBG Board Games application.
 * Handles user creation by adding credentials to the UserDatabase.
 * On successful account creation, closes the account creation window and opens the main screen.
 * Design Pattern:
 * Observer Pattern (Java Swing)
 * Subject: createAccountButton
 * Observer: This button use a ActionListener instance
 * When a button is clicked it updates the userDatabase and userXML to
 * create a new User object.
 *
 */
public class AccountCreation
{
    /**Input field for the user's username */
    private JTextField     usernameField;
    /** Input field for the user's password */
    private JPasswordField passwordField;
    /** Second input for the user's password to see if they typed it in correctly */
    private JPasswordField confirmField;
    /** Displays error or validation message for the user */
    private JLabel         statusLabel;
    /** The user database used to place the user's newly made info */
    private final UserDatabase userDatabase;

    /** Game library to pass through to the login screen */
    private final GameDatabase gameLibrary;
    /** Path to the reviews XML, passed through to the login screen */
    private final String reviewsXMLPath;

    /**
     * Constructs the account creation screen and displays it.
     *
     * @param userDatabase the user database used for authentication
     * @param gameLibrary the game library that will be used for login screen
     * @param reviewsXMLPath the reviews file path that will be used by login screen
     */
    public AccountCreation(UserDatabase userDatabase, GameDatabase gameLibrary, String reviewsXMLPath)
    {
        this.userDatabase=userDatabase;
        this.gameLibrary=gameLibrary;
        this.reviewsXMLPath=reviewsXMLPath;
        JFrame frame = buildFrame();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Builds and configures the main application frame.
     *
     * @return the fully constructed JFrame for the account creation screen
     */
    private JFrame buildFrame()
    {
        JFrame frame = new JFrame("Account Creation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Outer background panel to fill the whole window with CREAM
        JPanel background=new JPanel(new GridBagLayout());
        background.setBackground(GUIColors.CREAM);
        background.setBorder(new EmptyBorder(60,60,60,60));
        frame.add(background);
        background.add(buildCard(frame)); // white rounded card in center of screen
        return frame;
    }

    /**
     * Builds the central account creation card panel containing all UI components
     *
     * @param frame frame used for dialog interactions
     *
     * @return the fully constructed account creation panel
     */
    private JPanel buildCard(JFrame frame)
    {
        JPanel accountCreationPanel = new RoundedPanel(20, GUIColors.LIGHT);
        accountCreationPanel.setLayout(new BoxLayout(accountCreationPanel, BoxLayout.Y_AXIS));
        accountCreationPanel.setBorder(new EmptyBorder(100,150,100,150));
        accountCreationPanel.setPreferredSize(new Dimension(1500,900));

        //welcome screen for creating account
        JLabel welcomeCreationTag = new JLabel("Account Creation");
        welcomeCreationTag.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeCreationTag.setFont(new Font("Arial Black", Font.BOLD,30));
        welcomeCreationTag.setForeground(GUIColors.DARK);

        //username field
        JLabel userLabel= new JLabel("Username");
        userLabel.setFont(new Font("Arial Black", Font.BOLD, 11));
        userLabel.setForeground(GUIColors.DARK);

        //password tag
        JLabel passLabel= new JLabel("Password");
        passLabel.setFont(new Font("Arial Black", Font.BOLD, 11));
        passLabel.setForeground(GUIColors.DARK);

        // confirm password field
        JLabel confirmLabel= new JLabel("Confirm Password");
        confirmLabel.setFont(new Font("Arial Black", Font.BOLD, 11));
        confirmLabel.setForeground(GUIColors.DARK);

        usernameField = new JTextField(80);
        passwordField = new JPasswordField(80);
        confirmField = new JPasswordField(80);

        //status label
        statusLabel=new JLabel(" ");
        statusLabel.setFont(new Font("Arial Black",Font.PLAIN,11));
        statusLabel.setForeground(GUIColors.ERR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //login button
        JButton createAccountButton = new RoundedButton("Create Account",290,50);
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        //create action listener for createAccountButton
        createAccountButton.addActionListener(event->
        {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmField.getPassword());
            // check empty fields FIRST, before hitting the database
            if (username.isEmpty() || password.isEmpty())
            {
                statusLabel.setText("Please fill in both fields.");
                statusLabel.revalidate();
                statusLabel.repaint();
            }
            else if (username.length()>=15)
            {
                statusLabel.setText("Username must be no more than 15 characters.");
                statusLabel.revalidate();
                statusLabel.repaint();
            }
            else if (password.length()<3 || password.length()>=25)
            {
                statusLabel.setText("Password must be 4-25 characters");
                statusLabel.revalidate();
                statusLabel.repaint();
            }
            else if (!password.equals(confirmPassword))
            {
                statusLabel.setText("Passwords do not match.");
                statusLabel.revalidate();
                statusLabel.repaint();
            }
            else if (userDatabase.isUsernameTaken(username))
            {
                statusLabel.setText("Username is taken.");
                statusLabel.revalidate();
                statusLabel.repaint();
            }
            else{
                //users are initalized to non-admins
                //userDatabase handles creating users internally
                userDatabase.addUser(username,password,false);
                frame.dispose();
                new LoginScreen(userDatabase,gameLibrary,reviewsXMLPath);
            }
        });

        //assemble login parts
        accountCreationPanel.add(welcomeCreationTag);
        accountCreationPanel.add(Box.createVerticalStrut(6));
        accountCreationPanel.add(leftAlign(userLabel));
        accountCreationPanel.add(Box.createVerticalStrut(10));
        accountCreationPanel.add(usernameField);
        accountCreationPanel.add(Box.createVerticalStrut(18));
        accountCreationPanel.add(leftAlign(passLabel));
        accountCreationPanel.add(Box.createVerticalStrut(6));
        accountCreationPanel.add(passwordField);
        accountCreationPanel.add(Box.createVerticalStrut(18));
        accountCreationPanel.add(leftAlign(confirmLabel));
        accountCreationPanel.add(Box.createVerticalStrut(6));
        accountCreationPanel.add(confirmField);
        accountCreationPanel.add(Box.createVerticalStrut(12));
        accountCreationPanel.add(createAccountButton);
        accountCreationPanel.add(Box.createVerticalStrut(6));
        accountCreationPanel.add(statusLabel);

        return accountCreationPanel;
    }

    /**
     * Wraps a component in a left-aligned panel for consistent layout.
     *
     * @param c the component to align
     * @return a JPanel containing the component aligned to the left
     */
    private JPanel leftAlign(JComponent c)
    {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height + 2));
        p.add(c);
        return p;
    }
}