package org.trypticon.bot.adapters.core;

import org.trypticon.irc.DefaultIrcAdapter;
import org.trypticon.irc.IrcConnection;
import org.trypticon.irc.IrcName;

/**
 * Adapter for handling PING-PONG messages.
 *
 * @author Trejkaz
 */
public class PingAdapter extends DefaultIrcAdapter {
    public void handlePING(IrcConnection conn, IrcName source, String daemon) {
        System.err.println("[PingAdapter] PING? PONG!");
        conn.sendPONG(daemon);
    }
}
