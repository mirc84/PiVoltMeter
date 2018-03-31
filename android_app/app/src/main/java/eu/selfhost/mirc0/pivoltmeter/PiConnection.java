package eu.selfhost.mirc0.pivoltmeter;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import static android.content.ContentValues.TAG;

public class PiConnection implements IPiConnection {

    private final Object _lock;
    // while this is true, the server will continue running
    private boolean _isConnected = false;
    // used to send messages
    private PrintWriter _outBuffer;
    // used to read messages from the server
    private BufferedReader _inBuffer;
    private Socket _socket;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public PiConnection() {
        _lock = new Object();
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    @Override
    public void sendMessage(final String message) {
        synchronized (_lock) {
            if (!_isConnected || _outBuffer == null)
                return;

            Log.d(TAG, "Sending: " + message);
            _outBuffer.println(message + "\r\n");
            _outBuffer.flush();
        }
    }

    @Override
    public void sendMessage(String method, String parameter) {
        sendMessage(String.format("-%s %s", method, parameter));
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    @Override
    public String sendAndReceiveMessage(final String message) throws IOException {
        synchronized (_lock) {
            if (!_isConnected || _outBuffer == null || _inBuffer == null)
                return null;

            Log.d(TAG, "Sending: " + message);
            _outBuffer.println(message + "\r\n");
            _outBuffer.flush();

            String answer = _inBuffer.readLine();
            return answer;
        }
    }

    @Override
    public boolean Connect(String serverIp, int serverPort) {

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(serverIp);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            _socket = new Socket();
            _socket.connect(new InetSocketAddress(serverAddr, serverPort), 2000);
            _socket.setKeepAlive(true);

            try {
                _outBuffer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream())), true);
                _inBuffer = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
                _isConnected = true;
            } catch (IOException e) {
                Log.e("TCP Client", "Error while connecting.", e);
                _inBuffer = null;
                _outBuffer = null;
                _socket.close();
                return false;
            }
        } catch (IOException e) {
            Log.e("TCP Client", "Error while connection.", e);
            _socket = null;
            return false;
        }
        return true;
    }

    @Override
    public void Disconnect() {
        synchronized (_lock) {
            _isConnected = false;
            sendMessage(TCPMessages.STOP);
            try {
                _socket.close();
            } catch (IOException e) {
                Log.e("TCP Client", "Error while closing connection.", e);
            }
            _inBuffer = null;
            _outBuffer = null;
            _socket = null;
        }
    }

    @Override
    public boolean getIsConnected() {
        return _isConnected;
    }
}