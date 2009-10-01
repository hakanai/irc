package org.trypticon.irc;

import java.util.HashMap;
import java.lang.reflect.Field;


/**
 * The Internet Relay Chat (IRC) protocol.
 * 
 * Provides methods for converting from IRC protocol strings (both alphabetic and numeric), to more convenient
 * integer constants, and for conversion in the other direction.  The alphabetic strings are numbered from 100 to 139,
 * so as to keep 3-digit codes without overlapping with the numeric messages (which start at 200.)
 *
 * @author Trejkaz
 */
public class Rfc1459 {

    // ---------- MESSAGE TYPE CONSTANTS ----------

    // TODO: Should use a hybrid enum/numeric way of doing this to avoid returning -1 as a magic value.

    // 4. MESSAGE DETAILS

    // 4.1. Connection Registration
    public static final int MSG_PASS = -1;   // <password>
    public static final int MSG_NICK = -2;   // <nickname> [<hopcount>]
    public static final int MSG_USER = -3;   // <username> <hostname> <servername> <realname>
    public static final int MSG_SERVER = -4;   // <server> <hopcount> <info>
    public static final int MSG_OPER = -5;   // <user> <password>
    public static final int MSG_QUIT = -6;   // [<quit message>]
    public static final int MSG_SQUIT = -7;   // <server> <comment>

    // 4.2. Channel operations
    public static final int MSG_JOIN = -8;   // <channel>{,<channel>} [<key>{,<key>}]
    public static final int MSG_PART = -9;   // <channel>{,<channel>}
    public static final int MSG_MODE = -10;  // <channel> {[+|-]|o|p|s|i|t|n|b|v} [<limit>] [<user>] [<ban mask>]
    // <nickname> {[+|-]|i|w|s|o}
    public static final int MSG_TOPIC = -11;  // <channel> [<topic>]
    public static final int MSG_NAMES = -12;  // <channel>{,<channel>}
    public static final int MSG_LIST = -13;  // [<channel>{,<channel>} [<server>]]
    public static final int MSG_INVITE = -14;  // <nickname> <channel>
    public static final int MSG_KICK = -15;  // <channel> <user> [<comment>]

    // 4.3. Server queries and commands
    public static final int MSG_VERSION = -16;  // [<server>]
    public static final int MSG_STATS = -17;  // [<query> [<server>]]
    public static final int MSG_LINKS = -18;  // [[<remote server>] <server mask>]
    public static final int MSG_TIME = -19;  // [<server>]
    public static final int MSG_CONNECT = -20;  // <target server> [<port> [<remote server>]]
    public static final int MSG_TRACE = -21;  // [<server>]
    public static final int MSG_ADMIN = -22;  // [<server>]
    public static final int MSG_INFO = -23;  // [<server>]

    // 4.4. Sending messages
    public static final int MSG_PRIVMSG = -24;  // <receiver>{,<receiver>} <text to be sent>
    public static final int MSG_NOTICE = -25;  // <nickname> <text>

    // 4.5. User based queries
    public static final int MSG_WHO = -26;  // [<name> [o]]
    public static final int MSG_WHOIS = -27;  // [<server>] <nickmask>{,<nickmask>}
    public static final int MSG_WHOWAS = -28;  // <nickname> [<count> [<server>]]

    // 4.6. Miscellaneous messages
    public static final int MSG_KILL = -29;  // <nickname> <comment>
    public static final int MSG_PING = -30;  // <server1> [<server2>]
    public static final int MSG_PONG = -31;  // <daemon> [<daemon2>]
    public static final int MSG_ERROR = -32;  // <error message>

    // 5. OPTIONALS

    public static final int MSG_AWAY = -33;  // <away message>
    public static final int MSG_REHASH = -34;  // none
    public static final int MSG_RESTART = -35;  // none
    public static final int MSG_SUMMON = -36;  // <user> [<server>]
    public static final int MSG_USERS = -37;  // [<server>]
    public static final int MSG_WALLOPS = -38;  // <text>
    public static final int MSG_USERHOST = -39;  // <nickname>{ <nickname>}
    public static final int MSG_ISON = -40;  // <nickname>{ <nickname>}

    // Special placeholder for CTCP messages, which btw aren't normal messages (NOT STRICTLY PART OF RFC!)
    // See IrcMessage class for where this comes in.
    public static final int MSG_CTCP = -100;
    public static final int MSG_CTCPREPLY = -101;


    // ---------- CONVENIENCE METHODS ----------

    /**
     * Check whether a given entity is a channel.  An entity is a channel if it begins
     * with '#' or '&amp;'.
     *
     * @param entity the entity to check.
     * @return true if the entity is a channel.
     */
    public static boolean isChannel(String entity) {
        if (entity.length() > 0) {
            char c = entity.charAt(0);
            return (c == '#' || c == '&');
        }
        return false;
    }

    /**
     * Convert a message from its string representation to its integer message id.
     * If the string is defined in the various MSG_ constants, then return the value
     * associated with that constant.  Otherwise, try to parse it as an integer.  If this
     * fails, return 0.
     *
     * @param str the string representation.
     * @return the message id, or 0 if the message could not be understood.
     */
    public static int getMessageIdFromString(String str) {
        Integer id = mapStringToId.get(str);
        if (id != null) {
            return id;
        } else {
            try {
                return Integer.parseInt(str);
            }
            catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    /**
     * Convert a message from its integer message id to its string representation.
     * If the number is less than zero, return the string associated with the MSG_ constant
     * with that value.  If it's greater than zero, return a 3-digit string numeric.
     *
     * @param id the message id.
     * @return the string representation.
     */
    public static String getMessageStringFromId(int id) {
        if (id < 0) {
            return mapIdToString.get(new Integer(id));
        }
        if (id > 0) {
            StringBuffer buf = new StringBuffer();
            buf.append(id);
            while (buf.length() < 3) {
                buf.insert(0, '0');
            }
            return buf.toString();
        }
        return null;
    }


    // ---------- QUICK HASHING SETUP ----------

    /**
     * The hash of string messages.
     */
    private static HashMap<String, Integer> mapStringToId = new HashMap<String, Integer>();
    private static HashMap<Integer, String> mapIdToString = new HashMap<Integer, String>();

    static {
        // Use reflection to build up the hash maps, because I can't be bothered writing a buttload of
        // map.put() calls.
        try {
            Class cls = Rfc1459.class;
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                if (fieldName.startsWith("MSG_")) {
                    String str = fieldName.substring(4);
                    int id = field.getInt(null);
                    mapStringToId.put(str, id);
                    mapIdToString.put(id, str);
                }
            }
        }
        catch (IllegalAccessException e) {
            System.err.println("Fatal error in Rfc1459: illegal access to field!");
            System.exit(1);
        }
    }

    // 8.2. Command Parsing
    public static final int BUFFER_SIZE = 512;
    public static final int LARGE_BUFFER_SIZE = BUFFER_SIZE * 8;
}
