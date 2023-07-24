package main;

import java.util.Arrays;

public class MessageManager {
    public MessageManager() { }

    public void displayLoginSignupMenu() {
        System.out.print("========== XMPP Client ==========\n" +
                        "1. Login\n" +
                        "2. Register\n" +
                        "3. Exit\n");
    }

    public void displayClientMenu(String client_name) {
        System.out.print("========== "+client_name+" Session ==========\n" +
                "1. Display Users information\n" +
                "2. Add friend\n" +
                "3. User information\n" +
                "4. Chat\n" +
                "5. \n" +
                "6. Quit session\n");
    }

    public void displayErrorMessage(String... args) {
        String mssg = args[0];
        if(Arrays.stream(args).count() == 3) {
            mssg += " between "+args[1]+" and "+args[2];
        }
        System.out.println(mssg);
    }

    public void displayMessage(String mssg) {
        System.out.println(mssg);
    }
}
