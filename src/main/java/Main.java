import main.ClientManager;
import main.MessageManager;

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
            // CONNECTION TO SERVER SUCCESSFULLY
            Scanner scanner = new Scanner(System.in);
            boolean is_running = true;
            int main_option = 0;
            // MAIN LOOP
            while(is_running) {
                if(client_handler.connectToServer(args[0])) {
                    message_handler.displayLoginSignupMenu();
                    message_handler.print("> Enter input");
                    // USER 1st OPTION
                    try {
                        main_option = Integer.parseInt(scanner.nextLine());
                    } catch(NumberFormatException e) {
                        message_handler.displayError("Input must be of type INT");
                        continue;
                    }
                    if(main_option == 1) {
                        // REGISTER NEW USER
                        System.out.println("\nEnter username: ");
                        String username = scanner.nextLine();;
                        System.out.println("Enter password: ");
                        String passwd = scanner.nextLine();
                        if (client_handler.registerUser(username, passwd)) {
                            // REGISTRATION SUCCESSFUL
                            message_handler.print("User created successfully ...");
                        } else {
                            // REGISTRATION FAILED
                            message_handler.print("User creation failed ...");
                        }
                    } else if(main_option == 2) {
                        // LOGIN
                        System.out.println("\nEnter username: ");
                        String username = scanner.nextLine();;
                        System.out.println("Enter password: ");
                        String passwd = scanner.nextLine();
                        if(client_handler.userLogin(username, passwd)) {
                            // LOGIN SUCCESSFUL
                            message_handler.print("Login successful ...");
                            message_handler.setUsername(username);
                            message_handler.displayClientMenu();
                        } else {
                            // LOGIN FAILED
                            message_handler.displayError("Login failed ...");
                        }
                    } else if(main_option == 3) {
                        // DISCONNECT FROM SERVER
                        message_handler.print("Disconnecting from server ...");
                        is_running = false;
                    } else {
                        // INVALID OPTION
                        message_handler.displayError("Enter number between 1 & 3");
                    }
                    client_handler.disconnectFromServer();
                } else {
                    // FAILED TO CONNECT TO SERVER
                    message_handler.displayError("Server connection error");
                    is_running = false;
                }
            }
            message_handler.print("Program closed");
        }).start();
    }
}