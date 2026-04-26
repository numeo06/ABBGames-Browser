//package model;
//
//import data.UserDatabase;
//
//import java.util.ArrayList;
//
///**
// * Admin class will be a user with administrative abilities
// * The user can perform user-management operations such as
// * deleting users or granting admin permissions.
// */
//public class Admin extends User
//{
//    /**
//     * Constructs an Admin user with default admin privileges enabled.
//     *
//     * @param username the admin's username
//     * @param password the admin's password
//     */
//    public Admin(String username, String password)
//    {
//        super(username,password,true, new ArrayList<>(),new ArrayList<>());
//    }
//
//    /**
//     * Deletes a user from the system database.
//     *
//     * @param username the username of the user to be deleted
//     * @param userDB   the user database where the user is stored
//     */
//    public void deleteUser(String username, UserDatabase userDB)
//    {
//        userDB.deleteUser(username);
//    }
//
//    /**
//     * Sets the admin status of a user.
//     *
//     * @param username the user to update
//     * @param isAdmin true to promote, false to demote
//     * @param userDB the database being modified
//     */
//    public void setAdminStatus(String username, boolean isAdmin, UserDatabase userDB)
//    {
//        userDB.setAdmin(username, isAdmin);
//    }
//}
