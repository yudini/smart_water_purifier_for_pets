package com.amazonaws.lambda.demo;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

//lambda function for storing in DynamoDB 
public class RecordingWaterLevel implements RequestHandler<Thing, String> {
    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME = "WaterLevel";   //DynamoDB Table name, 저장할 DynamoDB 이름

    @Override
    public String handleRequest(Thing input, Context context) {
        this.initDynamoDbClient();

        persistData(input);
        return "Success in storing to DB!";
    }

    private PutItemOutcome persistData(Thing thing) throws ConditionalCheckFailedException {

        // Epoch Conversion Code: https://www.epochconverter.com/
        SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));    //setting Region  ,동일한 리전 선택
        String timeString = sdf.format(new java.util.Date (thing.timestamp*1000));
        
        return this.dynamoDb.getTable(DYNAMODB_TABLE_NAME)   
                .putItem(new PutItemSpec().withItem(new Item().withPrimaryKey("time", thing.timestamp)  //pirimarykey is time,기본키는 time
                        .withString("water_level", thing.state.reported.Water_level)  //store WaterLevel state, 물 양(수위) 저장
                        .withString("motion", thing.state.reported.Motion) //store motion state, 모션 감지 여부 저장
                        .withString("timestamp",timeString)));   //store timestamp  ,타임스탬프 저장
    }
    
    private void initDynamoDbClient() {   
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("ap-northeast-2").build();

        this.dynamoDb = new DynamoDB(client);
    }

}

// lambda runtime change JSON data to following object, 
//Lambda 런타임이 JSON 형식의 입력 데이터를 해당되는 객체로 변환할 때 필요
class Thing {
    public State state = new State();
    public long timestamp;

    public class State {
        public Tag reported = new Tag();   
        public Tag desired = new Tag();

        public class Tag {  //Tag which is stored in DB, DB에 저장될 값들
            public String Water_level;   
            public String Motion;
        }
    }
}