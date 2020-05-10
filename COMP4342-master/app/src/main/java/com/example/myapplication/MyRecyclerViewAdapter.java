package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    public interface OnRecyclerViewClickListener {
        void onItemClickListener(View view);
    }

    private OnRecyclerViewClickListener listener;
    public void setItemClickListener(OnRecyclerViewClickListener itemClickListener) {
        listener = itemClickListener;
    }

    private List<String> mData;
    private LayoutInflater mInflater;
    private String Type;
    private ArrayList<String> other_playlist_names;
    private String display_song_type;

    // data is passed into the constructor for playlists / server_search
    MyRecyclerViewAdapter(Context context, List<String> data, String type) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.Type = type;
    }

    // data is passed into the constructor for display_song
    MyRecyclerViewAdapter(Context context, List<String> data, String type, ArrayList<String> other_playlist_names, String display_song_type) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.Type = type;
        this.other_playlist_names = other_playlist_names;
        this.display_song_type = display_song_type;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        if(listener != null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(v);
                }
            });
        }
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String song = mData.get(position);
        holder.myTextView.setText(song);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView myTextView;
        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.each_row);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if (Type.equals("display_song")) {
                int itemId = 1;
                for (String playlist_name: other_playlist_names) {
                    menu.add(0, itemId ++, this.getAdapterPosition(),"Add to " + playlist_name);
                }

                // only enable removal of songs from a playlist (e.g. cannot remove from all songs)
                if (display_song_type.equals("playlist")) menu.add(0, itemId, this.getAdapterPosition(), R.string.remove_button);
            }
            if (Type.equals("playlists")) {
                MenuItem rename = menu.add(1, 1, this.getAdapterPosition(), R.string.rename_button);
                MenuItem remove = menu.add(1, 2, this.getAdapterPosition(), R.string.remove_button);
            }
            if (Type.equals("server_search")) {
                menu.add(2, 1, this.getAdapterPosition(), "Download");
            }
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // convenience method for edit data at click position
    void addItem(String txt) {
        this.mData.add(txt);
        notifyDataSetChanged();
    }

    // convenience method for edit data at click position
    void editItem(int id, String txt) {
        this.mData.set(id, txt);
        notifyDataSetChanged();
    }

    // convenience method for remove data at click position
    void removeItem(int id) {
        this.mData.remove(id);
        notifyDataSetChanged();
    }
}
