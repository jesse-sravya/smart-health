package com.jworks.smartcity.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.jworks.smartcity.MainActivity;
import com.jworks.smartcity.R;

public class LedControl extends AppCompatActivity {

    Button btn1, btnDis, receiveButton;
    String address = null;
    TextView lumn;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    TextView receivedTextInput;
    String receivedText = "";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);


        Intent intent = getIntent();
        address = intent.getStringExtra(HomeFragment.EXTRA_ADDRESS);

        btn1 =  findViewById(R.id.button2);
        btnDis = findViewById(R.id.button4);
        receivedTextInput = findViewById(R.id.textViewReceived);
        lumn =  findViewById(R.id.textView2);
        receiveButton = findViewById(R.id.button3);

        new LedControl.ConnectBT().execute();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("1");
            }
        });

//        getDataFromDevice();

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                disconnect();
            }
        });

        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                getDataFromDevice();
            }
        });

    }

    private void sendSignal ( String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void disconnect() {
        if ( btSocket!=null ) {
            try {
                btSocket.close();
            } catch(IOException e) {
                msg("Error");
            }
        }

        finish();
    }

    private void getDataFromDevice() {
        byte[] buffer = new byte[256];  // buffer store for the stream
        int bytes; // bytes returned from read()
        try {
            InputStream tmpIn = null;

            // Get the BluetoothSocket input and output streams
            tmpIn = btSocket.getInputStream();

            DataInputStream mmInStream = new DataInputStream(tmpIn);

            // Read from the InputStream
//            while (true) {
//                bytes = mmInStream.read(buffer);
//
//                String readMessage = new String(buffer, 0, bytes);
//                // Send the obtained bytes to the UI Activity
//
//                receivedTextInput.setText(readMessage);
//                Log.i("DATA---", readMessage);
//            }

            int limit = 2000;
            int counter = 0;
            while (counter < limit) {
                counter++;
                bytes = mmInStream.read(buffer);

//                String readMessage = new String(buffer, 0, bytes);
                // Send the obtained bytes to the UI Activity

//                if (readMessage.length() > 0) {
//                    receivedTextInput.setText(readMessage);
//                    Log.i("DATA---", readMessage);
//                }
            }
            disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
            disconnect();
        }
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(LedControl.this, "Connecting...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }


}