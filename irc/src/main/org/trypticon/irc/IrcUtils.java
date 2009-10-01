package org.trypticon.irc;

/**
 * A collection of common methods for IRC.
 */
public class IrcUtils {
    /**
     * Convenience method to check if two names are equal, IRC-style.
     *
     * This implements the following requirement from RFC 1459:
     * <q>Because of IRC's scandanavian origin, the characters {}|
     * are considered to be the lower case equivalents of the characters []\,
     * respectively. This is a critical issue when determining the equivalence
     * of two nicknames.</q>
     *
     * @param name1 the first name.
     * @param name2 the second name.
     * @return true if the two names are equal.
     */
    public static boolean areNamesEqual(String name1, String name2) {
        // Ignoring here because the point of the first comparison is speed.
        //noinspection StringEquality
        return name1 == name2 || !(name1 == null || name2 == null) && toLowerCase(name1).equals(toLowerCase(name2));
    }

    /**
     * Lowercases a name, IRC-style.
     * 
     * This implements the following requirement from RFC 1459:
     * <q>Because of IRC's scandanavian origin, the characters {}|
     * are considered to be the lower case equivalents of the characters []\,
     * respectively. This is a critical issue when determining the equivalence
     * of two nicknames.</q>
     *
     * @param name the name.
     * @return the IRC-style lowercased name.
     */
    public static String toLowerCase(String name) {
        return name.toLowerCase().replace('[', '{').replace(']', '}').replace('\\', '|');
    }
}
