package com.example.musictest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class GeDanAdapter extends ArrayAdapter {
    private Context context;
    private List<PlayList> PlayList;
    private PlayList play1;

    public GeDanAdapter(Context context,int textViewResourceId, List<PlayList> PlayList){
        super(context,textViewResourceId, PlayList);
        this.context = context;
        this.PlayList = PlayList;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        play1 = PlayList.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.music_list, null);
        TextView list_name = (TextView)view.findViewById(R.id.list_name);
     list_name.setText(play1.getGeDanName());
        return view;
    }
}