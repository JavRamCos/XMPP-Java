import main.ClientManager;
import main.InputManager;
import main.MessageManager;

import java.util.List;

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
                    main_option = input_handler.getIntInput(1, 3, 0);
                    if(main_option == 0) message_handler.displayError("Enter number between 1 & 3");
                    else if(main_option == 1) {
                        // REGISTER NEW USER
                        String username = input_handler.getStringInput("new username (exit to cancel)");
                        if(username.equals("exit")) continue;
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
                        String username = input_handler.getStringInput("username (exit to cancel)");
                        if(username.equals("exit")) continue;
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
                                user_option = input_handler.getIntInput(1, 6, 0);
                                if(user_option == 0) message_handler.displayError("Enter number between 1 & 6");
                                else if(user_option == 1) {
                                    // INFORMATION
                                    message_handler.displayInfoOptions();
                                    user_option = input_handler.getIntInput(1, 4, 0);
                                    if(user_option == 0) message_handler.displayError("Enter number between 1 & 4");
                                    else if(user_option == 1) {
                                        // ALL USERS INFO
                                        List<List<String>> info = client_handler.getRosterInformation(1, "");
                                        if(info.size() == 0) {
                                            message_handler.displayError("No users found");
                                        } else {
                                            message_handler.displayUsersInfo(info);
                                        }
                                    } else if(user_option == 2) {
                                        // USER INFO
                                        String usr = input_handler.getStringInput("username");
                                        List<List<String>> info = client_handler.getRosterInformation(2, usr);
                                        if(info.size() == 0) {
                                            message_handler.displayError("No user found");
                                        } else {
                                            message_handler.displayUsersInfo(info);
                                        }
                                    } else if(user_option == 3) {
                                        // SELF INFO
                                        List<String> info = client_handler.getUserInformation();
                                        message_handler.print("-> Username: "+info.get(0));
                                        message_handler.print("   Password: "+info.get(1));
                                    }
                                } else if(user_option == 2) {
                                    // CONTACTS
                                    message_handler.displayContactOptions();
                                    user_option = input_handler.getIntInput(1, 2, 0);
                                    if(user_option == 0) message_handler.displayError("Enter a number between 1 & 3");
                                    else if(user_option == 1) {
                                        // SEND FRIEND REQUEST
                                        String usr = input_handler.getStringInput("username");
                                        String nick = input_handler.getStringInput("user's nickname");
                                        int res = client_handler.sendFriendRequest(usr, nick);
                                        if(res < 0) message_handler.displayError("Unable to send friend request");
                                        else if(res == 0) message_handler.displayError("User is already in roster");
                                        else if(res == 1) message_handler.print("Friend request send successfully");
                                    }
                                } else if(user_option == 3) {
                                    //ADMINISTRATION
                                    message_handler.displayAdminOptions();
                                    user_option = input_handler.getIntInput(1, 3, 0);
                                    if(user_option == 0) message_handler.displayError("Enter a number between 1 & 3");
                                    else if(user_option == 1) {
                                        // CHANGE STATUS
                                        message_handler.displayStatusOptions();
                                        int st_option = input_handler.getIntInput(1, 5, 1);
                                        String message = input_handler.getStringInput("status");
                                        if(client_handler.changeUserStatus(st_option, message)) {
                                            message_handler.print("Status changed successfully");
                                        } else {
                                            message_handler.displayError("Status change unsuccessful");
                                        }
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
                                    // NOTIFICATION
                                    message_handler.displayNotificationsOptions();
                                } else if(user_option == 5) {
                                    // CHAT
                                    message_handler.displayChatOptions();
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