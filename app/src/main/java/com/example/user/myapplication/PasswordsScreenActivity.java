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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.os.AsyncTask;

public class PasswordsScreenActivity extends AppCompatActivity
{

    //-----------------------------------------------------------------------------
    private static final String host_name = "10.0.0.7";//ipv4 wifi address
    private static final int port_number = 8080;
    private static final String debug_string = "debug";
    private boolean delete = false;
    private String temp_global_account = "";
    int debug_eran = 0;

    //DoInBackround global variables
    String global_message_string = "";
    String add_message = "";
    String delete_message = "";
    String returnFromServer_global = "";
    boolean user_sign_in = false;
    boolean user_add = false;
    boolean user_delete = false;
    String sign_in_string;
    boolean response_finish = false;
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

        MyClientTask Task = new MyClientTask(host_name, port_number);
        Task.execute();


        final String PREFS_NAME = "accountInfo"; //the official SharedPreferences of the client side
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        settings.edit().putBoolean("my_first_time", true).commit();//JUST FOR DEBUGING DELETE THIS LINE********************

        if (settings.getBoolean("my_first_time", true)) //Very first time
        {
            //the app is being launched for first time, do something
            // record the fact that the app has been started at least once

            //NOW THE server gives a unique ID for identification ********************
            //here we send to the server a userId that is not exist like -999 (because its the first time, the user has no id)

            //String returnFromServer = send_message("100,-999");  //**************we need this*************************************************

            sign_in_string = "100,-999";
            user_sign_in = true;
            while(response_finish == false); //waiting for the server to response(from the doInbackRound)
            response_finish = false;
            //createAccountsList(returnFromServer_global, adapter);

            String message_parts[] = returnFromServer_global.split(","); // cheack this

            //here we save the user ID that will send in every future message
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("ID",Integer.parseInt(message_parts[1]));//****crashes here
            editor.commit();
            editor.putBoolean("my_first_time", false).commit();
            debug_eran = settings.getInt("ID",-1000);
            //editor.putBoolean("add", false);
            //boolean nice = settings.getBoolean("add", true);
            editor.commit();
        }
        else
        {
            //this line will run at start on every user that already used the app
            //this line returns the AccountsTableString from the server
            // String returnFromServer = "101,1\n"+    //example of what the server sent
            //"facebook,eransharon,123";

            debug_eran = settings.getInt("ID",-1000);
            //String returnFromServer = send_message("100," + Integer.toString(settings.getInt("ID",-1000)));
            sign_in_string = "100," + Integer.toString(settings.getInt("ID",-1000));
            user_sign_in = true;
            while(response_finish == false); //waiting for the server to response(from the doInbackRound)
            response_finish = false;
            createAccountsList(returnFromServer_global, adapter);

        }

        //-----------------------------------------------------------------------------------


        Button button = (Button) findViewById(R.id.button6);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //adding(adapter);

                startActivity(new Intent(PasswordsScreenActivity.this, AddingActivity.class));

                MessageBox("fasfas");
                temp_global_account =  settings.getString("account","error");
                //adapter.add(stringToOneAccount(temp_global_account)); //Client side add
                //adapter.notifyDataSetChanged();

                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("add", false);
                editor.commit();
            }
        });

        Button btn = (Button) findViewById(R.id.button8); //update button click
        btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //adding(adapter);
                adapter.add(stringToOneAccount(settings.getString("account","error"))); //Easy and bad solution ***********************
                adapter.notifyDataSetChanged();
                add_message = "200," + Integer.toString(settings.getInt("ID",-1000)) + "," + settings.getString("account", "error");//fix this
                user_add = true;
                //send_message("400," + Integer.toString(settings.getInt("ID",-1000)) + "," + adapter.getItem(position));
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
                    //  delete the account from the list view (client side)
                    delete_message = "400," + Integer.toString(settings.getInt("ID",-1000)) + "," + Integer.toString(position);
                    user_delete = true;
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



    //--------------------------------------------------------------------------------------------------------------------server's-string to accounts list-view parsing
    public void createAccountsList(String fullAccountsStringFromServer, ArrayAdapter<Account> adapter)
    {
        String[] accounts = fullAccountsStringFromServer.split("\n"); //separate the string into all the Accounts

        String[] temp=new String[accounts.length - 1];
        System.arraycopy(accounts,1,temp,0,accounts.length - 1);

        for (String account:  temp)
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


    public class MyClientTask extends AsyncTask<Void, Void, Void>
    {

        TextView txtView;
        String dstAddress;
        int dstPort;

        MyClientTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected void onPreExecute() {
            //txtView = (TextView)findViewById(R.id.response);
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            Socket socket = null;
            try
            {

                socket = new Socket(dstAddress, dstPort);
                int bytesRead = 0;
                while(bytesRead != -1)
                {
                    if (user_sign_in == true)
                    {
                        ByteArrayOutputStream bos;
                        bos = new ByteArrayOutputStream(1024);
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                        //String sendInfo = "101" + makeString(2, add_message.length()) + add_message;
                        out.write(sign_in_string.getBytes(), 0, sign_in_string.getBytes().length);

                        byte[] buffer = new byte[1024];
                        InputStream inputStream = socket.getInputStream();
                        bytesRead = inputStream.read(buffer);
                        ByteArrayOutputStream response = new ByteArrayOutputStream(1024);
                        response.write(buffer, 0, bytesRead);

                        returnFromServer_global = response.toString("UTF-8");
                        response_finish = true;
                        user_sign_in = false;
                    }
                    else if(user_add == true)
                    {

                        ByteArrayOutputStream bos;
                        bos = new ByteArrayOutputStream(1024);
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                        //String sendInfo = "101" + makeString(2, add_message.length()) + add_message;
                        out.write(add_message.getBytes(), 0, add_message.getBytes().length);

                        byte[] buffer = new byte[1024];
                        InputStream inputStream = socket.getInputStream();
                        bytesRead = inputStream.read(buffer);
                        ByteArrayOutputStream response = new ByteArrayOutputStream(1024);
                        response.write(buffer, 0, bytesRead);

                        returnFromServer_global = response.toString("UTF-8");
                        user_add = false;
                    }
                    else if(user_delete == true)
                    {
                        ByteArrayOutputStream bos;
                        bos = new ByteArrayOutputStream(1024);
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                        //String sendInfo = "101" + makeString(2, delete_message.length()) + delete_message;
                        out.write(delete_message.getBytes(), 0, delete_message.getBytes().length);

                        byte[] buffer = new byte[1024];
                        InputStream inputStream = socket.getInputStream();
                        bytesRead = inputStream.read(buffer);
                        ByteArrayOutputStream response = new ByteArrayOutputStream(1024);
                        response.write(buffer, 0, bytesRead);

                        returnFromServer_global = response.toString("UTF-8");
                        user_delete = false;
                    }
                }
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
                returnFromServer_global = "UnknownHostException: " + e.toString();
                Log.d("MyApp1", returnFromServer_global);
            } catch (IOException e) {
                e.printStackTrace();
                returnFromServer_global = "IOException: " + e.toString();
                Log.d("MyApp2", returnFromServer_global);
            }
            finally
            {
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        /*
        private String makeString(int numOfBytes, int number)
        {
            int length = (int)(Math.log10(number)+1);
            if(length > numOfBytes)
                return null;

            String str = "";
            for(int i = 0; i <  numOfBytes - length; i++)
                str = str + "0";

            str = str + Integer.toString(number);
            return str;
        }
        */
        /*
        private int getTypeMessage(String str)
        {
            return Integer.parseInt(str.substring(0,3));
        }

        private String getStringMessage(String str)
        {
            String size = str.substring(3,5);
            int to = Integer.parseInt(size) + 5;
            return str.substring(5,to);
        }

        private String handleGetMessage(String str)
        {
            int type = getTypeMessage(str);
            if(type == 101)
            {
                return getStringMessage(str);
            }
            else
                return getStringMessage(str);
        }
        */
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
        }
    }


    /*
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
                    socket.close();

                }
                catch (IOException e)
                {
                    Log.e(debug_string, e.getMessage());
                }
            }
        }.start();
        return global_message_string;
    }
    */


    public void adding(ArrayAdapter<Account> adapter)//View view)
    {
        MessageBox("fasfas");
        startActivity(new Intent(PasswordsScreenActivity.this, AddingActivity.class));
        SharedPreferences sp = getSharedPreferences("accountInfo", Context.MODE_PRIVATE);
        while(true) //while adding activity finish
        {
            if (sp.getBoolean("add",false))
            {
                MessageBox("fasfas");
                temp_global_account =  sp.getString("account","error");
                adapter.add(stringToOneAccount(temp_global_account)); //Client side add
                adapter.notifyDataSetChanged();
                break;
            }
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("add", false);
        editor.commit();
    }

    public void MessageBox(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}




