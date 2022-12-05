package com.amazonaws.lambda.demo;

import com.amazonaws.services.iotdata.AWSIotData;
import com.amazonaws.services.iotdata.AWSIotDataClientBuilder;
import com.amazonaws.services.iotdata.model.GetThingShadowRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class GetDeviceHandler implements RequestHandler<Event, String> {

    @Override
    public String handleRequest(Event event, Context context) {
    	
    	//get the AWSIoT object, AWSIot ��ü�� ����  
        AWSIotData iotData = AWSIotDataClientBuilder.standard().build();

        GetThingShadowRequest getThingShadowRequest  = 
        new GetThingShadowRequest()
            .withThingName(event.device);   //generate and init the getThingshadowRequest object with event.device, �̺�Ʈ�� ���� ����̽��̸����� ������ ��ü ���� �� �ʱ�ȭ

      //get the result by getThingShadow method, getThingShadow �޼ҵ� ȣ���Ͽ� ��� ����
        iotData.getThingShadow(getThingShadowRequest);  

        return new String(iotData.getThingShadow(getThingShadowRequest).getPayload().array());  //return payload by string object
    }
}

class Event {
    public String device;
}
