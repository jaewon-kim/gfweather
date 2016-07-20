package com.iok.generator;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception{
        Schema schema = new Schema(3, "com.iok.gfweather.db");

        Entity wallpaper = schema.addEntity("Wallpaper");

        wallpaper.addIdProperty();
        wallpaper.addStringProperty("path");

        DaoGenerator dg = new DaoGenerator();
        dg.generateAll(schema, "./app/src/main/java");

    }
}
