package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class playlists extends AppCompatActivity {
    MyRecyclerViewAdapter adapter;
    String txt;
    String username;
    ArrayList<String> playlist_names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlists);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        playlist_names = intent.getStringArrayListExtra("playlist_names");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.My_playlist_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_name);
        // back to menu when icon clicked
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(playlists.this,menu_activity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        // set up the RecyclerView
        final RecyclerView recyclerView = findViewById(R.id.playlists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> temp_playlist_names = new ArrayList<>(playlist_names);
        // Not including Favourites in My playlists
        temp_playlist_names.remove("Favourites");
        adapter = new MyRecyclerViewAdapter(this, temp_playlist_names, "playlists");
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new MyRecyclerViewAdapter.OnRecyclerViewClickListener() {
            // Action when single clicked
            @Override
            public void onItemClickListener(View view) {
                Intent intent = new Intent(playlists.this, display_song.class);
                intent.putExtra("username", username);
                intent.putExtra("type", "playlist");
                String playlist_name = ((TextView)((ViewGroup) view).getChildAt(0)).getText().toString();
                intent.putExtra("playlist_name", playlist_name);
                intent.putExtra("playlist_names", menu_activity.updatedPlaylist(username));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_add :
                // Action for "add" playlist at playlist toolbar
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Name");
                final EditText input = new EditText(this);
                input.setSingleLine();
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txt = input.getText().toString();
                        new playlists_network(playlists.this).execute(username, txt, "", adapter, "Add", -1);
                    }
                });
                builder.create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull final MenuItem item) {
        switch(item.getItemId()){
//      Action for 1. "Rename", 2. "Remove"
            case 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Rename");
                final EditText input = new EditText(this);
                input.setSingleLine();
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txt = input.getText().toString();
                        new playlists_network(playlists.this).execute(username, txt, adapter.getItem(item.getOrder()), adapter, "Rename", item.getOrder());
                    }
                });
                builder.create().show();
                return true;
            case 2:
                new playlists_network(playlists.this).execute(username, "", adapter.getItem(item.getOrder()), adapter, "Remove", item.getOrder());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
