package org.trypticon.bot.adapters.core;

import org.trypticon.irc.DefaultIrcAdapter;
import org.trypticon.irc.IrcConnection;
import org.trypticon.bot.util.Configuration;
import org.trypticon.bot.util.Configurable;
import org.trypticon.irc.IrcName;
import org.trypticon.irc.IrcChannelName;
import org.trypticon.irc.IrcUserName;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Adapter for handling automatic join of channels.
 */
public class ChannelAdapter extends DefaultIrcAdapter implements Configurable {
    /**
     * The list of channels.
     */
    private List<IrcChannelName> channels;

    @Override
    public void configure(Configuration props) {
        String channels = props.getString("channels");
        this.channels = new ArrayList<IrcChannelName>(4);
        StringTokenizer tok = new StringTokenizer(channels, ", ");
        while (tok.hasMoreTokens()) {
            this.channels.add(new IrcChannelName(tok.nextToken()));
        }
    }

    public void handleNumeric(IrcConnection conn, int numeric, IrcName source, List args) {
        // 001 is sent when registered, hopefully on all networks.
        if (numeric == 1) {
            for (IrcChannelName channel : channels) {
                conn.sendJOIN(channel, null);
            }
        }
    }

    public void handleKICK(IrcConnection conn, IrcName source, IrcChannelName channel, IrcUserName user, String message) {
        // Should check the username as well, but for now we'll just do it for any kick anyway.
        if (channels.contains(channel)) {
            conn.sendJOIN(channel, null);
        }
    }

    public void handleINVITE(IrcConnection conn, IrcName source, IrcChannelName channel) {
        // Only join on invite if the channel is in the list.
        if (channels.contains(channel)) {
            conn.sendJOIN(channel, null);
        }
    }

}
