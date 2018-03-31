package eu.selfhost.mirc0.pivoltmeter;

import android.util.Log;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class PiClient {

    private final IPiConnection _connection;
    // sends message received notifications
    private OnMessageReceived _messageListener;
    private boolean _isActive;
    private Thread _readingThread;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public PiClient(OnMessageReceived listener) {
        _messageListener = listener;
        _connection = new PiConnection();
    }

    public boolean connect(String serverIp, int serverPort){
        if (_connection.getIsConnected())
            _connection.Disconnect();
        return _connection.Connect(serverIp, serverPort);
    }

    public void disconnect() {
        if (!_connection.getIsConnected())
            return;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                _connection.Disconnect();
            }
        });
        t.start();
    }

    public void start(){
        if (!_connection.getIsConnected())
            return;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                _connection.sendMessage(TCPMessages.START);
                startReadingEndlessVoltage();
            }
        });
        t.start();
    }

    public void stop() {
        if (!_connection.getIsConnected())
            return;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                _connection.sendMessage(TCPMessages.STOP);
                _isActive = false;
            }
        });
        t.start();
    }

    public void startReadingEndlessVoltage() {

        _isActive = true;
        if (_readingThread != null && _readingThread.getState() != Thread.State.TERMINATED)
            return;

        _readingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                start();
                //in this while the client listens for the messages sent by the server
                while (_connection.getIsConnected() && _isActive) {
                    try {
                        String v = _connection.sendAndReceiveMessage(TCPMessages.GET_VOLTAGE);
                        try {
                            Double number = Double.parseDouble(v);
                            v = String.format ("%.2f", number);
                        }
                        catch (Exception e){

                        }
                        Log.d(TAG, String.format("Received: {0}", v));
                        _messageListener.messageReceived(v);
                    } catch (IOException e) {
                        break;
                    }
                }
            }
        });
        _readingThread.start();
    }

    public void setMeasureRate(double rate) {

        _connection.sendMessage(TCPMessages.SET_RATE);
    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}