package org.trypticon.bot;

import java.util.Iterator;

import org.jdom.Element;
import org.trypticon.irc.IrcAdapter;
import org.trypticon.bot.util.Configuration;
import org.trypticon.bot.util.Configurable;

/**
 * Configuration for an adapter.
 */
public class AdapterInfo {
    /**
     * The adapter's identifier.
     */
    private String id;

    /**
     * The adapter object itself.
     */
    private IrcAdapter adapter;

    /**
     * Initialise the adapter from an <adapter/> element.
     *
     * @param adapterElement the element from the XML config file.
     */
    public AdapterInfo(Element adapterElement) {
        id = adapterElement.getAttributeValue("id");
        System.err.println("Reading configuration for adapter '" + id + "':");

        String adapterClassName = adapterElement.getAttributeValue("class");
        try {
            // Get the adapter classname and create the adapter object.
            Class adapterClass = Class.forName(adapterClassName);
            System.err.println("Found adapter: " + adapterClass);
            adapter = (IrcAdapter) adapterClass.newInstance();
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Can't find class: " + adapterClassName);
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException("Error instantiating class: " + adapterClassName);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Can't access constructor: " + adapterClassName);
        }

        // Initialise the adapter.
        if (adapter instanceof Configurable) {
            // Get the <param> elements into a properties object.
            Iterator params = adapterElement.getChildren("param").iterator();
            Configuration config = new Configuration();
            while (params.hasNext()) {
                Element param = (Element) params.next();
                config.setProperty(param.getAttributeValue("name"), param.getAttributeValue("value"));
            }

            ((Configurable) adapter).configure(config);
        }
    }

    public String getId() {
        return id;
    }

    public IrcAdapter getAdapter() {
        return adapter;
    }
}
