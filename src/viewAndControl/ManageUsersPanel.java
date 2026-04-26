package viewAndControl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

import data.UserDatabase;
import model.User;

/**
 * ManageUsersPanel displays the Users available in the UserDatabase
 * It shows their username and on the right side has the options for promote/demote based
 * on the current admin privileges and also a delete functionality.
 */
public class ManageUsersPanel extends JPanel
{
    /** The userDatabase to reference the current users in the panel*/
    private final UserDatabase userDatabase;
    /** The currentUser with access to admin privileges*/
    private final User currentUser;
    /** The scrollPane wraps the user list panel */
    private JScrollPane scrollPane;

    /**
     * Constructs the ManageUsersPanel with access to the given UserDatabase.
     *
     * @param userDatabase the database containing all users
     */
    public ManageUsersPanel(UserDatabase userDatabase,User currentUser)
    {
        this.userDatabase = userDatabase;
        this.currentUser = currentUser;
        this.setLayout(new BorderLayout());
        this.setBackground(GUIColors.MID);

        //Header panel containing title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(50, 80));
        headerPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        //Title
        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 22));
        titleLabel.setForeground(GUIColors.MID);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        this.add(headerPanel, BorderLayout.NORTH);

        //build the user list display
        buildUserList();
    }

    /**
     * Build the user list by retrieving all users from the database
     * and creating a row for each user with action buttons (demoting/promoting, deleting).
     */
    private void buildUserList()
    {
        // User List
        ArrayList<User> users = userDatabase.getAllUsers();

        //create list panel
        //The listPanel displays the list of usernames and their admin privileges
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(GUIColors.MID);
        listPanel.setBorder(new EmptyBorder(20,40,20,40));

        //create row for each user
        for (User user:users)
        {
            RoundedPanel userRow = new RoundedPanel(10, GUIColors.CREAM);
            userRow.setLayout(new BorderLayout());
            userRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            userRow.setBorder(new EmptyBorder(10, 15, 10, 15));

            JLabel nameLabel = new JLabel(user.getUsername());
            nameLabel.setFont(new Font("Arial Black", Font.PLAIN, 14));
            nameLabel.setForeground(GUIColors.DARK);

            // Buttons panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setBackground(GUIColors.CREAM);

            //determine if demote or promote user's admin ability
            String buttonText = user.getIsAdmin() ? "Demote": "Promote";
            RoundedButton promoteButton = new RoundedButton(buttonText, 150 ,50);
            promoteButton.setFont(new Font("Arial Black", Font.PLAIN, 12));
            promoteButton.addActionListener(event ->
            {
                userDatabase.setAdmin(user.getUsername(),!user.getIsAdmin());
                refresh(); //rebuild screen to show the changes
            });

            RoundedButton deleteButton=new RoundedButton("Delete", 150, 50);
            deleteButton.setFont(new Font("Arial Black", Font.PLAIN, 12));
            deleteButton.addActionListener(event ->
            {
                // Confirm deletion
                int confirmDelete=JOptionPane.showConfirmDialog(
                        this,
                        "Delete user \"" + user.getUsername() + "\"?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmDelete==JOptionPane.YES_OPTION)
                {
                    //userDatabase.deleteUser(user.getUsername());
                    //Admin admin = (Admin) currentUser;
                    userDatabase.deleteUser(user.getUsername());
                    //admin.deleteUser(user.getUsername(), userDatabase);
                    refresh();
                }
            });
            buttonPanel.add(promoteButton);
            buttonPanel.add(deleteButton);

            userRow.add(nameLabel, BorderLayout.WEST);
            userRow.add(buttonPanel, BorderLayout.EAST);

            listPanel.add(userRow);
            listPanel.add(Box.createVerticalStrut(10));
        }
        //wrap user list panel in scroll pane
        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add to the panel
        this.add(scrollPane, BorderLayout.CENTER);
    }
    /**
     * Refreshes the panel by rebuilding the user list UI.
     * Called after any change (promote/demote/delete).
     */
    public void refresh()
    {
        if (scrollPane != null)
        {
            this.remove(scrollPane); //remove old list
        }
        buildUserList(); //rebuild with new user list
        revalidate();
        repaint();
    }

}