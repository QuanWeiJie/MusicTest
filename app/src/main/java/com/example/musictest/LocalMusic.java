package com.example.musictest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static java.lang.Math.random;

public class LocalMusic extends AppCompatActivity {
    private Boolean PAUSE=false;
    private Boolean Exist = false;
    private int maxtime=0;
    private int progresstime=0;
    private DatabaseHelper databaseHelper;
    private  SQLiteDatabase db;
    private  List<Music> musics = new ArrayList<>();
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
    private Button back;
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
             Toast.makeText(LocalMusic.this, a + "", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_local_music);
        play =(ImageView) findViewById(R.id.local_music_play);
        playlast =(ImageView) findViewById(R.id.local_music_last);
        playnext =(ImageView) findViewById(R.id.local_music_next);
        seekBar = (SeekBar)findViewById(R.id.seek_bar);
        song = (TextView )findViewById(R.id.local_music_song);
        singer = (TextView)findViewById(R.id.local_music_singer);
        biaoti = (TextView) findViewById(R.id.localMusicTextView);
        imageView = (ImageView) findViewById(R.id.image123);
        Intent intent = getIntent();
        Name = intent.getStringExtra("GeDanName");
        biaoti.setText("本地音乐");
        Toast.makeText(LocalMusic.this,Name,Toast.LENGTH_SHORT).show();
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LocalMusic.this,Main2Activity.class);
                startActivity(intent);
            }
        });
        databaseHelper = new DatabaseHelper(LocalMusic.this,"MusicPlayer",null,3);
        db = databaseHelper.getWritableDatabase();
        //读取SD卡中的音乐数据
     getMusic(this.getContentResolver());
         MusicAdapter adapter = new MusicAdapter(LocalMusic.this,R.layout.musicitem,musics);
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
                Intent intent = new Intent(LocalMusic.this,MusicService.class);
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
                Intent intent = new Intent(LocalMusic.this,MusicService.class);
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
                Intent intent = new Intent(LocalMusic.this,MusicService.class);
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


    public List<Music> getMusic(ContentResolver contentResolver){
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

       if(cursor.moveToFirst()) {
           for (int i = 0; i < cursor.getCount(); i++) {
               String song = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题
               String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
               int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
               SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
               String time = simpleDateFormat.format(new Date(duration));
               String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));    //文件路径
               String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)); //唱片图片
               int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐
               if (isMusic != 0 && duration / (1000 * 60) >= 1&& duration / (1000 * 60) <=4) {        //只把1分钟以上的音乐添加到集合当中
                  n++;
                   Music music = new Music(n+"",album,song,singer,time,url); //新建一个歌曲对象,将从cursor里读出的信息存放进去,直到取完cursor里面的内容为止.
                    music.setSeekbar_time(duration);
                  /* ContentValues values = new ContentValues();
                   values.put("name",Name);
                   values.put("title",song);
                   values.put("artist",singer);
                   values.put("url",url);
                   db.insert("Music",null,values);*/
                   musics.add(music);
               }
               cursor.moveToNext();
           }
       }
        cursor.close();
        return musics;
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
    public String getName1(){
        return Name;
    }
}
