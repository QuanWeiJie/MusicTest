package com.example.musictest;

public class Music {
    private String id;
    private String album;
    private String song;
    private String singer;
    private String duration;
    private int seekbar_time;

    public int getSeekbar_time() {
        return seekbar_time;
    }

    public void setSeekbar_time(int seekbar_time) {
        this.seekbar_time = seekbar_time;
    }

    private String path;
    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public Music() {
    }

    public Music(String id, String album, String song, String singer, String duration, String path) {
        this.id = id;
        this.album = album;
        this.song = song;
        this.singer = singer;
        this.duration = duration;
        this.path = path;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




}
