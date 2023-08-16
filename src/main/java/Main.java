import main.ClientManager;
import main.InputManager;
import main.OutputManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ClientManager client_handler = new ClientManager();
        if(args.length < 1) {
            OutputManager.getInstance().displayError("Not enough arguments provided ...");
            System.exit(1);
        }
        new Thread(() -> {
            // CONNECTION TO SERVER SUCCESSFULLY
            boolean is_running = true, in_session;
            int main_option, user_option;
            // MAIN LOOP
            while(is_running) {
                if(client_handler.connectToServer(args[0])) {
                    OutputManager.getInstance().displayLoginSignupMenu();
                    // USER'S MAIN OPTION
                    main_option = InputManager.getInstance().getIntInput(1, 3, 0);
                    if(main_option == 0) OutputManager.getInstance().displayError("Enter number between 1 & 3");
                    else if(main_option == 1) {
                        // REGISTER NEW USER
                        String username = InputManager.getInstance().getStringInput("Enter new username (exit to cancel)");
                        if(username.equals("exit")) continue;
                        String passwd = InputManager.getInstance().getStringInput("Enter new password");
                        if (client_handler.registerUser(username, passwd)) {
                            // REGISTRATION SUCCESSFUL
                            OutputManager.getInstance().print("User created successfully ...");
                        } else {
                            // REGISTRATION FAILED
                            OutputManager.getInstance().print("User creation failed ...");
                        }
                    } else if(main_option == 2) {
                        // LOGIN
                        String username = InputManager.getInstance().getStringInput("Enter username (exit to cancel)");
                        if(username.equals("exit")) continue;
                        String passwd = InputManager.getInstance().getStringInput("Enter password");
                        if(client_handler.userLogin(username, passwd)) {
                            // LOGIN SUCCESSFUL
                            OutputManager.getInstance().print("Login successful ...");
                            OutputManager.getInstance().setUsername(username);
                            in_session = true;
                            // USER SESSION
                            while(in_session) {
                                OutputManager.getInstance().displaySessionMenu();
                                // USER'S SESSION OPTION
                                user_option = InputManager.getInstance().getIntInput(1, 6, 0);
                                if(user_option == 0) OutputManager.getInstance().displayError("Enter number between 1 & 5");
                                else if(user_option == 1) {
                                    // INFORMATION
                                    OutputManager.getInstance().displayInfoOptions();
                                    user_option = InputManager.getInstance().getIntInput(1, 4, 0);
                                    if(user_option == 0) OutputManager.getInstance().displayError("Enter number between 1 & 4");
                                    else if(user_option == 1) {
                                        // ALL USERS INFO
                                        List<List<String>> info = client_handler.getRosterInformation(1, "");
                                        if(info.size() == 0) {
                                            OutputManager.getInstance().print("-- No users found --");
                                        } else {
                                            OutputManager.getInstance().displayUsersInfo(info);
                                        }
                                    } else if(user_option == 2) {
                                        // USER INFO
                                        String usr = InputManager.getInstance().getStringInput("Enter username");
                                        List<List<String>> info = client_handler.getRosterInformation(2, usr);
                                        if(info.size() == 0) {
                                            OutputManager.getInstance().displayError("No user found");
                                        } else {
                                            OutputManager.getInstance().displayUsersInfo(info);
                                        }
                                    } else if(user_option == 3) {
                                        // SELF INFO
                                        List<String> info = client_handler.getUserInformation();
                                        OutputManager.getInstance().print("-> Username: "+info.get(0));
                                        OutputManager.getInstance().print("   Password: "+info.get(1));
                                    }
                                } else if(user_option == 2) {
                                    // CONTACTS
                                    OutputManager.getInstance().displayContactOptions();
                                    user_option = InputManager.getInstance().getIntInput(1, 3, 0);
                                    if(user_option == 0) OutputManager.getInstance().displayError("Enter a number between 1 & 3");
                                    else if(user_option == 1) {
                                        // SEND FRIEND REQUEST
                                        String usr = InputManager.getInstance().getStringInput("Enter username");
                                        int res = client_handler.sendFriendRequest(usr);
                                        if(res < 0) OutputManager.getInstance().displayError("Unable to send friend request");
                                        else if(res == 0) OutputManager.getInstance().displayError("User is already in Contacts List");
                                        else if(res == 1) OutputManager.getInstance().print("Friend request send successfully");
                                    } else if(user_option == 2) {
                                        // MANAGE PENDING REQUESTS
                                        if(client_handler.getPendingRequests() > 0) {
                                            client_handler.handleRequests();
                                        } else {
                                            OutputManager.getInstance().print("No Pending Requests ...");
                                        }
                                    }
                                } else if(user_option == 3) {
                                    //ADMINISTRATION
                                    OutputManager.getInstance().displayAdminOptions();
                                    user_option = InputManager.getInstance().getIntInput(1, 3, 0);
                                    if(user_option == 0) OutputManager.getInstance().displayError("Enter a number between 1 & 3");
                                    else if(user_option == 1) {
                                        // CHANGE STATUS
                                        OutputManager.getInstance().displayStatusOptions();
                                        int st_option = InputManager.getInstance().getIntInput(1, 5, 1);
                                        String message = InputManager.getInstance().getStringInput("Enter new status");
                                        if(client_handler.changeUserStatus(st_option, message)) {
                                            OutputManager.getInstance().print("Status changed successfully");
                                        } else {
                                            OutputManager.getInstance().displayError("Status change unsuccessful");
                                        }
                                    } else if(user_option == 2) {
                                        // DELETE ACCOUNT
                                        if(InputManager.getInstance().getConfirmation("Enter confirmation")) {
                                            if(client_handler.deleteAccount()) {
                                                OutputManager.getInstance().print("Account deleted successfully");
                                                in_session = false;
                                            } else {
                                                OutputManager.getInstance().displayError("Account deletion unsuccessful");
                                            }
                                        }
                                    }
                                } else if(user_option == 4) {
                                    // CHAT
                                    OutputManager.getInstance().displayChatOptions();
                                    user_option = InputManager.getInstance().getIntInput(1, 3, 0);
                                    if(user_option == 0) OutputManager.getInstance().displayError("Enter a number between 1 & 3");
                                    else if(user_option == 1) {
                                        // CHAT WITH USER (1v1)
                                        String usr = InputManager.getInstance().getStringInput("Enter username");
                                        client_handler.chatWithUser(usr);
                                    } else if(user_option == 2) {
                                        // CHAT WITH USERS (ROOM)
                                        String room_name = InputManager.getInstance().getStringInput("Enter room name");
                                        client_handler.chatWithRoom(room_name);
                                    }
                                } else if(user_option == 5) {
                                    // SEND FILE
                                    String usr = InputManager.getInstance().getStringInput("Enter username");
                                    String file_name = InputManager.getInstance().getStringInput("Enter file name");
                                    if(client_handler.fileTransfer(usr, file_name)) {
                                        OutputManager.getInstance().print("File transfered successfully");
                                    }
                                } else if(user_option == 6) {
                                    // CLOSE SESSION
                                    OutputManager.getInstance().print("Closing session ...\n");
                                    in_session = false;
                                }
                            }
                        } else {
                            // LOGIN FAILED
                            OutputManager.getInstance().displayError("Login failed ...\n");
                        }
                    } else if(main_option == 3) {
                        // DISCONNECT FROM SERVER
                        OutputManager.getInstance().print("Disconnecting from server ...");
                        is_running = false;
                    }
                    client_handler.disconnectFromServer();
                } else {
                    // FAILED TO CONNECT TO SERVER
                    OutputManager.getInstance().displayError("Server connection error\n");
                    is_running = false;
                }
            }
            OutputManager.getInstance().print("\nProgram closed");
        }).start();
    }
}