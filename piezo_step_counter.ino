#include <SoftwareSerial.h>
SoftwareSerial BTserial(8, 9);
#include "pulse-sensor-arduino.h"
#define HEARTPULSE_PIN_SIG  A2
PulseSensor heartpulse;

int sensorPiezoL = A1;    // piezo input
int sensorValueL = 0; 
int sensorValueBeforeL = 0;
int tempL;

int sensorPiezoR = A2;    // piezo input
int sensorValueR = 0; 
int sensorValueBeforeR = 0;
int tempR;

void setup() {
Serial.begin(9600);  
Serial.println("WELCOME!!!");
BTserial.begin(9600);
heartpulse.begin(HEARTPULSE_PIN_SIG);
//Serial.println();
}

void loop() {

tempL = analogRead(sensorPiezoL);
sensorValueL = (tempL-5);    // read the value from the sensor:
//Serial.println(sensorValueL);    // tell the value from the sensor:

tempR = analogRead(sensorPiezoR);
sensorValueR = (tempR-5);    // read the value from the sensor:
//Serial.println(sensorValueR);    // tell the value from the sensor:

if (sensorValueL > 0) {
    if (sensorValueL != sensorValueBeforeL)
    {
      Serial.println("LEFT step");
      BTserial.println("LEFT");
      //Serial.print("\t");
      Serial.println(sensorValueL);
      //delay(100);
    }
}   

if (sensorValueR > 0) {
    if (sensorValueR != sensorValueBeforeR)
    {
      Serial.println("RIGHT step");
      BTserial.println("RIGHT");
      //Serial.print("\t");
      Serial.println(sensorValueR);
      //delay(100);
    }
}
int heartpulseBPM = heartpulse.BPM;
Serial.println(heartpulseBPM);
BTserial.println(heartpulseBPM);
    
delay(50);
sensorValueBeforeL=sensorValueL;
sensorValueBeforeR=sensorValueR;

}
