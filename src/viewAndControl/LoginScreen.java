package viewAndControl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import data.GameDatabase;
import data.UserDatabase;
import model.User;


/**
 * Displays the login screen for the ABBG Board Games application.
 * Handles user authentication by validating credentials against the UserDatabase.
 * On successful login, closes the login window and opens the main screen.
 *
 */

public class LoginScreen
{
    /** Displays error or validation messages to the user. */
    private JLabel         statusLabel;
    /** Input field for the user's username. */
    private JTextField     usernameField;
    /** Input field for the user's password, masking characters as typed. */
    private JPasswordField passwordField;
    /** The user database used to validate login credentials. */
    private final UserDatabase userDatabase;
    /** model.Game Library to load in for the main screen */
    private final GameDatabase gameLibrary;
    /** Reviews to load in for the main screen */
    private final String reviewsXMLPath;

    /**
     * Constructs the login screen and displays it.
     *
     * @param userDatabase the user database used for authentication
     */
    public LoginScreen(UserDatabase userDatabase, GameDatabase gameLibrary, String reviewsXMLPath)
    {
        this.userDatabase=userDatabase;
        this.gameLibrary = gameLibrary;
        this.reviewsXMLPath = reviewsXMLPath;

        JFrame frame=buildFrame(); //build login screen first
        frame.pack(); //size the window
        frame.setLocationRelativeTo(null); //center it
        frame.setVisible(true);
    }
    /**
     * Builds and configures the main application frame.
     *
     * @return the fully constructed JFrame for the login screen
     */
    private JFrame buildFrame()
    {
        JFrame frame= new JFrame("ABB Games - Login");
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
     * Builds the central login card panel containing all UI components.
     *
     * @param frame the parent frame used for dialog interactions
     * @return the constructed login panel
     */
    private JPanel buildCard(JFrame frame)
    {
        //The main container panel holding all login UI components.
        JPanel loginPanel = new RoundedPanel(20, GUIColors.LIGHT);
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(new EmptyBorder(60,150,60,150));
        loginPanel.setPreferredSize(new Dimension(1500,900));

        //welcome sign
        JLabel subtitle=new JLabel("Welcome To");
        subtitle.setFont(new Font("Montserrat", Font.PLAIN, 13));
        subtitle.setForeground(GUIColors.DARK);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        //welcome tag
        //Displays the application title
        JLabel welcomeTag = new JLabel("ABB Games");
        welcomeTag.setFont(new Font("Arial Black", Font.BOLD, 22));
        welcomeTag.setForeground(GUIColors.DARK);
        welcomeTag.setAlignmentX(Component.CENTER_ALIGNMENT);

        //username field
        JLabel userLabel= new JLabel("Username");
        userLabel.setFont(new Font("Arial Black", Font.BOLD, 11));
        userLabel.setForeground(GUIColors.DARK);

        //password tag
        JLabel passLabel= new JLabel("Password");
        passLabel.setFont(new Font("Arial Black", Font.BOLD, 11));
        passLabel.setForeground(GUIColors.DARK);

        usernameField = new JTextField(80);
        passwordField = new JPasswordField(80);

        //create account link
        JLabel createAccount=new JLabel("Create Account");
        createAccount.setFont(new Font("Arial Black", Font.PLAIN,11));
        createAccount.setForeground(GUIColors.MID);
        createAccount.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createAccount.setAlignmentX(Component.LEFT_ALIGNMENT);
        createAccount.addMouseListener(new MouseAdapter()
        {
            public void mouseEntered(MouseEvent event)
            {
                createAccount.setForeground(GUIColors.DARK);
            }
            public void mouseExited(MouseEvent event)
            {
                createAccount.setForeground(GUIColors.MID);
            }
            public void mouseClicked(MouseEvent event)
            {
                frame.dispose();
                new AccountCreation(userDatabase, gameLibrary, reviewsXMLPath);
            }
        });

        //status label
        statusLabel=new JLabel(" ");
        statusLabel.setFont(new Font("Arial Black",Font.PLAIN,11));
        statusLabel.setForeground(GUIColors.ERR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //login button
        JButton loginButton = new RoundedButton("Log In", 290, 50);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        //create action listener for loginbutton
        loginButton.addActionListener(event->
        {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            // check empty fields FIRST, before hitting the database
            if (username.isEmpty() || password.isEmpty())
            {
                statusLabel.setText("Please fill in both fields.");
                statusLabel.revalidate();
                statusLabel.repaint();
            }
            else
            {
                User user = userDatabase.validateCredentials(username, password);

                if (user != null)
                {
                    frame.dispose();
                    new MainFrame(gameLibrary, userDatabase, user, reviewsXMLPath).setVisible(true);
                }
                else
                {
                    statusLabel.setText("Invalid username or password.");
                    statusLabel.revalidate();
                    statusLabel.repaint();
                    passwordField.setText("");
                }
            }
        });

        passwordField.addActionListener(loginButton.getActionListeners()[0]);

        //assemble login parts
        loginPanel.add(Box.createVerticalStrut(4));
        loginPanel.add(subtitle);
        loginPanel.add(Box.createVerticalStrut(4));
        loginPanel.add(welcomeTag);
        loginPanel.add(Box.createVerticalStrut(6));
        loginPanel.add(leftAlign(userLabel));
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(usernameField);
        loginPanel.add(Box.createVerticalStrut(18));
        loginPanel.add(leftAlign(passLabel));
        loginPanel.add(Box.createVerticalStrut(6));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(8));
        loginPanel.add(leftAlign(createAccount));
        loginPanel.add(Box.createVerticalStrut(8));
        loginPanel.add(loginButton);
        loginPanel.add(Box.createVerticalStrut(6));
        loginPanel.add(statusLabel);

        return loginPanel;
    }
    /**
     * Wraps a component in a left-aligned panel for consistent layout.
     *
     * @param component the component to align
     * @return a JPanel containing the component aligned to the left
     */
    private JPanel leftAlign(JComponent component)
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height + 2));
        panel.add(component);
        return panel;
    }
}
