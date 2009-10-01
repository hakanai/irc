package org.trypticon.bot.util;

/**
 * Interface to put on adapters to receive configuration from the config file.
 */
public interface Configurable {

    /**
     * Called to configure the adapter.
     *
     * @param configuration the configuration.
     */
    void configure(Configuration configuration);
}
