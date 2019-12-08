package com.example.musictest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends ArrayAdapter<Music> {
    private Context context;
    private List<Music> musics;
    private Music music;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    List<PlayList>playLists  = new ArrayList<>();;
    public MusicAdapter(Context context,int textViewResourceId,List<Music> musics){
        super(context,textViewResourceId, musics);
        this.context = context;
        this.musics = musics;
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        music = musics.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.musicitem, null);
        TextView num = (TextView)view.findViewById(R.id.item_local_music_num);
        TextView songname = (TextView)view.findViewById(R.id.item_local_music_song);
        TextView singer = (TextView)view.findViewById(R.id.item_local_music_singer);
        TextView textView_add = (TextView) view.findViewById(R.id.textView_add);
        songname.setText(music.getSong());
        singer.setText(music.getSinger());
        num.setText(music.getId());
        textView_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Music music2 = musics.get(position);
                final   LocalMusic localMusic = (LocalMusic) getContext();
                Intent intent1 = localMusic.getIntent();
                String data = intent1.getStringExtra("GeDanName");
                databaseHelper = new DatabaseHelper(localMusic,"MusicPlayer",null,3);
                db = databaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("name",data);
                values.put("title",music2.getSong());
                values.put("artist",music2.getSinger());
                values.put("url",music2.getPath());
                values.put("time",music2.getSeekbar_time());
                db.insert("Music",null,values);
                Toast.makeText(context,"添加成功",Toast.LENGTH_SHORT).show();
            }
        });
       /* final View v = LayoutInflater.from(context).inflate(R.layout.activity_local_music, null);
        popupMenu.getMenuInflater().inflate(R.menu.song_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_song_addToSheet:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        final Dialog dialog = builder.create();
                        databaseHelper = new DatabaseHelper(getContext(), "MusicPlayer", null, 1);
                        db = databaseHelper.getWritableDatabase();
                        Cursor cursor = db.query("playlist", null, null, null, null, null, null);
                        playLists.clear();
                        if (cursor.moveToFirst()) {
                            do {
                                String name = cursor.getString(cursor.getColumnIndex("name"));
                                PlayList p1 = new PlayList(name);
                                playLists.add(p1);
                            } while (cursor.moveToNext());
                        }
                        ListView listView = v.findViewById(R.id.MusicList);
                        listView.setAdapter(new GeDanAdapter(getContext(), R.layout.music_list, playLists));
                        dialog.show();
                        if (dialog.getWindow() != null) {
                            dialog.getWindow().setContentView(v);
                        }
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                PlayList p1 = playLists.get(i);
                                String name = p1.getGeDanName();
                                String title = music.getSong();
                                String artist = music.getSinger();
                                String url = music.getPath();
                                ContentValues values = new ContentValues();
                                values.put("name", name);
                                values.put("title", title);
                                values.put("artist", artist);
                                values.put("url", url);
                                db.insert("Music", null, values);
                                dialog.dismiss();
                            }
                        });
                        break;
                    case R.id.menu_song_delete:
                        databaseHelper = new DatabaseHelper(getContext(), "MusicPlayer", null, 1);
                        db = databaseHelper.getWritableDatabase();
                        cursor = db.query("playlist", null, null, null, null, null, null);
                        LocalMusic localMusic = (LocalMusic) getContext();
                        String name = localMusic.Name;
                        String url = music.getPath();
                        db.delete("Music", "name = ? && url = ?", new String[]{"Name", "url"});
                        break;
                }
                return false;
            }
            });
        popupMenu.show();*/
        return view;
}


}
