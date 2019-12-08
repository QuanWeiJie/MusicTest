package com.example.musictest;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;


public class MusicService extends Service {
    public MediaPlayer mediaPlayer = new MediaPlayer();
    private MyBinder myBinder;
    private static final int SET_SEEKBAR_MAX = 3;
    private static final int UPDATE_PROGRESS = 1;
    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
        public int getDuration(){
            if(mediaPlayer!=null)
                return mediaPlayer.getDuration();
            else return 0;
        }
        public int getCurrentProgress() {
            if (mediaPlayer != null) {
                return mediaPlayer.getCurrentPosition();
            }
            return 0;
        }
    }
    public MusicService getService() {
        return MusicService.this;
    }
    public void seekto(int n){
        mediaPlayer.seekTo(n);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();

    }



    @Override
    public IBinder onBind(Intent intent) {
        // return the binder to the activity
        myBinder = new MyBinder();
        return myBinder;
    }
    public void start() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }

    public void startnew(String path) throws Exception {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();}

        mediaPlayer.release();
        mediaPlayer = null;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(path);
        mediaPlayer.prepare();
        mediaPlayer.start();
        handler.sendEmptyMessage(SET_SEEKBAR_MAX);
        handler.sendEmptyMessage(UPDATE_PROGRESS);
    }

    private Handler handler = new Handler() {
    @SuppressLint("HandlerLeak")
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case UPDATE_PROGRESS:
                // 设置最大当前播放进度

                handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
                break;
            case SET_SEEKBAR_MAX:
                // 因为进度条只需要设置一次就够了,所以不需要反复发送Message;
                break;
            default:
                break;
              }
            };
           };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            try {
                Toast.makeText(getApplicationContext(), intent.getStringExtra("title"), Toast.LENGTH_SHORT).show();
                startnew(intent.getStringExtra("url"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }
}