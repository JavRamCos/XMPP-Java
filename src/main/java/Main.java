import main.ClientManager;
import main.MessageManager;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MessageManager message_handler = new MessageManager();
        ClientManager client_handler = new ClientManager();
        if(args.length < 1) {
            message_handler.displayError("Not enough arguments provided ...");
            System.exit(1);
        }
        new Thread(() -> {
            if(client_handler.connectToServer(args[0])) {
                // CONNECTION TO SERVER SUCCESSFULLY
                Scanner scanner = new Scanner(System.in);
                message_handler.print("Connected ...");
                // MAIN LOOP
                while(true) {
                    message_handler.displayLoginSignupMenu();
                    message_handler.print("> Enter input");
                    // USER 1st OPTION
                    int option;
                    try {
                        option = scanner.nextInt();
                    } catch(InputMismatchException e) {
                        message_handler.displayError("Input must be of type INT");
                        scanner.nextLine();
                        continue;
                    }
                    if(option == 1) {
                        // REGISTER NEW USER
                    } else if(option == 2) {
                        // LOGIN
                    } else if(option == 3) {
                        // DISCONNECT FROM SERVER
                        message_handler.print("Disconnecting from server ...");
                        break;
                    } else {
                        // INVALID OPTION
                        message_handler.displayError("Enter number between 1 & 3");
                    }
                }
                client_handler.disconnectFromServer();
                message_handler.print("Disconnected from server");
            } else {
                // FAILED TO CONNECT TO SERVER
                message_handler.displayError("Error connecting to server ...");
            }
        }).start();
    }
}