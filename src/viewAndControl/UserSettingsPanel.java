package viewAndControl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import data.UserDatabase;
import model.User;

/**
 * UserSettingsPanel allow current user to view
 * and update the profile pictures. If they have admin privileges, the user
 * will be given access to manage user accounts.
 * */
public class UserSettingsPanel extends JPanel {
    /** Reference to the main application frame*/
    private final MainFrame mainFrame;
    /**CurrentUser is current logged-in user whose settings are being edited */
    private final User currentUser;
    /** UserDatabase used to persist user updates ( in this for profile pic changes)*/
    private final UserDatabase userDatabase;
    /** UploadPictureButton used to upload a new profile picture */
    private JButton uploadPictureButton;
    /** PicturePreview label displaying the current profile picture */
    private JLabel picturePreview;

    /**
     * Constructs the settings panel and initializes all UI components.
     *
     * @param currentUser   the currently logged-in user
     * @param mainFrame     reference to main application frame for navigation
     * @param userDatabase  database used to persist user changes
     */
    public UserSettingsPanel(User currentUser, MainFrame mainFrame, UserDatabase userDatabase) {
        this.mainFrame = mainFrame;
        this.currentUser=currentUser;
        this.userDatabase=userDatabase;

        this.setLayout(new BorderLayout());
        this.setBackground(GUIColors.MID);

        //Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(GUIColors.DARK);
        headerPanel.setPreferredSize(new Dimension(50, 80));
        headerPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 22));
        titleLabel.setForeground(GUIColors.LIGHT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        //Username display
        RoundedPanel userCard = new RoundedPanel(10, GUIColors.CREAM);
        userCard.setLayout(new BorderLayout());
        userCard.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel userLabel = new JLabel("Logged in as: " + currentUser.getUsername());
        userLabel.setFont(new Font("Arial Black", Font.BOLD, 14));
        userLabel.setForeground(GUIColors.DARK);
        userCard.add(userLabel, BorderLayout.WEST);

        headerPanel.add(userCard, BorderLayout.EAST);

        this.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground((GUIColors.MID));
        contentPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        //make profile section on display
        JPanel profileSection=makeProfilePictureSection();
        contentPanel.add(profileSection, BorderLayout.NORTH);

        //display ManageUserButton only if user is model.Admin
        RoundedButton manageUsersButton = new RoundedButton("Manage Users", 150, 50);
        manageUsersButton.addActionListener(event ->
                mainFrame.navigateManageUsers()
        );

        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomRight.setBackground(GUIColors.MID);

        //display manageUsersButton
        if (currentUser.getIsAdmin()) {
            bottomRight.add(manageUsersButton);
        }

        contentPanel.add(bottomRight, BorderLayout.SOUTH);

        this.add(contentPanel, BorderLayout.CENTER);
    }
    /**
     * Creates the profile picture upload section including preview and upload button.
     *
     * @return JPanel containing profile picture UI components
     */
    private JPanel makeProfilePictureSection()
    {
        JPanel picturePanel=new JPanel(new FlowLayout(FlowLayout.LEFT,15,10));
        picturePanel.setBackground(GUIColors.MID);

        //picture preview
        picturePreview=new JLabel("No Picture");
        picturePreview.setPreferredSize(new Dimension(120,120));
        picturePreview.setBorder(BorderFactory.createLineBorder(GUIColors.DARK,2));
        picturePreview.setHorizontalAlignment(SwingConstants.CENTER);
        picturePreview.setOpaque(true);
        picturePreview.setBackground(GUIColors.LIGHT);

        //make picture upload button
        uploadPictureButton = new JButton("Upload Picture");
        uploadPictureButton.addActionListener(event ->
                handlePictureUpload());

        picturePanel.add(picturePreview);
        picturePanel.add(uploadPictureButton);

        loadCurrentPicture();
        return picturePanel;
    }

    /**
     * Allows the user to select an image file and saves it as their profile picture.
     * The selected file is copied into the application's "profile_pictures" folder,
     * and the user's profile picture path is updated in the database.
     */
    private void handlePictureUpload() {
        JFileChooser chooser = new JFileChooser();
        // Only allow image file types
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files (*.jpg, *jpeg,*.png)", "jpg", "jpeg", "png");
        chooser.setFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile(); //picture user wants

            try {
                //create pictures directory if it doesnt exist
                File picturesDirectory = new File("profile_pictures");
                if (!picturesDirectory.exists()) {
                    picturesDirectory.mkdir();
                }
                //generate filename from username
                String extension = getFileExtension(selected);
                String newFileName = currentUser.getUsername() + extension;
                File destinationFile = new File(picturesDirectory, newFileName);
                //copy file into folder
                Files.copy(selected.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                //update user object and store filepath
                currentUser.setProfilePicturePath(destinationFile.getPath());

                //save updated user info to database
                userDatabase.saveUsers();

                //update picture on UI
                loadCurrentPicture();
                mainFrame.refreshSidePanel();

                JOptionPane.showMessageDialog(this,
                        "Profile picture uploaded successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(this, "Error uploading picture: " + ex.getMessage(),
                        "Upload Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Extracts the file extension from a given file.
     *
     * @param file the file whose extension is needed
     * @return the file extension including dot or ".jpg" if unknown
     */
    private String getFileExtension(File file)
    {
        String fileName=file.getName();
        //find the last '.' in filename
        int lastDot=fileName.lastIndexOf('.');
        //make sure dot is not at the start or the end
        if (lastDot>0&&lastDot<fileName.length()-1)
        {
            return fileName.substring(lastDot); //get file extension type with dot
        }
        return ".jpg"; //default extension
    }

    /**
     * Loads and displays the current user's profile picture if it exists.
     * If no valid image is found, a placeholder is shown instead.
     */
    private void loadCurrentPicture()
    {
        //get stored file path from user
        String picturePath=currentUser.getProfilePicturePath();

        //check if picture path exists
        if (picturePath !=null && !picturePath.isEmpty())
        {
            File pictureFile=new File(picturePath);
            //check if file exists on disk
            if (pictureFile.exists())
            {
                try
                {
                    //load image
                    ImageIcon icon = new ImageIcon(picturePath);
                    //extract the image from the icon
                    Image image=icon.getImage();
                    //scale to fit preview
                    Image scaledImage=image.getScaledInstance(120,120,Image.SCALE_SMOOTH);
                    //set image into label
                    picturePreview.setIcon(new ImageIcon(scaledImage));
                    picturePreview.setText("");
                    return;
                }
                catch(Exception e)
                {
                    System.err.println("Error loading picture: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        // Show placeholder if no picture or error occurred
        picturePreview.setIcon(null);
        picturePreview.setText("No Picture");
    }
}

