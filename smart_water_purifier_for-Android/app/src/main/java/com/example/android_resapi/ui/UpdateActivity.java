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
import com.example.android_resapi.ui.apicall.UpdateShadow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateActivity extends AppCompatActivity {
    String urlStr;
    final static String TAG = "AndroidAPITest";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Intent intent = getIntent();
        urlStr = intent.getStringExtra("updateShadowURL");

        Button updateBtn = findViewById(R.id.updateBtn);  //변경 버튼
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  //버튼 클릭 시
                EditText edit_std = findViewById(R.id.edit_std);

                JSONObject payload = new JSONObject();  //JSON 객체 생성

                //JSON 형식으로 값 보내기
                try {
                    JSONArray jsonArray = new JSONArray();   //JSONArray 객체 생성
                    String temp_input = edit_std.getText().toString();  //edittext에서 받은 텍스트 string으로 변환
                    if (temp_input != null && !temp_input.equals("")) {  //null값이 아닐 때
                        JSONObject tag1 = new JSONObject();   //JSON객체 (태그값) 생성
                        tag1.put("tagName", "Water_Sensor");  //Wate_Sensor put
                        tag1.put("tagValue", temp_input);   //사용자에게 받은 값 put

                        jsonArray.put(tag1);   //Array에 put
                    }

                    if (jsonArray.length() > 0)   //jsonArray 길이가 0보다 길면,(즉 하나 이상이 존재할 때)
                        payload.put("tags", jsonArray);   //payload JSON 객체에 tags라는 이름으로 jsonArray  put
                } catch (JSONException e) {
                    Log.e(TAG, "JSONEXception");
                }
                Log.i(TAG,"payload="+payload);
                if (payload.length() >0 )
                    new UpdateShadow(UpdateActivity.this,urlStr).execute(payload);  //해당 url로 섀도우를 통해 값을 보냄
                else
                    Toast.makeText(UpdateActivity.this,"변경할 상태 정보 입력이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        });


    }

}