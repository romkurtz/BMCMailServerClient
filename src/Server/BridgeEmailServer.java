package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Represents a bridge email server.
 * @author Kurtz, Rom
 * @version 1.0
 */
public class BridgeEmailServer implements AutoCloseable
{
    /**
     * the logger of the class
     */
    private static final Logger _logger = Logger.getLogger(BridgeEmailServer.class.getName());
    /**
     * hashMap with all admin accounts of the vendors
     */
    private final HashMap<String, VendorAccount> _adminAccounts = new HashMap<String, VendorAccount>();
    /**
     * an arbitrary port number that the server listen to
     */
    public final int ServerPort = 4444;
    /**
     * the server host is local
     */
    public final String ServerHost = "localhost";
    /**
     * the supported vendors
     */
    private static final String[] _supportedVendors = {"gmail.com", "walla.co.il", "yahoo.com"};
    /**
     *  the thread that continually listen to the port and take care of incoming messages
     */
    private Thread _workerThread;
    /**
     * The server socket object that this server listens to
     */
    private final ServerSocket _serverSocket;
    /**
     * is the server stopped or not
     */
    private boolean _stopped = true;
    /**
     * stop timeout limit for any still ongoing mails
     */
    private static final int STOP_TIMEOUT = 20000;

    /**
     * the singleton server instance
     */
    private static BridgeEmailServer _serverInstance;

    /**
     *
     * @return the singleton server instance or create if not created yet
     */
    public static BridgeEmailServer GetInstance() throws IOException
    {
        if (_serverInstance == null)
        {
            _serverInstance = new BridgeEmailServer();
        }

        return _serverInstance;
    }

    private BridgeEmailServer() throws IOException
    {
        VendorAccount _gmailAccount = new VendorAccount("admin", "admin", "smtp.gmail.com");
        _adminAccounts.put("gmail.com", _gmailAccount);
        VendorAccount _wallaAccount = new VendorAccount("admin", "admin", "smtp.walla.co.il");
        _adminAccounts.put("walla.co.il", _wallaAccount);
        VendorAccount _yahooAccount = new VendorAccount("admin", "admin", "smtp.yahoo.com");
        _adminAccounts.put("yahoo.com", _yahooAccount);
        _serverSocket = new ServerSocket(ServerPort);
    }

    /**
     * start the server if stopped (initiate the worker thread)
     */
    public void Start()
    {
        _logger.entering(BridgeEmailServer.class.getName(), "Start");
        if (_stopped)
        {
        _stopped = false;
        _workerThread = new Thread(this::performWork);
        _workerThread.start();
        }
        _logger.exiting(BridgeEmailServer.class.getName(), "Start");
    }

    /**
     * Main loop of the server. listen to sockets and handle them
     */
    private void performWork()
    {
        _logger.entering(BridgeEmailServer.class.getName(), "performWork");
        try
        {
            // Server: loop until stopped
            while (!_stopped)
            {
                // Start server socket and listen for client connections
                Socket clientSocket = _serverSocket.accept();
                // a dedicated object will handle the received msg with a new thread because we want to listen for more msgs
                ClientMsgHandler clientSocketHandler = new ClientMsgHandler(clientSocket, _supportedVendors, _adminAccounts);
                Thread t = new Thread(clientSocketHandler);
                t.start();
            }
        }
        catch (Exception e)
        {
            // SocketException expected when stopping the server
            if (!_stopped)
            {
                _logger.log(Level.WARNING,"exception in performWork running server", e);
                try
                {
                    _serverSocket.close();
                }
                catch (IOException ex)
                {
                    _logger.log(Level.WARNING," exception closing the _serverSocket", ex);
                }
            }
        }
        finally
        {
            _logger.exiting(BridgeEmailServer.class.getName(), "performWork");
        }
    }

    /**
     * stop the server from waiting to sockets
     */
    @Override
    public void close() throws IOException, InterruptedException
    {
        _logger.entering(BridgeEmailServer.class.getName(), "close");
        try
        {
            if (_stopped) return;

            _stopped = true;
            // close the server socket
            _serverSocket.close();

            // block until worker finished
            _workerThread.join(STOP_TIMEOUT);
        }
        catch (IOException e)
        {
            _logger.log(Level.WARNING,"trouble closing the server socket", e);
        }
        catch (InterruptedException e)
        {
            _logger.log(Level.WARNING ,"interrupted when waiting for worker thread to finish", e);
        }
        finally
        {
            _serverInstance = null;
            _logger.exiting(BridgeEmailServer.class.getName(), "close");
        }

    }
}
