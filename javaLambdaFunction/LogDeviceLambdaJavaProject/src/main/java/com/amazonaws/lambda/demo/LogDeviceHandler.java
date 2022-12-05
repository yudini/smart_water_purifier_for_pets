package com.amazonaws.lambda.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.TimeZone;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LogDeviceHandler implements RequestHandler<Event, String> {

    private DynamoDB dynamoDb;

    @Override
    public String handleRequest(Event input, Context context) {
        this.initDynamoDbClient();

        Table table = dynamoDb.getTable(input.device); //get the dynamDB by using event.device, 이벤트로 받아온 디바이스로 dynamoDB테이블을 가져옴

        long from=0;
        long to=0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");  //format
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));  //choose region, 리전 선택

            from = sdf.parse(input.from).getTime() / 1000;  //이벤트로 얻어온 조회시작날짜를 파싱해서 타임으로 저장, store the startDate by using event to time
            to = sdf.parse(input.to).getTime() / 1000;     //이벤트로 얻어온 조회종료날짜를 파싱해서 타임으로 저장, store the stopDate by using event to time
        } catch (ParseException e1) {  //exception, 예외처리
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("#t between :from and :to").withNameMap(new NameMap().with("#t", "time")) //표현 형식, format
                .withValueMap(new ValueMap().withNumber(":from", from).withNumber(":to", to)); //mapping

        ItemCollection<ScanOutcome> items=null;
        try {
            items = table.scan(scanSpec);
        }
        catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
        }

        return getResponse(items);
    }

    private String getResponse(ItemCollection<ScanOutcome> items) {

        Iterator<Item> iter = items.iterator();
        String response = "{ \"data\": [";  // string format to response, 응답보낼 문자열 형식
        for (int i =0; iter.hasNext(); i++) {
            if (i!=0) 
                response +=",";
            response += iter.next().toJSON();
        }
        response += "]}";
        return response;   
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("ap-northeast-2").build();

        this.dynamoDb = new DynamoDB(client);
    }
}

class Event {
    public String device;
    public String from;
    public String to;
}