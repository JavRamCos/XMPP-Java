package main;

import java.util.List;

public class MessageManager {
    String username;
    public MessageManager() { }

    public void setUsername(String username) {
        this.username = username;
    }

    public void displayLoginSignupMenu() {
        System.out.print("""
                        \n========== XMPP Client ==========
                        1. Register
                        2. Login
                        3. Exit""");
    }

    public void displaySessionMenu() {
        System.out.print("\n========== "+this.username+" Session ==========\n" +
                "1. Display User/s information\n" +
                "2. Manage contacts\n" +
                "3. Manage Account\n" +
                "4. Chat\n" +
                "5. Close session");
    }

    public void displayInfoOptions() {
        System.out.print("""
                \n========= INFORMATION OPTIONS ==========
                1. Display all users information
                2. Display user information
                3. Display own information
                4. Cancel""");
    }

    public void displayUsersInfo(List<List<String>> users) {
        System.out.println("===== CONTACT INFORMATION =====");
        for(List<String> user : users) {
            System.out.println("-> Username: "+user.get(0));
            System.out.println("   Status: "+user.get(1));
            System.out.println("   Message: "+user.get(2));
            System.out.println("   Available: "+user.get(3));
        }
    }

    public void displayContactOptions() {
        System.out.print("""
                \n========== CONTACTS OPTIONS ==========
                1. Send Friend Request
                2. Manage Pending Requests
                3. Cancel""");
    }

    public void displayAdminOptions() {
        System.out.print("""
                \n========== ADMINISTRATION OPTIONS ==========
                1. Change status
                2. Delete Account
                3. Cancel""");
    }

    public void displayStatusOptions() {
        System.out.print("""
                \n========== STATUS OPTIONS ==========
                1. Available (default)
                2. Chat
                3. Away
                4. Extended Away
                5. Do not disturb""");
    }

    public void displayChatOptions() {
        System.out.print("""
                \n========== CHAT OPTIONS ==========
                1. User chat
                2. Group chat
                3. Cancel""");
    }

    public void displayError(String msg) {
        System.out.println("> ERROR: "+msg);
    }

    public void print(String msg) {
        System.out.println(msg);
    }
}
