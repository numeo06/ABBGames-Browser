package viewAndControl;

import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * SidePanel is used for quick navigation and user identifications.
 * Provides quick access to buttons for ore navigations(Home,Library
 * Settings, Logout). It holds the user's username and profile photo.
 */
public class SidePanel extends JPanel
{
    /** Button to send the user to their library */
    private JButton libraryButton;
    /** Button to send the user to the settings menu */
    private JButton settingsButton;
    /** Button to send the user to logout */
    private JButton logoutButton;
    /** Button to send the uesr to the main screen */
    private JButton homeButton;
    /** Label displaying profile picture */
    private JLabel profilePictureLabel;
    /** Reference to the main application frame for navigation actions */
    private final MainFrame mainFrame;
    /** The currently logged-in user whose data is displayed */
    private final User currentUser;

    /**
     * Constructs the SidePanel and initializes all UI components.
     *
     * @param mainFrame   reference to the main application frame used for navigation
     * @param currentUser the currently logged-in user whose data is displayed
     */
    public SidePanel(MainFrame mainFrame, User currentUser)
    {
        //this.currentUser=currentUser;
        this.currentUser=currentUser;
        this.mainFrame=mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(GUIColors.LIGHT);
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setPreferredSize(new Dimension(200, 900));

        buildComponents();
    }
    /**
     * Builds and arranges all UI components inside the sidebar,
     * including profile display and navigation buttons.
     */
    private void buildComponents()
    {
        //Profile Picture
        profilePictureLabel=new JLabel();
        profilePictureLabel.setPreferredSize(new Dimension(80, 80));
        profilePictureLabel.setMaximumSize(new Dimension(80, 80));
        profilePictureLabel.setMinimumSize(new Dimension(80, 80));
        profilePictureLabel.setBorder(BorderFactory.createLineBorder(GUIColors.DARK, 2));
        profilePictureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profilePictureLabel.setOpaque(true);
        profilePictureLabel.setBackground(GUIColors.CREAM);
        profilePictureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username label
        JLabel userLabel = new JLabel(currentUser.getUsername());
        userLabel.setFont(new Font("Arial Black", Font.BOLD, 18));
        userLabel.setForeground(GUIColors.DARK);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // home button
        homeButton = new RoundedButton("Home", 40, 40);
        homeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        homeButton.addActionListener(e -> mainFrame.navigateHome());

        // library button
        libraryButton = new RoundedButton("Library", 40, 40);
        libraryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        libraryButton.addActionListener(e -> mainFrame.navigateLibrary());

        // settings button
        settingsButton = new RoundedButton("Settings", 20, 10);
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsButton.addActionListener(e -> mainFrame.navigateSettings());

        // logout button
        logoutButton = new RoundedButton("Logout", 20, 10);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> mainFrame.logout());

        add(Box.createVerticalStrut(20));
        add(profilePictureLabel);
        add(Box.createVerticalStrut(10));
        add(userLabel);
        add(Box.createVerticalStrut(6));
        add(homeButton);
        add(Box.createVerticalStrut(600));
        add(settingsButton);
        add(Box.createVerticalStrut(6));
        add(libraryButton);
        add(Box.createVerticalStrut(6));
        add(logoutButton);

        //load profile photo
        loadProfilePicture();
    }
    /**
     * Loads the user's profile picture from user's computer and scales it to fit the UI.
     * If no valid image exists, a placeholder is displayed instead.
     */
    private void loadProfilePicture()
    {
        String picturePath=currentUser.getProfilePicturePath();
        if (picturePath!=null && !picturePath.isEmpty())
        {
            File picFile=new File(picturePath);

            if (picFile.exists())
            {
                try {
                    ImageIcon icon=new ImageIcon(picturePath);
                    Image img=icon.getImage();
                    Image scaledImg=img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    profilePictureLabel.setIcon(new ImageIcon(scaledImg));
                    profilePictureLabel.setText("");
                    return;
                }
                catch (Exception e)
                {
                    System.err.println("Error loading profile picture in sidebar: " + e.getMessage());
                }
            }
            //if no image yet make a placeholder
            String placeHolder= ("<3");
            profilePictureLabel.setIcon(null);
            profilePictureLabel.setText(placeHolder);
            profilePictureLabel.setFont(new Font("Arial Black", Font.BOLD, 28));
            profilePictureLabel.setForeground(GUIColors.DARK);
        }
    }
    /**
     * Refreshes the profile picture display (call after uploading new picture)
     */
    public void refreshProfilePicture()
    {
        loadProfilePicture();
        revalidate();
        repaint();
    }
}