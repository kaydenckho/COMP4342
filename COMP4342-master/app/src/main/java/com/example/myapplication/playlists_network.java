package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class playlists_network extends AsyncTask {
    private Context context;
    private MyRecyclerViewAdapter adapter;
    private String username;
    private String playlistname;
    private String oldPlaylistname;
    private String mode;
    private int position;

    public playlists_network(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            username = (String)objects[0];
            playlistname = (String)objects[1];
            oldPlaylistname = (String)objects[2];
            adapter = (MyRecyclerViewAdapter)objects[3];
            mode = (String)objects[4];
            position = (int)objects[5];

            String data  = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("playlistname", "UTF-8") + "=" + URLEncoder.encode(playlistname, "UTF-8");
            data += "&" + URLEncoder.encode("oldPlaylistname", "UTF-8") + "=" + URLEncoder.encode(oldPlaylistname, "UTF-8");
            data += "&" + URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode(mode, "UTF-8");

            URL url = new URL (MainActivity.ip_address + "playlists.php");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream());

            // post data to the php
            w.write(data);
            w.flush();

            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));

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
        // Add a item to the view when database operation succeeds
        if (mode.equals("Add") && result.equals("A new playlist is created successfully")) {
            adapter.addItem(playlistname);
        }
        // Rename the play list when database operation succeeds
        if (mode.equals("Rename") && result.equals("The playlist is renamed successfully")) {
            adapter.editItem(position, playlistname);
        }

        // Remove the item from the view when database operation succeeds
        if (mode.equals("Remove") && result.equals("The playlist is removed successfully")) {
            adapter.removeItem(position);
        }

    }
}
