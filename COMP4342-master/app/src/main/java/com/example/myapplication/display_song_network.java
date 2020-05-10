package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class display_song_network extends AsyncTask {
    private Context context;
    private String username;
    private String type;
    private String playlist_name;
    private String song_name;
    private String mode;
    private MyRecyclerViewAdapter adapter;
    private int position;

    public display_song_network(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            username = (String)objects[0];
            type = (String)objects[1];
            playlist_name = (String)objects[2];
            song_name = (String)objects[3];
            mode = (String)objects[4];
            adapter = (MyRecyclerViewAdapter)objects[5];
            position = (int)objects[6];

            String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");;
            data += "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
            data += "&" + URLEncoder.encode("playlistname", "UTF-8") + "=" + URLEncoder.encode(playlist_name, "UTF-8");
            data += "&" + URLEncoder.encode("songname", "UTF-8") + "=" + URLEncoder.encode(song_name, "UTF-8");
            data += "&" + URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode(mode, "UTF-8");

            URL url = new URL (MainActivity.ip_address + "display_song.php");

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
        }
        catch (Exception e) {
            e.printStackTrace();
            return ("Exception: " + e.getMessage());
        }
    }

    protected void onPostExecute(Object result){
        Toast.makeText(context, (String) result, Toast.LENGTH_SHORT).show();
        // Remove the item in the view when data is removed successfully from database
        if (mode.equals("Remove") && result.equals(song_name + " is removed from " + playlist_name + " successfully")) {
            adapter.removeItem(position);
        }
    }
}
