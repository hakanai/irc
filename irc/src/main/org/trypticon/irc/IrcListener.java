package org.trypticon.irc;

/**
 * Listener to implement to receive IRC events and messages.
 *
 * @author Trejkaz
 */
public interface IrcListener {

    /**
     * Called when a message is received from an IRC connection.
     *
     * @param conn the IRC connection.
     * @param line the line which was received.
     */
    public void messageReceived(IrcConnection conn, IrcMessage line);

    /**
     * Called when a message is sent from an IRC connection.
     *
     * @param conn the IRC connection.
     * @param line the line which was sent.
     */
    public void messageSent(IrcConnection conn, String line);

    /**
     * Called when beginning to connect to an IRC server.
     *
     * @param conn the IRC connection.
     */
    public void connecting(IrcConnection conn);

    /**
     * Called when connected to an IRC server.
     *
     * @param conn the IRC connection.
     */
    public void connected(IrcConnection conn);

    /**
     * Called when disconnected from an IRC server.
     *
     * @param conn the IRC connection.
     */
    public void disconnected(IrcConnection conn);
}
