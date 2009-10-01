package org.trypticon.irc;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Class to encapsulate a connection to an IRC server.
 *
 * @author Trejkaz
 */
public class IrcConnection {
    /**
     * The connection ID.
     */
    private String id;

    /**
     * Factory for creating new connections.
     */
    private ConnectionFactory connectionFactory;

    /**
     * Socket connected to the server.
     */
    private Socket socket;

    /**
     * Writer which goes to the server.
     */
    private PrintWriter toServer;

    /**
     * Thread performing the work.
     */
    private Thread runner;

    /**
     * List of subscribed listeners.
     */
    private List<IrcListener> ircListeners = new ArrayList<IrcListener>(5);

    /**
     * Creates an IRC connection.
     *
     * @param id the connection ID.  TODO: Document more.
     * @param connectionFactory the connection factory to use for creating new connections.
     */
    public IrcConnection(String id, ConnectionFactory connectionFactory) {
        this.id = id;
        this.connectionFactory = connectionFactory;
    }

    /**
     * Gets the connection ID.
     *
     * @return the connection ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Starts the connection.
     */
    public synchronized void start() {
        if (runner == null) {
            runner = new Thread(new ConnectionRunner());
            runner.start();
        }
    }

    /**
     * Stopd the connection.
     */
    public synchronized void stop() {
        runner = null;
    }

    /**
     * Sends the USER command.
     *
     * @param username the claimed username.
     * @param hostname the claimed hostname.
     * @param servername the name of the server.
     * @param realname the realname for the user.
     */
    public void sendUSER(String username, String hostname, String servername, String realname) {
        sendRawLine("USER " + username + " " + hostname + " " + servername + " :" + realname);
    }

    /**
     * Sends the USER command.  This version of the method permits omitting the servername,
     * taking the server's name from the connection parameters.
     *
     * @param username the claimed username.
     * @param hostname the claimed hostname.
     * @param realname the realname for the user.
     */
    public void sendUSER(String username, String hostname, String realname) {
        sendUSER(username, hostname, socket.getInetAddress().getHostName(), realname);
    }


    /**
     * Sends the NICK command.
     *
     * @param nickname the nickname.
     */
    public void sendNICK(IrcUserName nickname) {
        sendRawLine("NICK " + nickname.toShortString());
    }

    /**
     * Sends the WHOIS command.
     *
     * @param nickname the nickname.
     * @param where TODO: Document this.
     */
    public void sendWHOIS(IrcUserName nickname, String where) {
        if (where != null) {
            sendRawLine("WHOIS " + nickname.toShortString() + " :" + where);
        } else {
            sendRawLine("WHOIS " + nickname.toShortString());
        }
    }

    /**
     * Sends the JOIN command.
     *
     * @param channel the channel to join.
     * @param key the key to use to get into the channel, if there is a key on the channel.
     */
    public void sendJOIN(IrcChannelName channel, String key) {
        if (key != null) {
            sendRawLine("JOIN " + channel.toShortString() + " " + key);
        } else {
            sendRawLine("JOIN " + channel.toShortString());
        }
    }

    /**
     * Sends the PART command.
     *
     * @param channel the channel to part.
     */
    public void sendPART(IrcChannelName channel) {
        sendRawLine("PART " + channel.toShortString());
    }

    /**
     * Sends the INVITE command.
     *
     * @param nick the nick of the user to invite.
     * @param channel the channel to invite them into.
     */
    public void sendINVITE(IrcUserName nick, IrcChannelName channel) {
        sendRawLine("INVITE " + nick.toShortString() + " " + channel.toShortString());
    }

    /**
     * Sends the PRIVMSG command.
     *
     * @param target the target (a user or a channel.)
     * @param message the message to send.
     */
    public void sendPRIVMSG(IrcName target, String message) {
        sendRawLine("PRIVMSG " + target.toShortString() + " :" + message);
    }

    /**
     * Sends the PING command.
     *
     * @param origin TODO: Document this
     */
    public void sendPING(String origin) {
        sendRawLine("PING :" + origin);
    }

    /**
     * Sends the PONG command.
     *
     * @param daemon TODO: Document this
     */
    public void sendPONG(String daemon) {
        sendRawLine("PONG :" + daemon);
    }

    /**
     * Sends a raw line to the server.
     *
     * @param line the line to send.
     */
    public void sendRawLine(String line) {
        toServer.println(line);
        fireMessageSent(line);
    }

    /**
     * Adds a listener.
     *
     * @param listener the listener to add.
     */
    public void addIrcListener(IrcListener listener) {
        ircListeners.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener the listener to remove.
     */
    public void removeIrcListener(IrcListener listener) {
        ircListeners.remove(listener);
    }

    /**
     * Fires the message received event to all listeners.
     *
     * @param line the line which was received.
     */
    protected void fireMessageReceived(IrcMessage line) {
        for (IrcListener listener : ircListeners) {
            try {
                listener.messageReceived(this, line);
            }
            catch (Throwable t) {
                System.err.println("UNEXPECTED ERROR IN EVENT CHAIN:");
                t.printStackTrace();
            }
        }
    }

    /**
     * Fires the message sent event to all listeners.
     *
     * @param line the line which was sent.
     */
    protected void fireMessageSent(String line) {
        for (IrcListener listener : ircListeners) {
            try {
                listener.messageSent(this, line);
            }
            catch (Throwable t) {
                System.err.println("UNEXPECTED ERROR IN EVENT CHAIN:");
                t.printStackTrace();
            }
        }
    }

    /**
     * Fires the connecting event to all listeners.
     */
    protected void fireConnecting() {
        for (IrcListener listener : ircListeners) {
            try {
                listener.connecting(this);
            }
            catch (Throwable t) {
                System.err.println("UNEXPECTED ERROR IN EVENT CHAIN:");
                t.printStackTrace();
            }
        }
    }

    /**
     * Fires the connected event to all listeners.
     */
    protected void fireConnected() {
        for (IrcListener listener : ircListeners) {
            try {
                listener.connected(this);
            }
            catch (Throwable t) {
                System.err.println("UNEXPECTED ERROR IN EVENT CHAIN:");
                t.printStackTrace();
            }
        }
    }

    /**
     * Fires the disconnected event to all listeners.
     */
    protected void fireDisconnected() {
        for (IrcListener listener : ircListeners) {
            try {
                listener.disconnected(this);
            }
            catch (Throwable t) {
                System.err.println("UNEXPECTED ERROR IN EVENT CHAIN:");
                t.printStackTrace();
            }
        }
    }

    /**
     * Runnable which when run, will perform the main loop of reading from the server.
     */
    private class ConnectionRunner implements Runnable {
        @Override
        public void run() {
            Thread currentThread = Thread.currentThread();
            if (currentThread != runner) {
                return;
            }
            try {
                fireConnecting();
                socket = connectionFactory.createConnection();
                toServer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                fireConnected();

                while (currentThread == runner) {
                    String line = fromServer.readLine();
                    if (line == null) {
                        break;
                    }
                    IrcMessage ircline = new IrcMessage(line);
                    fireMessageReceived(ircline);
                }

                // TODO: Should probably quit gracefully.
            }
            catch (IOException e) {
                // Most likely a disconnect, so ignore it and we'll just disconnect.
            }
            finally {
                // Unset the runner thread so that it will let us start again.
                runner = null;
                fireDisconnected();
            }
        }
    }
}
