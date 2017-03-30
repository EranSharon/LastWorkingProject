package com.example.user.myapplication;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class PasswordsScreenActivity extends AppCompatActivity {

    private static final String host_name = "10.23.168.46";//ipv4 wifi adress
    private static final int port_number = 8080;
    private static final String debug_string = "debug";
    private boolean delete = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwords_screen);


     /*  new Thread() NEED TO ADD THE CLIENT SIDE
        {
            @Override
            public void run()
            {
                Socket socket = null;
                try
                {
                    //connecting
                    Log.i(debug_string, "Attempting to connect to server");
                    socket = new Socket(host_name, port_number);
                    Log.i(debug_string, "Connection established");

                    // Send Message To server
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bw.write("From Client");
                    bw.newLine();
                    bw.flush();

                    // Receive Message from server
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    System.out.println("Message from server:" + br.readLine());
                }
                catch (IOException e)
                {
                    Log.e(debug_string, e.getMessage());
                }
            }
        }.start(); */
        final ListView listView1 = (ListView) findViewById(R.id.my_list);

        Account[] items = {
                new Account(1,"Facebook", "eranTheCool", "1234567"),
                new Account(2,"Instagram", "EranThefucker", "Eran1234567"),
                new Account(3,"PSN", "eransharon10@gmail.com", "eran3333"),
                new Account(4,"Steam",  "RoiTheHnoon", "roi4321"),
                new Account(5,"PayPal", "RoiTheKafotBoy", "roi123456"),
                new Account(5,"PayPal", "RoiTheKafotBoy", "roi123456"),
                new Account(5,"PayPal", "RoiTheKafotBoy", "roi123456"),
                new Account(5,"PayPal", "RoiTheKafotBoy", "roi123456"),
                new Account(5,"PayPal", "RoiTheKafotBoy", "roi123456"),
                new Account(5,"PayPal", "RoiTheKafotBoy", "roi123456"),
    };
        ArrayList<Account> lst = new ArrayList<Account>(Arrays.asList(items));
        final ArrayAdapter<Account> adapter = new ArrayAdapter<Account>(this, android.R.layout.simple_list_item_1, lst);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (delete)
                {
                    adapter.remove(adapter.getItem(position));
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    String item = ((TextView)view).getText().toString();
                    Toast.makeText(getBaseContext(), adapter.getItem(position).get_username_password(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void sendMessage2(View view)
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void delete(View view)
    {
        delete = !delete;
        if (delete)
            Toast.makeText(getApplicationContext(),"You have entered delete mode",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(),"You have quit delete mode",Toast.LENGTH_SHORT).show();
    }


    public void MessageBox(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}