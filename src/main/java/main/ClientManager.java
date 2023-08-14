package main;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
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
    RosterListener rost_listener;
    SubscribeListener sub_listener;
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
        this.rost_listener = new RosterListener() {
            @Override
            public void entriesAdded(Collection<Jid> collection) { }
            @Override
            public void entriesUpdated(Collection<Jid> collection) { }
            @Override
            public void entriesDeleted(Collection<Jid> collection) { }
            @Override
            public void presenceChanged(Presence presence) {
                String usr = presence.getFrom().toString().substring(0, presence.getFrom().toString().indexOf("@"));
                String status = presence.getStatus() == null ? " " : " ("+presence.getStatus()+") ";
                System.out.print("\n***** "+usr+" is now " +presence.getMode().toString()+status+"*****");
            }
        };
        this.sub_listener = new SubscribeListener() {
            @Override
            public SubscribeAnswer processSubscribe(Jid jid, Presence presence) {
                String usr = presence.getFrom().toString().substring(0, presence.getFrom().toString().indexOf("@"));
                System.out.println("\n***** "+usr+"is request a subscription *****");
                return null;
            }
        };
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
            this.roster.addRosterListener(this.rost_listener);
            this.roster.addSubscribeListener(this.sub_listener);
            if(!this.roster.isLoaded()) roster.reloadAndWait();
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
            this.roster.reloadAndWait();
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
        this.roster.removeRosterListener(this.rost_listener);
        this.roster.removeSubscribeListener(this.sub_listener);
        this.connection.disconnect();
        this.s_username = "";
        this.s_password = "";
    }
}
