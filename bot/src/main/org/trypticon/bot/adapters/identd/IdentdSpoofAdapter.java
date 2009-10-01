package org.trypticon.bot.adapters.identd;

import org.trypticon.irc.IrcConnection;
import org.trypticon.irc.DefaultIrcAdapter;
import org.trypticon.bot.util.Configuration;
import org.trypticon.bot.util.Configurable;
import org.trypticon.irc.IrcName;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;

/**
 * Adapter for UNIX machines which puts an entry in the user's ~/.ispoof file to fake
 * the identd for the user.
 *
 * @author Trejkaz
 */
public class IdentdSpoofAdapter extends DefaultIrcAdapter implements Configurable {

    private String spoofUser;
    private boolean spoofed;

    @Override
    public void configure(Configuration props) {
        this.spoofUser = props.getString("spoofUser");
        this.spoofed = false;
    }

    public void connecting(IrcConnection conn) {
        if (!spoofed) {
            changeIspoofUser(spoofUser);
            spoofed = true;
        }
    }

    public void handleNumeric(IrcConnection conn, int numeric, IrcName source, List args) {
        // Unspoof if we get a 'Welcome to IRC' numeric
        if (numeric < 200 && spoofed) {
            wipeIspoofUser();
            spoofed = false;
        }
    }

    private void changeIspoofUser(String newUser) {
        System.err.println("[IdentdSpoofAdapter] Changing .ispoof to '" + newUser + "'");

        // Open the file .ispoof in the user's home directory (create it if it doesn't exist.)
        File ispoof = new File(System.getProperty("user.home"), ".ispoof");
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(ispoof), true);
            writer.println(newUser);
            writer.close();
        }
        catch (IOException e) {
            System.err.println("[IdentdSpoofAdapter] Couldn't write to " + ispoof);
        }
    }

    private void wipeIspoofUser() {
        File ispoof = new File(System.getProperty("user.home"), ".ispoof");
        if (!ispoof.delete()) {
            System.err.println("[IdentdSpoofAdapter] Couldn't delete " + ispoof);
        }
    }
}
