package data;

import java.util.*;
import model.Game;

/**
 * Represents a database of board games that provides access to and
 * operations on a collection of Game objects.
 *
 * The database acts as a container for games loaded from an external
 * source and serves as the primary data source for queries and user collections.
 */
public class GameDatabase
{
    protected ArrayList<Game> games;
    protected HashSet<String> allCategories;
    protected HashSet<String> allMechanics;
    protected HashMap<String, HashSet<Game>> wordMap;
    protected HashMap<String, HashSet<Game>> categoryMap;
    protected HashMap<String, HashSet<Game>> mechanicMap;

    /**
     * Constructs a master game database of Game objects.
     * @param gameDB parsed boardgame array list
     */
    public GameDatabase(ArrayList<Game> gameDB)
    {
        this.games = gameDB;
        buildCategoryAndMechanicSets();
        buildWordMap();
        buildCategoryMap();
        buildMechanicMap();
    }

    /**
     * Empty Constructor for user collections.
     */
    protected GameDatabase()
    {
        this.games = new ArrayList<>();
        this.allCategories = new HashSet<>();
        this.allMechanics = new HashSet<>();
        this.wordMap = new HashMap<>();
        this.categoryMap = new HashMap<>();
        this.mechanicMap = new HashMap<>();

    }

    public void printAllGames()
    {
        for(Game g : games)
        {
            System.out.println(g);
        }
    }

    /**
     * Returns a copy of a list of all games within a game database.
     *
     * @return list of games within game database
     */
    public ArrayList<Game> getAllGames()
    {
        return new ArrayList<>(games);
    }

    /**
     *  Returns a game based on unique id.
     *
     * @param id game ID
     * @return game object that matches id
     */
    public Game getGameById(int id)
    {
        for (Game game : games)
        {
            if (game.getID() == id)
            {
                return game;
            }
        }
        return null;
    }

    /**
     * Returns all unique categories.
     *
     * @return list of categories
     */
    public ArrayList<String> getAllCategories()
    {
        return new ArrayList<>(allCategories);
    }

    /**
     * Returns all unique mechanics.
     *
     * @return list of mechanics
     */
    public ArrayList<String> getAllMechanics()
    {
        return new ArrayList<>(allMechanics);
    }

    /**
     * Returns games that match user query.
     *
     * @param Query user inputted string
     * @return list of games matching user query
     */
    public ArrayList<Game> searchByName(String Query)
    {
        HashSet<Game> results = new HashSet<>();
        String lowerQuery = Query.toLowerCase();

        if (lowerQuery.isEmpty())
        {
            return new ArrayList<>(games);
        }

        //split query into individual words
        String[] queryWords=lowerQuery.split("\\s+");

        //single word queries
        if (queryWords.length==1)
        {
            String word=queryWords[0];

            if(wordMap.containsKey(word)) //add games with exact word matches
            {
                results.addAll(wordMap.get(word));
            }

            for (String key: wordMap.keySet()) // handles substring matches
            {
                if(key.contains(lowerQuery) && !key.equals(lowerQuery)) // compares query to keys of wordMap
                {                                                       // but prevents double adding exact matches
                    results.addAll(wordMap.get(key));
                }
            }
        }
        //multi word queries find games matching all words
        else
        {
            HashSet<Game> candidates=new HashSet<>();
            String firstWord=queryWords[0];

            if(wordMap.containsKey(firstWord))
            {
                candidates.addAll(wordMap.get(firstWord));
            }
            for(String key:wordMap.keySet())
            {
                if(key.contains(firstWord))
                {
                    candidates.addAll(wordMap.get(key));
                }
            }
            //filter games with names containing all query word
            for (Game game:candidates)
            {
                String gameName=game.getName().toLowerCase();
                boolean matchesAll=true;

                for(String queryWord:queryWords)
                {
                    if(!gameName.contains(queryWord))
                    {
                        matchesAll=false;
                        break;
                    }
                }
                if(matchesAll)
                {
                    results.add(game);
                }
            }
        }

        return new ArrayList<>(results);
    }

    /**
     *  Filters games within a game database by a collection of categories.
     *
     * @param selectedCategories a collection of category names; maybe null or empty
     * @return list of game objects that fit the selected categories
     */
    public ArrayList<Game> filterByCategory(Collection<String> selectedCategories)
    {
        // empty selection
        if(selectedCategories == null || selectedCategories.isEmpty())
        {
            return new ArrayList<>(games);
        }

        Iterator<String> iter = selectedCategories.iterator();

        // iterate through first categories set
        HashSet<Game> intersection = new HashSet<>(categoryMap.getOrDefault(iter.next(), new HashSet<>()));

        // intersect with the rest
        while(iter.hasNext())
        {
            String cat = iter.next();
            HashSet<Game> catSet = categoryMap.getOrDefault(cat, new HashSet<>());
            intersection.retainAll(catSet);
        }

        return new ArrayList<>(intersection);
    }

    /**
     * Filters games within a game database by a collection of mechanics.
     *
     * @param selectedMechanics a collection of mechanics; maybe null or empty
     * @return list of game objects that fit the selected mechanics
     */
    public ArrayList<Game> filterByMechanic(Collection<String> selectedMechanics)
    {
        // empty selection
        if(selectedMechanics == null || selectedMechanics.isEmpty())
        {
            return new ArrayList<>(games);
        }

        Iterator<String> iter = selectedMechanics.iterator();

        // iterate through first categories set
        HashSet<Game> intersection = new HashSet<>(mechanicMap.getOrDefault(iter.next(), new HashSet<>()));

        // intersect with the rest
        while(iter.hasNext())
        {
            String mech = iter.next();
            HashSet<Game> mechSet = mechanicMap.getOrDefault(mech, new HashSet<>());
            intersection.retainAll(mechSet);
        }

        return new ArrayList<>(intersection);
    }

    /**
     *  Filters games within a game database by a collection of categories and mechanics.
     *
     * @param selectedCategories a collection of category names; maybe null or empty
     * @param selectedMechanics a collection of mechanics; maybe null or empty
     * @return list of game objects that fit the selected criteria
     */
    public ArrayList<Game> filterByCategoryAndMechanic(Collection<String> selectedCategories, Collection<String> selectedMechanics)
    {
        boolean noCategories = selectedCategories == null || selectedCategories.isEmpty();
        boolean noMechanics = selectedMechanics == null || selectedMechanics.isEmpty();

        if (noCategories && noMechanics) // no category or mechanic selected
        {
            return new ArrayList<>(games);
        }

        if(noCategories) // no categories selected
        {
            return filterByMechanic(selectedMechanics);
        }

        if(noMechanics) // no mechanics selected
        {
            return filterByCategory(selectedCategories);
        }

        HashSet<Game> resultSet = new HashSet<>(filterByCategory(selectedCategories)); // categories and mechanics selected
        resultSet.retainAll(filterByMechanic(selectedMechanics));

        return new ArrayList<>(resultSet);
    }

    /**
     * Adds game's categories, mechanics, and words to a collection's maps.
     *
     * @param g game object
     */
    protected void addToMaps(Game g)
    {
        games.add(g);

        allCategories.addAll(g.getBgCategories());
        allMechanics.addAll(g.getBgMechanics());

        String[] words =g.getName().toLowerCase().split("\\s+");
        for(String word: words)
        {
            wordMap.computeIfAbsent(word, k-> new HashSet<>()).add(g);
        }
        for(String cat: g.getBgCategories())
        {
            categoryMap.computeIfAbsent(cat, k -> new HashSet<>()).add(g);
        }
        for(String mech: g.getBgMechanics())
        {
            mechanicMap.computeIfAbsent(mech, k -> new HashSet<>()).add(g);
        }
    }

    /**
     *  Removes game from game list, and then rebuilds collection maps
     *
     * @param g game object
     */
    protected void removeFromMaps(Game g)
    {
        games.remove(g);
        buildCategoryAndMechanicSets();
        buildWordMap();
        buildCategoryMap();
        buildMechanicMap();
    }

    /**
     *  Parent pass for collection maps.
     *
     * @param g
     */
    protected void addGame(Game g)
    {
        addToMaps(g);
    }

    /**
     * Builds unique sets for categories and mechanics for filter drop down.
     *
     */
    private void buildCategoryAndMechanicSets()
    {
        allCategories = new HashSet<>();
        allMechanics = new HashSet<>();

        for(Game g: games)
        {
            allCategories.addAll(g.getBgCategories());
            allMechanics.addAll(g.getBgMechanics());
        }
    }

    /**
     *  Builds categories map for fast filtering
     */
    private void buildCategoryMap()
    {
        categoryMap = new HashMap<>();

        for(Game g: games)
        {
            ArrayList<String> categories = g.getBgCategories();
            for(String cat: categories)
            {
                HashSet<Game> set = categoryMap.get(cat);
                categoryMap.computeIfAbsent(cat, k -> new HashSet<>()).add(g);
            }
        }
    }

    /**
     *  Builds mechanics map for fast filtering.
     */
    private void buildMechanicMap()
    {
        mechanicMap = new HashMap<>();

        for(Game g: games)
        {
            ArrayList<String> mechanics = g.getBgMechanics();
            for(String mech: mechanics)
            {
                HashSet<Game> set = mechanicMap.get(mech);
                mechanicMap.computeIfAbsent(mech, k -> new HashSet<>()).add(g);
            }
        }
    }

    /**
     * Builds word maps for fast search
     */
    private void buildWordMap()
    {
        wordMap = new HashMap<>();

        for(Game g: games)
        {
            String[] words = g.getName().toLowerCase().split("\\s+");
            for(String word: words)
            {
                wordMap.computeIfAbsent(word, k-> new HashSet()).add(g);
            }
        }
    }

}
