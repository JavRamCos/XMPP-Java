package main;

import java.util.Arrays;

public class MessageManager {
    String username;
    public MessageManager() { }

    public void setUsername(String username) {
        this.username = username;
    }

    public void displayLoginSignupMenu() {
        System.out.print("========== XMPP Client ==========\n" +
                        "1. Login\n" +
                        "2. Register\n" +
                        "3. Exit\n");
    }

    public void displayClientMenu() {
        System.out.print("========== "+this.username+" Session ==========\n" +
                "1. Display Users information\n" +
                "2. Add friend\n" +
                "3. User information\n" +
                "4. Chat\n" +
                "5. \n" +
                "6. Quit session\n");
    }

    public void displayError(String msg) {
        System.out.println("> ERROR: "+msg);
    }

    public void print(String msg) {
        System.out.println(msg);
    }
}