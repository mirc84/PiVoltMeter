package eu.selfhost.mirc0.pivoltmeter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private PiClient _client;
    private TextView _label;
    private ArrayAdapter<String> _spinnerAdapter;
    private Button _button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        _label = findViewById(R.id.current_voltage);
        final EditText ipTextView = findViewById(R.id.server_edit_host);
        final Spinner serversSpinner = findViewById(R.id.servers_spinner);
        final EditText portTextView = findViewById(R.id.server_edit_port);
        _button = findViewById(R.id.start_measuring_button);
        _button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedServer = ipTextView.getText().toString();
                String portText = portTextView.getText().toString();
                int port = Integer.parseInt(portText);
                StartMeasuring(selectedServer, port);
            }
        });

        _spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        _spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serversSpinner.setAdapter(_spinnerAdapter);
        serversSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] selectedServer = serversSpinner.getSelectedItem().toString().split(" - ");

                String ip;
                if (selectedServer.length == 1)
                    ip = selectedServer[0];
                else if (selectedServer.length == 2)
                    ip = selectedServer[1];
                else
                    return;

                ipTextView.setText(ip);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        NetworkScanner scanner = new NetworkScanner(new NetworkScanner.HostFoundHandler() {
            @Override
            public void FoundHost(final String hostAddress, final String hostName) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AddHost(hostAddress, hostName);
                    }
                });
            }
        });
        scanner.SearchNetwork();
    }

    private void AddHost(String hostAddress, String hostName) {
        String host;
        if (hostName == null)
            host = hostAddress;
        else
            host = hostName + " - " + hostAddress;

        _spinnerAdapter.add(host);
        _spinnerAdapter.notifyDataSetChanged();
    }

    private void StartMeasuring(final String ip, final int port) {
        if (_client != null)
        {
            _client.stopClient();
            _client = null;
            _button.setText("Start");
            return;
        }

        _client = new PiClient(new PiClient.OnMessageReceived() {

            @Override
            public void messageReceived(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _label.setText(message + "V");
                    }
                });
            }
        });

        Runnable connect = new Runnable() {
            @Override
            public void run() {
                _client.connect(ip, port);
            }
        };
        AsyncTask.execute(connect);
        _button.setText("Stop");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
