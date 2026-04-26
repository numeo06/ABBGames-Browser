import data.FileScannerXML;
import data.GameDatabase;
import data.UserDatabase;
import model.Game;
import model.User;
import viewAndControl.LoginScreen;

import javax.swing.*;

/**
 * Entry point for the application.
 * This method initializes the application by loading game data from an XML
 * file into a GameDatabase loading user data from an XML file into a UserDatabase
 * by providing the path for review data
 * launching the LoginScreen

 * @param args command-line arguments (it is not used)
 */
public static void main(String[] args)
{
    SwingUtilities.invokeLater(() ->
    {
        File gamesFile = new File("resources/bgg90Games.xml");
        File usersFile = new File("resources/usersInfo.xml");
        String reviewsPath = "resources/reviews.xml";

        //Load in Game Database
        FileScannerXML gameScanner = new FileScannerXML(gamesFile, null);
        ArrayList<Game> gameList = gameScanner.parseGamesFromXML();
        GameDatabase gameDatabase = new GameDatabase(gameList);

        //Load in User Database
        FileScannerXML userScanner = new FileScannerXML(usersFile, gameDatabase);
        ArrayList<User> userList = userScanner.parseUsersFromXML();
        UserDatabase userDatabase = new UserDatabase(usersFile, gameDatabase);

        new LoginScreen(userDatabase, gameDatabase, reviewsPath);
    });
}
