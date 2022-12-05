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
    	
    	//get the AWSIoT object, AWSIot 객체를 얻음  
        AWSIotData iotData = AWSIotDataClientBuilder.standard().build();

        GetThingShadowRequest getThingShadowRequest  = 
        new GetThingShadowRequest()
            .withThingName(event.device);   //generate and init the getThingshadowRequest object with event.device, 이벤트로 받은 디바이스이름으로 새도우 객체 생성 및 초기화

      //get the result by getThingShadow method, getThingShadow 메소드 호출하여 결과 얻음
        iotData.getThingShadow(getThingShadowRequest);  

        return new String(iotData.getThingShadow(getThingShadowRequest).getPayload().array());  //return payload by string object
    }
}

class Event {
    public String device;
}
