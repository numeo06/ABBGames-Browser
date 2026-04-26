package data;

import model.Game;
import model.User;
import model.UserCollection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.stream.StreamResult;

/**
 * The UserDatabase manages all users in the system and handles persistence to XML.
 * Functionalities include loading users from the XML, validating login credentials,
 * adding and deleting users, managing admin privileges, managing user collections, and saving changes
 * to the XML
 */
public class UserDatabase
{
    private final Map<String, User> userList=new HashMap<>();
    private final File userXMLPath;
    private final GameDatabase gameDatabase;

    /**
     * Constructs a UserDatabase and loads existing users from the XML file.
     *
     * @param gameDatabase the game database used to resolve game references
     */
    public UserDatabase(File userXMLPath,GameDatabase gameDatabase)
    {
        this.userXMLPath=userXMLPath;
        this.gameDatabase=gameDatabase;
        loadUsers();
    }
    /**
     * Loads users from the XML file into memory.
     * If the file does not exist, no users are loaded.
     */
    private void loadUsers()
    {
        File xmlFile=userXMLPath;
        if(!xmlFile.exists())
        {
            return;
        }
        //parse users from XML file
        FileScannerXML scanner=new FileScannerXML(xmlFile,gameDatabase);
        ArrayList<User> userArrayList=scanner.parseUsersFromXML();
        //store users in hashmap for fast lookup by username
        for (User user: userArrayList)
        {
            userList.put(user.getUsername(),user);
        }
    }

    /**
     * Validates a user's login credentials.
     *
     * @param username the username entered
     * @param password the password entered
     * @return fresh model.User session if credentials are valid, null otherwise
     */
    public User validateCredentials(String username, String password)
    {
        User user=userList.get(username);
        if (user==null)
        {
            return null; //if username doesnt exist return null
        }

        if(!user.getPassword().equals(password))
        {
            return null; //wrong password
        }

        return user;
    }
    /**
     * Checks whether a username is already taken.
     *
     * @param newUsername the username to check
     * @return true if the username already exists, false otherwise
     */
    public boolean isUsernameTaken(String newUsername)
    {
        return userList.containsKey(newUsername);
    }
    /**
     * Saves all users to persistent storage.
     */
    public void saveUsers()
    {
        writeToXML();
    }

    /**
     * Change the admin privilege state in the user database
     *
     * @param username the username to change
     * @param isAdmin whether the user has admin privileges
     */
    public void setAdmin(String username, Boolean isAdmin)
    {
        User user = userList.get(username);
        if (user == null)
        {
            throw new IllegalArgumentException("model.User not found: " + username);
        }
        user.setIsAdmin(isAdmin);
        writeToXML(); // persist the change
    }
    /**
     * Adds a new user and persists the change.
     *
     * @param username the username for the new user
     * @param password the password for the new user
     * @param isAdmin whether the user has admin privileges
     */

    public void addUser(String username, String password, boolean isAdmin)
    {
        User newUser=new User(username, password, isAdmin, new ArrayList<>(),new ArrayList<>());
        userList.put(username,newUser);
        writeToXML();
    }
    /**
     * Retrieves a user by their username.
     *
     * @param username the username to search for
     * @return the model.User if found, null otherwise
     */
    public User getUserByName(String username)
    {
        return userList.get(username);
    }
    /**
     * Retrieves list of users in the database
     *
     * @return list of all users
     */
    public ArrayList<User> getAllUsers()
    {
        return new ArrayList<>(userList.values());
    }
    /**
     * Deletes a user from the database and persists the change.
     *
     * @param username the username of the user to delete
     * @throws IllegalArgumentException if the user does not exist
     */
    public void deleteUser(String username)
    {
        if (!userList.containsKey(username))
        {
            throw new IllegalArgumentException("model.User not found: " + username);
        }
        userList.remove(username);
        writeToXML();
    }
    /**
     * Adds a collection to a specific user and persists the change.
     *
     * @param username the username of the user
     * @param collection the collection to add
     * @throws IllegalArgumentException if the user does not exist
     */
    public void addCollectionToUser(String username, UserCollection collection)
    {
        User user = userList.get(username);
        if (user == null)
        {
            throw new IllegalArgumentException("User not found: " + username);
        }
        user.addCollection(collection);
        writeToXML();

    }
    /**
     * Writes all user data to an XML file.
     * This includes user credentials, collections, and associated game IDs.
     *
     * @throws RuntimeException if writing to XML fails
     */
    private void writeToXML()
    {
        try
        {
            //create XML document
            DocumentBuilderFactory userFileFactory=DocumentBuilderFactory.newInstance();
            DocumentBuilder userFileBuilder=userFileFactory.newDocumentBuilder();
            Document userDoc=userFileBuilder.newDocument();

            Element root=userDoc.createElement("users");
            userDoc.appendChild(root);

            for (User user: userList.values())
            {
                // create user node
                Element userElement=userDoc.createElement("user");
                userElement.setAttribute("username", user.getUsername());
                userElement.setAttribute("password", user.getPassword());
                userElement.setAttribute("isAdmin", String.valueOf(user.getIsAdmin()));
                userElement.setAttribute("profilePicture",
                        user.getProfilePicturePath() != null ? user.getProfilePicturePath() : "");
                root.appendChild(userElement);
                //collections wrapper tag
                Element collectionsElement=userDoc.createElement("collections");
                userElement.appendChild(collectionsElement);

                for (UserCollection collection : user.getCollections())
                {
                    Element collectionElement=userDoc.createElement("collection");
                    collectionElement.setAttribute("name",collection.getName());
                    collectionsElement.appendChild(collectionElement);

                    for (Game game: collection.getAllGames())
                    {
                        // store game reference by id
                        Element gameIdElement=userDoc.createElement("gameId");
                        gameIdElement.setAttribute("value", String.valueOf(game.getID()));
                        collectionElement.appendChild(gameIdElement);
                    }
                }
                Element reviewsElement=userDoc.createElement("reviews");
                userElement.appendChild(reviewsElement);
            }
            // write XML document to file
            TransformerFactory transformerFactory=TransformerFactory.newInstance();
            Transformer transformer=transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            DOMSource source =new DOMSource(userDoc);
            StreamResult result=new StreamResult(userXMLPath);
            transformer.transform(source,result);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Failed to write user XML");
        }
    }
}