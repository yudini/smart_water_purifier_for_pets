package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.example.android_resapi.R;
import com.example.android_resapi.httpconnection.GetRequest;

//사물 상태 받아오는 섀도우
public class GetThingShadow extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;
    public GetThingShadow(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    @Override
    protected void onPreExecute() {
        try {
            Log.e(TAG, urlStr);
            url = new URL(urlStr);

        } catch (MalformedURLException e) {
            Toast.makeText(activity,"URL is invalid:"+urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            activity.finish();
        }
    }

    @Override
    protected void onPostExecute(String jsonString) {
        if (jsonString == null)
            return;
        Map<String, String> state = getStateFromJSONString(jsonString);
        //textView id로 찾아서 가져옴(물 수위, 모터상태, led, 모션감지여부)
        TextView reported_waterLevelTV = activity.findViewById(R.id.reported_waterLevel);
        TextView reported_pirStateTV = activity.findViewById(R.id.reported_pirState);
        TextView reported_ledTV = activity.findViewById(R.id.reported_led);
        TextView reported_waterSensorTV = activity.findViewById(R.id.reported_waterSensor);
        //텍스트 설정
        reported_waterSensorTV.setText(state.get("reported_waterSensor"));
        reported_ledTV.setText(state.get("reported_LED"));
        reported_pirStateTV.setText(state.get("reported_pirState"));
        reported_waterLevelTV.setText(state.get("reported_waterLevel"));


    }

    protected Map<String, String> getStateFromJSONString(String jsonString) {
        Map<String, String> output = new HashMap<>();
        try {
            // 처음 double-quote와 마지막 double-quote 제거
            jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" 를 \"로 치환
            jsonString = jsonString.replace("\\\"","\"");
            Log.i(TAG, "jsonString="+jsonString);
            JSONObject root = new JSONObject(jsonString);
            JSONObject state = root.getJSONObject("state");  //state값 받아와서 JSONObject 객체 생성 및 초기화
            JSONObject reported = state.getJSONObject("reported");  //reported값 받아와서 JSONObject 객체 생성 및 초기화
            String waterLevelValue = reported.getString("Water_Level");  //WaterLevel값 받아옴
            String waterSensorValue = reported.getString("Water_Sensor"); //물 양 최소 기준 값 받아옴
            String pirStateValue = reported.getString("pirState"); //모터 값 받아옴
            String ledValue = reported.getString("LED"); //led값 받아옴

            //output(Map 형태)에 받아온 값들 put
            output.put("reported_waterLevel",waterLevelValue);
            output.put("reported_waterSensor",waterSensorValue);
            output.put("reported_pirState",pirStateValue);
            output.put("reported_LED",ledValue);


        } catch (JSONException e) {  //예외 처리
            Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }
}
