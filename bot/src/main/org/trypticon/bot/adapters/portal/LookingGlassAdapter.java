package org.trypticon.bot.adapters.portal;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.trypticon.irc.DefaultIrcAdapter;
import org.trypticon.irc.IrcConnection;
import org.trypticon.bot.util.Configuration;
import org.trypticon.bot.util.Configurable;
import org.trypticon.irc.IrcName;
import org.trypticon.irc.IrcNameFactory;
import org.trypticon.irc.IrcUserName;
import org.trypticon.irc.IrcChannelName;

/**
 * Adapter for portal (cross-server chat) functionality.
 *
 * @author Trejkaz
 */
public class LookingGlassAdapter extends DefaultIrcAdapter implements Configurable {
    /**
     * The name of this portal.
     */
    private String name;

    /**
     * The name of the portal to link to.
     */
    private String destName;

    /**
     * The channels to use.
     */
    private IrcName channel;

    /**
     * A map of registered portals.
     */
    private static Map<String, LookingGlassAdapter> portalMap = new HashMap<String, LookingGlassAdapter>(4);

    //TODO: Move somewhere else
    private IrcConnection conn;

    /**
     * Initialise from properties.
     *
     * @param props the properties.
     */
    @Override
    public void configure(Configuration props) {
        name = props.getString("name");
        destName = props.getString("destination");
        channel = IrcNameFactory.create(props.getString("channel"));

        // Put it in the map so we can get it out from another portal.
        portalMap.put(name, this);
    }

    /**
     * Called when connected.
     * Used to store off the connection so another portal can get at it.
     *
     * @param conn the connection.
     */
    public void connected(IrcConnection conn) {
        this.conn = conn;
    }

    /**
     * Called when disconnected.
     * Used to clear the connection so another portal knows it's disconnected.
     *
     * @param conn the connection.
     */
    public void disconnected(IrcConnection conn) {
        this.conn = null;
    }

    public void handleJOIN(IrcConnection conn, IrcName source, IrcChannelName channel) {
        IrcUserName user = (IrcUserName) source;
        send(channel, "* " + user.getNick() +
                " [" + user.getUsername() + "@" + user.getHostname() +
                "] joined the channel.");
    }

    public void handlePART(IrcConnection conn, IrcName source, IrcChannelName channel) {
        IrcUserName user = (IrcUserName) source;
        send(channel, "* " + user.getNick() +
                " [" + user.getUsername() + "@" + user.getHostname() +
                "] parted the channel.");
    }

    public void handleQUIT(IrcConnection conn, IrcName source, String message) {
        IrcUserName user = (IrcUserName) source;
        send(channel, "* " + user.getNick() +
                " [" + user.getUsername() + "@" + user.getHostname() +
                "] has quit IRC (" + message + ")");
    }

    public void handleKICK(IrcConnection conn, IrcName source, IrcChannelName channel, IrcUserName user, String message) {
        send(channel, "* " + source.toShortString() + " has kicked " +
                user.toShortString() + " from " + channel.toShortString() + " (" + message + ")");
    }

    public void handleMODE(IrcConnection conn, IrcName source, IrcName target, List args) {
        StringBuffer buf = new StringBuffer();
        buf.append("* ").append(source.toShortString());
        buf.append(" sets mode on ").append(target.toShortString()).append(":");
        for (Object arg : args) {
            buf.append(" ").append(arg);
        }
        send(target, buf.toString());
    }

    public void handlePRIVMSG(IrcConnection connection, IrcName source, IrcName target, String message) {
        send(target, "<" + source.toShortString() + "> " + message);
    }

    public void handleNOTICE(IrcConnection conn, IrcName source, IrcName target, String message) {
        // Don't print server notices!
        if (source instanceof IrcUserName) {
            send(target, "-" + source.toShortString() + "- " + message);
        }
    }

    public void handleCTCP(IrcConnection conn, IrcName source, IrcName target,
                           String type, String rest) {
        // Handle the /me hack gracefully.
        if (type.equalsIgnoreCase("ACTION")) {
            send(target, "* " + source.toShortString() + " " + rest);
        } else {
            send(target, ">>> " + source.toShortString() + " sent CTCP: " + type + " " + rest);
        }
    }

    public void handleCTCPREPLY(IrcConnection conn, IrcName source, IrcName target,
                                String type, String rest) {
        send(target, ">>> " + source.toShortString() + " sent CTCP REPLY: " + type + " " + rest);
    }

    /**
     * Convenience method to send a message to another portal,
     * assuming the channel is the one we are mirroring.
     *
     * @param channel the channel the original message was sent from.
     * @param line the line to send.
     */
    private void send(IrcName channel, String line) {
        if (!this.channel.equals(channel)) {
            return;
        }
        LookingGlassAdapter dest = portalMap.get(destName);
        String prefixedLine = "[" + name + "] " + line;
        if (dest.conn != null) {
            dest.conn.sendPRIVMSG(dest.channel, prefixedLine);
        }
    }

}
