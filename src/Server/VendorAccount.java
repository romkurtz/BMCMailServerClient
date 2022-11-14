package Server;

import Utilis.EMsgResponseCodeWord;
import Utilis.EmailMsg;

import java.io.PrintWriter;
import java.util.logging.Logger;

/** Represents a vendor account of one vendor.
 * @author Kurtz, Rom
 * @version 1.0
 */
public class VendorAccount
{
    /**
     * the logger of the class
     */
    private static final Logger _logger = Logger.getLogger(BridgeEmailServer.class.getName());
    /**
     * the password of vendor account user
     */
    private final String _password;
    /**
     * the user name of vendor account
     */
    private final String _userName;
    /**
     * the vendor server address
     */
    private final String _serverAddress;
    /**
     * the vendor name
     */
    private final String _vendorName;

    public VendorAccount(String userName, String password, String serverAddress)
    {
        _userName = userName;
        _password = password;
        _serverAddress = serverAddress;
        _vendorName = serverAddress.replaceFirst("smtp.","");
        //if (!logIn()) _logger.log(Level.WARNING, "logIn failed");
    }

    /**
     * send msg to the vendor server and from there to the destination
     * @param msg the msg to send
     * @param out the out buffer to write response for client
     */
    public void SendMsg(EmailMsg msg, PrintWriter out)
    {
        _logger.entering(VendorAccount.class.getName(), "SendMsg");
        // "sending msg" code...
        // code...
        // code...
        StringBuilder sb = new StringBuilder();
        out.println(EMsgResponseCodeWord.Success.name());
        out.println(msg.toString());
        out.println();
        out.println(String.format("Sent with vendor engine: %s", _vendorName));

        _logger.exiting(VendorAccount.class.getName(), "SendMsg");
    }

    /**
     * perform login to the vendor sender with the credentials
     * @return true if login succeeded and false otherwise
     */

    private boolean logIn()
    {
        _logger.entering(VendorAccount.class.getName(), "logIn");
        // "login" code...
        // code...
        // code../
        _logger.exiting(VendorAccount.class.getName(), "logIn");
        return false;
    }


}
