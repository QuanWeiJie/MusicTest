package com.example.musictest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Main2Activity extends AppCompatActivity {
    private Boolean PAUSE=false;
    private Boolean Exist = false;
    private int maxtime=0;
    private int progresstime=0;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private List<Music> musics = new ArrayList<>();
    private  MusicService musicService;
    private static  final int UpdateSeekBar =1;
    private static  final int ChangeSeekBar =2;
    private ImageView play;
    private ImageView playnext;
    private ImageView playlast;
    private TextView song;
    private TextView singer;
    public SeekBar seekBar;
    private TextView biaoti;
    private  ImageView imageView;
    private Button add;
    private  int i=0;
    public int position1=0;
    int n =0;
    long a=0;
    private int flags=0;
    public String Name = "";
    private MyThread myThread = new MyThread();
    private MusicService.MyBinder myBinder;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MusicService.MyBinder) service;
            musicService = ((MusicService.MyBinder) service).getService();
            a= myBinder.getDuration();
            Toast.makeText(Main2Activity.this, a + "", Toast.LENGTH_SHORT).show();
            //  seekBar.setProgress(10);
            Log.d("LocalMusic","Connect");
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        play =(ImageView) findViewById(R.id.local_music_play);
        playlast =(ImageView) findViewById(R.id.local_music_last);
        playnext =(ImageView) findViewById(R.id.local_music_next);
        seekBar = (SeekBar)findViewById(R.id.seek_bar);
        song = (TextView )findViewById(R.id.local_music_song);
        singer = (TextView)findViewById(R.id.local_music_singer);
        biaoti = (TextView) findViewById(R.id.localMusicTextView);
        imageView = (ImageView) findViewById(R.id.image123);
        add = (Button) findViewById(R.id.Btn_add_music);
        Intent intent = getIntent();
        Name = intent.getStringExtra("GeDanName");
        biaoti.setText(Name);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this,LocalMusic.class);
                intent.putExtra("GeDanName",Name);
                startActivity(intent);
                finish();
            }
        });
        databaseHelper = new DatabaseHelper(Main2Activity.this,"MusicPlayer",null,3);
        db = databaseHelper.getWritableDatabase();
        getMusic();
        MusicSonAdapter adapter = new MusicSonAdapter(Main2Activity.this,R.layout.musicitemsong,musics);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (flags){
                    case 1: imageView.setImageResource(R.drawable.shunxu);flags=0;break;
                    case 0: imageView.setImageResource(R.drawable.suiji);flags=1;break;
                }
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (i == 0) {
                    musicService.start();
                    PAUSE=true;
                    play.setBackgroundResource(R.drawable.pause);
                    Log.d("i的值", i + "");
                    i = 1;
                } else {
                    Log.d("i的值", i + "");
                    PAUSE=false;
                    musicService.start();
                    play.setBackgroundResource(R.drawable.play);
                    i = 0;
                }

            }
        });
        playnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flags==0)
                {position1=(position1+1)%n;}
                if(flags==1){
                    Random random = new Random();
                    position1=random.nextInt(n);
                }
                Music music = musics.get(position1);
                String url = music.getPath();
                String title = music.getSong();
                String artist = music.getSinger();
                String time = music.getDuration();
                Log.d("url",url);
                Log.d("title",title);
                Log.d("artist",artist);
                song.setText(title);
                singer.setText(artist);
                maxtime = music.getSeekbar_time();
                progresstime=0;
                Intent intent = new Intent(Main2Activity.this,MusicService.class);
                intent.putExtra("url",url);
                intent.putExtra("title",title);
                intent.putExtra("artist",artist);
                bindService(intent,conn,BIND_AUTO_CREATE);//绑定服务
                play.setBackgroundResource(R.drawable.play);
                startService(intent);
            }
        });
        playlast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flags==0){
                    position1 = position1-1;
                    if(position1<0) position1+=n;
                }
                if(flags==1){
                    Random random = new Random();
                    position1=random.nextInt(n);
                }
                Music music = musics.get(position1);
                String url = music.getPath();
                String title = music.getSong();
                String artist = music.getSinger();
                String time = music.getDuration();
                Log.d("url",url);
                Log.d("title",title);
                Log.d("artist",artist);
                song.setText(title);
                singer.setText(artist);
                maxtime=music.getSeekbar_time();
                progresstime=0;
                Intent intent = new Intent(Main2Activity.this,MusicService.class);
                intent.putExtra("url",url);
                intent.putExtra("title",title);
                intent.putExtra("artist",artist);
                bindService(intent,conn,BIND_AUTO_CREATE);//绑定服务
                play.setBackgroundResource(R.drawable.play);
                startService(intent);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("a11当前进度",""+i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicService.seekto(seekBar.getProgress());
                progresstime=seekBar.getProgress();
                Message message = new Message();
                message.what = ChangeSeekBar;
                handler.sendMessage(message);
            }
        });


        listView.setOnItemClickListener (new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Log.e("huizhong", "onitemclick");
                position1=position;
                Music music = musics.get(position);
                String url = music.getPath();
                String title = music.getSong();
                String artist = music.getSinger();
                String time = music.getDuration();
                maxtime = music.getSeekbar_time();
                progresstime = 0;
                song.setText(title);
                singer.setText(artist);
                Intent intent = new Intent(Main2Activity.this,MusicService.class);
                intent.putExtra("url",url);
                intent.putExtra("title",title);
                intent.putExtra("artist",artist);
                intent.putExtra("duration",time);
                bindService(intent,conn,BIND_AUTO_CREATE);//绑定服务
                play.setBackgroundResource(R.drawable.play);
                startService(intent);
                // myThread.interrupt();
                if(Exist==false){
                    new Thread(myThread).start();
                    Exist=true;
                }
            }
        });

    }


    public void getMusic(){
     /*   String s1 = "name =?";
        Cursor cursor = db.query("playlist",null,s1,null,null,null,null);
        playLists.clear();
        if(cursor.moveToFirst()){
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                PlayList p1 = new PlayList(name);
                playLists.add(p1);
            }while (cursor.moveToNext());
        }cursor.close();*/
        Cursor cursor = db.query("Music",null,null,null,null,null,null);
        if(cursor.moveToFirst()) {
         do{
             String name1 =   cursor.getString((cursor.getColumnIndex("name")));
             if(name1.equals(Name))
             {
                String song = cursor.getString((cursor.getColumnIndex("title")));//音乐标题
                String singer = cursor.getString(cursor.getColumnIndex("artist"));//艺术家
                int duration = cursor.getInt(cursor.getColumnIndex("time"));
                 String url = cursor.getString((cursor.getColumnIndex("url")));
                 Log.d("name1",name1);
                 Log.d("song",song);
                  n++;
                Music music = new Music(); //新建一个歌曲对象,将从cursor里读出的信息存放进去,直到取完cursor里面的内容为止.
                   music.setId(n+"");
                   music.setSong(song);
                   music.setSinger(singer);
                    music.setSeekbar_time(duration);
                    music.setPath(url);
                 /*   ContentValues values = new ContentValues();
                    values.put("name",Name);
                    values.put("title",song);
                    values.put("artist",singer);
                    values.put("url",url);
                    db.insert("Music",null,values);*/
                    musics.add(music);
             }
                }while(cursor.moveToNext());
            }
        cursor.close();
       // return musics;
    }


    class MyThread extends  Thread{
        @Override
        public void run(){
            try{
                while(progresstime<maxtime) {
                    if(PAUSE==true){

                    }
                    else {
                        Thread.sleep(1000);
                        Message message = new Message();
                        message.what = UpdateSeekBar;
                        handler.sendMessage(message);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
    }
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case UpdateSeekBar:{
                    seekBar.setMax(maxtime);
                    seekBar.setProgress(progresstime);
                    progresstime+=1000;
                  /*  seekBar.setMax(258704);
                    seekBar.setProgress(0);
                    Log.d("a111handler","abc"+myBinder.getDuration());
                    Log.d("a123handler","abc"+myBinder.getCurrentProgress());*/
                } break;
                case ChangeSeekBar: break;
                default: break;
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

