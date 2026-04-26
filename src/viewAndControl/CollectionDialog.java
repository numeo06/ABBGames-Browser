package viewAndControl;

import model.Game;
import model.User;
import model.UserCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * A dialog window used for managing a user's game collections.
 * It allows the user to either add a game to one of their collections
 * or remove it if it is already inside one.
 * When adding, the user can also create a new collection directly from the dialog.
 */
public class CollectionDialog extends JDialog
{
    /**
     * Constructs a CollectionDialog for adding or removing a game from a user's collections.
     * Displays a scrollable list of the user's collections as buttons.
     * Includes a "New" button when adding to allow creation of a new collection on the fly.
     *
     * @param parent   the parent component for positioning the dialog, null centers on screen
     * @param game     the game to add or remove
     * @param user     the logged-in user whose collections are displayed
     * @param isAdding true if adding the game to a collection, false if removing
     */
    public CollectionDialog(Component parent, Game game, User user, boolean isAdding)
    {
        setModal(true);
        setUndecorated(true);
        setSize(300, 400);
        setLocationRelativeTo(parent);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(GUIColors.DARK);
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Add to Collection");
        title.setFont(new Font("Arial Black", Font.BOLD, 16));
        title.setForeground(GUIColors.LIGHT);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(GUIColors.DARK);

        ArrayList<UserCollection> collections = user.getCollections();

        for (UserCollection collection : collections)
        {
            RoundedButton collectionActionButton = new RoundedButton(collection.getName(), 50, 35);
            collectionActionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            collectionActionButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            //if the collection already contains the game disable the collectionActionButton
            // adding: disable the ability to add game if already in the collection
            if (isAdding && collection.containsGame(game))
            {
                collectionActionButton.setEnabled(false);
            }
            // removing: disable the ability to remove game if the game is not in the collection
            else if (!isAdding && !collection.containsGame(game))
            {
                collectionActionButton.setEnabled(false);
            }
            // otherwiseaction is valid and user can add/remove game
            else
            {
                collectionActionButton.addActionListener(event ->
                {
                    if (isAdding)
                    {
                        collection.addGame(game);
                        dispose();
                        JOptionPane.showMessageDialog(parent,
                                game.getName() + " added to " + collection.getName());
                    }
                    else
                    {
                        int confirm = JOptionPane.showConfirmDialog(
                                parent,
                                "Remove \"" + game.getName() + "\" from " + collection.getName() + "?",
                                "Confirm Remove",
                                JOptionPane.YES_NO_OPTION
                        );
                        if (confirm == JOptionPane.YES_OPTION)
                        {
                            collection.removeGame(game);
                            dispose();
                            JOptionPane.showMessageDialog(parent,
                                    game.getName() + " removed from " + collection.getName());
                        }
                    }
                });
            }

            listPanel.add(collectionActionButton);
            listPanel.add(Box.createVerticalStrut(6));
        }

        JScrollPane listScroll = new JScrollPane(listPanel);
        listScroll.setBorder(null);
        listScroll.getViewport().setBackground(GUIColors.DARK);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(GUIColors.DARK);

        RoundedButton cancelButton = new RoundedButton("Cancel", 90, 25);
        cancelButton.addActionListener(event -> dispose());
        bottomPanel.add(cancelButton);

        // only show + Add button when adding
        if (isAdding)
        {
            RoundedButton newCollectionButton = getRoundedButton(parent, game, user);
            bottomPanel.add(cancelButton);
            bottomPanel.add(newCollectionButton);
        }

        content.add(title, BorderLayout.NORTH);
        content.add(listScroll, BorderLayout.CENTER);
        content.add(bottomPanel, BorderLayout.SOUTH);

        add(content);
    }

    /**
     * Creates the "+ New" button used when adding a game to a collection.
     * When clicked, it opens another small window where the user can type
     * a name for a new collection. The game is then added to it right away.
     *
     * @param parent the parent component used for positioning dialogs
     * @param game   the game that will be added to the new collection
     * @param user   the user creating the collection
     * @return a button that opens the new collection dialog
     */
    private RoundedButton getRoundedButton(Component parent, Game game, User user) {
        RoundedButton newCollectionButton = new RoundedButton("+ New", 90, 25);
        newCollectionButton.addActionListener(event ->
        {
            dispose();

            JDialog nameDialog = new JDialog();
            nameDialog.setModal(true);
            nameDialog.setUndecorated(true);
            nameDialog.setSize(280, 160);
            nameDialog.setLocationRelativeTo(parent);

            JPanel nameContent = new JPanel();
            nameContent.setLayout(new BoxLayout(nameContent, BoxLayout.Y_AXIS));
            nameContent.setBackground(GUIColors.DARK);
            nameContent.setBorder(new EmptyBorder(20, 20, 20, 20));

            JLabel nameLabel = new JLabel("Collection Name");
            nameLabel.setForeground(GUIColors.LIGHT);
            nameLabel.setFont(new Font("Arial Black", Font.PLAIN, 13));
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField nameField = new JTextField();
            nameField.setBackground(GUIColors.MID);
            nameField.setForeground(GUIColors.LIGHT);
            nameField.setCaretColor(GUIColors.LIGHT);
            nameField.setBorder(new EmptyBorder(5, 8, 5, 8));
            nameField.setFont(new Font("Arial Black", Font.PLAIN, 13));
            nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
            nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

            JPanel nameButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            nameButtons.setBackground(GUIColors.DARK);

            RoundedButton confirmButton = new RoundedButton("Create", 100, 30);
            RoundedButton cancelButton = new RoundedButton("Cancel", 100, 30);

            // Create a new collection from user input and add the selected game to it.
            // If the input name is valid new collection is created+game is added to it.
            // The dialog is then closed regardless of input validity.
            confirmButton.addActionListener(eventConfirm ->
            {
                String newName = nameField.getText().trim();
                if (!newName.isEmpty())
                {
                    user.createCollection(newName);
                    UserCollection newCol = user.getCollections().getLast();
                    newCol.addGame(game);
                    JOptionPane.showMessageDialog(parent, game.getName() + " added to " + newName);
                }
                nameDialog.dispose();
            });

            cancelButton.addActionListener(eventCancel -> nameDialog.dispose());

            nameButtons.add(cancelButton);
            nameButtons.add(confirmButton);

            nameContent.add(nameLabel);
            nameContent.add(Box.createVerticalStrut(8));
            nameContent.add(nameField);
            nameContent.add(Box.createVerticalStrut(10));
            nameContent.add(nameButtons);

            nameDialog.add(nameContent);
            nameDialog.setVisible(true);
        });
        return newCollectionButton;
    }
}
