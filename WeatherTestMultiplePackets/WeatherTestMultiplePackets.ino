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
  int timeStamp;  //number of measurement ticks since start of data
};
typedef struct datarec DataRecord;

//Data sending stuff
int data;
byte receivedChar;
boolean messageArrived = false;
volatile DataRecord dr;
char volatile dataArr[sizeof(DataRecord)];

//Data managing stuff
#define MAX_RECORDS 100 //size of the dataList
#define CULL_COUNT 30 //number of records that will be culled when the dataList runs out of space
int usedRecords;  //# of DataRecords in the dataList
DataRecord dataList[MAX_RECORDS]; //measured data

int dataTick; //time between measurements TODO: milliseconds or seconds?

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
  delay(1000);
}
void serialEvent() {
  while(Serial.available()) {
    receivedChar = Serial.read();
    messageArrived = true;
  }
    Serial.write('D');
    Serial.write(8);
    dr.temperature = (int) bme.readTemperature();
    dr.humidity = (byte) bme.readHumidity();
    dr.pressure = (int) (bme.readPressure()/100.0);
    dr.light = (byte) analogRead(14)/4;
    memcpy((void*)(&dataArr),(void*)(&dr),sizeof(DataRecord));
    Serial.write((uint8_t *)&dataArr,sizeof(DataRecord));
    messageArrived = false;
}

void measure(DataRecord *dr) {
  dr->temperature = (int)((bme.readTemperature() * 1.8 + 32.0f) * 100);
  dr->humidity = (byte)bme.readHumidity();
  dr->pressure = (int)bme.readPressure();
  dr->light = 0;//TODO: (byte)light.getShort() / 4;
  dr->timeStamp = 0;
}

//linear interpolation
float lerp(float a, float b, float x) {
  return a + (b - a) * x;
}

float deLerp(float a, float b, float c) {
  return (c-a)/(b-a);
}

void cullRecord() {
  int minI = -1;
  float minValue = 1729;
  for (int i = 1; i < usedRecords-1; ++i) {
    float betwixt = deLerp((float)dataList[i-1].timeStamp, (float)dataList[i+1].timeStamp, (float)dataList[i].timeStamp);
    
    float tValue = deLerp(MIN_TEMPERATURE, MAX_TEMPERATURE, lerp((float)dataList[i-1].temperature, (float)dataList[i+1].temperature, betwixt));
    tValue += deLerp(MIN_TEMPERATURE, MAX_TEMPERATURE, (float)dataList[i].temperature);
    float pValue = deLerp(MIN_PRESSURE, MAX_PRESSURE, lerp((float)dataList[i-1].pressure, (float)dataList[i+1].pressure, betwixt));
    pValue += deLerp(MIN_PRESSURE, MAX_PRESSURE, (float)dataList[i].pressure);
    float hValue = deLerp(MIN_HUMIDITY, MAX_HUMIDITY, lerp((float)dataList[i-1].humidity, (float)dataList[i+1].humidity, betwixt));
    hValue += deLerp(MIN_HUMIDITY, MAX_HUMIDITY, (float)dataList[i].humidity);
    float lValue = deLerp(MIN_LIGHT, MAX_LIGHT, lerp((float)dataList[i-1].light, (float)dataList[i+1].light, betwixt));
    hValue += deLerp(MIN_LIGHT, MAX_LIGHT, (float)dataList[i].light);

    float value = tValue * tValue + pValue * pValue + hValue * hValue + lValue * lValue;
    if (value < minValue) {
      minValue = value;
      minI = i;
    }
  }

  for (int i = minI; i < usedRecords - 1; ++i) {
    dataList[i] = dataList[i+1];
  }

  --usedRecords;
}
