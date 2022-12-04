package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.example.android_resapi.R;
import com.example.android_resapi.httpconnection.GetRequest;

public class GetLog extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;
    public GetLog(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    @Override
    protected void onPreExecute() {
        try {
            //textview를 id값으로 받아옴 (조회시작 날짜 및 시간과 조회종료 날짜 및 시간)
            TextView textView_Date1 = activity.findViewById(R.id.textView_date1);
            TextView textView_Time1 = activity.findViewById(R.id.textView_time1);
            TextView textView_Date2 = activity.findViewById(R.id.textView_date2);
            TextView textView_Time2 = activity.findViewById(R.id.textView_time2);

            //다음 string 형식으로 변환
            String params = String.format("?from=%s:00&to=%s:00",textView_Date1.getText().toString()+textView_Time1.getText().toString(),
                                                            textView_Date2.getText().toString()+textView_Time2.getText().toString());

            Log.i(TAG,"urlStr="+urlStr+params); //url에 parmas 추가
            url = new URL(urlStr+params);  //새로운 url형식 생성

        } catch (MalformedURLException e) {
            Toast.makeText(activity,"URL is invalid:"+urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        TextView message = activity.findViewById(R.id.message2);
        message.setText("조회중...");
    }

    @Override
    protected void onPostExecute(String jsonString) {
        TextView message = activity.findViewById(R.id.message2);
        if (jsonString == null) {   //받아온 jsonString이 없는 경우
            message.setText("로그 없음");
            return;
        }
        message.setText("");
        ArrayList<Tag> arrayList = getArrayListFromJSONString(jsonString);

        final ArrayAdapter adapter = new ArrayAdapter(activity,
                android.R.layout.simple_list_item_1,
                arrayList.toArray());
        ListView txtList = activity.findViewById(R.id.logList);
        txtList.setAdapter(adapter);
        txtList.setDividerHeight(10);
    }

    protected ArrayList<Tag> getArrayListFromJSONString(String jsonString) {
        ArrayList<Tag> output = new ArrayList();
        try {
            // 처음 double-quote와 마지막 double-quote 제거
            jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" 를 \"로 치환
            jsonString = jsonString.replace("\\\"","\"");

            Log.i(TAG, "jsonString="+jsonString);

            JSONObject root = new JSONObject(jsonString);
            JSONArray jsonArray = root.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = (JSONObject)jsonArray.get(i);

                Tag thing = new Tag(jsonObject.getString("water_level"),  //water_level 물 수위 값
                                    jsonObject.getString("motion"),  //모션 감지 여부 값
                                    jsonObject.getString("timestamp"));  //타임스탬프 값

                output.add(thing);
            }

        } catch (JSONException e) {  //예외 처리
            //Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }

    class Tag {  //태그 클래스
        String Water_level;
        String Motion;
        String timestamp;

        public Tag(String level, String motion, String time) {  //태그 객체
            Water_level = level;
            Motion = motion;
            timestamp = time;
        }

        public String toString() {
            return String.format("[%s] Water_level: %s, Motion: %s", timestamp, Water_level, Motion);
        }
    }
}

