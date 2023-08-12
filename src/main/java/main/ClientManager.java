package main;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;
import java.io.IOException;

public class ClientManager {
    AbstractXMPPConnection connection;
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
        } catch (SmackException | IOException | XMPPException | InterruptedException e) {
            return false;
        }
        return true;
    }

    public boolean registerUser(String username, String password) {
        try {
            AccountManager accounts = AccountManager.getInstance(this.connection);
            if (accounts.supportsAccountCreation()) {
                accounts.createAccount(Localpart.from(username), password);
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

    public void disconnectFromServer() {
        this.connection.disconnect();
        this.s_username = "";
        this.s_password = "";
    }
}
