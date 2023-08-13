package main;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PresenceBuilder;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;
import java.io.IOException;

public class ClientManager {
    AbstractXMPPConnection connection;
    AccountManager acc_manager;
    String s_username, s_password;
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
