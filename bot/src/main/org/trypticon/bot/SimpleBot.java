package org.trypticon.bot;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * A simple test bot.
 *
 * @author Trejkaz
 */
public class SimpleBot {

    /**
     * Map of adapters by ID.
     */
    private Map<String, AdapterInfo> adapterMap = new HashMap<String, AdapterInfo>();

    /**
     * Map of clients by ID.
     */
    private Map<String, ClientInfo> clientMap = new HashMap<String, ClientInfo>();

    /**
     * Main method.
     *
     * @param args the command-line arguments.
     */
    public static void main(final String[] args) {
        // Parse the XML configuration file.
        File configFile = new File(args[0]);
        Document doc = null;
        try {
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(configFile);
        }
        catch (IOException e) {
            System.err.println("Error reading XML from " + configFile);
            System.exit(1);
        }
        catch (JDOMException e) {
            System.err.println("Error parsing XML from " + configFile);
            System.exit(1);
        }

        Element root = doc.getRootElement();
        SimpleBot bot = new SimpleBot(root);
        bot.start();


    }

    /**
     * Creates the bot from the given <bot/> element.
     *
     * @param root the root XML element from the configuration.
     */
    public SimpleBot(Element root) {

        // Load the adaptors and map them by id.
        for (Object o : root.getChildren("adapter")) {
            Element adapterElement = (Element) o;
            AdapterInfo adapter = new AdapterInfo(adapterElement);
            adapterMap.put(adapter.getId(), adapter);
        }

        // Load the clients and map them by id.
        for (Object o : root.getChildren("client")) {
            Element clientElement = (Element) o;
            ClientInfo client = new ClientInfo(clientElement, adapterMap);
            clientMap.put(client.getId(), client);
        }
    }

    /**
     * Starts the bot.
     */
    public void start() {
        // Start all the clients.
        for (ClientInfo client : clientMap.values()) {
            client.start();
        }
    }

}
