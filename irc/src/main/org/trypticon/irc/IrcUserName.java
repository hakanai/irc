package org.trypticon.irc;

/**
 * Represents an IRC username.
 */
public class IrcUserName implements IrcName {
    /**
     * The full username, as encountered.
     */
    private String name;

    /**
     * The user's nick.
     */
    private String nick;

    /**
     * The user's username.
     */
    private String username;

    /**
     * The user's hostname.
     */
    private String hostname;

    /**
     * Construct an IRC username.
     *
     * @param name the name.
     */
    protected IrcUserName(String name) {
        this.name = name;
        int bangIndex = name.indexOf('!');
        int atIndex = name.indexOf('@');
        if (bangIndex != -1) {
            nick = name.substring(0, bangIndex);
            if (atIndex != -1) {
                username = name.substring(bangIndex + 1, atIndex);
                hostname = name.substring(atIndex + 1);
            } else {
                username = name.substring(bangIndex + 1);
            }
        } else {
            if (atIndex != -1) {
                nick = name.substring(0, atIndex);
                hostname = name.substring(atIndex + 1);
            } else {
                nick = name;
            }
        }
    }

    /**
     * Gets the user's nick.
     *
     * @return the user's nick.
     */
    public String getNick() {
        return nick;
    }

    /**
     * Gets the user's username, if available.
     *
     * @return the user's username, or <code>null</code> if not available.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the user's hostname, if available.
     *
     * @return the user's hostname, or <code>null</code> if not available.
     */
    public String getHostname() {
        return hostname;
    }

    public String toShortString() {
        return nick;
    }

    public String toLongString() {
        return name;
    }

    /**
     * Returns a string representation of the username, in the standard form:
     * 
     * nick [!username] [@hostname]
     *
     * @return the string representation.
     */
    public String toString() {
        return name;
    }

    /**
     * Compares this object to another.
     *
     * @param o the other object.
     * @return true if the two objects are equal.
     */
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IrcUserName)) {
            return false;
        }
        IrcUserName n = (IrcUserName) o;
        return (IrcUtils.areNamesEqual(n.name, name));
    }

    /**
     * Creates a hash code for this object.
     *
     * @return the hash code.
     */
    public int hashCode() {
        return IrcUtils.toLowerCase(name).hashCode();
    }
}
