package viewAndControl;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import model.Review;
import model.Game;
import data.GameDatabase;
import data.FileScannerXML;
import model.User;

/**
 * GameDescriptionPanel displays information about a selected board game.
 * It shows the game image, game info (name, year, players, description, categories, mechanics),
 * a list of user reviews, and a form to submit a new review.
 * Users can also add the game to one of their collections by a dialog.
 * Design Pattern:
 *  Observer Pattern (Java Swing)
 *  Subject: addReviewButton,saveReviewButon,cancelReviewButton
 *  Observer: These buttons use ActionListener instances
 *  When a button is clicked it notifies registered ActionListeners,
 *  triggering the corresponding event-handling logic.
 */

public class GameDescriptionPanel extends JPanel
{
    /** Displays the game's cover image. */
    private final JLabel imageLabel;
    /** Displays the game name. */
    private final JLabel nameLabel;
    /** Displays the player count range. */
    private final JLabel playerLabel;
    /** Displays the year the game was published. */
    private final JLabel yearLabel;
    /** Displays the game description. */
    private final JTextArea description;
    /** Displays the game's categories. */
    private final JLabel categoriesLabel;
    /** Displays the game's mechanics. */
    private final JLabel mechanicsLabel;
    /** Displays the list of reviews for the current game. */
    private final JList<Review> reviewList;
    /** Stores the 5 rating radio buttons so they can be looped over on save. */
    private final JRadioButton [] ratingButtons;
    /** The game currently being displayed. Updated via setDisplayedGame. */
    private Game currentGame;
    /** Placeholder for GameDatabase ( used for file scanner)*/
    private final GameDatabase holderGameDB=null;

    /**
     * Constructs a viewAndControl.GameDescriptionPanel for the given user.
     * Builds the full layout including image area, game info scroll panel,
     * reviews card, and review form card with a CardLayout switcher.
     *
     * @param currentUser     the logged-in user
     * @param reviewsXMLPath  file path to the reviews XML file
     */
    public GameDescriptionPanel(User currentUser, String reviewsXMLPath)
    {
        /*
         * Comments about the Panels
         * Left Side of Screen: Image + Reviews
         * Right Side: Game Information
         * Bottom: Holds review list and the form
         */

        //The logged-in user interacting with this panel.
        this.ratingButtons= new JRadioButton[5];

        // components for model.Review Form
        JLabel userField= new JLabel(currentUser.getUsername());
        userField.setFont(new Font("Arial Black", Font.PLAIN, 13));
        userField.setForeground(GUIColors.DARK);
        userField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel textLabel= new JLabel("Review");
        textLabel.setFont(new Font("Arial Black", Font.PLAIN, 13));
        textLabel.setForeground(GUIColors.LIGHT);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea textField= new JTextArea();
        textField.setBackground(GUIColors.CREAM);
        textField.setForeground(GUIColors.DARK);
        textField.setFont(new Font("Arial Black", Font.PLAIN, 13));
        textField.setLineWrap(true);
        textField.setWrapStyleWord(true);
        textField.setRows(4); //set starting height for text field, will expand as user types if needed

        // ratingRow holds the radio buttons visually
        // ratingButtons[] holds references for reading selection
        JLabel ratingLabel= new JLabel("Rating (1-5)");
        ratingLabel.setFont(new Font("Arial Black", Font.PLAIN, 13));
        ratingLabel.setForeground(GUIColors.LIGHT);
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel ratingRow = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        ratingRow.setBackground(GUIColors.DARK);
        ratingRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        //create 5 buttons to select rating
        ButtonGroup ratingGroup = new ButtonGroup();
        for (int i = 1; i <= 5; i++) {
            JRadioButton btn = new JRadioButton(String.valueOf(i));
            btn.setForeground(GUIColors.LIGHT);
            btn.setOpaque(false);
            ratingGroup.add(btn);
            ratingButtons[i-1] = btn; // store in array
            ratingRow.add(btn);       // add to panel
        }
        setLayout(new BorderLayout(20,0));
        setBackground(GUIColors.MID);

        //wrap each box into JPanel for lighter color background
        //username displayed in a lighter box
        JPanel userLabelBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        userLabelBox.setBackground(GUIColors.CREAM);
        userLabelBox.add(userField);// lighter than DARK
        userLabelBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        userLabelBox.setMaximumSize(new Dimension(userLabelBox.getPreferredSize().width, userLabelBox.getPreferredSize().height));

        // wrap the text area directly, expands with content
        JPanel reviewBox = new JPanel(new BorderLayout());
        reviewBox.setBackground(GUIColors.CREAM);
        reviewBox.add(textField, BorderLayout.CENTER); // text where ers will add review
        reviewBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        //MAIN LAYOUT
        //mainPanel contains layout
        JPanel mainPanel= new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        //wrap mainPanel in scrollPane
        JScrollPane mainScrollPane= new JScrollPane(mainPanel);

        //set left side of screen (hold image and reviews)
        JPanel leftPanel = new JPanel(new BorderLayout(0,8));
        leftPanel.setBackground(GUIColors.MID);

        //splits into left ( image + review ) and right (info scrolling)
        JPanel topPanel = new JPanel(new BorderLayout(10,0));
        topPanel.setBackground(GUIColors.MID);

        //card layout for review (switches between the list and adding review form)
        JPanel reviewsCard=new JPanel(new BorderLayout());
        //panel containing form for submitting new review
        JPanel formCard= new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(GUIColors.DARK);
        formCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        //fields for username review text, rating buttons, and cancel+save
        JPanel formFields = new JPanel();
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));
        formFields.setBackground(GUIColors.DARK);

        //panel for reviews
        CardLayout bottomLayout= new CardLayout();
        JPanel bottomPanel = new JPanel(bottomLayout);
        bottomPanel.setPreferredSize(new Dimension(0,350));

        //create image label
        imageLabel=new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        //review list
        reviewList=new JList<>();
        reviewList.setBackground(GUIColors.DARK);
        reviewList.setForeground(GUIColors.LIGHT);
        reviewList.setFont(new Font("Arial Black", Font.PLAIN, 13));
        reviewList.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane reviewScroll = new JScrollPane(reviewList);
        reviewScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        reviewScroll.setBorder(null);

        //BUTTONS
        RoundedButton addReviewButton = new RoundedButton("Add Review", 50, 45);
        addReviewButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        //set action listener for add review
        addReviewButton.addActionListener(event->{
            bottomLayout.show(bottomPanel, "form");
        });

        RoundedButton cancelReviewButton = new RoundedButton("Cancel", 35, 20);
        cancelReviewButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        //set action listener to return from review panel
        cancelReviewButton.addActionListener(event->{
            bottomLayout.show(bottomPanel,"Reviews");
        });

        RoundedButton saveReviewButton = new RoundedButton("Save", 35, 20);
        saveReviewButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        //set action listener to return from review panel
        saveReviewButton.addActionListener(event->
        {
            String reviewText= textField.getText().trim();
            //determine which button is selected
            int rating = -1;
            for (JRadioButton btn : ratingButtons) {
                if (btn.isSelected()) {
                    rating = Integer.parseInt(btn.getText());
                    break;
                }
            }
            if (reviewText.isEmpty() || rating == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a review and select a rating.",
                        "Incomplete Review",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Review newReview = new Review(currentGame.getID(), currentUser.getUsername(), rating, reviewText);
            newReview.writeReviewsXML(reviewsXMLPath);
            //refresh list to show new review by rereadign XML
            FileScannerXML reader = new FileScannerXML(new File(reviewsXMLPath), holderGameDB);
            ArrayList<Review> updated = reader.getReviewForGame(reviewsXMLPath, currentGame);
            reviewList.setListData(updated.toArray(new Review[0]));

            bottomLayout.show(bottomPanel,"Reviews");
        });

        //GAME INFO PANEL
        //set right hand side of screen (hold all game info)
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(GUIColors.DARK);
        infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.Y_AXIS));
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        //wrap infoPanel in a scroll to fit description
        JPanel infoWrapper = new JPanel(new BorderLayout());
        infoWrapper.setBackground(GUIColors.DARK);
        infoWrapper.add(infoPanel, BorderLayout.NORTH); //put north to take preferred height inside scroll

        //scroll pane used to wrap game info panel
        JScrollPane infoScroll = new JScrollPane(infoWrapper);
        infoScroll.getViewport().setBackground(GUIColors.DARK);
        infoScroll.setPreferredSize(new Dimension(500, 0));
        infoScroll.setBorder(null);
        infoScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        infoScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        nameLabel = new JLabel("Game");
        nameLabel.setFont(new Font("Arial Black", Font.BOLD,22));
        nameLabel.setForeground(GUIColors.LIGHT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        yearLabel = new JLabel("Published: ?");
        yearLabel.setFont(new Font("Arial Black", Font.PLAIN, 14));
        yearLabel.setForeground(GUIColors.LIGHT);
        yearLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        playerLabel = new JLabel("Players: ? - ?");
        playerLabel.setFont(new Font("Arial Black", Font.PLAIN, 14));
        playerLabel.setForeground(GUIColors.LIGHT);
        playerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        //description box
        description= new JTextArea("Description");
        description.setFont(new Font("Arial Black", Font.PLAIN,13));
        description.setForeground(GUIColors.LIGHT);
        description.setBackground(GUIColors.DARK);
        //auto line size of description area depending on text
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setEditable(false);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        //set categories and mechanics on page
        categoriesLabel = new JLabel("Categories");
        categoriesLabel.setFont(new Font("Arial Black", Font.PLAIN, 13));
        categoriesLabel.setForeground(GUIColors.LIGHT);
        categoriesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        mechanicsLabel = new JLabel("Mechanics");
        mechanicsLabel.setFont(new Font("Arial Black", Font.PLAIN, 13));
        mechanicsLabel.setForeground(GUIColors.LIGHT);
        mechanicsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        //button to add game to collection+ dialog
        RoundedButton addGameButton = new RoundedButton("Add Game", 200, 45);
        addGameButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addGameButton.addActionListener(event ->
                new CollectionDialog(this, currentGame, currentUser, true).setVisible(true));

        // put every piece together
        bottomPanel.add(reviewsCard, "Reviews");
        bottomPanel.add(formCard, "form");

        leftPanel.add(imageLabel, BorderLayout.CENTER);
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        topPanel.add(leftPanel, BorderLayout.CENTER);
        topPanel.add(infoScroll, BorderLayout.EAST);

        reviewsCard.add(reviewScroll, BorderLayout.CENTER);
        reviewsCard.add(addReviewButton, BorderLayout.NORTH);

        mainPanel.add(topPanel);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(playerLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(yearLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(description);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(categoriesLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(mechanicsLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(addGameButton);

        formFields.add(userLabelBox);
        formFields.add(Box.createVerticalStrut(4));
        formFields.add(textLabel);
        formFields.add(reviewBox);
        formFields.add(Box.createVerticalStrut(4));
        formFields.add(ratingLabel);
        formFields.add(ratingRow);
        formFields.add(Box.createVerticalStrut(4));
        formFields.add(cancelReviewButton);
        formFields.add(Box.createVerticalStrut(4));
        formFields.add(saveReviewButton);

        formCard.add(formFields);
        add(mainScrollPane);
    }

    /**
     * Updates the panel to display the given game and its reviews.
     * Loads the game image through SwingWorker
     *
     * @param game    the game to display
     * @param reviews the list of reviews for the game
     */
    public void setDisplayedGame(Game game, ArrayList<Review> reviews)
    {
        this.currentGame=game;

        nameLabel.setText(game.getName());
        yearLabel.setText("Published: " + game.getYearPublished());
        playerLabel.setText("Players: " + game.getMinPlayer() + " - " + game.getMaxPlayer());
        description.setText(game.getDescription());
        categoriesLabel.setText("Categories: " + String.join(", ", game.getBgCategories()));
        mechanicsLabel.setText("Mechanics: " + String.join(", ", game.getBgMechanics()));
        reviewList.setListData(reviews.toArray(new Review[0])); // needed to populate reviews in JList
        // image loading from viewAndControl.GameBrowserPanel
        imageLabel.setText("Loading...");
        imageLabel.setIcon(null);
        // load image on a background thread so the UI stays responsive
        new SwingWorker<ImageIcon, Void>()
        {
            protected ImageIcon doInBackground() throws Exception
            {
                URL url = new URL(game.getImageURL());
                BufferedImage img = ImageIO.read(url);
                Image scaled = img.getScaledInstance(600, 600, Image.SCALE_DEFAULT);
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
                    imageLabel.setText("Image failed to load");
                }
            }
        }.execute();
    }
}

