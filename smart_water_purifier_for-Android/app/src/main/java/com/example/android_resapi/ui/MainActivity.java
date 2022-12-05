package com.example.android_resapi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android_resapi.R;


public class MainActivity extends AppCompatActivity {
    final static String TAG = "AndroidAPITest";
    EditText listThingsURL, getShadowURL,updateShadowURL, getLogsURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      /*  listThingsURL = findViewById(R.id.listThingsURL);
        getShadowURL = findViewById(R.id.getShadowURL);
        updateShadowURL = findViewById(R.id.updateShadowURL);
        getLogsURL = findViewById(R.id.getLogsURL);
*/
        //사물 목록 조회
        //listThingBtn 을 클릭시에 동작하는 listener
        final Button listThingsBtn = findViewById(R.id.listThingsBtn);
        listThingsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String listThingsURL = "https://kjgnr8bpzc.execute-api.ap-northeast-2.amazonaws.com/prod/devices";
                Log.i(TAG, "listThingsURL=" + listThingsURL);
                String listURL = "https://kjgnr8bpzc.execute-api.ap-northeast-2.amazonaws.com/prod/devices";
                Intent intent = new Intent(MainActivity.this, ListThingsActivity.class);  // ListThingActivity화면으로 이동하기위한 인텐트
                intent.putExtra("listThingsURL",listURL);
                startActivity(intent);  //새로운 화면으로 이동(사물 조회 화면)
                //  new GetThings(MainActivity.this).execute();
                //  new GetThingShadow(MainActivity.this, "MyMKRWiFi1010").execute();

            }
        });

        //사물 상태 조회(물그릇 상태 조회)
        //getShadowBtn 을 클릭시에 동작하는 listener
        Button getShadowBtn = findViewById(R.id.getShadowBtn);
        getShadowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getShadowURL = "https://kjgnr8bpzc.execute-api.ap-northeast-2.amazonaws.com/prod/devices/Smart_Water_Purifier_for_Dog_send";
                Intent intent = new Intent(MainActivity.this, DeviceActivity.class); //DeviceActivity화면으로 이동하기위한 인텐트
                intent.putExtra("getShadowURL", getShadowURL);  //getShadowURL을 같이 넘겨줌
                startActivity(intent);//새로운 화면으로 이동(물그릇 상태 조회 화면)

            }
        });

        //사물 상태 변경(급수할 물 양 변경)
        //updateShadowBtn 을 클릭시에 동작하는 listener
        Button updateShadowBtn = findViewById(R.id.updateShadowBtn);
        updateShadowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updateShadowURL = "https://kjgnr8bpzc.execute-api.ap-northeast-2.amazonaws.com/prod/devices/Smart_Water_Purifier_for_Dog";
                Intent intent = new Intent(MainActivity.this, UpdateActivity.class); //UpdateActivity화면으로 이동하기위한 인텐트
                intent.putExtra("updateShadowURL", updateShadowURL);//updateShadowURL을 같이 넘겨줌
                startActivity(intent); //새로운 화면으로 이동(물그릇 상태 조회 화면)

            }
        });

        //사물 로그 조회(물 양 조회)
        //listLogBtn 을 클릭시에 동작하는 listener
        Button listLogsBtn = findViewById(R.id.listLogsBtn);
        listLogsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getLogsURL = "https://kjgnr8bpzc.execute-api.ap-northeast-2.amazonaws.com/prod/devices/WaterLevel/log";
                Intent intent = new Intent(MainActivity.this, LogActivity.class);//LogActivity화면으로 이동하기위한 인텐트
                intent.putExtra("getLogsURL", getLogsURL);//getLogURL을 같이 넘겨줌
                startActivity(intent);
            }
        });
    }
}


