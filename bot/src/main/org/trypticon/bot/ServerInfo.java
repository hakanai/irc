package org.trypticon.bot;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jdom.Element;

/**
 * Information about a single server.
 */
public class ServerInfo {
    /**
     * The server's address.
     */
    private InetAddress address;

    /**
     * The server's port.
     */
    private int port;

    /**
     * Loads the server info from a <server/> element.
     *
     * @param serverElement the XML element containing information about the server.
     */
    public ServerInfo(Element serverElement) {
        port = Integer.parseInt(serverElement.getAttributeValue("port"));
        String serverHostName = serverElement.getAttributeValue("host");
        try {
            address = InetAddress.getByName(serverHostName);
            System.err.println("Using server " + address + ":" + port);
        }
        catch (UnknownHostException e) {
            throw new IllegalArgumentException("Unknown host '" + serverHostName + "'");
        }
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
