/*  Pretty header information here later */

#include "button.h"

/*
#ifndef DEBUG
#define DEBUG
#endif
*/
////////////////////////////////////////////////////////////////////////////////
// ermButton::ermButton()
//////////////////////////////////////////////////////////////////////////////// 
ermButton::ermButton(int channel, int LED) {
  myChannel = channel;
  myLED = LED;
  
  value = 0;
  targetTime = 0;
}

////////////////////////////////////////////////////////////////////////////////
// void ermButton::update()
//////////////////////////////////////////////////////////////////////////////// 
void ermButton::update() {
  // grab the time and do diff in current time to last wave
  if ((value > 0) && (millis() > targetTime)) {
    value = value - 1;
    #ifdef DEBUG
    if (value == 0)
      Serial.println("Channel "+String(myChannel)+" done!");
    #endif
  }
  
  // set my values in the PWM driver
  Tlc.set(myChannel,value);
  Tlc.set(myLED,value);
}

////////////////////////////////////////////////////////////////////////////////
// void ermButton::sendPulse()
//////////////////////////////////////////////////////////////////////////////// 
void ermButton::sendPulse(int val, int dur) {
  value = val*16;
  unsigned long curTime = millis();
  targetTime = curTime + dur;
  #ifdef DEBUG
  Serial.print("Started channel: "+String(myChannel)+" with value "+String(value));
  Serial.println("  currentTime:"+String(curTime)+" targetTime:"+String(targetTime));
  #endif
}
