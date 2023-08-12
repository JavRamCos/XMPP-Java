package main;

public class MessageManager {
    String username;
    public MessageManager() { }

    public void setUsername(String username) {
        this.username = username;
    }

    public void displayLoginSignupMenu() {
        System.out.print("""
                        ========== XMPP Client ==========
                        1. Register
                        2. Login
                        3. Exit""");
    }

    public void displaySessionMenu() {
        System.out.print("\n========== "+this.username+" Session ==========\n" +
                "1. Display Users information\n" +
                "2. Manage contacts\n" +
                "3. Manage Account\n" +
                "4. Chat\n" +
                "5. Send notification\n" +
                "6. Close session");
    }

    public void displayInfoOptions() {
        System.out.print("""
                ========== INFORMATION OPTIONS ==========
                1. Display all users information
                2. Display user information
                3. Display own information
                4. Cancel""");
    }

    public void displayContactOptions() {
        System.out.print("""
                ========== CONTACTS OPTIONS ==========
                1. Send Friend Request
                2. See pending Requests
                3. Cancel""");
    }

    public void displayChatOptions() {
        System.out.print("""
                ========== CHAT OPTIONS ==========
                1. User chat
                2. Group chat
                3. Cancel""");
    }

    public void displayAdminOptions() {
        System.out.print("""
                ========== ADMINISTRATION OPTIONS ==========
                1. Change status
                2. Delete Account
                3. Cancel""");
    }

    public void displayError(String msg) {
        System.out.println("> ERROR: "+msg);
    }

    public void print(String msg) {
        System.out.println(msg);
    }
}
