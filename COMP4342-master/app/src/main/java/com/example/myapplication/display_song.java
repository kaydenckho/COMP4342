package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class display_song extends AppCompatActivity{
    static MediaPlayer mPlayer;
    static String currentSong;
    MyRecyclerViewAdapter adapter;
    String username;
    String type;
    String playlist_name;
    ArrayList<String> playlist_names;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.display_song);

        SearchView search = findViewById(R.id.search_bar_display);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener () {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(display_song.this, display_song.class);
                intent.putExtra("username", username);
                intent.putExtra("type", "server_search");
                intent.putExtra("query", query);
                startActivity(intent);
                if (type.equals("server_search")) display_song.this.finish();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        type = intent.getStringExtra("type");
        if (type.equals("all_song") || type.equals("playlist")) {
            playlist_name = intent.getStringExtra("playlist_name");
            playlist_names = intent.getStringArrayListExtra("playlist_names");
        }
        else {
            query = intent.getStringExtra("query");
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // set up the RecyclerView
        final RecyclerView recyclerView = findViewById(R.id.song_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (type.equals("all_song") || type.equals("playlist")) {
            ArrayList<String> temp_playlist_names = new ArrayList<>(playlist_names);
            temp_playlist_names.remove(playlist_name);
            adapter = new MyRecyclerViewAdapter(this, updatedSongList(username, type, playlist_name), "display_song", temp_playlist_names, type);
        }
        // search result from server
        else {
            adapter = new MyRecyclerViewAdapter(this, updatedSongResult(query), type);
        }
        recyclerView.setAdapter(adapter);

        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        adapter.setItemClickListener(new MyRecyclerViewAdapter.OnRecyclerViewClickListener() {

            @Override
            public void onItemClickListener(View view) {
                // play song when song clicked in playlist or all_song
                if (type.equals("all_song") || type.equals("playlist")) {
                    int position = recyclerView.getChildAdapterPosition(view);
                    String SongName = adapter.getItem(position);

                    // stop the playing song when a song is clicked
                    if (mPlayer.isPlaying()) {
                        mPlayer.stop();
                        mPlayer.reset();

                        // play the new song if the clicked song and the playing song are different
                        if (!currentSong.equals(SongName)) {
                            try {
                                mPlayer.setDataSource(getExternalFilesDir(null) + "/Songs/" + SongName + ".mp3");
                                mPlayer.prepare();
                                mPlayer.start();
                                currentSong = SongName;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // else play the song
                    else {
                        try {
                            mPlayer.setDataSource(getExternalFilesDir(null) + "/Songs/" + SongName + ".mp3");
                            mPlayer.prepare();
                            mPlayer.start();
                            currentSong = SongName;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // context menu for playlist and All songs
        if (type.equals("all_song") || type.equals("playlist")) {
            ArrayList<String> temp_playlist_names = new ArrayList<>(playlist_names);
            temp_playlist_names.remove(playlist_name);
            // Remove a song from a play list
            if (item.getItemId() == temp_playlist_names.size() + 1) {
                new display_song_network(display_song.this).execute(username, type, playlist_name, adapter.getItem(item.getOrder()), "Remove", adapter, item.getOrder());
                return true;
                // Add a song to a play list
            } else if (item.getItemId() <= temp_playlist_names.size()) {
                new display_song_network(display_song.this).execute(username, type, temp_playlist_names.get(item.getItemId() - 1), adapter.getItem(item.getOrder()), "Add", adapter, -1);
                return true;
            } else {
                return super.onContextItemSelected(item);
            }
        }
        // context menu for search result
        else {
            // download song
            if (item.getItemId() == 1) {
                try {
                    String data = URLEncoder.encode("SongName", "UTF-8") + "=" + URLEncoder.encode(adapter.getItem(item.getOrder()), "UTF-8");
                    data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");

                    URL url = new URL (MainActivity.ip_address + "download.php");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream());

                    w.write(data);
                    w.flush();

                    BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] file = new byte[16384];
                    int current = 0;
                    while ((current = bis.read(file,0,file.length)) != -1) {
                        buffer.write(file,0,current);
                    }
                    file = buffer.toByteArray();
                    if ((new String(file)).equals(adapter.getItem(item.getOrder()) + " is already downloaded")) {
                        Toast.makeText(display_song.this, new String(file), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        File dir = new File (getExternalFilesDir(null) + "/Songs/");
                        dir.mkdirs();
                        File mypath = new File (dir, adapter.getItem(item.getOrder()) + ".mp3");
                        mypath.createNewFile();
                        OutputStream outputStream = new FileOutputStream(mypath);
                        outputStream.write(file);
                        outputStream.flush();
                        outputStream.close();
                        Toast.makeText(display_song.this, "Downloaded" + adapter.getItem(item.getOrder()) + " to " + mypath.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            }
            else {
                return super.onContextItemSelected(item);
            }
        }
    }

    // Retrieve up-to-date songs' names for display in song list
    ArrayList<String> updatedSongList (String username, String type, String playlist_name) {
        try {
            String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");;
            data += "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
            if (type.equals("all_song")) data += "&" + URLEncoder.encode("playlistname", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
            else data += "&" + URLEncoder.encode("playlistname", "UTF-8") + "=" + URLEncoder.encode(playlist_name, "UTF-8");
            data += "&" + URLEncoder.encode("songname", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
            data += "&" + URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode("Show", "UTF-8");

            URL url = new URL (MainActivity.ip_address + "display_song.php");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream());

            w.write(data);
            w.flush();

            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = null;

            ArrayList<String> Song_list = new ArrayList<String>();;

            while((line = r.readLine()) != null) {
                Song_list.add(line);
            }
            Song_list.remove(Song_list.size() - 1);

            return Song_list;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Retrieve up-to-date songs' names for display in search result from server
    ArrayList<String> updatedSongResult (String query) {
        try {
            String data = URLEncoder.encode("query", "UTF-8") + "=" + URLEncoder.encode(query, "UTF-8");;

            URL url = new URL (MainActivity.ip_address + "search.php");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream());

            w.write(data);
            w.flush();

            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = null;

            ArrayList<String> Song_list = new ArrayList<String>();;

            while((line = r.readLine()) != null) {
                Song_list.add(line);
                break;
            }

            if (Song_list.size() == 0) Toast.makeText(display_song.this, "No Song Found", Toast.LENGTH_SHORT).show();
            return Song_list;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
