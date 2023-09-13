import main.ClientManager;
import main.InputManager;
import main.OutputManager;
import main.Message;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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

    public static List<List<String>> getTopology() {
        // DEFINE NETWORK TOPOLOGY (NON DIRECTIONAL)
        List<List<String>> topology = new ArrayList<>();
        // (list[0] -> list[1] -> list[2]) | (list[2] -> list[1] -> list[0])
        topology.add(new ArrayList<>(Arrays.asList("A", "1", "B")));
        topology.add(new ArrayList<>(Arrays.asList("A", "3", "C")));
        topology.add(new ArrayList<>(Arrays.asList("B", "2", "C")));
        return topology;
    }

    public static List<String> getNodeNames(List<List<String>> topology) {
        // GET each list[0] & list[2] in topology and return set result
        Set<String> hash_set = new HashSet<>();
        for(List<String> temp : topology) {
            hash_set.add(temp.get(0));
            hash_set.add(temp.get(2));
        }
        return new ArrayList<>(hash_set);
    }

    public static Message parseJSON() {
        // READ .JSON FILE AND
        String json_file = "src/Message.json";
        StringBuilder json = new StringBuilder();
        json.append("[");
        try {
            BufferedReader br = new BufferedReader(new FileReader(json_file));
            String line;
            while((line = br.readLine()) != null) {
                // APPEND INFORMATION
                json.append(line);
            }
            json.append("]");
            // CREATE NEW JSON ARRAY OBJECT
            JSONArray jsonArray = new JSONArray(json.toString());
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            // STORE INFORMATION (MESSAGE CLASS)
            Message result = new Message();
            result.setType(jsonObject.getString("type"));
            result.setFrom(jsonObject.getJSONObject("headers").getString("from"));
            result.setTo(jsonObject.getJSONObject("headers").getString("to"));
            result.setHop_count(jsonObject.getJSONObject("headers").getInt("hop_count"));
            if(jsonObject.getString("type").equalsIgnoreCase("message")) {
                result.setMessage(jsonObject.getString("payload"));
            } else {
                // OBTAIN TABLE NEW INFORMATION
                List<Object> temp = jsonObject.getJSONObject("payload").getJSONArray("distance_vector").toList();
                List<List<String>> table_info = new ArrayList<>();
                for(Object obj : temp) {
                    // OBTAIN VALUES
                    String vvv = obj.toString().substring(1, obj.toString().length()-1);
                    List<String> entries = new ArrayList<>(Arrays.stream(vvv.split(",")).toList());
                    entries.replaceAll(String::strip);
                    table_info.add(entries);
                }
                result.setTable_info(table_info);
            }
            // RETURN INFORMATION
            return result;
        } catch (IOException e) {
            // ERROR OCURRED DURING .JSON FILE READING
            System.out.println("An error ocurred while reading .json file");
            return null;
        }
    }
}