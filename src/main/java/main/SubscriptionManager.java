package main;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.SubscribeListener;
import org.jxmpp.jid.Jid;

import java.util.HashMap;
import java.util.Map;

public class SubscriptionManager implements SubscribeListener {
    Map<Jid, Presence> requests;

    public SubscriptionManager() {
        this.requests = new HashMap<>();
    }

    @Override
    public SubscribeAnswer processSubscribe(Jid jid, Presence presence) {
        String usr = presence.getFrom().toString().substring(0, presence.getFrom().toString().indexOf("@"));
        System.out.println("\n***** "+usr+" sent You An Invitation *****");
        this.requests.put(jid, presence);
        return null;
    }

    public Map<Jid, Presence> getRequests() {
        return this.requests;
    }

    public void resetRequests() {
        this.requests.clear();
    }
}
