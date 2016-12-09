/***************************************************************************
  This is a library for the BME280 humidity, temperature & pressure sensor

  Designed specifically to work with the Adafruit BME280 Breakout
  ----> http://www.adafruit.com/products/2650

  These sensors use I2C or SPI to communicate, 2 or 4 pins are required
  to interface.

  Adafruit invests time and resources providing this open source code,
  please support Adafruit andopen-source hardware by purchasing products
  from Adafruit!

  Written by Limor Fried & Kevin Townsend for Adafruit Industries.
  BSD license, all text above must be included in any redistribution
 ***************************************************************************/

#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>

Adafruit_BME280 bme;
struct datarec {
  int temperature;  //2 bytes, centiFahrenheit
  int pressure; //2 bytes, hPa
  byte humidity;  //1 byte, %saturation
  byte light; //1 byte, 4 Rendens
  int timeStamp;  //number of measurement ticks since start of data
};
typedef struct datarec DataRecord;

//Data sending stuff
int data;
byte receivedChar;
boolean messageArrived = false;
boolean firstTransmission;
volatile DataRecord dr;
char volatile dataArr[sizeof(DataRecord)];

//Data managing stuff
#define MAX_RECORDS 100 //size of the dataList
#define CULL_COUNT 30 //number of records that will be culled when the dataList runs out of space
int usedRecords;  //# of DataRecords in the dataList
DataRecord dataList[MAX_RECORDS]; //measured data
unsigned long lastMeasureTime;
unsigned long rightNow;
int volatile dataTick; //time between measurements in seconds

//Expected minimums and maximums for data, NOT HARD LIMITS, just rescales the spaces for valuing these things
#define MIN_TEMPERATURE -2000.0
#define MAX_TEMPERATURE 10000.0
#define MIN_PRESSURE 900.0
#define MAX_PRESSURE 1150.0
#define MIN_HUMIDITY 0.0
#define MAX_HUMIDITY 100.0
#define MIN_LIGHT 0.0
#define MAX_LIGHT 256.0


void setup() {
  firstTransmission = true;
  pinMode(13,OUTPUT);
  dataTick = 0; // Assuming seconds for now, since we don't really need millis accuracy.
  usedRecords = 0; // Initialize to zero records.
  lastMeasureTime = millis();
  DataRecord dr;
  Serial.begin(9600);
  Serial.flush();
  Serial.print("RTS");
  if(!bme.begin()){
    Serial.println("Can't communicate with sensor! Check the wiring!");
  }
}

void loop() {
  // Do other logging stuff here
  rightNow = millis();
  if(rightNow - lastMeasureTime >= 60000){
    lastMeasureTime = rightNow;
    digitalWrite(13,HIGH);
    if(usedRecords < MAX_RECORDS){
      measure(&dataList[usedRecords]);
      ++dataTick;
      ++usedRecords;
    }else{
      for (int i = 0; i < CULL_COUNT; ++i)  //cull records after we've grabbed a bunch so we know which points are actually valuable
        cullRecord();
    }
  }else{
    digitalWrite(13,LOW);
  }
}

void serialEvent() {
	// Read from the Serial port
  while(Serial.available()) {
    receivedChar = Serial.read();
    messageArrived = true;
  }
    // Send a normal data packet.
    if(receivedChar == 'A') {

      // Send a packet with all current data.
      Serial.write('D');
      Serial.write(usedRecords);
      for(int i = 0; i < usedRecords; ++i){
         memcpy((void*)(&dataArr),(void*)(&dataList[i]),sizeof(DataRecord));
         Serial.write((uint8_t *)&dataArr,sizeof(DataRecord));
      }
    }else if(receivedChar == 'C'){
      // Just send one packet.
      Serial.write('D');
      Serial.write(1);
      measure(&dr);
      memcpy((void*)(&dataArr),(void*)(&dr),sizeof(DataRecord));
      Serial.write((uint8_t *)&dataArr,sizeof(DataRecord));
      messageArrived = false;
      ++dataTick;
    }
}

//reads measurement data into the specified DataRecord
void measure(volatile DataRecord *dr) {
  dr->temperature = (int) bme.readTemperature();
  dr->humidity = (byte)bme.readHumidity();
  dr->pressure = (int) (bme.readPressure()/100.0);
  dr->light = (byte) analogRead(14)/4;
  dr->timeStamp = dataTick;
}

//linear interpolation
float lerp(float a, float b, float x) {
  return a + (b - a) * x;
}

//inverse of linear interpolation
float deLerp(float a, float b, float c) {
  return (c-a)/(b-a);
}

//this algorithm finds the least valuable record and deletes it.
//a record is considered to be low value if it is very close to a a straight line between its next and previous records
//The idea is to delete records that we'll be able to accurately guess later
void cullRecord() {
  int minI = -1;  //will be the index of the lowest value record
  float minValue = 1729;  //initialize minimum found to arbitrary large number, will be the value of the lowest value record
  for (int i = 1; i < usedRecords-1; ++i) {
    float betwixt = deLerp((float)dataList[i-1].timeStamp, (float)dataList[i+1].timeStamp, (float)dataList[i].timeStamp);
    
    float tValue = deLerp(MIN_TEMPERATURE, MAX_TEMPERATURE, lerp((float)dataList[i-1].temperature, (float)dataList[i+1].temperature, betwixt)); //find interpolated measurement
    tValue += deLerp(MIN_TEMPERATURE, MAX_TEMPERATURE, (float)dataList[i].temperature);                                                         //subtract actual measurement to find difference
    float pValue = deLerp(MIN_PRESSURE, MAX_PRESSURE, lerp((float)dataList[i-1].pressure, (float)dataList[i+1].pressure, betwixt));
    pValue += deLerp(MIN_PRESSURE, MAX_PRESSURE, (float)dataList[i].pressure);
    float hValue = deLerp(MIN_HUMIDITY, MAX_HUMIDITY, lerp((float)dataList[i-1].humidity, (float)dataList[i+1].humidity, betwixt));
    hValue += deLerp(MIN_HUMIDITY, MAX_HUMIDITY, (float)dataList[i].humidity);
    float lValue = deLerp(MIN_LIGHT, MAX_LIGHT, lerp((float)dataList[i-1].light, (float)dataList[i+1].light, betwixt));
    hValue += deLerp(MIN_LIGHT, MAX_LIGHT, (float)dataList[i].light);

    float value = tValue * tValue + pValue * pValue + hValue * hValue + lValue * lValue;  //4D eucidean length of the sum of the distances (total difference)
    if (value < minValue) {
      minValue = value;
      minI = i;
    }
  }

  //delete the lowest value record
  for (int i = minI; i < usedRecords - 1; ++i) {
    dataList[i] = dataList[i+1];
  }

  --usedRecords;
}
