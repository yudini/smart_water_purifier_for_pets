package com.example.android_resapi.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.android_resapi.R;
import com.example.android_resapi.ui.apicall.GetLog;

public class LogActivity extends AppCompatActivity {
    String getLogsURL;

    private TextView textView_Date1;
    private TextView textView_Date2;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    final static String TAG = "AndroidAPITest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_log);

        Intent intent = getIntent();  //새도우를 받기위한 인텐트
        getLogsURL = intent.getStringExtra("getLogsURL");  //getLogsURL이름으로 받아온 값을 저장
        Log.i(TAG, "getLogsURL="+getLogsURL);

        Button startDateBtn = findViewById(R.id.start_date_button);  //조회시작 날짜 버튼
        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  //버튼 클릭 시
                callbackMethod = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {  //연,월,일 설정
                        textView_Date1 = (TextView)findViewById(R.id.textView_date1);
                        textView_Date1.setText(String.format("%d-%d-%d ", year ,monthOfYear+1,dayOfMonth)); //해당 stirng 형식으로 표기
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(LogActivity.this, callbackMethod, 2022, 11, 0);
                //초기 날짜 dialog 값
                dialog.show();


            }
        });

        Button startTimeBtn = findViewById(R.id.start_time_button); //조회시작 시간 버튼
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //버튼 클릭 시

                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) { //시, 분 설정
                        TextView textView_Time1 = (TextView)findViewById(R.id.textView_time1);
                        textView_Time1.setText(String.format("%d:%d", hourOfDay, minute));  //해당 string 형식으로 표기
                    }
                };

                //초기 시간 dialog 값
                TimePickerDialog dialog = new TimePickerDialog(LogActivity.this, listener, 0, 0, false);
                dialog.show();

            }
        });


        Button endDateBtn = findViewById(R.id.end_date_button); //조회종료 날짜 버튼
        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //버튼 클릭 시
                callbackMethod = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    { //연,월,일 설정
                        textView_Date2 = (TextView)findViewById(R.id.textView_date2);
                        textView_Date2.setText(String.format("%d-%d-%d ", year ,monthOfYear+1,dayOfMonth)); //해당 string형식으로 표기
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(LogActivity.this, callbackMethod, 2022, 11, 0);

                dialog.show();


            }
        });

        Button endTimeBtn = findViewById(R.id.end_time_button);  //조회 종료 시간 버튼
        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {   //버튼 클릭 시

                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {  //시, 분 설정
                        TextView textView_Time2 = (TextView)findViewById(R.id.textView_time2);
                        textView_Time2.setText(String.format("%d:%d", hourOfDay, minute));  //해당 string 형식으로 표기
                    }
                };

                //초기 시간 값 설정
                TimePickerDialog dialog = new TimePickerDialog(LogActivity.this, listener, 0, 0, false);
                dialog.show();

            }
        });

        Button start = findViewById(R.id.log_start_button);  //로그 조회 시작 버튼
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetLog(LogActivity.this,getLogsURL).execute();  //getLogsURL로 GetLog함수 호출
            }
        });
    }
}
