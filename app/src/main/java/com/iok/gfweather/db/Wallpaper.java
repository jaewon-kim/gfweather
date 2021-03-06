package com.iok.gfweather.db;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "WALLPAPER".
 */
@Entity
public class Wallpaper {

    @Id
    private Long id;
    private String path;
    private String exifdate;
    private String weather;

    @Generated
    public Wallpaper() {
    }

    public Wallpaper(Long id) {
        this.id = id;
    }

    @Generated
    public Wallpaper(Long id, String path, String exifdate, String weather) {
        this.id = id;
        this.path = path;
        this.exifdate = exifdate;
        this.weather = weather;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExifdate() {
        return exifdate;
    }

    public void setExifdate(String exifdate) {
        this.exifdate = exifdate;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

}
