package com.example.user.myapplication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class PasswordsScreenActivity extends AppCompatActivity
{

    //-----------------------------------------------------------------------------
    private static final String host_name = "10.0.0.2";//ipv4 wifi address
    private static final int port_number = 8080;
    private static final String debug_string = "debug";
    private boolean delete = false;
    private String temp_global_account = "";
    private String global_message_string = "";
    int  debug_eran = 0;



    //-----------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwords_screen);


        Account[] items = {};
        ArrayList<Account> lst = new ArrayList<Account>(Arrays.asList(items));
        final ArrayAdapter<Account> adapter = new ArrayAdapter<Account>(this, android.R.layout.simple_list_item_1, lst);
        final ListView accountsListView = (ListView) findViewById(R.id.my_list);
        accountsListView.setAdapter(adapter);

        //--------------------------------------------------------------------------First time run code
        final String PREFS_NAME = "accountInfo";
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        settings.edit().putBoolean("my_first_time", true).commit();//JUST FOR DEBUGING DELETE THIS LINE********************

        if (settings.getBoolean("my_first_time", true)) //Very first time
        {
            //the app is being launched for first time, do something
            // record the fact that the app has been started at least once

            //NOW THE server gives a unique ID for identification ********************
            //here we send to the server a userId that is not exist like -999 (because its the first time, the user has no id)

            //String returnFromServer = send_message("100,-999");  //**************we need this

            String returnFromServer = "101,1,facebook,eransharon,123\n" +
                    "facebook,eransharon,123\n" +
                    "facebook,eransharon,123\n" +
                    "facebook,eransharon,123\n" +
                    "facebook,eransharon,123\n" +
                    "facebook,eransharon,123"
                    ;

            String message_parts[] = returnFromServer.split(","); // cheack this

            //here we save the user ID that will send in every future message
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("ID",Integer.parseInt(message_parts[1]));//****crashes here
            editor.commit();
            int id = settings.getInt("ID",-1001);
            editor.putBoolean("my_first_time", false).commit();
            editor.putBoolean("add", false);
            boolean nice = settings.getBoolean("add", true);
            editor.commit();
            createAccountsList(returnFromServer, adapter);

        }
        else
        {
            //this line will run at start on every user that already used the app
            //this line returns the AccountsTableString from the server
            debug_eran = settings.getInt("ID",-1000);
            send_message("100," + Integer.toString(settings.getInt("ID",-1000)));
        }
        //-----------------------------------------------------------------------------------


        Button button = (Button) findViewById(R.id.button6);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                adding(adapter);
                /*
                startActivity(new Intent(PasswordsScreenActivity.this, AddingActivity.class));

                if (settings.getBoolean("add",false))
                {
                    MessageBox("fasfas");
                    SharedPreferences sp = getSharedPreferences("accountInfo", Context.MODE_PRIVATE);
                    temp_global_account =  sp.getString("account","error");

                    adapter.add(stringToOneAccount(temp_global_account)); //Client side add
                    adapter.notifyDataSetChanged();

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("add", false);
                    editor.commit();
                }
                */
            }
        });


        //--------------------------------------------------------------------------------Here we click on one of the accounts
        accountsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (delete)
                {

                    //send_message("400," + Integer.toString(settings.getInt("ID",-1000)) + "," + adapter.getItem(position));
                    //  delete the account from the list view (client side)
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
        //------------------------------------------------------------------------------------
    }

    public void adding(ArrayAdapter<Account> adapter)//View view)
    {
        MessageBox("fasfas");
        final String PREFS_NAME = "accountInfo";
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);



        Intent intent = new Intent(this,AddingActivity.class);
        startActivity(intent);


        SharedPreferences sp = getSharedPreferences("accountInfo", Context.MODE_PRIVATE);
        temp_global_account =  sp.getString("account","error");

        //send_message("200," + Integer.toString(settings.getInt("ID",-1000)) + "," + temp_global_account);

        adapter.add(stringToOneAccount(temp_global_account)); //Client side add
        adapter.notifyDataSetChanged();
    }

    //--------------------------------------------------------------------------------------------------------------------server's-string to accounts list-view parsing
    public void createAccountsList(String fullAccountsStringFromServer, ArrayAdapter<Account> adapter)
    {

        String[] accounts = fullAccountsStringFromServer.split("\n"); //separate the string into all the Accounts
        for (String account:  accounts)
        {
            //adds all the accounts into the current listView(accountsListView),(using the adapter of this listView)
            adapter.add(stringToOneAccount(account));
        }
    }

    public Account stringToOneAccount(String codedAccount) //should not be here, im just writing this
    {
        String[] fields = codedAccount.split(","); //separate the string into all the Account fields
        Account retAccount = new Account(fields[0], fields[1], fields[2]);
        return retAccount;
        //add exeptions
    }
    //--------------------------------------------------------------------------------------------------------------------

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



    public String send_message(final String message)
    {
        new Thread()
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
                    bw.write(message);//*****************************HERE WE SEND THE MESSAGE TO THE SERVER
                    bw.newLine();
                    bw.flush();


                    //func1(adapter);
                    // Receive Message from server
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    global_message_string = br.readLine(); //******************RETURNS THE ANSWER FROM THE SERVER
                    System.out.println("Message from server:" + global_message_string);
                    //socket.close();

                }
                catch (IOException e)
                {
                    Log.e(debug_string, e.getMessage());
                }
            }
        }.start();
        return global_message_string;
    }
}




