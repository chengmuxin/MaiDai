package com.hooha.maidai.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.view.MovieRecorderView;

/**
 * Created by MG on 2016/10/25.
 */
public class Movie_playActivity  extends Activity {
    private MovieRecorderView mplayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_play);
        mplayView = (MovieRecorderView) findViewById(R.id.moive_play);
    }

}