package eu.selfhost.mirc0.pivoltmeter;

import java.io.IOException;

interface IPiConnection {
    void sendMessage(String message);
    void sendMessage(String method, String parameter);

    String sendAndReceiveMessage(String message) throws IOException;

    boolean Connect(String serverIp, int serverPort);

    void Disconnect();

    boolean getIsConnected();
}
