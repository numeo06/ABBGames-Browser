//import model.Game;
//import model.Review;
//import model.User;
//import data.FileScannerXML;
//import data.GameDatabase;
//import data.UserDatabase;
//import model.UserCollection;
//import viewAndControl.GameDescriptionPanel;
//
//import javax.swing.*;
//import java.util.ArrayList;
//import java.io.File;
//
//public class mainTest {
//    public static void main (String []args) {
//        File gamesFile = new File("bgg90Games.xml");
//        FileScannerXML scanner = new FileScannerXML(gamesFile,null);
//
//        ArrayList<Game> gameList = scanner.parseGamesFromXML();
//        GameDatabase gameDB = new GameDatabase(gameList);
//        UserDatabase userDB=new UserDatabase(userFile,gameDB);
//
//        //gameDB.printAllGames();
//
//        // test addUser
//        userDB.addUser("testUser", "pass123", false);
//        System.out.println("Added testUser");
//
//        // test isUsernameTaken
//        System.out.println("Is testUser taken? " + userDB.isUsernameTaken("testUser")); // true
//        System.out.println("Is fakeUser taken? " + userDB.isUsernameTaken("fakeUser")); // false
//
//        // test getUserByName
//        User found = userDB.getUserByName("testUser");
//        System.out.println("Found user: " + found.getUsername());
//
//        // test validateCredentials
//        User valid = userDB.validateCredentials("testUser", "pass123");
//        System.out.println("Valid login: " + (valid != null)); // true
//
//        User invalid = userDB.validateCredentials("testUser", "wrongpass");
//        System.out.println("Invalid login: " + (invalid != null)); // false
//
//        UserCollection col = new UserCollection("Favorites");
//        Game game=gameDB.getGameById(374173);
//        if (game!=null)
//        {
//            col.addGame(game);
//        }
//
//        //add collection to user and save
//        userDB.addCollectionToUser("testUser",col);
//        // verify
//        User user = userDB.getUserByName("testUser");
//        for (UserCollection c : user.getCollections())
//        {
//            System.out.println("Collection: " + c.getName());
//            for (Game g : c.getAllGames())
//            {
//                System.out.println("  model.Game: " + g.getName());
//            }
//        }
//
//        JFrame frame = new JFrame("Board model.Game App");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(1200, 800);
//
//        Game testGame = gameDB.getAllGames().get(0);
//        ArrayList<Review> reviews=scanner.getReviewForGame("reviews.xml", testGame);
//
//        // build the panel and set the game
//        GameDescriptionPanel panel = new GameDescriptionPanel(user,"reviews.xml");
//        panel.setDisplayedGame(testGame,reviews);
//
//        JFrame frameT = new JFrame("model.Game Description Test");
//        frameT.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frameT.setSize(1200, 800);
//        frameT.add(panel);
//        frameT.setVisible(true);
//
////        // test deleteUser
////        userDB.deleteUser("testUser");
////        System.out.println("Deleted testUser");
////        System.out.println("Is testUser still there? " + userDB.isUsernameTaken("testUser")); // false
//
//    frameT.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//    frameT.add(panel);
//    frameT.setVisible(true);
//
//    frameT.addWindowListener(new java.awt.event.WindowAdapter()
//    {
//        @Override
//        public void windowClosing(java.awt.event.WindowEvent e)
//        {
//            System.out.println("\n--- Collections after UI ---");
//            for (UserCollection c : user.getCollections())
//            {
//                System.out.println("Collection: " + c.getName());
//                for (Game g : c.getAllGames())
//                {
//                    System.out.println("  model.Game: " + g.getName());
//                }
//            }
//
//            System.out.println("\n--- Reviews after UI ---");
//            ArrayList<Review> updatedReviews = scanner.getReviewForGame("reviews.xml", testGame);
//            if (updatedReviews.isEmpty())
//            {
//                System.out.println("No reviews found.");
//            }
//            else
//            {
//                for (Review r : updatedReviews)
//                {
//                    System.out.println(r.toString());
//                }
//            }
//        }
//    });
//    }
//}
