package model;

import java.util.ArrayList;

/**
 *  Represents a board game with its core attributes.
 *  Each Game object stores its unique ID, name, description, publication year,
 *  minimum and maximum player counts, and lists of categories and mechanics.
 */
public class Game {

    private final int id;
    private final String name;
    private final String thumbnailURL;
    private final String imageURL;
    private final String desc;
    private final int pubYear;
    private final int minPlayer;
    private final int maxPlayer;
    private final int playingTime;
    private final ArrayList<String> bgCategories;
    private final ArrayList<String> bgMechanics;

    /**
     * Constructs a game object from various parameters
     *
     * @param id the game id
     * @param name the game name
     * @param desc the game description
     * @param pubYear the game publication year as a string
     * @param minPlayer required minimum player count as string; returns as an int
     * @param maxPlayer maximum player count; returns as an int
     * @param playingTime gameplay time
     * @param bgCategories list of game categories
     * @param bgMechanics list of game mechanics
     */
    public Game(String id, String name, String thumbnailURL, String imageURL, String desc, String pubYear, String minPlayer, String maxPlayer, String playingTime, ArrayList<String> bgCategories, ArrayList<String> bgMechanics)
    {
        this.id = stringToInt(id);
        this.name = name;
        this.thumbnailURL = thumbnailURL;
        this.imageURL = imageURL;
        this.desc = desc;
        this.pubYear = stringToInt(pubYear);
        this.minPlayer = stringToInt(minPlayer);
        this.maxPlayer = stringToInt(maxPlayer);
        this.playingTime = stringToInt(playingTime);
        this.bgCategories = new ArrayList<>(bgCategories);
        this.bgMechanics = new ArrayList<>(bgMechanics);

    }

    /**
     * Returns the unique game identifier.
     *
     * @return the game ID
     */
    public int getID()
    {
        return id;
    }

    /**
     * Returns the game's name.
     *
     * @return the game name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns a game's Thumbnail URL.
     *
     * @return thumbnail URL
     */
    public String getThumbnailURL()
    {
        return thumbnailURL;
    }

    /**
     * Returns a game's image URL.
     *
     * @return image URL
     */
    public String getImageURL()
    {
        return imageURL;
    }

    /**
     *  Returns the game's description.
     *
     * @return the game description
     */
    public String getDescription()
    {
        return desc;
    }

    /**
     *  Returns the game's publication year.
     *
     * @return the publication year
     */
    public int getYearPublished()
    {
        return pubYear;
    }

    /**
     *  Returns the game's minimum player supported player count.
     *
     * @return game minimum player count
     */
    public int getMinPlayer()
    {
        return minPlayer;
    }

    /**
     *  Return the game's maximum player count
     *
     * @return game maximum player count
     */
    public int getMaxPlayer()
    {
        return maxPlayer;
    }

    /**
     * Returns a list of a game's playstyle categories.
     *
     * @return list of game categories
     */
    public ArrayList<String> getBgCategories()
    {
        return bgCategories;
    }

    /**
     * Returns a list of a game's mechanics.
     *
     * @return list of game mechanics
     */
    public ArrayList<String> getBgMechanics()
    {
        return bgMechanics;
    }

    /**
     * Returns an integer from string conversion
     *
     * @param num string integer
     * @return string conversion int
     */
    private int stringToInt(String num)
    {
        try
        {
            return Integer.parseInt(num);
        }
        catch (NumberFormatException e)
        {
            return 1;
        }
    }

    @Override
    public String toString()
    {
        return "GameID: " + id + '\n' +
                "Name: " + name + '\n' +
                "Year: " + pubYear + '\n' +
                "ThumbnailURL: " + thumbnailURL + '\n' +
                "ImageURL: " + imageURL + '\n' +
                "Description: " + desc + '\n' +
                "Minimum Players: " + minPlayer + '\n' +
                "Playing Time: " + playingTime + '\n' +
                "Maximum Players: " + maxPlayer + '\n' +
                "Categories: " + bgCategories + '\n' +
                "Mechanics: " + bgMechanics + '\n' +
                "-------------------------------\n";
    }
}