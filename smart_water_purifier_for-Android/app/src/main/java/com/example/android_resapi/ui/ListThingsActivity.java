package com.example.android_resapi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android_resapi.R;
import com.example.android_resapi.ui.apicall.GetThings;


public class ListThingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_things);

        Intent intent = getIntent();  //새도우를 받기위한 인텐트 생성
        String url = intent.getStringExtra("listThingsURL");  //listThingsURL 이름으로 받아온 값을 저장

        new GetThings(ListThingsActivity.this, url).execute(); //사물을 받아옴


    }
}

