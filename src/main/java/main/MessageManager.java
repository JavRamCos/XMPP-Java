package main;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;

public class MessageManager implements IncomingChatMessageListener {
    @Override
    public void newIncomingMessage(EntityBareJid entityBareJid, Message message, Chat chat) {
        String usr = entityBareJid.toString().substring(0, entityBareJid.toString().indexOf("@"));
        OutputManager.getInstance().print("\n-> ("+usr+") "+message.getBody());
    }
}
