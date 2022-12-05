package com.amazonaws.lambda.demo;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


public class Monitoring implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);
        String json = ""+input;
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json);   //JSON parser
        JsonElement state = element.getAsJsonObject().get("state");   //Parse the value of name is "state",state���� �Ľ� 
        JsonElement reported = state.getAsJsonObject().get("reported");  //Parse the value of name is "reported",reported���� �Ľ� 
        String Water_Level = reported.getAsJsonObject().get("Water_Level").getAsString(); //Parse the value of name is "Water_Level",�� ��(����)���� �Ľ� 
        double water_level = Double.valueOf(Water_Level);  //change the wate_level type, water_level ������ Ÿ���� ����

        // setting for SNS service, SNS ���񽺸� ���� ����
        final String AccessKey="AKIA2NJSSCUZ2FIUDQE7";
        final String SecretKey="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";
        final String topicArn="arn:aws:sns:ap-northeast-2:715754968371:Smart_Water_Purifier_for_Dog";

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(AccessKey, SecretKey);  
        AmazonSNS sns = AmazonSNSClientBuilder.standard()   //the SNS Object with setting, SNS�� ������ ���� ��ü ���� �� �ʱ�ȭ
                    .withRegion(Regions.AP_NORTHEAST_2)       
                    .withCredentials( new AWSStaticCredentialsProvider(awsCreds) )
                    .build();

        final String msg = "*Water_Level_Warning*\n" + "Your Purifier Water_Level is " + water_level;  //Message format, �޽��� ����
        final String subject = "Critical Warning";
        if (water_level < 600.0) {  //when water level under 600, �� ���� ���� 600 �̸��� �� 
            PublishRequest publishRequest = new PublishRequest(topicArn, msg, subject);
            PublishResult publishResponse = sns.publish(publishRequest);  //publishing message to sns, sns�� �޽��� ����
        }

        return subject+ "water_level = " + Water_Level + "!";
    }

}
