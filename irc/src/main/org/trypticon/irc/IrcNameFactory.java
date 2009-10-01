package org.trypticon.irc;

/**
 * Factory to create {@link IrcName}s.
 */
public class IrcNameFactory {

    /**
     * Creates an {@link IrcName} of the appropriate type.
     *
     * @param name the name of the entity.
     * @return the appropriate {@link IrcName} instance.
     */
    public static IrcName create(String name) {
        if (name == null) {
            return null;
        } else if (name.startsWith("#") || name.startsWith("&")) {
            return new IrcChannelName(name);
        } else if (name.indexOf('.') != -1 && name.indexOf('@') == -1 && name.indexOf('!') == -1) {
            return new IrcServerName(name);
        } else {
            return new IrcUserName(name);
        }
    }
}
