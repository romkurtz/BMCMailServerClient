package Server;

import Utilis.EMsgResponseCodeWord;
import Utilis.EmailMsg;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static Utilis.EmailAddressValidator.IsValidEmail;
import static Utilis.EmailAddressValidator.IsValidEmailVendor;

/** Represents a handler that the server get help from in order to take care of incoming msgs.
 * @author Kurtz, Rom
 * @version 1.0
 */
public class ClientMsgHandler implements Runnable
{
    /**
     * the logger of the class
     */
    private static final Logger _logger = Logger.getLogger(BridgeEmailServer.class.getName());
    /**
     * the Socket of a specific client msg
     */
    private final Socket _clientSocket;
    /**
     * the supported vendors
     */
    private final String[] _supportedVendors;
    /**
     * hashMap with all admin accounts of the vendors
     */
    private final HashMap<String, VendorAccount> _adminAccounts;

    public ClientMsgHandler(Socket socket, String[] supportedVendors, HashMap<String, VendorAccount> adminAccounts)
    {
        _clientSocket = socket;
        _supportedVendors = supportedVendors;
        _adminAccounts = adminAccounts;
    }

    /**
     * initiate the input & output buffers and handle the msg
     */
    @Override
    public void run()
    {
        _logger.entering(ClientMsgHandler.class.getName(), "run");
        try(BufferedReader input = new BufferedReader(new InputStreamReader(_clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(_clientSocket.getOutputStream())))
        {
            handleMsg(output, input);
        }
        catch (IOException e)
        {
            _logger.log(Level.WARNING,"failed to handle msg", e);
        }
        finally
        {
            try
            {
                _clientSocket.close();
            }
            catch (IOException e)
            {
                _logger.log(Level.WARNING,"failed to close client socket", e);
            }
            finally
            {
                _logger.exiting(ClientMsgHandler.class.getName(), "run");
            }
        }
    }

    /**
     * handle the msg and sending with appropriate vendor and write response
     * @param out the server write a response to it
     * @param input the client wrote to here
     */
    private void handleMsg(PrintWriter out, BufferedReader input) throws IOException
    {
        _logger.entering(ClientMsgHandler.class.getName(), "handleMsg");
        EmailMsg msg = null;
        try
        {
            String recipient = input.readLine();
            String sender = input.readLine();
            String senderVendor = getEmailVendor(sender);
            String bodyEmail = input.readLine();
            msg = new EmailMsg(recipient, bodyEmail);
            msg.Sender = sender;
            EMsgResponseCodeWord validateResult = validateReceivedMail(msg, senderVendor);
            if (validateResult != EMsgResponseCodeWord.Success)
            {
                _logger.log(Level.INFO, validateResult.name());
                out.println(validateResult.name());
                out.println(msg);
                return;
            }
            sendMsg(msg, senderVendor, out);
        }
        catch (Exception e)
        {
            String errorMsg = String.format("failed to send msg %s", msg);
            out.println(errorMsg);
            _logger.log(Level.WARNING, errorMsg, e);
        } finally
        {
            _logger.exiting(ClientMsgHandler.class.getName(), "handleMsg");
        }
    }

    /**
     * validate the mail
     * @param msg the msg to validate
     * @param senderVendor the sender vendor
     * @return EMsgResponseCodeWord with relevant error or success
     */
    private EMsgResponseCodeWord validateReceivedMail(EmailMsg msg, String senderVendor)
    {
        _logger.entering(ClientMsgHandler.class.getName(), "validateReceivedMail");
        try
        {
            if(!IsValidEmail(msg.Recipient))
            {
                return EMsgResponseCodeWord.NotValidRecipientMail;
            }

            if(!IsValidEmail(msg.Sender))
            {
                return EMsgResponseCodeWord.NotValidSenderMail;
            }


            if(!IsValidEmailVendor(senderVendor, _supportedVendors))
            {
                return EMsgResponseCodeWord.NotSupportedVendor;
            }
        }
        catch (Exception e)
        {
            String errorMsg = String.format("failed to validate received mail %s", msg);
            _logger.log(Level.WARNING, errorMsg, e);
        }
        finally
        {
            _logger.exiting(ClientMsgHandler.class.getName(), "validateReceivedMail");
        }
        return EMsgResponseCodeWord.Success;
    }

    /**
     * send the msg with appropriate vendor
     * @param msg the msg to send
     * @param senderVendor the sender vendor
     * @param out where to write the response
     */
    private void sendMsg(EmailMsg msg, String senderVendor, PrintWriter out)
    {
        _logger.entering(ClientMsgHandler.class.getName(), "sendMsg");
        _adminAccounts.get(senderVendor).SendMsg(msg, out);
        _logger.exiting(ClientMsgHandler.class.getName(), "sendMsg");
    }


    private static String getEmailVendor(String email)
    {
        return email.split("@")[1];
    }

}
