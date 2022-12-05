package com.amazonaws.lambda.demo;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.amazonaws.services.iotdata.AWSIotData;
import com.amazonaws.services.iotdata.AWSIotDataClientBuilder;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.amazonaws.services.iotdata.model.UpdateThingShadowResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.annotation.JsonCreator;

//update the values through updateDeviceShadow to arduino , 업데이트 디바이스 섀도우를 통해 받아온 값을 아두이노에 보내기 위한 람다함수
public class UpdateDeviceHandler implements RequestHandler<Event, String> {

    @Override
    public String handleRequest(Event event, Context context) {
        context.getLogger().log("Input: " + event);

        AWSIotData iotData = AWSIotDataClientBuilder.standard().build();

        String payload = getPayload(event.tags);   //change the event values to JSON format , 값을 JSON형식으로 변환

        //Request object for updating the values through updateDeviceShadow to arduino
        //값을 JSON으로 변경한 payload를 이용하여 request 객체 생성 및 초기화
        UpdateThingShadowRequest updateThingShadowRequest  = 
                new UpdateThingShadowRequest()
                    .withThingName(event.device)
                    .withPayload(ByteBuffer.wrap(payload.getBytes()));

        //the object for sending values to arduino ,아두이노로 값을 보냄 
        UpdateThingShadowResult result = iotData.updateThingShadow(updateThingShadowRequest);
        byte[] bytes = new byte[result.getPayload().remaining()];
        result.getPayload().get(bytes);
        String resultString = new String(bytes);
        return resultString;
    }

    private String getPayload(ArrayList<Tag> tags) {   //funtion for changing the values format to JSON format , 값을 JSON 형식으로 변환하기 위한 함수
        String tagstr = "";
        for (int i=0; i < tags.size(); i++) {
            if (i !=  0) tagstr += ", ";
            tagstr += String.format("\"%s\" : \"%s\"", tags.get(i).tagName, tags.get(i).tagValue);
               // values format  ,값 형식
        }
        return String.format("{ \"state\": { \"desired\": { %s } } }", tagstr);   //return the values by JSON format , JSON 형식으로 값 리턴
    }

}

class Event {    //the class for getting event , 이벤트를 받기 위한 클래스
    public String device;
    public ArrayList<Tag> tags;

    public Event() {
         tags = new ArrayList<Tag>();
    }
}

class Tag {    //the class for getting values ,값을 받기위한 클래스
    public String tagName;
    public String tagValue;

    @JsonCreator 
    public Tag() {
    }

    public Tag(String n, String v) {   //the updating values and updating name, 변경할 값과 이름
        tagName = n;  
        tagValue = v;
    }
}
