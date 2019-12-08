package com.example.musictest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String sql1= "create table playlist("
            +"name String primary key)";
    public static final String sql= "create table Music("
            +"id integer  primary key Autoincrement,"
            +"name String ,"
            +"title String,"
            +"artist String,"
            +"time integer,"
            +"url String)";
    private Context mcontext;
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mcontext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(sql);
        db.execSQL(sql1);
        Toast.makeText(mcontext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("drop table if exists Music");
        db.execSQL("drop table if exists playlist");
        onCreate(db);
    }
}
