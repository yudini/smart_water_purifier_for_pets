//급수통 수위를 조회

#include <ArduinoBearSSL.h>
#include <ArduinoECCX08.h>
#include <ArduinoMqttClient.h>
#include <WiFiNINA.h> // change to #include <WiFi101.h> for MKR1000

#include "arduino_secrets.h"   //와이파이 연결하기 위한 정보가 담긴 라이브러리

#define LED_1_PIN 5

#include <ArduinoJson.h>
#include "Led.h"

/////// Enter your sensitive data in arduino_secrets.h , 와이파이 연결을 위한 설정
const char ssid[]        = SECRET_SSID;
const char pass[]        = SECRET_PASS;
const char broker[]      = SECRET_BROKER;
const char* certificate  = SECRET_CERTIFICATE;

WiFiClient    wifiClient;            // Used for the TCP socket connection
BearSSLClient sslClient(wifiClient); // Used for SSL/TLS connection, integrates with ECC508
MqttClient    mqttClient(sslClient);

int water_Level = 0;   //물 수위 값을 담을 변수
unsigned long lastMillis = 0;  
const char* waterState = "HIGH";   //물 양 상태를 담을 변수

Led led1(LED_1_PIN);

void setup() {
  Serial.begin(115200);
  while (!Serial);

  if (!ECCX08.begin()) {
    Serial.println("No ECCX08 present!");
    while (1);
  }
  // Set a callback to get the current time
  // used to validate the servers certificate
  ArduinoBearSSL.onGetTime(getTime);

  // Set the ECCX08 slot to use for the private key
  // and the accompanying public certificate for it
  sslClient.setEccSlot(0, certificate);

  // Optional, set the client id used for MQTT,
  // each device that is connected to the broker
  // must have a unique client id. The MQTTClient will generate
  // a client id for you based on the millis() value if not set
  //
  // mqttClient.setId("clientId");

  // Set the message callback, this function is
  // called when the MQTTClient receives a message
  mqttClient.onMessage(onMessageReceived);
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) {
    connectWiFi();   //WiFi 연결
  }

  if (!mqttClient.connected()) {
    // MQTT client is disconnected, connect
    connectMQTT();  //mqtt 연결
  }

  // poll for new MQTT messages and send keep alives
  mqttClient.poll();

  // publish a message roughly every 5 seconds.
  if (millis() - lastMillis > 5000) { //5초마다 
    lastMillis = millis();
    char payload[512];
    getDeviceStatus(payload);
    sendMessage(payload);
  }
}

unsigned long getTime() {
  // get the current time from the WiFi module  
  return WiFi.getTime();   //WiFi 현재 시간 얻어옴
}

void connectWiFi() {   //WiFi 연결 함수
  Serial.print("Attempting to connect to SSID: ");
  Serial.print(ssid);
  Serial.print(" ");

  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the network");
  Serial.println();
}

void connectMQTT() {    //mqtt 연결 함수
  Serial.print("Attempting to MQTT broker: ");
  Serial.print(broker);
  Serial.println(" ");

  while (!mqttClient.connect(broker, 8883)) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the MQTT broker");
  Serial.println();

  // subscribe to a topic
  mqttClient.subscribe("$aws/things/Smart_Water_Purifier/shadow/update/delta");
}

//디바이스 상태를 받아오기위한 함수 , 물 양이 기준 미만이면 led on & 알림
void getDeviceStatus(char* payload) {
  // Read Water_level as int (the default)
  int water_level = analogRead(A0);  //아날로그 핀 A0값을 가져옴
  if(water_Level<600){    //급수통의 물 양이 600 미만일 때, 
    led1.on();   //led on 
    waterState = "LOW";   //물 수위 상태 Low로 변경
  }
  else{
    led1.off();     //led off
    waterState = "HIGH";  //물 수위 상태 HIGH로 변경
  }
  // Read led status
  const char* led = (led1.getState() == LED_ON)? "ON" : "OFF";

  // make payload for the device update topic ($aws/things/Smart_Water_Purifier/shadow/update)
  // JSON형식으로 메시지 저장
  sprintf(payload,"{\"state\":{\"reported\":{\"Water_Level\":\"%d\",\"Water_State\":\"%s\",\"LED\":\"%s\"}}}",water_level,waterState,led);
}

//해당 토픽을 구독한 곳으로 메시지를 보냄
//급수통 수위 값에 따라 SNS용 메시지를 보내기 위한 함수
void sendMessage(char* payload) {
  char TOPIC_NAME[]= "$aws/things/Smart_Water_Purifier/shadow/update";  //해당 토픽으로 메시지를 보냄
  Serial.print("Publishing send message:");   //시리얼모니터에 출력
  Serial.println(payload);
  mqttClient.beginMessage(TOPIC_NAME);    //mqtt클라이언트로 메시지를 보냄
  mqttClient.print(payload);
  mqttClient.endMessage();
}

void onMessageReceived(int messageSize) {
  // we received a message, print out the topic and contents
  Serial.print("Received a message with topic '");
  Serial.print(mqttClient.messageTopic());
  Serial.print("', length ");
  Serial.print(messageSize);
  Serial.println(" bytes:");

  // store the message received to the buffer
  char buffer[512] ;
  int count=0;
  while (mqttClient.available()) {
     buffer[count++] = (char)mqttClient.read();
  }
  buffer[count]='\0'; // 버퍼의 마지막에 null 캐릭터 삽입
  Serial.println(buffer);
  Serial.println();

  // JSon 형식의 문자열인 buffer를 파싱하여 필요한 값을 얻어옴.
  // 디바이스가 구독한 토픽이 $aws/things/MyMKRWiFi1010/shadow/update/delta 이므로,
  // JSon 문자열 형식은 다음과 같다.
  // {
  //    "version":391,
  //    "timestamp":1572784097,
  //    "state":{
  //        "LED":"ON"
  //    },
  //    "metadata":{
  //        "LED":{
  //          "timestamp":15727840
  //         }
  //    }
  // }
  //
  DynamicJsonDocument doc(1024);
  deserializeJson(doc, buffer);
  JsonObject root = doc.as<JsonObject>();
  JsonObject state = root["state"];
  const char* led = state["LED"];
  Serial.println(led);
  
  char payload[512];
  
  if (strcmp(led,"ON")==0) {
    led1.on();
    sprintf(payload,"{\"state\":{\"reported\":{\"LED\":\"%s\"}}}","ON");
    sendMessage(payload);
    
  } else if (strcmp(led,"OFF")==0) {
    led1.off();
    sprintf(payload,"{\"state\":{\"reported\":{\"LED\":\"%s\"}}}","OFF");
    sendMessage(payload);
  }
 
}
