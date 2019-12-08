package com.example.musictest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MusicService musicService;
    LinearLayout addLayout;
    private DatabaseHelper databaseHelper;
private  SQLiteDatabase db;
  List<PlayList>playLists  = new ArrayList<>();;
    GeDanAdapter adapter;
    private TextView bendi_textView;
    private ServiceConnection conn = new ServiceConnection() {
        //成功绑定时调用 即bindService()执行成功同时返回非空IBinder对象
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MyBinder) service).getService();
        }
        //不成功绑定时调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            Log.i("binding is fail","binding is fail");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(MainActivity.this,"MusicPlayer",null,3);
        db = databaseHelper.getWritableDatabase();
       // btn_local = (Button) findViewById(R.id.LocalMusic);
 //初始化显示歌单
        bendi_textView = (TextView)findViewById(R.id.bendi);
        bendi_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LocalMusic.class);
                intent.putExtra("GeDanName","本地音乐");
                startActivity(intent);
            }
        });
        initView();


    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        switch (item.getItemId()){
            case R.id.add_list:
                addLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.add, null);  //从另外的布局关联组件
                dialog.setTitle("创建歌单").setMessage("请输入歌单名称").setView(addLayout);
                dialog.setPositiveButton("确定", new loginClick1());
                dialog.setNegativeButton("取消", new exitClick());
                dialog.show(); break;
            case R.id.delete_list:
                addLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.add, null);  //从另外的布局关联组件
                dialog.setTitle("删除歌单").setMessage("请输入歌单名称").setView(addLayout);
                dialog.setPositiveButton("确定", new loginClick());
                dialog.setNegativeButton("取消", new exitClick());
                dialog.show();
        }
        return true;
    }
    //输入对话框的“确定”按钮
    class loginClick implements DialogInterface.OnClickListener{
        EditText username;
        EditText txt;
        public void onClick(DialogInterface dialog,int which){
            int flag =1;
            username = (EditText)addLayout.findViewById(R.id.edit_test);
            String s1 = username.getText().toString();
            Cursor cursor = db.query("playlist",null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                do{ String name = cursor.getString(cursor.getColumnIndex("name"));
                if(name.equals(s1)){
                    db.delete("playlist","name = ?",new String[]{s1});
                    Toast.makeText(getApplicationContext(),"删除成功",Toast.LENGTH_SHORT).show();
                    flag =0; initView();
                }
                }while (cursor.moveToNext());
            }
            cursor.close();
            if(flag==1){
                Toast.makeText(getApplicationContext(),"歌单不存在",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //输入对话框的“退出”按钮事件
    class exitClick implements DialogInterface.OnClickListener{
        public void onClick(DialogInterface dialog,int which){
        }
    }
    class loginClick1 implements DialogInterface.OnClickListener{
        EditText username;
        EditText txt;
        public void onClick(DialogInterface dialog,int which){
            int flag =1;
            username = (EditText)addLayout.findViewById(R.id.edit_test);
            String s1 = username.getText().toString();
            Cursor cursor = db.query("playlist",null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                do{ String name = cursor.getString(cursor.getColumnIndex("name"));
                    if(name.equals(s1)){
                        Toast.makeText(getApplicationContext(),"此歌单已存在",Toast.LENGTH_SHORT).show();
                        flag =0;
                    }
                }while (cursor.moveToNext());
            }
            cursor.close();
            if(flag==1){
                ContentValues values = new ContentValues();
                values.put("name",s1);
                db.insert("playlist",null,values);
                Toast.makeText(getApplicationContext(),"创建成功",Toast.LENGTH_SHORT).show();
                initView();
            }
        }
    }

    private void  initView(){
        Cursor cursor = db.query("playlist",null,null,null,null,null,null);
        playLists.clear();
        if(cursor.moveToFirst()){
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                PlayList p1 = new PlayList(name);
                playLists.add(p1);
            }while (cursor.moveToNext());
        }cursor.close();
        adapter = new GeDanAdapter(MainActivity.this,R.layout.music_list,playLists);
        ListView listView = (ListView) findViewById(R.id.MusicList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PlayList p1 = playLists.get(i);
                String name  = p1.getGeDanName();
                if(name.equals("本地音乐")){
                Intent intent = new Intent(MainActivity.this,LocalMusic.class);
                intent.putExtra("GeDanName",name);
                startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                    intent.putExtra("GeDanName",name);
                    startActivity(intent);
                }
            }
        });
    }

}
