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

int motionPin = 2;    //모션감지 센서 
int motor_control = 4;  //모터 핀 4번

const char* pirState;   //모터 상태 
const char* waterState = "HIGH";    
int motion = 0;      //모션 감지 여부를 담을 변수
int water_level = 0;   //물 수위 값을 담을 변수
unsigned long lastMillis = 0;
int Water_Sensor = 600;  //물 양 상태를 판단할 기준 

Led led1(LED_1_PIN);

void setup() {
  // 인체 감지 센서  INPUT 설정
  pinMode(motionPin, INPUT);
  // put your setup code here, to run once:
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
    connectMQTT();   //mqtt 연결
  }

  // poll for new MQTT messages and send keep alives
  mqttClient.poll();
  
  // put your main code here, to run repeatedly:
 if (millis() - lastMillis > 5000) {  //5초마다
    lastMillis = millis();
    char payload[512];
    getDeviceStatus(payload);
    sendMessage(payload);
  }
}

unsigned long getTime() {
  // get the current time from the WiFi module  
  return WiFi.getTime();  //WiFi 현재 시간 얻어옴
}

void connectWiFi() { //WiFi 연결 함수
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

void connectMQTT() { //mqtt 연결 함수
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

  //subcribe to a topic
  mqttClient.subscribe("$aws/things/Smart_Water_Purifier_for_Dog/shadow/update/accepted");
}

//디바이스 상태를 받아오기위한 함수, 물 양이 사용자 기준 미만이면 led on & 알림
void getDeviceStatus(char* payload) {
  // Read Water_level as int (the default)
  water_level = analogRead(A0);  //아날로그 핀 A0값을 읽어옴
  motion = digitalRead(motionPin);  //디지털 motorpin(4번) 값을 읽어옴
  
  if(water_level<Water_Sensor){   //물 양이 사용자가 정한 기준 미만일 경우
    led1.on();       
  }
  else{
    led1.off();
  }
  if(motion == HIGH && led1.getState() == LED_ON){   //모션이 감지되고 led가 켜져있을 경우
    pirState = "Motor on";     //모터 on, (자동급수)
  }
  else{
    pirState = "Stop";   //모터 off(자동급수 정지)
  }
  // Read led status
  const char* led = (led1.getState() == LED_ON)? "ON" : "OFF";

  // make payload for the device update topic ($aws/things/Smart_Water_Purifier_for_Dog_send/shadow/update)
  sprintf(payload,"{\"state\":{\"reported\":{\"Water_Level\":\"%d\",\"Water_Sensor\":\"%d\",\"pirState\":\"%s\",\"LED\":\"%s\"}}}",water_level,Water_Sensor,pirState,led); 

  if(motion ==HIGH){   //모션이 감지되면
    char payload2[512];
    // make payload for the device update topic ($aws/things/Smart_Water_DB/shadow/update)
    //JSON 형식으로 메시지 저장
    sprintf(payload2,"{\"state\":{\"reported\":{\"Water_level\":\"%d\",\"Motion\":\"%d\"}}}",water_level,motion);   
    storeDB(payload2);    //DB에 저장하기 위한 함수
  }
}

//해당 토픽을 구독한 곳으로 메시지를 보냄
//물그릇 수위센서 값에 따라 SNS용 메시지를 주기 위한 함수
void sendMessage(char* payload) {
  char TOPIC_NAME[]= "$aws/things/Smart_Water_Purifier_for_Dog_send/shadow/update";  //해당 토픽으로 메시지를 보냄
  Serial.print("Publishing send message:");  //시리얼모니터에 출력
  Serial.println(payload);
  mqttClient.beginMessage(TOPIC_NAME);  //mqtt클라이언트로 메시지를 보냄
  mqttClient.print(payload);
  mqttClient.endMessage();
}

//모션감지 여부에 따라 값을 DynamoDB에 저장하기 위한 함수
void storeDB(char* payload2) {
  char TOPIC_NAME2[]="$aws/things/Smart_Water_DB/shadow/update";  //해당 토픽으로 메시지를 보냄
  Serial.print("Publishing send message:");
  Serial.println(payload2);
  mqttClient.beginMessage(TOPIC_NAME2);  //mqtt클라이언트로 메시지를 보냄
  mqttClient.print(payload2);
  mqttClient.endMessage();
  
}

//사용자가 정의한 기준 값 메시지를 받는 함수 (안드로이드 앱으로부터 값을 받아옴)
void onMessageReceived(int messageSize) {

  // store the message received to the buffer
  char buffer[512] ;
  int count=0;
  while (mqttClient.available()) {
     buffer[count++] = (char)mqttClient.read();
  }
  buffer[count]='\0'; // 버퍼의 마지막에 null 캐릭터 삽입
  
    Serial.print("Received a message ");
    Serial.print("Change Water Sensor value to ");
    DynamicJsonDocument doc(1024);
    deserializeJson(doc, buffer);
    JsonObject root = doc.as<JsonObject>();
    JsonObject state = root["state"];   //JSON객체로 state 받아옴 
    JsonObject desired = state["desired"];   //JSON객체로 desired 받아옴
    String wStd = desired["Water_Sensor"];   //사용자가 정의한 물 수위 기준 값을 받아옴.
    Water_Sensor = wStd.toInt();    //변수에 넣기 위해 string을 int형식으로 변환
    Serial.println(Water_Sensor);
  

  
}