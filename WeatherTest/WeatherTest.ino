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
  float temperature;
  float humidity;
  unsigned long pressure;
};
typedef struct datarec DataRecord;
int data;
byte receivedChar;
boolean messageArrived = false;
DataRecord dr;
char dataArr[sizeof(DataRecord)];
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

