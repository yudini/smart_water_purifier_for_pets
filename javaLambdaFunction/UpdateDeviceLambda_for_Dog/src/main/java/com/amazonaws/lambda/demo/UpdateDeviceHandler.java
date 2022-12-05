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

//update the values through updateDeviceShadow to arduino , ������Ʈ ����̽� �����츦 ���� �޾ƿ� ���� �Ƶ��̳뿡 ������ ���� �����Լ�
public class UpdateDeviceHandler implements RequestHandler<Event, String> {

    @Override
    public String handleRequest(Event event, Context context) {
        context.getLogger().log("Input: " + event);

        AWSIotData iotData = AWSIotDataClientBuilder.standard().build();

        String payload = getPayload(event.tags);   //change the event values to JSON format , ���� JSON�������� ��ȯ

        //Request object for updating the values through updateDeviceShadow to arduino
        //���� JSON���� ������ payload�� �̿��Ͽ� request ��ü ���� �� �ʱ�ȭ
        UpdateThingShadowRequest updateThingShadowRequest  = 
                new UpdateThingShadowRequest()
                    .withThingName(event.device)
                    .withPayload(ByteBuffer.wrap(payload.getBytes()));

        //the object for sending values to arduino ,�Ƶ��̳�� ���� ���� 
        UpdateThingShadowResult result = iotData.updateThingShadow(updateThingShadowRequest);
        byte[] bytes = new byte[result.getPayload().remaining()];
        result.getPayload().get(bytes);
        String resultString = new String(bytes);
        return resultString;
    }

    private String getPayload(ArrayList<Tag> tags) {   //funtion for changing the values format to JSON format , ���� JSON �������� ��ȯ�ϱ� ���� �Լ�
        String tagstr = "";
        for (int i=0; i < tags.size(); i++) {
            if (i !=  0) tagstr += ", ";
            tagstr += String.format("\"%s\" : \"%s\"", tags.get(i).tagName, tags.get(i).tagValue);
               // values format  ,�� ����
        }
        return String.format("{ \"state\": { \"desired\": { %s } } }", tagstr);   //return the values by JSON format , JSON �������� �� ����
    }

}

class Event {    //the class for getting event , �̺�Ʈ�� �ޱ� ���� Ŭ����
    public String device;
    public ArrayList<Tag> tags;

    public Event() {
         tags = new ArrayList<Tag>();
    }
}

class Tag {    //the class for getting values ,���� �ޱ����� Ŭ����
    public String tagName;
    public String tagValue;

    @JsonCreator 
    public Tag() {
    }

    public Tag(String n, String v) {   //the updating values and updating name, ������ ���� �̸�
        tagName = n;  
        tagValue = v;
    }
}
