package org.trypticon.bot.adapters.debug;

import org.trypticon.irc.IrcAdapter;
import org.trypticon.irc.IrcConnection;
import org.trypticon.irc.IrcMessage;

/**
 * Adapter for printing debug messages.
 *
 * @author Trejkaz
 */
public class DebugAdapter extends IrcAdapter {

    public void messageReceived(IrcConnection conn, IrcMessage line) {
        System.err.println("[DEBUG/" + conn.getId() + "] Message Received: " + line.getRawLine());
    }

    public void messageSent(IrcConnection conn, String line) {
        System.err.println("[DEBUG/" + conn.getId() + "] Message Sent: " + line);
    }

    public void connecting(IrcConnection conn) {
        System.err.println("[DEBUG/" + conn.getId() + "] Connecting...");
    }

    public void connected(IrcConnection conn) {
        System.err.println("[DEBUG/" + conn.getId() + "] Connected.");
    }

    public void disconnected(IrcConnection conn) {
        System.err.println("[DEBUG/" + conn.getId() + "] Disconnected.");
    }
}
