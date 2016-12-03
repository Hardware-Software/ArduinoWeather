/***************************************************************************
  This is a library for the BME280 humidity, temperature & pressure sensor

  Designed specifically to work with the Adafruit BME280 Breakout
  ----> http://www.adafruit.com/products/2650

  These sensors use I2C or SPI to communicate, 2 or 4 requiredpins are 
  to interface.

  Adafruit invests time and resources providing this open source code,
  please support Adafruit andopen-source hardware by purchasing products
  from Adafruit!

  Written by Limor Fried & Kevin Townsend for Adafruit Industries.
  BSD license, all text above must be included in any redistribution
 ***************************************************************************/

#include <Wire.h>
#include <SPI.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>
#include <SoftwareSerial.h>

#define BME_SCK 13
#define BME_MISO 12
#define BME_MOSI 11
#define BME_CS 10

#define SEALEVELPRESSURE_HPA (1013.25)

Adafruit_BME280 bme; // I2C
//Adafruit_BME280 bme(BME_CS); // hardware SPI
//Adafruit_BME280 bme(BME_CS, BME_MOSI, BME_MISO,  BME_SCK);
struct datarec {
  int temperature;  //2 bytes, centiFahrenheit
  int pressure; //2 bytes, hPa
  byte humidity;  //1 byte, %saturation
  byte light; //1 byte, 4 Rendens
  unsigned int timeStamp;  //number of measurement ticks since start of data
};
typedef struct datarec DataRecord;

byte receivedChar;
boolean messageArrived = false;
DataRecord dr;
char dataArr[sizeof(DataRecord)]; 

#define MAX_RECORDS 100 //size of the dataList
#define CULL_COUNT 30 //number of records that will be culled when the dataList runs out of space
int usedRecords;  //# of DataRecords in the dataList
DataRecord dataList[MAX_RECORDS]; //measured data

int dataTick; //time between measurements TODO: milliseconds or seconds?




void setup() {
  DataRecord dr;
  Serial.begin(9600);
  Serial.print("RTS");
  if(!bme.begin()){
    Serial.println("Can't communicate with sensor! Check the wiring!");
  }
}

void loop() {
  // Do other logging stuff here
  if(messageArrived){
    //Process the message here.
    dr.temperature = bme.readTemperature();
    dr.humidity = bme.readHumidity();
    dr.pressure = bme.readPressure();
    memcpy(&dataArr,&dr,sizeof(DataRecord));
    Serial.write((uint8_t *)&dataArr,sizeof(DataRecord));
    messageArrived = false;
  }
}

void serialEvent() {
  while(Serial.available()) {
    receivedChar = Serial.read();
    messageArrived = true;
  }
}

void measure(DataRecord *dr) {
  dr->temperature = (int)((bme.readTemperature() * 1.8 + 32.0f) * 100);
  dr->humidity = (byte)bme.readHumidity();
  dr->pressure = (int)bme.readPressure();
  dr->light = 0;//TODO: (byte)light.getShort() / 4;
  dr->timeStamp = 0;
}

void cullRecord() {
  int nearestI = -1;
  float nearestDist = 0;
  for (int i = 0; i < usedRecords; ++i) {
    float dist = 0; //TODO
    if (dist < nearestDist) {
      nearestDist = dist;
      nearestI = i;
    }
  }

  for (int i = nearestI; i < usedRecords - 1; ++i) {
    dataList[i] = dataList[i+1];
  }

  --usedRecords;
}


//character 'D'
//size (1 byte)
//data
//.
//.
//.

