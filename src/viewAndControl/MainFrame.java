package viewAndControl;

import data.FileScannerXML;
import data.GameDatabase;
import data.UserDatabase;
import model.Review;
import model.User;
import model.Game;
import model.UserCollection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * MainFrame controls navigation between the major screens. It acts as the central frame that holds reusable panels like the game browser,
 *  collection browser, settings, and game description views.
 *
 *  Design Pattern:
 *  Facade Patter-centralizes navigation logic and manages transitions between different UI views.
 */
public class MainFrame extends JFrame
{
    private final GameDatabase gameLibrary;
    private final UserDatabase userDatabase;
    private User currentUser;
    private final String reviewsXMLPath;

    //panel to switch screen
    private final JPanel currentScreen;

    //all the main panels that are reused
    private final GameBrowserPanel homePanel; //use to generate game browser panel for search and collection search
    private final CollectionBrowserPanel libraryBrowserPanel;
    private final GameDescriptionPanel gameDescriptionPanel;
    private final UserSettingsPanel userSettingsPanel;
    private final ManageUsersPanel manageUsersPanel;
    private final SidePanel sidePanel;

    /**
     * Creates the main application window and initializes all core panels.
     * Sets up navigation, loads the current user, and builds the persistent
     * UI components such as the side panel and reusable screens.
     *
     * @param gameLibrary     the database containing all games in the system
     * @param userDatabase    the database containing all users
     * @param currentUser     the user currently logged in
     * @param reviewsXMLPath  file path to the reviews XML data
     */
    public MainFrame(GameDatabase gameLibrary, UserDatabase userDatabase, User currentUser, String reviewsXMLPath)
    {
        this.gameLibrary=gameLibrary;
        this.userDatabase=userDatabase;
        this.currentUser=currentUser;
        this.reviewsXMLPath=reviewsXMLPath;

        setTitle("ABB Games");
        setSize(1500,940);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //create the current screen (first to display will be the main screen (search))
        currentScreen=new JPanel(new BorderLayout());
        currentScreen.setBackground(GUIColors.CREAM);

        //build the persisting panels
        homePanel=new GameBrowserPanel(gameLibrary,currentUser);
        homePanel.setCardListener(this::showGameDescription); //navigate to gameDescription

        libraryBrowserPanel= new CollectionBrowserPanel(currentUser);
        libraryBrowserPanel.setCardListener(this::navigateCollection); //navigate to collection screen

        gameDescriptionPanel=new GameDescriptionPanel(currentUser, reviewsXMLPath);
        this.userSettingsPanel= new UserSettingsPanel(currentUser, this,userDatabase);
        this.manageUsersPanel= new ManageUsersPanel(userDatabase,currentUser);

        //add side panel
        this.sidePanel = new SidePanel(this, currentUser);
        add(sidePanel, BorderLayout.EAST);
        add(currentScreen, BorderLayout.CENTER);

        navigateHome();
    }

    /**
     * keep UI up to date when updating profile photo
     * */
    public void refreshSidePanel()
    {
        sidePanel.refreshProfilePicture();
    }

    //Side Panel Navigations
    /**
     * Swap panels to the Home Screen
     * */
    public void navigateHome()
    {
        swapContent(homePanel);
    }
    /**
     * Swap panel to the Library screen and reload when collections is updated elsewhere in the program
     * */
    public void navigateLibrary()
    {
        libraryBrowserPanel.reloadCollections(); //update when collection is added from main/game description page
        swapContent(libraryBrowserPanel);
    }

    /**
     * Swap panel to the user settings
     * */
    public void navigateSettings()
    {
        swapContent(userSettingsPanel);
    }

    /**
     * Swap panel back to the login screen when a user logs out
     * */
    public void logout()
    {
        dispose();
        //bring users back to the login page
        currentUser=null;
        new LoginScreen(userDatabase,gameLibrary,reviewsXMLPath);
    }

    /**
     * Swap panel to the Game Descripition Page for the chosen game
     *
     * @param game The chosen game to display information for
     * */
    //Navigate through other panels
    public void showGameDescription(Game game)
    {
        //load game's reviews in
        FileScannerXML reader = new FileScannerXML(new File(reviewsXMLPath), gameLibrary);
        ArrayList<Review> reviews = reader.getReviewForGame(reviewsXMLPath,game);
        gameDescriptionPanel.setDisplayedGame(game, reviews);
        swapContent(gameDescriptionPanel);
    }
    /**
     * Swap panel to collection's screen and display its games
     *
     * @param collection The specific collection chosen by the user
     * */
    //create an updated panel per collection
    public void navigateCollection(UserCollection collection)
    {
        GameBrowserPanel collectionView = new GameBrowserPanel(collection,currentUser);
        collectionView.setCardListener(this::showGameDescription);
        swapContent(collectionView);
    }

    /**
     * Swap panels to the manageUsers privilege(given to admin)
     * */
    public void navigateManageUsers()
    {
        swapContent(manageUsersPanel);
    }

    /**
     * Reload the panels to display what the user swapped to
     *
     * @param panel The panel being swapped to
     * */
    private void swapContent(JPanel panel)
    {
        currentScreen.removeAll();
        currentScreen.add(panel, BorderLayout.CENTER);
        currentScreen.revalidate();
        currentScreen.repaint();
    }
}