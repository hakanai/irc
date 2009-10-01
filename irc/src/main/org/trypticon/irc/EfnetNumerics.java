/*
 * Created by IntelliJ IDEA.
 * User: dpnoll
 * Date: Jun 28, 2001
 * Time: 10:03:00 AM
 * To change template for new class use 
 * Source Code | Class Templates options (Tools | IDE Options).
 */
package org.trypticon.irc;

/**
 * Numeric values returned for functionality on Efnet not covered by standard commands.
 *
 * @author Trejkaz
 */
public interface EfnetNumerics extends IrcNumerics {
    // TODO: Needs documentation but I have forgotten what most of them mean.
    // XXX: These might have standardised by now.  Needs more research on servers around today.
    // Non-standard messages for Efnet (NOT STRICTLY PART OF RFC!)
    public static final int RPL_WELCOME = 1; // actually 001, etc.
    public static final int RPL_YOURHOST = 2;
    public static final int RPL_CREATED = 3;
    public static final int RPL_MYINFO = 4;
    public static final int RPL_STATSCONN = 250;
    public static final int RPL_LOCALUSERS = 265;
    public static final int RPL_GLOBALUSERS = 266;
    public static final int RPL_TOPICWHOTIME = 333;
}
