package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity{
    TextView title;
    Button register_button;
    Button login_button;
    static String ip_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ip_address == null) {
            ip_address = "http://10.0.2.2:8080/";
        }
        title = (TextView) findViewById(R.id.Title);
        title.setSelected(true);

        // go to registration page when button clicked
        register_button = findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,register_activity.class);
                startActivity(intent);
            }
        });

        // start login processing when button clicked
        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((TextInputEditText) findViewById(R.id.login_username)).getText().toString();
                String password = ((TextInputEditText) findViewById(R.id.login_password)).getText().toString();
                // post data to the php
                new login_network(MainActivity.this).execute(username, password);
            }
        });

    }
}
