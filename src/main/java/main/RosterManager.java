package main;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterListener;
import org.jxmpp.jid.Jid;

import java.util.Collection;

public class RosterManager implements RosterListener {
    @Override
    public void entriesAdded(Collection<Jid> collection) {}

    @Override
    public void entriesUpdated(Collection<Jid> collection) {}

    @Override
    public void entriesDeleted(Collection<Jid> collection) {}

    @Override
    public void presenceChanged(Presence presence) {
        String usr = presence.getFrom().toString().substring(0, presence.getFrom().toString().indexOf("@"));
        String status = presence.getStatus() == null ? " " : " ("+presence.getStatus()+") ";
        System.out.print("\n***** "+usr+" is now " +presence.getMode().toString()+status+"*****");
    }
}
