package org.trypticon.irc;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a line of information received from an IRC server.
 */
public class IrcMessage {

    /**
     * The raw line from the server.
     */
    private String rawLine;

    /**
     * The source of the message.
     */
    private IrcName source;

    /**
     * The type of command.
     */
    private int type;

    /**
     * The string value of the type of command.
     */
    private String typeString;

    /**
     * The list of arguments to the command.   TODO: Pass this around as a list.
     */
    private List<String> args;

    /**
     * Constructs an IRC message, parsing the raw line from the server.
     *
     * @param rawLine the raw line from the server.
     */
    public IrcMessage(String rawLine) {
        this.rawLine = rawLine;
        int point = 0;
        int space;

        // The line beginning with a colon indicates a source.  Set the source as everything up to the space
        // (except the colon), and trim off the front for further parsing.
        if (rawLine.charAt(0) == ':') {
            space = rawLine.indexOf(' ');
            source = IrcNameFactory.create(rawLine.substring(1, space));
            point = space + 1;
        }

        // Now, everything up to the *next* space is a message.
        space = rawLine.indexOf(' ', point);
        typeString = rawLine.substring(point, space);
        type = Rfc1459.getMessageIdFromString(typeString);
        if (type == -1) {
            System.err.println("[WARNING: UNRECOGNISED MESSAGE]  " + rawLine);
        }
        point = space + 1;

        // Now, parse the rest of the arguments.  Remember that if an argument starts with a colon, it's the
        // last one, and includes everything up to the end of the raw line.
        args = new ArrayList<String>(6);
        while (true) {
            if (rawLine.length() < point + 1) {
                break;
            }
            if (rawLine.charAt(point) == ':') {
                if (rawLine.length() > point + 1) {
                    args.add(rawLine.substring(point + 1));
                } else {
                    args.add("");
                }
                break;
            }
            space = rawLine.indexOf(' ', point);
            if (space == -1) {
                args.add(rawLine.substring(point));
                break;
            }
            args.add(rawLine.substring(point, space));
            point = space + 1;
        }

        // If the message type was PRIVMSG or NOTICE, it needs to be converted to a CTCP or CTCPREPLY
        // message if it is surrounded by '\001' characters.  If this is so, we strip off the
        // '\001' characters and change the message type.
        if (type == Rfc1459.MSG_PRIVMSG) {
            String msg = args.get(1);
            int len = msg.length();
            if (msg.charAt(0) == '\001' && msg.charAt(len - 1) == '\001') {
                args.set(1, msg.substring(1, len - 1));
                type = Rfc1459.MSG_CTCP;
            }
        } else if (type == Rfc1459.MSG_NOTICE) {
            String msg = args.get(1);
            int len = msg.length();
            if (msg.charAt(0) == '\001' && msg.charAt(len - 1) == '\001') {
                args.set(1, msg.substring(1, len - 1));
                type = Rfc1459.MSG_CTCPREPLY;
            }
        }
    }

    /**
     * Gets the name of the entity which sent the message.
     *
     * @return the name of the entity which sent the message.
     */
    public IrcName getSource() {
        return source;
    }

    /**
     * Gets the type of the message.
     *
     * @return the type of the message.
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the type string.
     *
     * @return the type string.
     */
    public String getTypeString() {
        return typeString;
    }

    /**
     * Gets the number of arguments.
     *
     * @return the number of arguments.
     */
    public int getNumArgs() {
        return args.size();
    }

    /**
     * Gets the argument at the given index.
     *
     * @param index the index to get.
     * @return the argument at the given index.
     */
    public String getArg(int index) {
        return args.get(index);
    }

    /**
     * Gets the arguments.
     *
     * @return the arguments.
     */
    public List<String> getArgs() {
        return args;
    }

    /**
     * Gets the arguments within the specified region.
     *
     * @param startIndex the index to start at (inclusive.)
     * @param endIndex the index to end at (exclusive.)
     * @return the arguments within the specified region.
     */
    public List<String> getArgs(int startIndex, int endIndex) {
        return args.subList(startIndex, endIndex);
    }

    /**
     * Gets the arguments from the specified index onwards.
     *
     * @param startIndex the index to start at (inclusive.)
     * @return the arguments from the specified index onwards.
     */
    public List<String> getArgs(int startIndex) {
        return args.subList(startIndex, args.size());
    }

    /**
     * Gets the raw line as it came from the server.
     *
     * @return the raw line.
     */
    public String getRawLine() {
        return rawLine;
    }

    @Override
    public String toString() {
        return "[source=" + source + ",type=" + typeString + ",args=" + args + "]";
    }
}
