package model;

public class User {
    
    private static String currentUsername;
    private static String currentUserid;
    private static boolean currentlyActive;

    public User(String currentUsername, String currentUserid, boolean currentlyActive) {
        this.currentUsername = currentUsername;
        this.currentUserid = currentUserid;
        this.currentlyActive = currentlyActive;
    }

    public static void setCurrentUsername(String currentUsername) {
        User.currentUsername = currentUsername;
    }

    public static void setCurrentUserid(String currentUserid) {
        User.currentUserid = currentUserid;
    }

    public static void setCurrentlyActive(boolean currentlyActive) {
        User.currentlyActive = currentlyActive;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static String getCurrentUserid() {
        return currentUserid;
    }   
}
    
     