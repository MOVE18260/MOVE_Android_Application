package com.example.move_whole_project;

import android.app.Application;

public class AppTest extends Application {
    private static int cnt;


    public static int getCnt() {
        return cnt;
    }

    public static void count() {
        cnt++;
    }



}
