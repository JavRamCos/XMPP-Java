package main;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.*;

public class ClientManager {
    AbstractXMPPConnection connection;
    AccountManager acc_manager;
    Roster roster;
    RosterManager roster_handler;
    SubscriptionManager sub_handler;
    ChatManager chat_manager;
    MessageManager messages_handler;
    MultiUserChatManager mult_chat_manager;
    MultiUserChat chat_room;
    RoomManager room_handler;
    String s_username, s_password, host_name;

    public ClientManager() {
        this.s_username = "";
        this.s_password = "";
        setListeners();
    }

    public boolean connectToServer(String server_name) {
        try {
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration
                    .builder()
                    .setXmppDomain(server_name)
                    .setHost(server_name)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setSendPresence(true)
                    .build();
            this.connection = new XMPPTCPConnection(config);
            this.connection.connect();
            this.host_name = "@"+server_name;
        } catch (SmackException | IOException | XMPPException | InterruptedException e) {
            return false;
        }
        return true;
    }

    public void setListeners() {
        this.roster_handler = new RosterManager();
        this.sub_handler = new SubscriptionManager();
        this.messages_handler = new MessageManager();
        this.room_handler = new RoomManager();
    }

    public boolean registerUser(String username, String password) {
        try {
            this.acc_manager = AccountManager.getInstance(this.connection);
            if (this.acc_manager.supportsAccountCreation()) {
                this.acc_manager.sensitiveOperationOverInsecureConnection(true);
                this.acc_manager.createAccount(Localpart.from(username), password);
            }
        } catch (XMPPException.XMPPErrorException | SmackException.NotConnectedException
                 | SmackException.NoResponseException | InterruptedException | XmppStringprepException e) {
            return false;
        }
        return true;
    }

    public boolean userLogin(String username, String password) {
        try {
            this.connection.login(username, password);
            this.s_username = username;
            this.s_password = password;
            this.acc_manager = AccountManager.getInstance(this.connection);
            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
            this.roster = Roster.getInstanceFor(this.connection);
            this.roster.addRosterListener(this.roster_handler);
            this.roster.addSubscribeListener(this.sub_handler);
            if(!this.roster.isLoaded()) roster.reloadAndWait();
            this.chat_manager = ChatManager.getInstanceFor(this.connection);
            this.chat_manager.addIncomingListener(this.messages_handler);
            this.mult_chat_manager = MultiUserChatManager.getInstanceFor(this.connection);
        } catch (XMPPException | SmackException | IOException | InterruptedException e) {
            return false;
        }
        return true;
    }

    public boolean changeUserStatus(int option, String message) {
        try {
            Stanza pres = new Presence(Type.available, message, 42, Mode.available);
            if(option == 2) pres = new Presence(Type.available, message, 42, Mode.chat);
            else if(option == 3) pres = new Presence(Type.available, message, 42, Mode.away);
            else if(option == 4) pres = new Presence(Type.available, message, 42, Mode.xa);
            else if(option == 5) pres = new Presence(Type.available, message, 42, Mode.dnd);
            this.connection.sendStanza(pres);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            return false;
        }
        return true;
    }

    public List<List<String>> getRosterInformation(int option, String username) {
        List<List<String>> result = new ArrayList<>();
        try {
            this.roster.reloadAndWait();
            Collection<RosterEntry> entries = this.roster.getEntries();
            Presence presence;
            if(option == 1) {
                // ALL USERS INFO
                for(RosterEntry entry : entries) {
                    presence = this.roster.getPresence(entry.getJid());
                    String name = entry.getJid().toString().substring(0, entry.getJid().toString().indexOf("@"));
                    String status = presence.getMode().toString();
                    String message = presence.getStatus() == null ? "" : presence.getStatus();
                    String available = presence.isAvailable() ? "Yes" : "No";
                    result.add(Arrays.asList(name, status, message, available));
                }
            } else {
                // USER INFO
                for(RosterEntry entry : entries) {
                    presence = this.roster.getPresence(entry.getJid());
                    String name = entry.getJid().toString().substring(0, entry.getJid().toString().indexOf("@"));
                    if(name.equals(username)) {
                        String status = presence.getMode().toString();
                        String message = presence.getStatus() == null ? "" : presence.getStatus();
                        String available = presence.isAvailable() ? "Yes" : "No";
                        result.add(Arrays.asList(name, status, message, available));
                        break;
                    }
                }
            }
        } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException
                 | InterruptedException e) {
            return result;
        }
        return result;
    }

    public List<String> getUserInformation() {
        return Arrays.asList(this.s_username, this.s_password);
    }

    public int sendFriendRequest(String username) {
        try {
            this.roster.reloadAndWait();
            EntityBareJid entity = JidCreate.entityBareFrom(username+this.host_name);
            if(this.roster.contains(entity)) return 0;
            this.roster.createItemAndRequestSubscription(entity, username, null);
        } catch (XmppStringprepException | SmackException.NotConnectedException
                | SmackException.NotLoggedInException | InterruptedException
                | XMPPException.XMPPErrorException | SmackException.NoResponseException e) {
            return -1;
        }
        return 1;
    }

    public void handleRequests() {
        Map<Jid, Presence> requests = this.sub_handler.getRequests();
        int num_requests = requests.size();
        for(Map.Entry<Jid, Presence> entry : requests.entrySet()) {
            Jid jid = entry.getKey();
            Presence pres = entry.getValue();
            String usr = pres.getFrom().toString().substring(0, pres.getFrom().toString().indexOf("@"));
            OutputManager.getInstance().print("*** " + num_requests + "Requests Left ***");
            if(InputManager.getInstance().getConfirmation("Approve "+usr+"'s request")) {
                Stanza subscribed = new Presence(Type.subscribed);
                subscribed.setTo(jid);
                Stanza subscribe = new Presence(Type.subscribe);
                subscribe.setTo(jid);
                try {
                    this.connection.sendStanza(subscribe);
                    this.connection.sendStanza(subscribed);
                } catch (SmackException.NotConnectedException | InterruptedException e) {
                    OutputManager.getInstance().displayError("Unable to process request");
                }
            }
            num_requests--;
        }
        this.sub_handler.resetRequests();
    }

    public int getPendingRequests() {
        return this.sub_handler.getRequests().size();
    }

    public void chatWithUser(String username) {
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(username+this.host_name);
            if(!this.roster.contains(jid)) {
                OutputManager.getInstance().displayError("User is not in your contacts list");
                return;
            }
            Chat chat = ChatManager.getInstanceFor(this.connection).chatWith(jid);
            OutputManager.getInstance().print("====== "+username+" CHAT ======");
            String message;
            while(true) {
                message = InputManager.getInstance().getStringInput("(exit to leave chat)");
                if(message.equals("exit")) break;
                chat.send(message);
            }
            OutputManager.getInstance().print("====================");
        } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException e) {
            OutputManager.getInstance().displayError("Chat could not be established with "+username);
        }
    }

    public void chatWithRoom(String room_name) {
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(room_name+"@conference."+this.host_name.substring(1));
            MultiUserChat muc = this.mult_chat_manager.getMultiUserChat(jid);
            MultiUserChat.MucCreateConfigFormHandle form = muc.createOrJoin(Resourcepart.from(this.s_username));
            if(form != null) {
                OutputManager.getInstance().print("Created room "+room_name);
                form.makeInstant();
                form.getConfigFormManager().submitConfigurationForm();
            }
            else OutputManager.getInstance().print("Joined room "+room_name);
            muc.addMessageListener(this.room_handler);
            while(true) {
                String message = InputManager.getInstance().getStringInput("(exit to leave room)");
                if(message.equals("exit")) break;
                muc.sendMessage(message);
            }
            muc.removeMessageListener(this.room_handler);
            muc.leave();
        } catch (XmppStringprepException | XMPPException.XMPPErrorException | SmackException.NotConnectedException |
                 SmackException.NoResponseException | InterruptedException |
                 MultiUserChatException.NotAMucServiceException | MultiUserChatException.MucNotJoinedException |
                 MultiUserChatException.MucAlreadyJoinedException e) {
            OutputManager.getInstance().displayError("Unable to connect to room "+room_name);
        }
    }

    public boolean deleteAccount() {
        try {
            if(this.acc_manager != null) {
                this.acc_manager.deleteAccount();
            }
        } catch (XMPPException.XMPPErrorException | SmackException.NotConnectedException |
                 SmackException.NoResponseException | InterruptedException e) {
            return false;
        }
        return true;
    }

    public void disconnectFromServer() {
        this.sub_handler.resetRequests();
        if(this.roster != null) {
            this.roster.removeRosterListener(this.roster_handler);
            this.roster.removeSubscribeListener(this.sub_handler);
        }
        if(this.chat_manager != null) {
            this.chat_manager.removeIncomingListener(this.messages_handler);
        }
        this.connection.disconnect();
        this.s_username = "";
        this.s_password = "";
    }
}
