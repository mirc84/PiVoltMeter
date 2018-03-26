package eu.selfhost.mirc0.pivoltmeter;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by m_irc on 25.03.2018.
 */

public class NetworkScanner {

    private final HostFoundHandler _handler;

    public NetworkScanner(HostFoundHandler listnener){
        _handler = listnener;
    }

    public void SearchNetwork(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Enumeration e;
                try {
                    e = NetworkInterface.getNetworkInterfaces();
                } catch (SocketException e1) {
                    e1.printStackTrace();
                    return;
                }
                while(e.hasMoreElements())
                {
                    NetworkInterface n = (NetworkInterface) e.nextElement();
                    Enumeration ee = n.getInetAddresses();
                    while (ee.hasMoreElements())
                    {
                        InetAddress i = (InetAddress) ee.nextElement();
                        byte[] address = i.getAddress();

                        if (address[0] != (byte)192 && address[0] != (byte)169)
                            continue;

                        ScanNetwork(address);
                    }
                }
            }
        });
    }

    private void ScanNetwork(byte[] ip) {
        try {
            byte own = ip[3];
            for (int i = 1; i <= 254; i++) {
                byte b = (byte) i;
                if (b == own)
                    continue;

                ip[3] = b;
                final byte[] currentIp = ip.clone();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LookForHost(currentIp);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LookForHost(byte[] ip) throws IOException {
        InetAddress address = InetAddress.getByAddress(ip);
        if (!address.isReachable(50)) {
            return;
        }

        String hostIP = address.getHostAddress();
        String hostName = address.getHostName();

        if (hostIP.equals(hostName))
            hostName = "";

        _handler.FoundHost(hostIP, hostName);
    }

    public interface HostFoundHandler{
        void FoundHost(String hostAddress, String hostName);
    }
}

