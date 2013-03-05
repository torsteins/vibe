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

class ermButton {
  
public:
  // Constructor
  ermButton(int, int); // gets the channel it's on
  
  // Functions
  void update();
  void sendPulse(int, int);
  
private:
  // Variables
  int myChannel;
  int myLED;
  int value;
  unsigned long targetTime;
};

#endif

