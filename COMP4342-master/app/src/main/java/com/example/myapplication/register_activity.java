package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.client.HttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;

public class register_activity extends AppCompatActivity {
    Button back_button;
    Button register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        // start registration process when button clicked
        register_button = findViewById(R.id.Register_button);
        register_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String username = ((TextInputEditText) findViewById(R.id.register_username)).getText().toString();
                String password = ((TextInputEditText) findViewById(R.id.register_password)).getText().toString();
                String email = ((TextInputEditText) findViewById(R.id.register_email)).getText().toString();

                // avoid empty password
                if (password.length() > 0) new register_network(register_activity.this).execute(username, password, email);
                else Toast.makeText(getBaseContext(), "Password is required", Toast.LENGTH_SHORT).show();
            }
        });

        // back to main page when button clicked
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(register_activity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
