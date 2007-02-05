package net.sf.freecol.server.control;

import java.io.IOException;
import java.util.logging.Logger;
import net.sf.freecol.common.networking.*;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.MessageHandler;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;
import org.w3c.dom.Element;

/**
 * Handles the network messages.
 * 
 * @see Controller
 */
public abstract class InputHandler extends FreeColServerHolder implements
        MessageHandler {
    private static Logger logger = Logger.getLogger(InputHandler.class
            .getName());
    public static final String COPYRIGHT = "Copyright (C) 2003-2005 The FreeCol Team";
    public static final String LICENSE = "http://www.gnu.org/licenses/gpl.html";
    public static final String REVISION = "$Revision$";

    /**
     * The constructor to use.
     * 
     * @param freeColServer The main server object.
     */
    public InputHandler(FreeColServer freeColServer) {
        super(freeColServer);
    }

    /**
     * Deals with incoming messages that have just been received.
     * 
     * @param connection The <code>Connection</code> the message was received
     *            on.
     * @param element The root element of the message.
     * @return The reply.
     */
    public abstract Element handle(Connection connection, Element element);

    /**
     * Handles a "logout"-message.
     * 
     * @param connection The <code>Connection</code> the message was received
     *            on.
     * @param logoutElement The element (root element in a DOM-parsed XML tree)
     *            that holds all the information.
     * @return The reply.
     */
    abstract protected Element logout(Connection connection,
            Element logoutElement);

    /**
     * Handles a "disconnect"-message.
     * 
     * @param connection The <code>Connection</code> the message was received
     *            on.
     * @param disconnectElement The element (root element in a DOM-parsed XML
     *            tree) that holds all the information.
     * @return The reply.
     */
    protected Element disconnect(Connection connection,
            Element disconnectElement) {
        // The player should be logged out by now, but just in case:
        FreeColServer fcs = getFreeColServer();
        if (fcs == null) {
            logger.warning("FreeColServer null!");
            return null;
        }
        ServerPlayer player = fcs.getPlayer(connection);
        logger.info("Disconnection by: " + connection
                + ((player != null) ? " (" + player.getName() + ") " : ""));
        if (player != null && player.isConnected()) {
            logout(connection, null);
        }
        try {
            connection.reallyClose();
        } catch (IOException e) {
            logger.warning("Could not close the connection.");
        }
        fcs.getServer().removeConnection(connection);
        return null;
    }

    /**
     * Handles a &quot;getRandomNumbers&quot;-message.
     * 
     * @param connection The <code>Connection</code> the message was received
     *            on.
     * @param element The element (root element in a DOM-parsed XML tree) that
     *            holds all the information.
     * @return reply.
     */
    protected Element getRandomNumbers(Connection conn, Element element) {
        StringBuffer sb = new StringBuffer();
        FreeColServer fcs = getFreeColServer();
        if (fcs != null) {
            int[] numbers = fcs.getRandomNumbers(Integer.parseInt(element
                    .getAttribute("n")));
            for (int i = 0; i < numbers.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(String.valueOf(numbers[i]));
            }
        }
        Element reply = Message
                .createNewRootElement("getRandomNumbersConfirmed");
        reply.setAttribute("result", sb.toString());
        return reply;
    }
}
