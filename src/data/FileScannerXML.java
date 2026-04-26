package data;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import model.User;
import model.Game;
import model.UserCollection;
import model.Review;

/**
 * Parses a board game XML file converting its content into Game objects,
 * parses users data XML file converting its content into User objects,
 * and parses review XML into Review objects
 * This class is responsible only for reading and interpreting XML data.
 * It does not store or manage games after parsing. TThe parsed data is returned
 * to database classes for storage.
 */
public class FileScannerXML
{
    private final File xmlFile;
    private GameDatabase gameDatabase;

    /**
     * Constructs a data.FileScannerXML with the given XML file.
     *
     * @param xmlFile the XML file to parse
     * @param gameDatabase the Game Library to reference when updating the users XML
     */
    public FileScannerXML(File xmlFile, GameDatabase gameDatabase)
    {
        this.xmlFile = xmlFile;
        this.gameDatabase=gameDatabase;
    }

    /**
     * Parses the XML file and returns a list of Game objects.
     *
     * @return an ArrayList of Game objects extracted from the XML file
     * @throws RuntimeException if there is an error reading or parsing the file
     */
    public ArrayList<Game> parseGamesFromXML()
    {

        ArrayList<Game> games = new ArrayList<>();
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList gameList = doc.getElementsByTagName("item");

            for (int i = 0; i < gameList.getLength(); i++) {
                Node node = gameList.item(i);

                if (node.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Element game = (Element) node;

                if (!"boardgame".equals(game.getAttribute("type")) && !"boardgameexpansion".equals(game.getAttribute("type")))
                    continue;

                games.add(parseGameElement(game));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML", e);
        }

        return games;
    }
    /**
     * Parses the XML file and returns a list of users.
     *
     * @return list of users extracted from XML
     * @throws RuntimeException if parsing fails
     */
    public ArrayList<User> parseUsersFromXML()
    {
        ArrayList<User> users=new ArrayList<>();
        try
        {
            DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document userDoc=builder.parse(xmlFile);
            userDoc.getDocumentElement().normalize();
            // get all user nodes
            NodeList userList=userDoc.getElementsByTagName("user");
            for (int i=0; i<userList.getLength();i++)
            {
                Node node=userList.item(i);
                if(node.getNodeType()!=Node.ELEMENT_NODE)
                {
                    continue;
                }
                Element user = (Element) node;
                //parse each user element
                users.add(parseUserElement(user));
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException("Failed to parse XML",e);
        }
        return users;
    }

    /**
     *  Parses a single XML element representing a game and constructs Game
     *  object.
     *
     * @param gameElement an XML parent node
     * @return a Game object with attributes populated by the XML
     */
    private Game parseGameElement(Element gameElement)
    {
        String id = gameElement.getAttribute("id");
        String name = getAttributeValue(gameElement, "name", "N/A");
        String thumbnailURL = getTextContent(gameElement, "thumbnail", "N/A");
        String imageURL = getTextContent(gameElement, "image", "N/A");
        String desc = getTextContent(gameElement, "description", "Unknown");
        String pubYear = getAttributeValue(gameElement, "yearpublished", "N/A");
        String minPlayers = getAttributeValue(gameElement, "minplayers", "N/A");
        String maxPlayers = getAttributeValue(gameElement, "maxplayers", "N/A");
        String playingTime = getAttributeValue(gameElement, "playingTime", "N/A");
        ArrayList<String> categories = getTagList(gameElement,"link", "boardgamecategory");
        ArrayList<String> mechanics = getTagList(gameElement,"link", "boardgamemechanic");

        return new Game(id, name, thumbnailURL, imageURL, desc, pubYear, minPlayers, maxPlayers, playingTime, categories, mechanics);
    }

    /**
     *  Parses a single XML element representing a user and constructs User
     *  object.
     *
     * @param userElement an XML parent node
     * @return a User object with attributes populated by the XML
     */
    private User parseUserElement(Element userElement)
    {
        // read basic user attributes
        String username=userElement.getAttribute("username");
        String password=userElement.getAttribute("password");
        String adminCheck=userElement.getAttribute("isAdmin");
        boolean isAdmin=Boolean.parseBoolean(adminCheck);

        String profilePicturePath=userElement.getAttribute("profilePicture");
        // parse collections
        NodeList collectionList=userElement.getElementsByTagName("collection");
        ArrayList<UserCollection> collections=new ArrayList<>();
        for (int i=0; i<collectionList.getLength();i++)
        {
            Node collectionNode=collectionList.item(i);

            if (collectionNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element collectionElement=(Element) collectionNode;
                String collectionName=collectionElement.getAttribute("name");
                // build collection object
                UserCollection collection=new UserCollection(collectionName);
                NodeList gameIdList=collectionElement.getElementsByTagName("gameId");
                for (int j=0; j<gameIdList.getLength();j++)
                {
                    Node gameNode=gameIdList.item(j);
                    if (gameNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element gameElement= (Element) gameNode;
                        int gameId=Integer.parseInt(gameElement.getAttribute("value"));
                        // resolve game reference using game database
                        Game game=gameDatabase.getGameById(gameId);
                        if (game!=null)
                        {
                            collection.addGame(game);
                        }
                    }
                }
                collections.add(collection);
            }
        }
        // create user with parsed collections
        User user=new User(username,password, isAdmin,collections, new ArrayList<>());

        if (profilePicturePath!=null && !profilePicturePath.isEmpty())
        {
            user.setProfilePicturePath(profilePicturePath);
        }
        return user;
    }

    /**
     * Gets the "value" of a child node by tag name.
     *
     * @param parent the parent XML node
     * @param tag the child element's tag name
     * @param defaultValue default return value if tag value is missing
     * @return the attribute value, or defaultValue if missing
     */
    private String getAttributeValue(Element parent, String tag, String defaultValue)
    {

        Node node = parent.getElementsByTagName(tag).item(0);

        if (node != null && node.getNodeType() == Node.ELEMENT_NODE)
        {
            return ((Element) node).getAttribute("value");
        }

        return defaultValue;
    }

    /**
     * Gets the text content of a child node by tag name.
     *
     * @param parent the parent XML node
     * @param tag the child element's text content
     * @param defaultValue default return value if text content is missing
     * @return the child element's textContent, or defaultValue if missing
     */
    private String getTextContent(Element parent, String tag, String defaultValue)
    {

        Node node = parent.getElementsByTagName(tag).item(0);

        if (node != null && node.getNodeType() == Node.ELEMENT_NODE)
        {
            return node.getTextContent();
        }

        return defaultValue;
    }

    /**
     * Gets a tag list of a child node by tag and type.
     *
     * @param parent the parent XML node
     * @param tag a link of child node
     * @param filterType type filter for link
     * @return list of string attributes based on filter
     */
    private ArrayList<String> getTagList(Element parent, String tag, String filterType)
    {
        ArrayList<String> stringList = new ArrayList<>();

        NodeList nodeList = parent.getElementsByTagName(tag);

        for(int i = 0; i < nodeList.getLength(); i++)
        {
            Node node = nodeList.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element element = (Element) node;

            if (filterType.equals(element.getAttribute("type")))
            {
                stringList.add(element.getAttribute("value"));
            }
        }
        return stringList;
    }
    /**
     * Retrieves all reviews associated with a specific game.
     *
     * @param reviewsXMLPath path to reviews XML file
     * @param game the game whose reviews should be loaded
     * @return list of reviews for the game (empty if none exist)
     */
    public ArrayList<Review> getReviewForGame(String reviewsXMLPath, Game game)
    {
        // load reviews file
        ArrayList<Review> reviewList=new ArrayList<>();
        File reviewFile= new File(reviewsXMLPath);
        if (!reviewFile.exists())
        {
            return reviewList;
        }
        try {
            DocumentBuilderFactory reviewFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder reviewBuilder = reviewFactory.newDocumentBuilder();
            Document reviewDoc = reviewBuilder.parse(reviewsXMLPath);
            // find all game review nodes
            NodeList gameReviews = reviewDoc.getElementsByTagName("gameReview");

            for (int i=0; i<gameReviews.getLength();i++)
            {
                Element gameReview=(Element) gameReviews.item(i);
                int id=Integer.parseInt(gameReview.getAttribute("gameId"));
                int gameId=game.getID();
                if(id==gameId)
                {
                    // parse user reviews
                    NodeList userReviews=gameReview.getElementsByTagName("userReview");
                    for (int j=0;j<userReviews.getLength();j++)
                    {
                        Element userReview=(Element) userReviews.item(j);
                        String username=userReview.getAttribute("username");
                        int rating = Integer.parseInt(userReview.getAttribute("rating"));
                        String text=userReview.getAttribute("text");
                        // create Review object and add to list
                        reviewList.add((new Review(gameId, username, rating, text)));
                    }
                    break; //found desired game
                }
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException("Unable to get reviews for game " + game.getID(), e);
        }
        return reviewList;
    }

}

