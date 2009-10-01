package org.trypticon.irc;

/**
 * Represents an IRC channel name.
 *
 * @author Trejkaz
 */
public class IrcChannelName implements IrcName {
    /**
     * The channel name.
     */
    private String name;

    /**
     * Construct an IRC channel name.
     *
     * @param name the name.
     */
    public IrcChannelName(String name) {
        if (!name.startsWith("#") && !name.startsWith("&")) {
            throw new IllegalArgumentException("'" + name + "' is not a valid channel name");
        }
        this.name = name;
    }

    @Override
    public String toShortString() {
        return name;
    }

    @Override
    public String toLongString() {
        return name;
    }

    /**
     * Returns a string representation of the channel name.
     *
     * @return the string representation.
     */
    @Override
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
        if (!(o instanceof IrcChannelName)) {
            return false;
        }
        IrcChannelName n = (IrcChannelName) o;
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
