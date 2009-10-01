package org.trypticon.bot;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.trypticon.irc.IrcConnection;
import org.trypticon.irc.ConnectionFactory;
import org.trypticon.irc.DefaultConnectionFactory;

/**
 * A single IRC client.
 */
public class ClientInfo {
    /**
     * The client's identifier.
     */
    private String id;

    /**
     * The address to be used for the local host.
     */
    private InetAddress localAddr;

    /**
     * The list of possible server addresses.
     */
    private ServerInfo[] servers;

    /**
     * The adapters for this connection.
     */
    private AdapterInfo[] adapters;

    /**
     * The active IRC connection.
     */
    private IrcConnection ircConnection;

    /**
     * Creates the client.  Reads configuration from the given element.
     *
     * @param clientElement the element from the XML file representing the client.
     * @param adapterMap the map of adapters.
     */
    public ClientInfo(Element clientElement, Map<String, AdapterInfo> adapterMap) {
        id = clientElement.getAttributeValue("id");
        System.err.println("Reading configuration for client '" + id + "':");

        String localHostName = clientElement.getAttributeValue("localHost");
        try {
            localAddr = InetAddress.getByName(localHostName);
            System.err.println("Using localhost " + localAddr);
        }
        catch (UnknownHostException e) {
            throw new IllegalArgumentException("Unknown host '" + localHostName + "'");
        }

        List<ServerInfo> serverInfoList = new ArrayList<ServerInfo>(2);
        @SuppressWarnings("unchecked")
        List<Element> serverElements = clientElement.getChildren("server");
        for (Element serverElement : serverElements) {
            serverInfoList.add(new ServerInfo(serverElement));
        }
        servers = serverInfoList.toArray(new ServerInfo[serverInfoList.size()]);

        // Add the adaptors... this is where we use their ids.
        List<AdapterInfo> adapterList = new ArrayList<AdapterInfo>(8);
        @SuppressWarnings("unchecked")
        List<Element> adapterElements = clientElement.getChildren("adapter");
        for (Element adapterElement : adapterElements) {
            String adapterId = adapterElement.getAttributeValue("refid");
            adapterList.add(adapterMap.get(adapterId));
        }
        adapters = adapterList.toArray(new AdapterInfo[adapterList.size()]);
    }

    public synchronized void start() {
        if (servers.length == 0) {
            throw new IllegalStateException("At least one server must be defined");
        }

        if (ircConnection == null) {
            // No multi-server support yet, but it can be done fairly easily.
            ConnectionFactory connectionFactory =
                    new DefaultConnectionFactory(servers[0].getAddress(), servers[0].getPort(), localAddr);
            ircConnection = new IrcConnection(id, connectionFactory);

            // Attach the adapters to this connection...
            for (AdapterInfo adapter : adapters) {
                ircConnection.addIrcListener(adapter.getAdapter());
            }

            ircConnection.start();
        }
    }

    public synchronized void stop() {
        if (ircConnection != null) {
            ircConnection.stop();
            ircConnection = null;
        }
    }

    public String getId() {
        return id;
    }

    public InetAddress getLocalAddr() {
        return localAddr;
    }

    public ServerInfo[] getServers() {
        return servers;
    }

    public IrcConnection getIrcConnection() {
        return ircConnection;
    }
}
