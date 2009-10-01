package org.trypticon.irc;

import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;

/**
 * Default implementation of {@link ConnectionFactory}.
 * Provides for creating connections to a single server.
 *
 * @author Trejkaz
 */
public class DefaultConnectionFactory extends ConnectionFactory {
    /**
     * The default timeout for connection in milliseconds (10 seconds.)
     */
    public static final int DEFAULT_TIMEOUT = 10000;

    /**
     * The server address.
     */
    private InetAddress serverAddr;

    /**
     * The server port.
     */
    private int serverPort;

    /**
     * The local address.
     */
    private InetAddress localAddr;

    /**
     * Constructs the connection factory.
     *
     * @param serverAddr the address of the server to connect to.
     * @param serverPort the port to connect to on the server.
     * @param localAddr the local address which will be bound to.
     */
    public DefaultConnectionFactory(InetAddress serverAddr, int serverPort, InetAddress localAddr) {
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
        this.localAddr = localAddr;
    }

    /**
     * Gets the address of the server to connect to.
     *
     * @return the address of the server to connect to.
     */
    public InetAddress getServerAddr() {
        return serverAddr;
    }

    /**
     * Gets the port to connect to on the server.
     *
     * @return the port to connect to on the server.
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Gets the local address which will be bound to.
     *
     * @return the local address which will be bound to.
     */
    public InetAddress getLocalAddr() {
        return localAddr;
    }

    @Override
    public Socket createConnection() throws IOException {
        return new Socket(serverAddr, serverPort, localAddr, 0);
    }
}
