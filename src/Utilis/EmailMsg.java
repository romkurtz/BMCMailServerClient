package Utilis;

/** Represents a email msg
 * @author Kurtz, Rom
 * @version 1.0
 */
public class EmailMsg
{
    /**
     * the mail body
     */
    public final String Body;
    /**
     * the mail recipient
     */
    public final String Recipient;
    /**
     * the mail sender
     */
    public String Sender;

    public EmailMsg(String recipient, String body)
    {
        Body = body;
        Recipient = recipient;
    }

    @Override
    public String toString()
    {
        return String.format("From: %s", Sender) + System.lineSeparator() +
               String.format("To: %s", Recipient) + System.lineSeparator() +
               String.format("Body: %s", Body);
    }
}
