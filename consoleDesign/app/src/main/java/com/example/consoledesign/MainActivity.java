package com.example.consoledesign;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements updateHelper.OnUpdateCheckListener
{
    private static final String TAG = "demoApp";
    private final String DEVICE_ADDRESS = "XXX";                //Mac Address of bluetooth module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    String command;                                             //Store value Transmitter to the bt module

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
        Button btnRight = findViewById(R.id.btnRight);      /*        btnRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("My Right", "Right L");
                Toast.makeText(getApplicationContext(),"Right", Toast.LENGTH_SHORT)
                        .show();
            }
        })*/;
        Button btnFrw = findViewById(R.id.btnForward);      /*        btnUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("My Up", "Up L");
                Toast.makeText(getApplicationContext(),"Up", Toast.LENGTH_SHORT)
                        .show();
            }
        });*/
        Button btnSlow = findViewById(R.id.btnSlow);        /*        btnSlow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("My Up", "slow");
                Toast.makeText(getApplicationContext(),"Up", Toast.LENGTH_SHORT)
                        .show();
            }
        });*/
        Button btnBTCon = findViewById(R.id.btnBTCon);


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

        btnRight.setOnTouchListener(new View.OnTouchListener()
        {
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

    public boolean btInit()
    {
        boolean found = false;

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null)                                      //Checking device supporting bluetooth
        {
            Toast.makeText(getApplicationContext(), "No BT Support!", Toast.LENGTH_SHORT)
                    .show();
        }
        if (!btAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);

            try
            {
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