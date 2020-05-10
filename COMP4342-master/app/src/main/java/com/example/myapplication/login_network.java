package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class login_network extends AsyncTask {
    private Context context;
    Intent intent;

    public login_network(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            String username = (String)objects[0];
            String password = (String)objects[1];
            intent = new Intent(context,menu_activity.class);
            intent.putExtra("username", username);

            // compute the Hash of password for transmission and storage
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuffer hex = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String h = Integer.toHexString(0xff & hash[i]);
                if(h.length() == 1) hex.append('0');
                hex.append(h);
            }
            password = hex.toString();

            String data  = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            URL url = new URL (MainActivity.ip_address +  "login.php");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream());

            // post data to the php
            w.write(data);
            w.flush();

            BufferedReader r = new BufferedReader(new
                    InputStreamReader(connection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // reading responses
            while((line = r.readLine()) != null) {
                sb.append(line);
                break;
            }

            return sb.toString();

        } catch (Exception e) {
            return ("Exception: " + e.getMessage());
        }
    }

    protected void onPostExecute(Object result){
        Toast.makeText(context, (String) result, Toast.LENGTH_SHORT).show();
        // go to menu page when login successfully
        if (result.equals("login successfully")) {
            context.startActivity(intent);
        }
    }
}
