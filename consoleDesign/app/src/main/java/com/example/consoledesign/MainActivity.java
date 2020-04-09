package com.example.consoledesign;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

//import com.google.firebase.database.

public class MainActivity extends AppCompatActivity implements updateHelper.OnUpdateCheckListener
{
    private static final String TAG = "demoApp";
    private static final String EXTRA_ADDRESS = "device_add";
    private final String DEVICE_ADDRESS = "XXX";                //Mac Address of bluetooth module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final int REQUEST_ENABLE_BT = 1;
    ListView listView;
    TextView textView;
    ArrayList<String> list = new ArrayList<String>();

    String address =null;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private BluetoothAdapter bluetoothAdapter;
    BluetoothAdapter listBTAdapter;
    DatabaseReference reff;


    String command;                                             //Store value Transmitter to the bt module
    private BluetoothDevice Device;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateHelper.with(this)
                .onUpdateCheck(this)
                .check();

        Button btnLeft = findViewById(R.id.btnLeft);        /*        btnLeft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("My Left", "Message");
                Toast.makeText(getApplicationContext(),"Left", Toast.LENGTH_SHORT)
                        .show();
            }
        });*/
        Button btnRight = findViewById(R.id.btnRight);
        Button btnFrw = findViewById(R.id.btnForward);
        Button btnSlow = findViewById(R.id.btnSlow);
        Button btnBTCon = findViewById(R.id.btnBTCon);
        Button btnReg = findViewById(R.id.btnRec);

        reff = FirebaseDatabase.getInstance().getReference().child("testReff");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        TextView textView1 = (TextView) findViewById(R.id.textView1);
        listBTAdapter = BluetoothAdapter.getDefaultAdapter();
        textView1.append("\nAdapter: " + listBTAdapter);


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


            }
        });

        //Forward Button - Long Press
        btnFrw.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)       //Holding Button
                {
                    command = "1";

                    try
                    {
                        outputStream.write(command.getBytes());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    command = "10";
                    try
                    {
                        outputStream.write(command.getBytes());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        btnSlow.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    command = "2";
                    try
                    {
                        outputStream.write(command.getBytes());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    command = "10";
                    try
                    {
                        outputStream.write(command.getBytes());
                    } catch (IOException e)
                    {
                        //e.printStackTrace();
                    }
                }
                return false;
            }
        });

        btnLeft.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    command = "3";
                    try
                    {
                        outputStream.write(command.getBytes());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    command = "10";
                    try
                    {
                        outputStream.write(command.getBytes());
                    } catch (IOException e)
                    {
                        //e.printStackTrace();
                    }
                }
                return false;
            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener()        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    command = "4";

                    try
                    {
                        outputStream.write(command.getBytes());
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    command = "10";
                    try
                    {
                        outputStream.write(command.getBytes());
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }

                }
                return false;
            }
        });

        btnBTCon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (btInit())
                {
                    btConnect();
                }
            }
        });

    }

    public boolean btInit()    {
        boolean found = false;

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null){                                     //Checking device supporting bluetooth
            Toast.makeText(getApplicationContext(), "No BT Support!", Toast.LENGTH_SHORT)
                    .show();
        }
        if (!btAdapter.isEnabled())        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevice = btAdapter.getBondedDevices();

        if (bondedDevice.isEmpty())                                         //Checking BT devices
        {
            Toast.makeText(getApplicationContext(),"Pair Device 1st!", Toast.LENGTH_SHORT)
                    .show();
        }
        else
        {
            for (BluetoothDevice iterator : bondedDevice)
            {
                if (iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }
        return found;
    }
    public void search(View v)    {
        list.clear();
        listView.setAdapter(null);
        discover();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice bt : pairedDevices)
        {
            list.add(bt.getName()+ "\n" + bt.getAddress());
        }
        Toast.makeText(getApplicationContext(),"Showing Devices", Toast.LENGTH_SHORT)
                .show();
        if (pairedDevices.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"No device Found", Toast.LENGTH_SHORT)
            .show();
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        listView.setAdapter(arrayAdapter);
        listView.setOnClickListener((View.OnClickListener) selectDevice);
    }


    public void discover()    {
        bluetoothAdapter.cancelDiscovery();
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible,0);
    }

    public AdapterView.OnItemClickListener selectDevice = (parent, view, position, id)
    {
        String info = ((TextView)view).getText().toString();
        String address = info.substring(info.length()-17);

        Intent i =new Intent(MainActivity.this,MainActivity.class);
        i.putExtra(EXTRA_ADDRESS,address);
        startActivity(i);
    }

    private void sendDataPairedDevice(String message, BluetoothDevice device){
        byte[] toSend = message.getBytes();

        try {
            UUID appUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(appUUID);
            OutputStream mmOutStream = socket.getOutputStream();
            mmOutStream.write(toSend);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Exception during write", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean btConnect()
    {
        boolean connected = true;
        try
        {
            socket = device.createInsecureRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();

            Toast.makeText(getApplicationContext(),"Connection is Successful", Toast.LENGTH_LONG)
                    .show();
        } catch (IOException e)
        {
            e.printStackTrace();
            connected = false;
        }

        if (connected)
        {
            try
            {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return connected;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }



    @Override
    public void onOnUpdateCheckListener(final String urlApp)
    {
        //Create Alert
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("New Version Available")
                .setMessage("Please Update")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Toast.makeText(MainActivity.this, ""+urlApp, Toast.LENGTH_SHORT ).show();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                }).create();
        alertDialog.show();
    }


}