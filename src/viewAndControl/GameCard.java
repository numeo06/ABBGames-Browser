package viewAndControl;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

import data.GameDatabase;
import model.Game;
import model.User;
import model.UserCollection;

public class GameCard extends JPanel{

    private final Game game;
    private final RoundedPanel card;

    /**
     * Constructs a GameCard component displaying a game's thumbnail, name, and action button.
     * Loads the game thumbnail asynchronously
     * The action button behavior differs based on whether the card is in a collection view or master database view.
     *
     * @param game         the game to display
     * @param isCollection true if this card is displayed within a model.UserCollection, false for master database
     * @param user         the logged-in user, used for collection dialog
     * @param gameDB       the data.GameDatabase or model.UserCollection this card belongs to
     * @param onRemove     callback fired when a game is successfully removed from a collection
     * @param onClick      callback fired when the card itself is clicked
     */
    public GameCard(Game game, boolean isCollection, User user, GameDatabase gameDB, Runnable onRemove, Runnable onClick)
    {
        this.game = game;
        card = new RoundedPanel(10,GUIColors.CREAM);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(175, 210));
        card.setMinimumSize(new Dimension(175, 210));
        card.setPreferredSize(new Dimension(175, 210));

        JLabel imageLabel = new JLabel("Loading...", SwingConstants.CENTER);
        imageLabel.setForeground(GUIColors.DARK);
        imageLabel.setForeground(GUIColors.DARK);
        imageLabel.setFont(new Font("Arial Black", Font.PLAIN, 10));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        new SwingWorker<ImageIcon, Void>()
        {
            protected ImageIcon doInBackground() throws Exception
            {
                URL url = new URL(game.getThumbnailURL());
                BufferedImage img = ImageIO.read(url);
                BufferedImage scaled = new BufferedImage(130, 130, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaled.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawImage(img, 0, 0, 130, 130, null);
                g2d.dispose();
                return new ImageIcon(scaled);
            }

            protected void done()
            {
                try
                {
                    imageLabel.setText("");
                    imageLabel.setIcon(get());
                }
                catch (Exception e)
                {
                    imageLabel.setText("No Image");
                }
            }
        }.execute();

        JLabel nameLabel = new JLabel(truncate(game.getName()), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial Black", Font.BOLD, 12));
        nameLabel.setForeground(GUIColors.DARK);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundedButton actionButton = new RoundedButton(isCollection ? "Remove" : "Add", 130, 25);
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        actionButton.addActionListener(e ->
        {
            if (isCollection)
            {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Remove \"" + game.getName() + "\" from collection?",
                        "Confirm Remove",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION)
                {
                    ((UserCollection) gameDB).removeGame(game);
                    onRemove.run();
                }
            }
            else
            {
                new CollectionDialog(null, game, user, true).setVisible(true);
            }
        });

        // stop click from bubbling to card mouse listener
        actionButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e)
            {
                e.consume();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e)
            {
                e.consume();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e)
            {
                e.consume();
            }
        });

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e)
            {
                onClick.run();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e)
            {
                card.setBackground(GUIColors.LIGHT);
                card.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e)
            {
                card.setBackground(GUIColors.CREAM);
                card.repaint();
            }
        });

        card.add(Box.createVerticalStrut(5));
        card.add(imageLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(actionButton);
        card.add(Box.createVerticalStrut(5));

        setLayout(new BorderLayout());
        add(card);
    }

    /**
     * Returns the game associated with this card.
     *
     * @return the game object
     */
    public Game getGame()
    {
        return game;
    }

    /**
     * Truncates a string to a maximum of 18 characters, appending "..." if truncated.
     *
     * @param text the string to truncate
     * @return the truncated string
     */
    private String truncate(String text)
    {
        if (text.length() > 18)
            return text.substring(0, 15) + "...";
        return text;
    }

}