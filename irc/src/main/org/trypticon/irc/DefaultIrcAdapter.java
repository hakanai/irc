package org.trypticon.irc;

import java.util.List;

/**
 * Default implementation for IrcListener.  Takes any messages received and interprets their contents to pass
 * down to various handleXXXX methods.
 *
 * @author Trejkaz
 */
public class DefaultIrcAdapter extends IrcAdapter {

    /**
     * Interprets the passed line and delegates to a the handle methods.
     *
     * @param conn the IRC connection.
     * @param line the received IRC line.
     */
    public void messageReceived(IrcConnection conn, IrcMessage line) {
        switch (line.getType()) {
            case Rfc1459.MSG_PASS:
                handlePASS(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_NICK:
                handleNICK(conn, line.getSource(), (IrcUserName) IrcNameFactory.create(line.getArg(0)));
                break;
            case Rfc1459.MSG_USER:
                handleUSER(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_SERVER:
                handleSERVER(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_OPER:
                handleOPER(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_QUIT:
                handleQUIT(conn, line.getSource(), line.getArg(0));
                break;
            case Rfc1459.MSG_SQUIT:
                handleSQUIT(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_JOIN:
                handleJOIN(conn, line.getSource(), (IrcChannelName) IrcNameFactory.create(line.getArg(0)));
                break;
            case Rfc1459.MSG_PART:
                handlePART(conn, line.getSource(), (IrcChannelName) IrcNameFactory.create(line.getArg(0)));
                break;
            case Rfc1459.MSG_MODE:
                handleMODE(conn, line.getSource(), IrcNameFactory.create(line.getArg(0)), line.getArgs(1));
                break;
            case Rfc1459.MSG_TOPIC:
                handleTOPIC(conn, line.getSource(), (IrcChannelName) IrcNameFactory.create(line.getArg(0)),
                        line.getArg(1));
                break;
            case Rfc1459.MSG_NAMES:
                handleNAMES(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_LIST:
                handleLIST(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_INVITE:
                handleINVITE(conn, line.getSource(),
                        (IrcChannelName) IrcNameFactory.create(line.getArg(1))); // arg 0 is own nick
                break;
            case Rfc1459.MSG_KICK:
                handleKICK(conn, line.getSource(), (IrcChannelName) IrcNameFactory.create(line.getArg(0)),
                        (IrcUserName) IrcNameFactory.create(line.getArg(1)), line.getArg(2));
                break;
            case Rfc1459.MSG_VERSION:
                handleVERSION(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_STATS:
                handleSTATS(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_LINKS:
                handleLINKS(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_TIME:
                handleTIME(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_CONNECT:
                handleCONNECT(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_TRACE:
                handleTRACE(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_ADMIN:
                handleADMIN(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_INFO:
                handleINFO(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_PRIVMSG:
                handlePRIVMSG(conn, line.getSource(), IrcNameFactory.create(line.getArg(0)), line.getArg(1));
                break;
            case Rfc1459.MSG_NOTICE:
                handleNOTICE(conn, line.getSource(), IrcNameFactory.create(line.getArg(0)), line.getArg(1));
                break;
            case Rfc1459.MSG_WHO:
                handleWHO(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_WHOIS:
                handleWHOIS(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_WHOWAS:
                handleWHOWAS(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_KILL:
                handleKILL(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_PING:
                handlePING(conn, line.getSource(), line.getArg(0));
                break;
            case Rfc1459.MSG_PONG:
                handlePONG(conn, line.getSource(), line.getArg(0), line.getArg(1));
                break;
            case Rfc1459.MSG_ERROR:
                handleERROR(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_AWAY:
                handleAWAY(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_REHASH:
                handleREHASH(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_RESTART:
                handleRESTART(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_SUMMON:
                handleSUMMON(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_USERS:
                handleUSERS(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_WALLOPS:
                handleWALLOPS(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_USERHOST:
                handleUSERHOST(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_ISON:
                handleISON(conn, line.getSource(), line.getArgs());
                break;
            case Rfc1459.MSG_CTCP: {
                String str = line.getArg(1);
                int spaceIndex = str.indexOf(' ');
                if (spaceIndex != -1) {
                    handleCTCP(conn, line.getSource(), IrcNameFactory.create(line.getArg(0)),
                            str.substring(0, spaceIndex), str.substring(spaceIndex + 1));
                } else {
                    handleCTCP(conn, line.getSource(), IrcNameFactory.create(line.getArg(0)),
                            str, null);
                }
            }
            break;
            case Rfc1459.MSG_CTCPREPLY: {
                String str = line.getArg(1);
                int spaceIndex = str.indexOf(' ');
                if (spaceIndex != -1) {
                    handleCTCPREPLY(conn, line.getSource(), IrcNameFactory.create(line.getArg(0)),
                            str.substring(0, spaceIndex), str.substring(spaceIndex + 1));
                } else {
                    handleCTCPREPLY(conn, line.getSource(), IrcNameFactory.create(line.getArg(0)),
                            str, null);
                }
            }
            break;
            default:
                handleNumeric(conn, line.getType(), line.getSource(), line.getArgs());
        }
    }

    public void handleNumeric(IrcConnection conn, int numeric, IrcName source, List args) {
    }

    public void handlePASS(IrcConnection conn, IrcName source, List args) {
    }

    public void handleNICK(IrcConnection conn, IrcName source, IrcUserName newNick) {
    }

    public void handleUSER(IrcConnection conn, IrcName source, List args) {
    }

    public void handleSERVER(IrcConnection conn, IrcName source, List args) {
    }

    public void handleOPER(IrcConnection conn, IrcName source, List args) {
    }

    public void handleQUIT(IrcConnection conn, IrcName source, String message) {
    }

    public void handleSQUIT(IrcConnection conn, IrcName source, List args) {
    }

    public void handleJOIN(IrcConnection conn, IrcName source, IrcChannelName channel) {
    }

    public void handlePART(IrcConnection conn, IrcName source, IrcChannelName channel) {
    }

    public void handleMODE(IrcConnection conn, IrcName source, IrcName target, List args) {
    }

    public void handleTOPIC(IrcConnection conn, IrcName source, IrcChannelName channel, String topic) {
    }

    public void handleNAMES(IrcConnection conn, IrcName source, List args) {
    }

    public void handleLIST(IrcConnection conn, IrcName source, List args) {
    }

    public void handleINVITE(IrcConnection conn, IrcName source, IrcChannelName channel) {
    }

    public void handleKICK(IrcConnection conn, IrcName source, IrcChannelName channel, IrcUserName user, String message) {
    }

    public void handleVERSION(IrcConnection conn, IrcName source, List args) {
    }

    public void handleSTATS(IrcConnection conn, IrcName source, List args) {
    }

    public void handleLINKS(IrcConnection conn, IrcName source, List args) {
    }

    public void handleTIME(IrcConnection conn, IrcName source, List args) {
    }

    public void handleCONNECT(IrcConnection conn, IrcName source, List args) {
    }

    public void handleTRACE(IrcConnection conn, IrcName source, List args) {
    }

    public void handleADMIN(IrcConnection conn, IrcName source, List args) {
    }

    public void handleINFO(IrcConnection conn, IrcName source, List args) {
    }

    public void handlePRIVMSG(IrcConnection conn, IrcName source, IrcName target, String message) {
    }

    public void handleNOTICE(IrcConnection conn, IrcName source, IrcName target, String message) {
    }

    public void handleWHO(IrcConnection conn, IrcName source, List args) {
    }

    public void handleWHOIS(IrcConnection conn, IrcName source, List args) {
    }

    public void handleWHOWAS(IrcConnection conn, IrcName source, List args) {
    }

    public void handleKILL(IrcConnection conn, IrcName source, List args) {
    }

    public void handlePING(IrcConnection conn, IrcName source, String daemon) {
    }

    public void handlePONG(IrcConnection conn, IrcName source, String daemon, String origin) {
    }

    public void handleERROR(IrcConnection conn, IrcName source, List args) {
    }

    public void handleAWAY(IrcConnection conn, IrcName source, List args) {
    }

    public void handleREHASH(IrcConnection conn, IrcName source, List args) {
    }

    public void handleRESTART(IrcConnection conn, IrcName source, List args) {
    }

    public void handleSUMMON(IrcConnection conn, IrcName source, List args) {
    }

    public void handleUSERS(IrcConnection conn, IrcName source, List args) {
    }

    public void handleWALLOPS(IrcConnection conn, IrcName source, List args) {
    }

    public void handleUSERHOST(IrcConnection conn, IrcName source, List args) {
    }

    public void handleISON(IrcConnection conn, IrcName source, List args) {
    }

    public void handleCTCP(IrcConnection conn, IrcName source, IrcName target, String type, String rest) {
    }

    public void handleCTCPREPLY(IrcConnection conn, IrcName source, IrcName target, String type, String rest) {
    }

}
