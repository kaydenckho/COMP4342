package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class menu_activity extends AppCompatActivity {
    Button playlist_button;
    Button favourite_button;
    Button all_song_button;
    Button recently_played_button;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_page);

        SearchView search = findViewById(R.id.search_bar_menu);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener () {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(menu_activity.this, display_song.class);
                intent.putExtra("username", username);
                intent.putExtra("type", "server_search");
                intent.putExtra("query", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // go to playlists when button clicked
        playlist_button = findViewById(R.id.My_playlist);
        playlist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu_activity.this,playlists.class);
                intent.putExtra("username", username);
                intent.putExtra("playlist_names", updatedPlaylist(username));
                startActivity(intent);
            }
        });

        // go to Favourites when button clicked
        favourite_button = findViewById(R.id.Favourites);
        favourite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu_activity.this, display_song.class);
                intent.putExtra("username", username);
                intent.putExtra("type", "playlist");
                intent.putExtra("playlist_name", "Favourites");
                intent.putExtra("playlist_names", updatedPlaylist(username));
                startActivity(intent);
            }
        });

        // go to All songs when button clicked
        all_song_button = findViewById(R.id.All_songs);
        all_song_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu_activity.this, display_song.class);
                intent.putExtra("username", username);
                intent.putExtra("type", "all_song");
                intent.putExtra("playlist_name", "");
                intent.putExtra("playlist_names", updatedPlaylist(username));
                startActivity(intent);
            }
        });

        // Logout when button clicked
        recently_played_button = findViewById(R.id.Logout);
        recently_played_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(menu_activity.this, "logout successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(menu_activity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (display_song.mPlayer != null) display_song.mPlayer.release();
                startActivity(intent);
            }
        });
    }

    // Retrieve up-to-date playlists' names for display
    static ArrayList<String> updatedPlaylist (String username) {
        try {
            String data  = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("playlistname", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
            data += "&" + URLEncoder.encode("oldPlaylistname", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
            data += "&" + URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode("Show", "UTF-8");

            URL url = new URL (MainActivity.ip_address +  "playlists.php");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream());

            w.write(data);
            w.flush();

            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = null;

            ArrayList<String> playlist_names = new ArrayList<>();

            while((line = r.readLine()) != null) {
                playlist_names.add(line);
            }
            playlist_names.remove(playlist_names.size() - 1);

            return playlist_names;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
