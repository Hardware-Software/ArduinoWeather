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
  int timestamp;  //number of measurement ticks since start of data
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

