package main;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;

public class ClientManager {
    AbstractXMPPConnection connection;
    public ClientManager() {

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

    public void disconnectFromServer() {
        this.connection.disconnect();
    }
}
