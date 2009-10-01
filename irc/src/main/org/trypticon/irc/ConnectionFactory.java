package org.trypticon.irc;

import java.net.Socket;
import java.io.IOException;

/**
 * A factory class for producing {@link java.net.Socket}s connected to IRC server(s).
 *
 * @author Trejkaz
 */
public abstract class ConnectionFactory {

    /**
     * Creates a connection.
     *
     * @return the socket.
     * @throws IOException if there was an error connecting.
     */
    public abstract Socket createConnection() throws IOException;

}
