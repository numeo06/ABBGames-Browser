import model.Review;

import java.io.File;

public class ReviewTest
{
    private static final String TEST_FILE = "reviews_test.xml";

    public static void main(String[] args)
    {
        testCase1();
        testCase2();
        testCase3();
    }

    // Case 1: File does not exist — should create new file from scratch
    private static void testCase1()
    {
        System.out.println("=== Test Case 1: File does not exist ===");
        new File(TEST_FILE).delete();

        Review review = new Review(101, "alice", 5, "Amazing game!");
        review.writeReviewsXML(TEST_FILE);

        File file = new File(TEST_FILE);
        if (file.exists())
            System.out.println("PASS: File was created");
        else
            System.out.println("FAIL: File was not created");

        System.out.println();
    }

    // Case 2: File exists, game already has a gameReview node — should append userReview
    private static void testCase2()
    {
        System.out.println("=== Test Case 2: File exists, game already has a gameReview node ===");

        Review review = new Review(101, "bob", 3, "Decent but slow.");
        review.writeReviewsXML(TEST_FILE);

        File file = new File(TEST_FILE);
        if (file.exists())
            System.out.println("PASS: File still exists after appending");
        else
            System.out.println("FAIL: File missing");

        System.out.println("Check " + TEST_FILE + " — gameId 101 should have two userReview entries");
        System.out.println();
    }

    // Case 3: File exists but game not found — should create new gameReview node
    private static void testCase3()
    {
        System.out.println("=== Test Case 3: File exists, game not found ===");

        Review review = new Review(202, "charlie", 4, "Great for groups!");
        review.writeReviewsXML(TEST_FILE);

        File file = new File(TEST_FILE);
        if (file.exists())
            System.out.println("PASS: File still exists after adding new gameReview");
        else
            System.out.println("FAIL: File missing");

        System.out.println("Check " + TEST_FILE + " — should now have gameId 101 and gameId 202");
        System.out.println();
    }
}