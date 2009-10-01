package org.trypticon.bot.adapters.core;

import org.trypticon.irc.IrcAdapter;
import org.trypticon.irc.IrcConnection;
import org.trypticon.bot.util.Configuration;
import org.trypticon.bot.util.Configurable;
import org.trypticon.irc.IrcNameFactory;
import org.trypticon.irc.IrcUserName;

/**
 * Class to handle logins on connection to the server.
 *
 * @author Trejkaz
 */
public class RegisterAdapter extends IrcAdapter implements Configurable {

    private IrcUserName nick;
    private String user;
    private String host;
    private String realName;

    @Override
    public void configure(Configuration props) {
        this.nick = (IrcUserName) IrcNameFactory.create(props.getString("nick"));
        this.user = props.getString("user");
        this.host = props.getString("host");

        // Convert formatting escapes for the real name.
        char[] tmp = props.getString("realName").toCharArray();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < tmp.length; i++) {
            char c = tmp[i];
            if (c == '%') {
                switch (tmp[++i]) {
                    case 'B':
                        buf.append('\002');
                        break;
                    default:
                        buf.append(tmp[i]);
                }
            } else {
                buf.append(c);
            }
        }
        realName = buf.toString();
    }

    public void connected(IrcConnection conn) {
        conn.sendUSER(user, host, realName);
        conn.sendNICK(nick);
    }
}
