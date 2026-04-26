package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Review represents a single user's review for a specified game
 * It will display the username, the rating, and the text comment.
 * Reviews for specific games are held in the reviews.xml file. If a
 * user already reviewed a game their review is updated to the newest version.
 * */
public class Review
{
    private final int gameId;
    private final String username;
    private final int rating;
    private final String text;

    /**
     * Constructs a Review object.
     *
     * @param gameRef id of the game being reviewed
     * @param user username of reviewer
     * @param rating rating value (expected 1–5)
     * @param reviewText review comment text
     */
    public Review(int gameRef, String user, int rating, String reviewText)
    {
      gameId = gameRef;
      username = user;
      this.rating = rating;
      text=reviewText;

    }
    /**
     * Returns the game id this review belongs to.
     * @return game id
     */
    public int getGameId()
    {
        return gameId;
    }
    /**
     * Returns the username of the reviewer.
     * @return username
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Returns the rating value.
     * If rating is outside valid bounds (1–10), returns 0.
     *
     * @return rating or 0 if invalid
     */
    public int getRating()
    {
        if (rating < 1 || rating > 5)
        {
            return 0;
        }

        return rating;
    }
    /**
     * Returns the review text.
     * @return review comment
     */
    public String getText()
    {
        return text;
    }
    /**
     * Returns formatted review string.
     * @return formatted review
     */
    @Override
    public String toString()
    {
        return username + " (" + rating + "/5): " + text;
    }
    /**
     * Save the reviews to specified XML file
     *
     * @param doc XML document to save
     * @param reviewXMLPath file path to write to
     * @throws Exception if saving fails
     * */
    private void saveDocument(Document doc, String reviewXMLPath) throws Exception
    {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult(new File(reviewXMLPath));
        transformer.transform(source, result);
    }

    /**
     * Write review to XML file
     * The XML is checked to see if the gameId exists already. If it does it will update
     * existing usre review if present. If not a new user review is appended
     * If the gameId does not exist, a new game review node is added.
     * If the file does not exist, a new XML structure is made
     *
     * @param reviewXMLPath to review XML file
     */
    public void writeReviewsXML(String reviewXMLPath)
    {
        File reviewsFile = new File(reviewXMLPath);

        if (reviewsFile.exists())
        {
            try {
                DocumentBuilderFactory reviewFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder reviewBuilder = reviewFactory.newDocumentBuilder();
                // load existing XML document
                Document reviewDoc = reviewBuilder.parse(reviewsFile);
                reviewDoc.getDocumentElement().normalize();

                String idEntry = String.valueOf(getGameId());
                // find all game review nodes
                NodeList reviewList = reviewDoc.getElementsByTagName("gameReview");

                boolean found = false;
                // search for matching game id
                for (int i = 0; i < reviewList.getLength(); i++)
                {
                    Node reviewNode = reviewList.item(i);
                    Element reviewElement = (Element) reviewNode;
                    if (idEntry.equals(reviewElement.getAttribute("gameId")))
                    {
                        // check if user already reviewed this game
                        NodeList existingUserReviews = reviewElement.getElementsByTagName("userReview");
                        for (int j = 0; j < existingUserReviews.getLength(); j++)
                        {
                            Element existing = (Element) existingUserReviews.item(j);
                            if (existing.getAttribute("username").equals(getUsername()))
                            {
                                // update existing review instead of creating duplicate
                                existing.setAttribute("rating", String.valueOf(getRating()));
                                existing.setAttribute("text", getText());
                                found = true;
                                saveDocument(reviewDoc, reviewXMLPath);
                                return;
                            }
                        }
                        // create new user review under existing game
                        Element newUserElement = reviewDoc.createElement("userReview");
                        newUserElement.setAttribute("username", getUsername());
                        newUserElement.setAttribute("rating", String.valueOf(getRating()));
                        newUserElement.setAttribute("text", getText());
                        reviewElement.appendChild(newUserElement);
                        found=true;
                    }
                }
                // if the gameId does not exist in the file create a new node
                if(!found)
                {
                    Element root=reviewDoc.getDocumentElement();
                    // create new game review if game id not found
                    Element newReview = reviewDoc.createElement("gameReview");
                    newReview.setAttribute("gameId", String.valueOf(getGameId()));
                    root.appendChild(newReview);
                    Element newUserElement = reviewDoc.createElement("userReview");
                    newUserElement.setAttribute("username", getUsername());
                    newUserElement.setAttribute("rating", String.valueOf(getRating()));
                    newUserElement.setAttribute("text", getText());
                    newReview.appendChild(newUserElement);
                }
                saveDocument(reviewDoc, reviewXMLPath);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
        // if the review XML doesnt exist create new instance
        else
        {
            try
            {
                DocumentBuilderFactory reviewFileFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder reviewFileBuilder = reviewFileFactory.newDocumentBuilder();
                Document reviewDoc = reviewFileBuilder.newDocument();
                // get root element for all reviews
                Element root = reviewDoc.createElement("reviews");
                reviewDoc.appendChild(root);

                Element reviewElement = reviewDoc.createElement("gameReview");
                reviewElement.setAttribute("gameId", String.valueOf(getGameId()));
                root.appendChild(reviewElement);
                //Nested usernames
                Element userElement = reviewDoc.createElement("userReview");
                userElement.setAttribute("username", getUsername());
                userElement.setAttribute("rating", String.valueOf(getRating()));
                userElement.setAttribute("text", getText());
                //Nest each user reviews under games
                reviewElement.appendChild(userElement);

                saveDocument(reviewDoc, reviewXMLPath);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to write reviews XML");
            }
        }
    }
}
