package main;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public Main() { }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MessageManager message_handler = new MessageManager();

        while(true) {
            message_handler.displayLoginSignupMenu();
            try {
                message_handler.displayMessage("Enter input: ");
                int option = scanner.nextInt();
                scanner.nextLine();
                if(option == 1) {
                    message_handler.displayMessage("Enter your new username: ");
                    String username = scanner.nextLine();
                    message_handler.displayMessage("Enter your new password: ");
                    String passwd = scanner.nextLine();
                } else if(option == 2) {
                    message_handler.displayMessage("Enter your new username: ");
                    String username = scanner.nextLine();
                    message_handler.displayMessage("Enter your new password: ");
                    String passwd = scanner.nextLine();
                    message_handler.displayMessage("Account created successfully!");
                } else if(option == 3) {
                    message_handler.displayMessage("Exiting program ...");
                    break;
                }
            } catch (InputMismatchException e) {
                message_handler.displayErrorMessage("Input must be integer","1","3");
            }
            scanner.nextLine();
        }
        scanner.close();
    }
}