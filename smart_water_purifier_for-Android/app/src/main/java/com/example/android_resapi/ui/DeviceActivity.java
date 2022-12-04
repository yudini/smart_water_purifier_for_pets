package com.example.android_resapi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android_resapi.R;
import com.example.android_resapi.ui.apicall.GetThingShadow;
import com.example.android_resapi.ui.apicall.UpdateShadow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceActivity extends AppCompatActivity {
    String urlStr;
    final static String TAG = "AndroidAPITest";
    Timer timer;
    Button startGetBtn;
    Button stopGetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        Intent intent = getIntent();   //새도우를 받기 위한 인텐트
        urlStr = intent.getStringExtra("getShadowURL");  //인텐트로 받은 getShadowURL이름의 섀도우 URL

        startGetBtn = findViewById(R.id.startGetBtn);  //조회 시작 버튼
        startGetBtn.setEnabled(true);  //조회시작버튼 활성화
        startGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        new GetThingShadow(DeviceActivity.this, urlStr).execute();  //url을 가지고 GetThingshadow 호출
                    }
                },
                        0,2000);

                startGetBtn.setEnabled(false);  //조회시작버튼 비활성화
                stopGetBtn.setEnabled(true);   //조회종료버튼 활성화
            }
        });

        stopGetBtn = findViewById(R.id.stopGetBtn);  //조회 종료 버튼
        stopGetBtn.setEnabled(false);    //조회중지버튼 비활성화
        stopGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {   //조회종료버튼 클릭 시
                if (timer != null)
                    timer.cancel();
                clearTextView();   //clearTextView 호출
                startGetBtn.setEnabled(true); //조회시작버튼 활성화
                stopGetBtn.setEnabled(false);//조회종료버튼 비활성화
            }
        });


    }

    private void clearTextView() {
        //id에 따라 textview를 받아옴
        TextView reported_waterLevelTV = findViewById(R.id.reported_waterLevel);
        TextView reported_pirStateTV = findViewById(R.id.reported_pirState);
        TextView reported_ledTV = findViewById(R.id.reported_led);
        TextView reported_motionTV = findViewById(R.id.reported_motion);
        //텍스트 값을 설정
        reported_ledTV.setText("");
        reported_pirStateTV.setText("");
        reported_waterLevelTV.setText("");
        reported_motionTV.setText("");

        TextView desired_waterLevelTV =findViewById(R.id.desired_waterLevel);  //최신 물 수위 상태
        TextView desired_pirStateTV = findViewById(R.id.desired_pirState);   //최신 모터 상태
        TextView desired_ledTV = findViewById(R.id.desired_led);   //최신 led상태
        TextView desired_motionTV = findViewById(R.id.desired_motion);  //최신 모션 감지 상태
        //텍스트 값을 설정
        desired_ledTV.setText("");
        desired_pirStateTV.setText("");
        desired_waterLevelTV.setText("");
        desired_motionTV.setText("");
    }

}


