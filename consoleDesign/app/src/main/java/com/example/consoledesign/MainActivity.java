package com.example.consoledesign;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.backup.BackupHelper;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements updateHelper.OnUpdateCheckListener
{
    private static final String TAG = "demoApp";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateHelper.with(this)
                .onUpdateCheck(this)
                .check();

        Button btnLeft =(Button) findViewById(R.id.btnLeft);
        btnLeft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("My App", "Message");
                Toast.makeText(getApplicationContext(),"Message", Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }

    @Override
    public void onOnUpdateCheckListener(final String urlApp)
    {
        //Create Alert
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("New Version Avaiable")
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