//import data.FileScannerXML;
//import data.GameDatabase;
//import data.UserDatabase;
//import model.Game;
//import model.Review;
//import model.User;
//import model.UserCollection;
//import viewAndControl.GameDescriptionPanel;
//
//import javax.swing.*;
//import java.io.File;
//import java.util.ArrayList;
///**
// * Test class for viewAndControl.GameDescriptionPanel.
// * Verifies that the containsGame logic correctly disables collection buttons
// * for games already in a collection, and that adding games through the UI
// * updates the in-memory collection correctly.
// * Test setup:
// * - Creates a user with one collection <name>containing game1
// * - Opens viewAndControl.GameDescriptionPanel on game1
// * - The <name> button in the Add to Collection dialog should be grayed out
// * - After closing, prints all collections and their games to verify any changes
// */
//public class GameDescTest
//{
//    /**
//     * Entry point for the viewAndControl.GameDescriptionPanel test.
//     * Loads games from XML, sets up a user and collection, and launches the panel.
//     *
//     * @param args command-line arguments (not used)
//     */
//    public static void main(String[] args) {
//        File gamesFile = new File("bgg90Games.xml");
//        FileScannerXML scanner = new FileScannerXML(gamesFile,null);
//
//        ArrayList<Game> gameList = scanner.parseGamesFromXML();
//        GameDatabase gameDB = new GameDatabase(gameList);
//
//        // create a test user
//        UserDatabase userDB = new UserDatabase(gameDB);
//        userDB.addUser("testUser", "pass123", false);
//        User user = userDB.getUserByName("testUser");
//
//        // get two games
//        Game game1 = gameDB.getAllGames().get(0);
//        Game game2 = gameDB.getAllGames().get(1);
//
//        // create a collection and add game 1
//        UserCollection col = new UserCollection("Gaga");
//        col.addGame(game1);
//        userDB.addCollectionToUser("testUser", col);
//
//        System.out.println("Before UI\n");
//        System.out.println("game1 (" + game1.getName() + ") in Gaga: " + col.containsGame(game1)); // should be true
//        System.out.println("game2 (" + game2.getName() + ") in Gaga: " + col.containsGame(game2)); // should be false
//
//        // open description panel on game1, cannot add to Gaga
//        GameDescriptionPanel panel = new GameDescriptionPanel(user, "reviews.xml");
//        ArrayList<Review> reviews = scanner.getReviewForGame("reviews.xml", game1);
//        panel.setDisplayedGame(game1, reviews);
//
//        JFrame frame = new JFrame("ContainsGame Test");
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frame.setSize(1200, 800);
//        frame.add(panel);
//        frame.setVisible(true);
//
//        frame.addWindowListener(new java.awt.event.WindowAdapter()
//        {
//            @Override
//            public void windowClosing(java.awt.event.WindowEvent e)
//            {
//                System.out.println("\nAfter UI\n");
//                for (UserCollection c : user.getCollections())
//                {
//                    System.out.println("Collection: " + c.getName());
//                    for (Game g : c.getAllGames())
//                    {
//                        System.out.println("  model.Game: " + g.getName());
//                    }
//                }
//            }
//        });
//    }
//}
