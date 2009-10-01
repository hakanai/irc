package org.trypticon.bot.adapters.games.pigs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.List;

import org.trypticon.irc.IrcConnection;
import org.trypticon.irc.DefaultIrcAdapter;
import org.trypticon.bot.util.Configuration;
import org.trypticon.bot.util.Configurable;
import org.trypticon.irc.IrcName;
import org.trypticon.irc.IrcNameFactory;
import org.trypticon.irc.IrcUserName;
import org.trypticon.irc.IrcChannelName;

/**
 * Pass the Pigs
 *
 * @author Trejkaz
 */
public class PassThePigs extends DefaultIrcAdapter implements Configurable {

    /**
     * An instance of the real game.
     */
    private Game g;

    /**
     * The channels to use.
     */
    private IrcName channel;

    @Override
    public void configure(Configuration props) {
        channel = IrcNameFactory.create(props.getString("channel"));
    }

    /**
     * Catch messages about users joining channels the bot is on.
     */
    public void handleJOIN(IrcConnection connection, IrcName source, IrcChannelName channel) {
        if (!this.channel.equals(channel)) {
            return;
        }
        System.err.println("[PassThePigs] JOIN: " + source + ", " + channel);
    }

    /**
     * Catch messages about users parting channels the bot is on.
     */
    public void handlePART(IrcConnection connection, IrcName source, IrcChannelName channel) {
        if (!this.channel.equals(channel)) {
            return;
        }
        System.err.println("[PassThePigs] PART: " + source + ", " + channel);
    }

    /**
     * Catch messages about users changing their nicks.
     */
    public void handleNICK(IrcConnection connection, IrcName source, IrcUserName newNick) {
        System.err.println("[PassThePigs] NICK: " + source + ", " + newNick);

        // Rename the nick in the game.
        @SuppressWarnings({"SuspiciousMethodCalls"})
        int i = g.players.indexOf(source);
        if (i != -1) {
            // We found a player on a game with the old nick, so change it
            // to the new nick.
            g.players.set(i, newNick);
            String oldNick = ((IrcUserName) source).getNick();
            connection.sendPRIVMSG(channel, "(Yes, I noticed " + oldNick + " renaming to " + newNick + ".)");
        }
    }

    /**
     * Catches messages sent to the bot, and also messages sent to channels the
     * bot is on.
     */
    public void handlePRIVMSG(IrcConnection connection, IrcName source, IrcName target, String message) {
        if (!this.channel.equals(target)) {
            return;
        }
        if (!(source instanceof IrcUserName)) {
            return;
        }

        IrcUserName player = (IrcUserName) source;

        if (g == null) {
            g = new Game();
        }

        // If the message starts with '!pigs', we take it as a game admin command.
        if (message.startsWith("!pigs ")) {
            String messageRest = message.substring(6);

            // Join a player to the game, provided it hasn't started yet.
            if (messageRest.equalsIgnoreCase("join")) {
                if (g.started) {
                    connection.sendPRIVMSG(target, "Sorry, " + player + ", you missed out. :-(");
                } else if (g.players.contains(player)) {
                    connection.sendPRIVMSG(target, "Real smart, " + player + ", you're already playing. :-)");
                } else {
                    g.players.add(player);
                    g.scores.add(0);
                    if (g.players.size() > 1) {
                        connection.sendPRIVMSG(target, "Okay, " + player + " is in.  That makes " + g.players.size() + " of you.  Type \002!pigs start\002 or wait for more.");
                    } else {
                        connection.sendPRIVMSG(target, "Okay, " + player + " is in.  That makes " + g.players.size() + " of you.  I need at least 2 to start, though.");
                    }
                }
                return;
            }

            if (messageRest.equalsIgnoreCase("leave")) {
                if (g.started) {
                    connection.sendPRIVMSG(target, "Sorry, " + player + ", you can't leave once the game is in progress.");
                } else if (!g.players.contains(player)) {
                    connection.sendPRIVMSG(target, "Real smart, " + player + ", you haven't joined. :-)");
                } else {
                    g.players.remove(player);
                    g.scores.remove(0);
                    if (g.players.size() > 1) {
                        connection.sendPRIVMSG(target, "Aww, " + player + " is out.  That makes " + g.players.size() + " of you.  Type \002!pigs start\002 or wait for more.");
                    } else {
                        connection.sendPRIVMSG(target, "Aww, " + player + " is out.  That makes " + g.players.size() + " of you.  I need at least 2 to start, though.");
                    }
                }
                return;
            }

            // Start the game, provided it hasn't been started, and at least
            // two people are playing.
            if (messageRest.length() >= 5 && messageRest.substring(0, 5).equalsIgnoreCase("start")) {
                if (g.started) {
                    connection.sendPRIVMSG(target, "Real smart, " + player + ", it's already started. :-)");
                } else if (g.players.size() < 2) {
                    connection.sendPRIVMSG(target, "Sorry, " + player + ", there aren't enough players yet. :-(");
                } else if (!g.players.contains(player)) {
                    connection.sendPRIVMSG(target, "Sorry, " + player + ", the game can only be started be a player. :-/");
                } else {
                    g.numRounds = 10;
                    g.scoreLimit = 100;
                    if (messageRest.length() > 6) {
                        StringTokenizer tok = new StringTokenizer(messageRest.substring(6));
                        while (tok.hasMoreTokens()) {
                            String s = tok.nextToken();
                            int eq = s.indexOf('=');
                            if (eq != -1) {
                                try {
                                    String key = s.substring(0, eq);
                                    String value = s.substring(eq + 1);
                                    if (key.equalsIgnoreCase("numRounds")) {
                                        g.numRounds = Integer.parseInt(value);
                                    } else if (key.equalsIgnoreCase("scoreLimit")) {
                                        g.scoreLimit = Integer.parseInt(value);
                                    }
                                }
                                catch (IllegalArgumentException e) {
                                    // ignore and go on.
                                }
                            }
                        }
                    }

                    // shuffle the list of players
                    Collections.shuffle(g.players);

                    g.started = true;
                    connection.sendPRIVMSG(target, "Type \002roll\002 to roll, \002pass\002 to pass.");
                    connection.sendPRIVMSG(target, "You get to start it off, " + g.players.get(0) + ".");
                }
                return;
            }

            // Stop the game, provided it has been started.
            if (messageRest.equalsIgnoreCase("stop")) {
                if (!g.started) {
                    connection.sendPRIVMSG(target, "There isn't a game on, " + player + ".");
                } else {
                    g.reset();
                    connection.sendPRIVMSG(target, "Okay, " + player + ", the game has been stopped.");
                }
                return;
            }
        }

        // Abort early if the text isn't a valid command.
        if (!message.equalsIgnoreCase("roll") && !message.equalsIgnoreCase("pass")) {
            return;
        }

        // The rest of the commands require a game to be going, so abort if
        // the game is null here, or if there aren't enough players.
        if (!g.started) {
            connection.sendPRIVMSG(target, "The game hasn't been started yet, " + player + ".");
            return;
        }

        // Abort early if the wrong player is trying to play.
        int find = g.players.indexOf(player);
        if (find != g.turn) {
            if (find == -1) {
                connection.sendPRIVMSG(target, "Um .. " + player + ", you're not in this game, dumbass.");
            } else {
                connection.sendPRIVMSG(target, "Um .. " + player + ", it's not your turn, dumbass.");
            }
            return;
        }

        // Will hold whether the turn has passed somehow.
        boolean passed = false;

        // If the message is 'roll', we give the player their roll.
        if (message.equalsIgnoreCase("roll")) {
            if (Math.random() < 0.005) {
                g.scores.set(g.turn, 0); // it's a cruel world
                connection.sendPRIVMSG(target, "Oinker!  You lose all points!  Gee, " + player + ", you suck ass!");
                passed = true;
            } else {
                Roll roll = new Roll();

                if (roll.isPigOut()) {
                    g.roundScore = 0;
                    connection.sendPRIVMSG(target, "Pig Out!  No points for this round.  Your total score for this game is still " + g.scores.get(g.turn) + ".");
                    passed = true;
                } else {
                    g.roundScore += roll.score;
                    connection.sendPRIVMSG(target, roll.description + " for " + roll.score + ", score for this round is now " + g.roundScore + ".");
                }
            }
        }

        // If the message is 'pass', we give the next player their turn.
        if (message.equalsIgnoreCase("pass")) {
            int oldScore = g.scores.get(g.turn);
            int newScore = oldScore + g.roundScore;
            g.scores.set(g.turn, newScore);
            connection.sendPRIVMSG(target, "You passed.  Your total score for this game is now " + newScore + ".");
            passed = true;
        }

        // If we've marked it as passed, increment the turn (and maybe the round),
        // and show the appropriate info.
        if (passed) {
            g.roundScore = 0;
            g.turn++;

            if (g.turn >= g.players.size()) {
                connection.sendPRIVMSG(target, "Round " + g.round + " is over.");
                g.turn = 0;
                g.round++;

                // Calculate the leading score.
                int numPlayers = g.players.size();
                int[] scores = new int[numPlayers];
                int winningScore = 0;
                for (int i = 0; i < numPlayers; i++) {
                    winningScore = Math.max(winningScore, scores[i] = g.scores.get(i));
                }

                // Calculate the players who have the score.
                StringBuffer winningPlayers = new StringBuffer();
                int count = 0;
                for (int i = 0; i < numPlayers; i++) {
                    if (scores[i] == winningScore) {
                        switch (count) {
                            case 0:
                                winningPlayers.append(g.players.get(i));
                                break;
                            case 1:
                                winningPlayers.insert(0, g.players.get(i) + " and ");
                                break;
                            default:
                                winningPlayers.insert(0, g.players.get(i) + ", ");
                        }
                        count++;
                    }
                }

                // If we've gone past the end of the rounds, then the game is over.
                boolean gameOver = false;
                if (g.round > g.numRounds) {
                    connection.sendPRIVMSG(target, "The game is over! (round " + g.numRounds + " reached)");
                    gameOver = true;
                } else if (winningScore >= g.scoreLimit) {
                    connection.sendPRIVMSG(target, "The game is over! (score " + g.scoreLimit + " reached)");
                    gameOver = true;
                }
                if (gameOver) {
                    connection.sendPRIVMSG(target, (count == 1 ? "The winner was " : "The winners were ") +
                            winningPlayers + " with a total of " + winningScore + " points.");
                    g = null;
                    return;
                } else {
                    connection.sendPRIVMSG(target, (count == 1 ? "The leader is " : "The leaders are ") +
                            winningPlayers + " with a total of " + winningScore + " points.");
                }
            }
            connection.sendPRIVMSG(target, "Okay, " + g.players.get(g.turn) + ", it's your turn now.");
        }
    }

    //-------- INNER CLASSES --------

    /**
     * Convenience class for holding game info.
     */
    class Game {
        private boolean started;
        private int numRounds;
        private int scoreLimit;
        private List<IrcUserName> players;
        private List<Integer> scores;
        private int round;
        private int turn;
        private int roundScore;

        Game() {
            reset();
        }

        void reset() {
            started = false;
            numRounds = -1;
            scoreLimit = -1;
            players = new ArrayList<IrcUserName>(5);
            scores = new ArrayList<Integer>(5);
            round = 1;
            turn = 0;
            roundScore = 0;
        }
    }

    static final String[] rolls = {"Side", "Side", "Razorback", "Trotter", "Snouter", "Leaning Jowler"};
    static final double[] odds = {0.361399, 0.361399, 0.168394, 0.072539, 0.028497, 0.007772};
    static final int[] scores = {0, 0, 5, 5, 10, 15};

    class Roll {
        int pig1, pig2;
        String description;
        int score;

        Roll() {
            //double r;

            pig1 = rollOne();
            pig2 = rollOne();

            if (pig1 < 2 && pig2 < 2) {
                if (pig1 == pig2) {
                    description = "Sider";
                    score = 1;
                } else {
                    description = "Pig Out";
                    score = 0;
                }
            } else if (pig1 < 2) {
                description = rolls[pig2];
                score = scores[pig2];
            } else if (pig2 < 2) {
                description = rolls[pig1];
                score = scores[pig1];
            } else if (pig1 == pig2) {
                description = "Double " + rolls[pig1];
                score = 4 * scores[pig1];
            } else {
                description = rolls[pig1] + ", " + rolls[pig2];
                score = scores[pig1] + scores[pig2];
            }
        }

        private int rollOne() {
            double r = Math.random();
            for (int i = odds.length - 1; i > 0; i--) {
                if (r < odds[i]) {
                    return i;
                }
                r -= odds[i];
            }
            // will get here for 0 only
            return 0;
        }

        boolean isPigOut() {
            return (score == 0);
        }
    }

}
