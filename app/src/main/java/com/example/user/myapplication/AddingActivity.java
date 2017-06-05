package com.example.user.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddingActivity extends AppCompatActivity
{
    EditText platform;
    EditText password;
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);
    }
    public void done_button(View view)
    {
        platform = (EditText) findViewById(R.id.editText);
        username = (EditText) findViewById(R.id.editText3);
        password = (EditText) findViewById(R.id.editText2);

        SharedPreferences sp = getSharedPreferences("accountInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor sedt = sp.edit();
        sedt.putString("account", platform.getText().toString() + "," + username.getText().toString() + "," + password.getText().toString());

        sedt.putBoolean("add", true);//****crashes here
        sedt.commit();
        finish();
    }
    /*
    public void onDestroy()
    {
        String PREFS_NAME = "FirstTimeRunFile";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("add", true);//****crashes here
        editor.commit();
        super.onDestroy();
    }
    */
}
