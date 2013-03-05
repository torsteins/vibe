/*  Pretty header information here later */

#ifndef EMR_BUTTON_H
#define EMR_BUTTON_H

#include <stdint.h>
#if defined(ARDUINO) && ARDUINO >= 100
	#include <Arduino.h>
#else
	#include <WProgram.h>
	#include <pins_arduino.h>
#endif

#include <avr/io.h>
#include "Tlc5940.h"

#define RISING 20
#define FALLING 21
#define HOLDING 22
#define IDILING 23

#define TRIG 24
#define SQR 25
#define SINUSOID 26

class ermButton {
public:
  ermButton(int, int); // gets the channel it's on
  
  void setNeighbors(ermButton*, ermButton*);
  void update();
  void setState();
  void neighborMessage(int);
  void impulseTriangle(int&);
  void impulseSquare();
  void impulseSin();
  int ableToTakeWave();
  
private:
  void doRising();
  void doHolding();
  void doFalling();
  void doIdiling();
  
  int myChannel;
  int myLED;
  int PWMValue;
  int state;
  float valueModifier;  // percentage that is actually used
  ermButton *leftNeighbor;
  ermButton *rightNeighbor;
  
  int targetAmp;
  int targetRiseTime;
  int targetHoldTime;
  int targetFallTime;
  int targetIdleTime;
  int waveStartTime;
  int timeSinceWave;
  
  int inAWave;
  
  int internalTime;
};

#endif

