package main;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PresenceBuilder;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ClientManager {
    AbstractXMPPConnection connection;
    AccountManager acc_manager;
    Roster roster;
    String s_username, s_password, host_name;
    public ClientManager() {
        this.s_username = "";
        this.s_password = "";
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
            this.acc_manager = AccountManager.getInstance(this.connection);
            this.host_name = "@"+server_name;
            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
            this.roster = Roster.getInstanceFor(this.connection);
        } catch (SmackException | IOException | XMPPException | InterruptedException e) {
            return false;
        }
        return true;
    }

    public boolean registerUser(String username, String password) {
        try {
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
        } catch (XMPPException | SmackException | IOException | InterruptedException e) {
            return false;
        }
        return true;
    }

    public boolean changeUserStatus(int option, String message) {
        PresenceBuilder pres = new Presence(Presence.Type.available).asBuilder();
        pres.setPriority(1);
        pres.setStatus(message);
        if(option == 1) pres.setMode(Presence.Mode.available);
        else if(option == 2) pres.setMode(Presence.Mode.chat);
        else if(option == 3) pres.setMode(Presence.Mode.away);
        else if(option == 4) pres.setMode(Presence.Mode.xa);
        else if(option == 5) pres.setMode(Presence.Mode.dnd);
        try {
            this.connection.sendStanza(pres.build());
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            return false;
        }
        return true;
    }

    public List<List<String>> getRosterInformation(int option, String username) {
        List<List<String>> result = new ArrayList<>();
        try {
            if (!this.roster.isLoaded()) this.roster.reloadAndWait();
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

    public int sendFriendRequest(String username, String nickname) {
        try {
            if (!this.roster.isLoaded()) this.roster.reloadAndWait();
            EntityBareJid entity = JidCreate.entityBareFrom(username+this.host_name);
            if(this.roster.contains(entity)) return 0;
            this.roster.createItemAndRequestSubscription(entity, nickname, null);
        } catch (XmppStringprepException | SmackException.NotConnectedException
                | SmackException.NotLoggedInException | InterruptedException
                | XMPPException.XMPPErrorException | SmackException.NoResponseException e) {
            return -1;
        }
        return 1;
    }

    public boolean changeUserPassword(String password) {
        this.s_password = password;
        return true;
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
        this.connection.disconnect();
        this.s_username = "";
        this.s_password = "";
    }
}
