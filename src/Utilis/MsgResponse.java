package Utilis;

import java.io.BufferedReader;
import java.io.IOException;
/** Represents a msg response from server
 * @author Kurtz, Rom
 * @version 1.0
 */
public class MsgResponse
{
    /**
     * the msg response code word
     */
    public EMsgResponseCodeWord MsgResponseCodeWord;
    /**
     * the response detailed description from server
     */
    private final StringBuilder _outputMsg = new StringBuilder();

    public MsgResponse(EMsgResponseCodeWord msgResponseCodeWord, String outputMsg)
    {
        MsgResponseCodeWord = msgResponseCodeWord;
        _outputMsg.append(outputMsg);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Response code: %s", MsgResponseCodeWord.name()));
        sb.append(System.lineSeparator());
        sb.append("Response to message: ");
        sb.append(System.lineSeparator());
        sb.append(_outputMsg);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());

        return sb.toString();
    }
}
