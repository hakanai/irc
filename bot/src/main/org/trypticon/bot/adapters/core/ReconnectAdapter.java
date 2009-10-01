package org.trypticon.bot.adapters.core;

import java.util.Timer;
import java.util.TimerTask;

import org.trypticon.irc.IrcAdapter;
import org.trypticon.irc.IrcConnection;
import org.trypticon.bot.util.Configuration;
import org.trypticon.bot.util.Configurable;

/**
 * Adapter which reconnects if the link is disconnected.
 */
public class ReconnectAdapter extends IrcAdapter implements Configurable {
    /**
     * The time to wait before reconnecting.
     */
    private int delay;

    @Override
    public void configure(Configuration props) {
        delay = props.getInt("delay");
    }

    /**
     * Called when disconnected.
     * Spawns the delay in a timer thread to stop some nastiness with infinite recursion.
     *
     * @param conn the connection.
     */
    public void disconnected(final IrcConnection conn) {
        new Timer().schedule(new TimerTask() {
            public void run() {
                conn.start();
            }
        }, delay);
    }
}
