package Client;

import Utilis.EMsgResponseCodeWord;
import Utilis.EmailMsg;
import Utilis.MsgResponse;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Represents an email client of one person.
 * @author Kurtz, Rom
 * @version 1.0
 */
public class EmailClient
{
    /**
     * the logger of the class
     */
    private static final Logger _logger = Logger.getLogger(EmailClient.class.getName());
    /**
     * the client mail address
     */
    private final String _mailAddress;

    public EmailClient(String mailAddress)
    {
        _mailAddress = mailAddress;
    }

    /**
     *
     * @param msg the msg that should be sent
     * @param serverHost the server host to send to it the msg
     * @param serverPort the port in the server host
     * @return a MsgResponse that the server responded
     */
    public MsgResponse SendMsg(EmailMsg msg, String serverHost, int serverPort) throws Exception
    {
        _logger.entering(EmailClient.class.getName(), "SendMsg");
        msg.Sender = _mailAddress;

        try(Socket socket = new Socket(serverHost, serverPort);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);)
        {
            out.println(msg.Recipient);
            out.println(msg.Sender);
            out.println(msg.Body);

            StringBuilder response = new StringBuilder();
            EMsgResponseCodeWord codeWord = EMsgResponseCodeWord.valueOf(input.readLine());
            for (String line = input.readLine(); line != null; line = input.readLine())
            {
                response.append(line);
                response.append(System.lineSeparator());
            }

            return new MsgResponse(codeWord, response.toString());
        }

        catch (IOException e)
        {
            //Thread.sleep(500);
            // try again..
            SendMsg(msg, serverHost, serverPort);
            _logger.log(Level.WARNING,String.format("Unable to send msg: %s", msg), e);
        }
        finally
        {
            _logger.exiting(EmailClient.class.getName(), "SendMsg");
        }

        return null;
    }

    @Override
    public String toString()
    {
        return String.format("Email client email address is: %s", _mailAddress);
    }
}
