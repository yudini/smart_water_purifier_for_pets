package com.amazonaws.lambda.demo;

import java.util.List;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClientBuilder;
import com.amazonaws.services.iot.model.ListThingsRequest;
import com.amazonaws.services.iot.model.ListThingsResult;
import com.amazonaws.services.iot.model.ThingAttribute;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ListingDeviceHandler implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {

    	//get the AWSIoT object, AWSIot ��ü�� ����  
        AWSIot iot = AWSIotClientBuilder.standard().build();

        //generate the ListThingsRequest, ListThingsRequest ��ü ���� 
        ListThingsRequest listThingsRequest = new ListThingsRequest();

        //get the result by listThings method, listThings �޼ҵ� ȣ���Ͽ� ��� ����
        ListThingsResult result = iot.listThings(listThingsRequest);

        //return API response string by result object, result��ü�κ��� API ����� ���ڿ� �����Ͽ� ��ȯ
        return getResponse(result);
    }

    /**
     * ListThingsResult ��ü�� result�� ���� thingName�� thingArn�� ��� JSON���� ������ ����� ����� ��ȯ
     * {
     *  "things": [ 
     *       { 
     *          "thingName": "string",
     *          "thingArn": "string"
     *       },
     *       ...
     *     ]
     * }
     */
    private String getResponse(ListThingsResult result) {
        List<ThingAttribute> things = result.getThings();

        String response = "{ \"things\": [";
        for (int i =0; i<things.size(); i++) {
            if (i!=0) 
                response +=",";
            response += String.format("{\"thingName\":\"%s\", \"thingArn\":\"%s\"}",  //string format,������ ���ڿ� �������� ����
                                                things.get(i).getThingName(),
                                                things.get(i).getThingArn());

        }
        response += "]}";
        return response;
    }

}