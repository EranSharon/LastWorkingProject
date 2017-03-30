package com.example.user.myapplication;

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
    public Account(int id, String platform,  String user_name, String password)
    {
        super();
        this.id = id;
        this.platform = platform;
        this.user_name = user_name;
        this.password = password;
    }

    @Override
    public String toString()
    {
        return this.platform ;
    }


    public String get_username_password()
    {
        return this.user_name + ": " + this.password ;
    }
}