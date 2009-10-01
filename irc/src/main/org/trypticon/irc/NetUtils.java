package org.trypticon.irc;

import java.net.InetAddress;
import java.net.Socket;
import java.io.InterruptedIOException;
import java.io.IOException;

/**
 * @author Trejkaz
 */
public class NetUtils {

    /**
     * Polling delay for socket connections, in milliseconds.
     */
    private static final int POLL_DELAY = 100;

    /**
     * Creates a new socket object with a timeout period.
     *
     * @param host the host address to connect to.
     * @param port the port.
     * @param timeoutMillis the timeout, in milliseconds.
     * @return a socket connected to the given host and port.
     * @throws IOException if an error occurs connecting.
     */
    public static Socket getSocket(InetAddress host, int port, int timeoutMillis)
            throws IOException {
        return getSocket(host, port, InetAddress.getLocalHost(), 0, timeoutMillis);
    }

    /**
     * Creates a new socket object with a timeout period.
     *
     * @param host the host address to connect to.
     * @param port the port to connect to.
     * @param localHost the host address to bind to.
     * @param localPort the port to bind to.
     * @param timeoutMillis the timeout, in milliseconds.
     * @return a socket connected to the given host and port.
     * @throws IOException if an error occurs connecting.
     */
    public static Socket getSocket(InetAddress host, int port, InetAddress localHost, int localPort, int timeoutMillis)
            throws IOException {
        SocketThread st = new SocketThread(host, port, localHost, localPort);
        st.start();

        int timer = 0;
        Socket sock;

        while (true) {
            if (st.isConnected()) {
                sock = st.getSocket();
                break;
            } else {
                if (st.isError()) {
                    throw st.getException();
                }
                try {
                    Thread.sleep(POLL_DELAY);
                }
                catch (InterruptedException e) {
                    // Ignore.
                }

                timer += POLL_DELAY;

                if (timer > timeoutMillis) {
                    throw new InterruptedIOException("Could not connect for " + timeoutMillis + "ms");
                }
            }
        }
        return sock;
    }


    /**
     * Inner class for establishing a socket thread.
     */
    static class SocketThread extends Thread {
        private InetAddress addr = null;
        private int port = 0;
        private InetAddress localAddr = null;
        private int localPort = 0;

        volatile private Socket connection = null;
        private IOException exception = null;

        public SocketThread(InetAddress addr, int port, InetAddress localAddr, int localPort) {
            this.addr = addr;
            this.port = port;
            this.localAddr = localAddr;
            this.localPort = localPort;
        }

        public void run() {
            Socket sock;
            try {
                sock = new Socket(addr, port, localAddr, localPort);
            }
            catch (IOException e) {
                exception = e;
                return;
            }
            connection = sock;
        }

        public boolean isConnected() {
            return (connection != null);
        }

        public boolean isError() {
            return (exception != null);
        }

        public Socket getSocket() {
            return connection;
        }

        public IOException getException() {
            return exception;
        }
    }
}
