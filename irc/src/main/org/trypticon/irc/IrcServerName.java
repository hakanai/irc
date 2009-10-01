package org.trypticon.irc;

/**
 * Represents an IRC server name.
 */
public class IrcServerName implements IrcName {
    /**
     * The server name.
     */
    private String name;

    /**
     * Construct an IRC server name.
     *
     * @param name the name.
     */
    protected IrcServerName(String name) {
        this.name = name;
    }

    public String toShortString() {
        return name;
    }

    public String toLongString() {
        return name;
    }

    /**
     * Returns a string representation of the server name.
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
        if (!(o instanceof IrcServerName)) {
            return false;
        }
        IrcServerName n = (IrcServerName) o;
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
