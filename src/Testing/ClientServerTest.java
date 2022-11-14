package Testing;

import Client.EmailClient;
import Server.BridgeEmailServer;
import Utilis.EMsgResponseCodeWord;
import Utilis.EmailMsg;
import Utilis.MsgResponse;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Testing sending msgs class
 * @author Kurtz, Rom
 * @version 1.0
 */
public class ClientServerTest
{
    /**
     * the logger of the class
     */
    private static final Logger _logger = Logger.getLogger(ClientServerTest.class.getName());
    /**
     * the server to send to him msgs
     */
    private BridgeEmailServer _server;

    /**
     * init the server before tests invoked
     */
    @BeforeClass
    void InitServerForTests()
    {
        _logger.entering(ClientServerTest.class.getName(), "InitServerForTests");
        try
        {
            _server = BridgeEmailServer.GetInstance();
            _server.Start();
        }
        catch (IOException e)
        {
            _logger.log(Level.WARNING, "InitServerForTests failed to start server", e);
        }
        finally
        {
            _logger.exiting(ClientServerTest.class.getName(), "InitServerForTests");
        }
    }

    /**
     * try to send one valid msg and assert the response with expected
     */
    @Test
    void sendOneValidMsg()
    {
        _logger.entering(ClientServerTest.class.getName(), "sendOneValidMsg");

        try
        {
            EmailClient johnDoe = new EmailClient("johnDoe@gmail.com");
            EmailMsg msg = new EmailMsg("janeDoe@walla.co.il", "bla bla.");
            MsgResponse res = johnDoe.SendMsg(msg,_server.ServerHost , _server.ServerPort);
            Assert.assertEquals(res.MsgResponseCodeWord, EMsgResponseCodeWord.Success);
            System.out.println(res);
        }
        catch (Exception e)
        {
            _logger.log(Level.WARNING, "sendOneValidMsg failed to send msg", e);
        }
        finally
        {
            _logger.exiting(ClientServerTest.class.getName(), "sendOneValidMsg");
        }

    }

    /**
     * try to send one invalid(vendor) msg and assert the response with expected
     */
    @Test
    void sendOneNotValidVendorMsg()
    {
        _logger.entering(ClientServerTest.class.getName(), "sendOneNotValidVendorMsg");

        try
        {
            EmailClient johnDoe = new EmailClient("johnDoe@hotmail.com");
            EmailMsg msg = new EmailMsg("jaheDoe@walla.co.il", "bla bla.");
            MsgResponse res = johnDoe.SendMsg(msg, _server.ServerHost, _server.ServerPort);
            Assert.assertEquals(res.MsgResponseCodeWord, EMsgResponseCodeWord.NotSupportedVendor);
            System.out.println(res);
        }

        catch (Exception e)
        {
            _logger.log(Level.WARNING, "sendOneNotValidVendorMsg failed to send msg", e);
        }
        finally
        {
            _logger.exiting(ClientServerTest.class.getName(), "sendOneNotValidVendorMsg");
        }
    }

    /**
     * try to send one invalid (sender email address) msg and assert the response with expected
     */
    @Test
    void sendOneNotValidSenderMailAddressMsg()
    {
        _logger.entering(ClientServerTest.class.getName(), "sendOneNotValidSenderMailAddressMsg");

        try
        {
            EmailClient johnDoe = new EmailClient(".johnDoe@gmail.com");
            EmailMsg msg = new EmailMsg("janeDoe@walla.co.il", "bla bla.");
            MsgResponse res = johnDoe.SendMsg(msg,_server.ServerHost , _server.ServerPort);
            Assert.assertEquals(res.MsgResponseCodeWord, EMsgResponseCodeWord.NotValidSenderMail);
            System.out.println(res);
        }
        catch (Exception e)
        {
            _logger.log(Level.WARNING, "sendOneNotValidSenderMailAddressMsg failed to send msg", e);
        }
        finally
        {
            _logger.exiting(ClientServerTest.class.getName(), "sendOneNotValidSenderMailAddressMsg");
        }
    }

    /**
     * try to send one invalid (recipient email address) msg and assert the response with expected
     */
    @Test
    void sendOneNotValidRecipientMailAddressMsg()
    {
        _logger.entering(ClientServerTest.class.getName(), "sendOneNotValidRecipientMailAddressMsg");

        try
        {
            EmailClient johnDoe = new EmailClient("johnDoe@gmail.com");
            EmailMsg msg = new EmailMsg("j@aneDoe@walla.co.il", "bla bla.");
            MsgResponse res = johnDoe.SendMsg(msg,_server.ServerHost , _server.ServerPort);
            Assert.assertEquals(res.MsgResponseCodeWord, EMsgResponseCodeWord.NotValidRecipientMail);
            System.out.println(res);
        }
        catch (Exception e)
        {
            _logger.log(Level.WARNING, "sendOneNotValidRecipientMailAddressMsg failed to send msg", e);
        }
        finally
        {
            _logger.exiting(ClientServerTest.class.getName(), "sendOneNotValidRecipientMailAddressMsg");
        }
    }

    /**
     * try to send ten valid msgs with ten threads and assert the responses with expected
     */
    @Test
    void sendTogetherTenValidMsgs()
    {
        _logger.entering(ClientServerTest.class.getName(), "sendTogetherTenValidMsgs");

        try
        {
            Thread[] tArr = new Thread[10];
            for (int i = 0; i < 10; i++)
            {
                EmailMsg msg = new EmailMsg("janeDoe@walla.co.il", String.format("msg number: %d", i));
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        MsgResponse res = null;
                        try {
                            EmailClient yuvalLevy = new EmailClient("johnDoe@gmail.com");
                            res = yuvalLevy.SendMsg(msg, _server.ServerHost, _server.ServerPort);
                        } catch (Exception e) {
                            _logger.log(Level.WARNING, "sendTogetherTenValidMsgs failed to send msgs", e);
                        }

                        Assert.assertEquals(res.MsgResponseCodeWord, EMsgResponseCodeWord.Success);
                        System.out.println(res);
                    }
                });
                tArr[i] = t;
                t.start();
            }

            for (int i = 0; i < 10; i++)
            {
             tArr[i].join();
            }
        }

        catch (Exception e)
        {
            _logger.log(Level.WARNING, "sendTogetherTenValidMsgs failed to send msgs", e);
        }
        finally
        {
            _logger.exiting(ClientServerTest.class.getName(), "sendTogetherTenValidMsgs");
        }
    }


    /**
     * after tests, close the server.
     */
    @AfterClass
    void CloseServerForTests() throws IOException, InterruptedException
    {
        _logger.entering(ClientServerTest.class.getName(), "CloseServerForTests");

        try
        {
            _server.close();
        }
        catch (IOException | InterruptedException e)
        {
            _logger.log(Level.WARNING, "CloseServerForTests failed to close server", e);
        }
        finally
        {
            _logger.exiting(ClientServerTest.class.getName(), "CloseServerForTests");
        }

    }
}
