package eu.selfhost.mirc0.pivoltmeter;

import android.util.Log;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        final int[] count = {0};

        PiClient client = new PiClient(new PiClient.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                count[0]++;
                System.out.println(message);
            }
        }
        );

        client.connect("127.0.0.0", 42000);
        while (true){
            if (count[0] > 10000)
                break;
        }

        client.stopClient();
    }
}