package main;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class RoomManager implements MessageListener {
    @Override
    public void processMessage(Message message) {
        String sender = message.getFrom().toString();
        if(sender.contains("/")) {
            String room_name = sender.substring(0, sender.indexOf("@"));
            String usr = sender.substring(sender.indexOf("/")+1);
            OutputManager.getInstance().print("\n-> ["+room_name+"@"+usr+"] "+message.getBody());
        }
    }
}
