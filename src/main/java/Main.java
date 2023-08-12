import main.ClientManager;
import main.InputManager;
import main.MessageManager;

public class Main {
    public static void main(String[] args) {
        MessageManager message_handler = new MessageManager();
        ClientManager client_handler = new ClientManager();
        InputManager input_handler = new InputManager();
        if(args.length < 1) {
            message_handler.displayError("Not enough arguments provided ...");
            System.exit(1);
        }
        new Thread(() -> {
            // CONNECTION TO SERVER SUCCESSFULLY
            boolean is_running = true, in_session;
            int main_option, user_option;
            // MAIN LOOP
            while(is_running) {
                if(client_handler.connectToServer(args[0])) {
                    message_handler.displayLoginSignupMenu();
                    // USER'S MAIN OPTION
                    main_option = input_handler.getIntInput(1, 3);
                    if(main_option == 0) message_handler.displayError("Enter number between 1 & 3");
                    else if(main_option == 1) {
                        // REGISTER NEW USER
                        String username = input_handler.getStringInput("new username");
                        String passwd = input_handler.getStringInput("new password");
                        if (client_handler.registerUser(username, passwd)) {
                            // REGISTRATION SUCCESSFUL
                            message_handler.print("User created successfully ...");
                        } else {
                            // REGISTRATION FAILED
                            message_handler.print("User creation failed ...");
                        }
                    } else if(main_option == 2) {
                        // LOGIN
                        String username = input_handler.getStringInput("username");
                        String passwd = input_handler.getStringInput("password");
                        if(client_handler.userLogin(username, passwd)) {
                            // LOGIN SUCCESSFUL
                            message_handler.print("Login successful ...");
                            message_handler.setUsername(username);
                            in_session = true;
                            // USER SESSION
                            while(in_session) {
                                message_handler.displaySessionMenu();
                                // USER'S SESSION OPTION
                                user_option = input_handler.getIntInput(1, 6);
                                if(user_option == 0) message_handler.displayError("Enter number between 1 & 6");
                                else if(user_option == 1) {
                                    // INFORMATION
                                    message_handler.displayInfoOptions();
                                } else if(user_option == 2) {
                                    // CONTACTS
                                    message_handler.displayContactOptions();
                                } else if(user_option == 3) {
                                    //ADMINISTRATION
                                    message_handler.displayAdminOptions();
                                    user_option = input_handler.getIntInput(1, 3);
                                    if(user_option == 0) message_handler.displayError("Enter a number between 1 & 3");
                                    else if(user_option == 1) {
                                        // CHANGE STATUS
                                    } else if(user_option == 2) {
                                        // DELETE ACCOUNT
                                        if(input_handler.getConfirmation()) {
                                            if(client_handler.deleteAccount()) {
                                                message_handler.print("Account deleted successfully");
                                                in_session = false;
                                            } else {
                                                message_handler.displayError("Account deletion unsuccessful");
                                            }
                                        }
                                    }
                                } else if(user_option == 4) {
                                    // CHAT
                                    message_handler.displayChatOptions();
                                } else if(user_option == 5) {
                                    // NOTIFICATION
                                } else if(user_option == 6) {
                                    // CLOSE SESSION
                                    message_handler.print("Closing session ...\n");
                                    in_session = false;
                                }
                            }
                        } else {
                            // LOGIN FAILED
                            message_handler.displayError("Login failed ...\n");
                        }
                    } else if(main_option == 3) {
                        // DISCONNECT FROM SERVER
                        message_handler.print("Disconnecting from server ...");
                        is_running = false;
                    }
                    client_handler.disconnectFromServer();
                } else {
                    // FAILED TO CONNECT TO SERVER
                    message_handler.displayError("Server connection error\n");
                    is_running = false;
                }
            }
            message_handler.print("\nProgram closed");
        }).start();
    }
}