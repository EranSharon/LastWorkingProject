package com.example.user.myapplication;

import android.widget.ListView;

/**
 * Created by User on 3/8/2017.
 */

public class Account
{
    private int id;
    private String platform;
    private String user_name;
    private String password;


    public Account()
    {
        super();
    }
    public Account(String platform,  String user_name, String password)
    {
        super();
        //this.id = id;
        this.platform = platform;
        this.user_name = user_name;
        this.password = password;
    }
    public String getId()
    {
        return Integer.toString(this.id);
    }
    @Override
    public String toString()
    {
        //return Integer.toString(this.id)+ "," + this.platform + "," + this.user_name + "," + this.password;
        return this.platform;
    }


    public String get_username_password()
    {
        return this.user_name + ": " + this.password ;
    }
}