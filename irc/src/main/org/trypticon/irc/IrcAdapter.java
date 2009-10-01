package org.trypticon.irc;

/**
 * Adapter for IrcListener with no functionality.
 *
 * @author Trejkaz
 */
public class IrcAdapter implements IrcListener {

    @Override
    public void messageReceived(IrcConnection conn, IrcMessage line) {
    }

    @Override
    public void messageSent(IrcConnection conn, String line) {
    }

    @Override
    public void connecting(IrcConnection conn) {
    }

    @Override
    public void connected(IrcConnection conn) {
    }

    @Override
    public void disconnected(IrcConnection conn) {
    }
}
