/*  Pretty header information here later */

#include "button.h"

////////////////////////////////////////////////////////////////////////////////
// ermButton::ermButton()
//////////////////////////////////////////////////////////////////////////////// 
ermButton::ermButton(int channel, int LED) {
  myChannel = channel;
  myLED = LED;
  
  leftNeighbor = 0;
  rightNeighbor = 0;
  
  valueModifier = 1.0;
  
  targetAmp = 0;
  targetRiseTime = 0;
  targetHoldTime = 0;
  targetFallTime = 0;
  targetIdleTime = 0;
  waveStartTime = 0;
  timeSinceWave = 0;
  
  internalTime = 0;
}

////////////////////////////////////////////////////////////////////////////////
// void ermButton::setNeighbors()
//////////////////////////////////////////////////////////////////////////////// 
void ermButton::setNeighbors(ermButton* left, ermButton* right) {
  leftNeighbor = left;
  rightNeighbor = right;
}

////////////////////////////////////////////////////////////////////////////////
// void ermButton::update()
//////////////////////////////////////////////////////////////////////////////// 
void ermButton::update() {
  // grab the time and do diff in current time to last wave
  internalTime = millis();
  timeSinceWave = internalTime - waveStartTime;
  
  // do statemachine stuff
  switch (state) {
    case RISING:
      doRising();
      break;
    case HOLDING:
      doHolding();
      break;
    case FALLING:
      doFalling();
      break;
    case IDILING:
      doIdiling();
      break;
    default:
      break;
  }
  
  // set my values in the PWM driver
  Tlc.set(myChannel,PWMValue * valueModifier);
  Tlc.set(myLED,PWMValue * valueModifier);
  
  // influence my neighbors if the value is high enough
  // 50% or higher = send info to neighbors
}

////////////////////////////////////////////////////////////////////////////////
// void ermButton::neighborMessage()
//////////////////////////////////////////////////////////////////////////////// 
void ermButton::neighborMessage(int amp) {
  
}

////////////////////////////////////////////////////////////////////////////////
// void ermButton::impulseTriangle()
//////////////////////////////////////////////////////////////////////////////// 
void ermButton::impulseTriangle(int & amplitude) {
  targetAmp = map(amplitude,0,255,0,4095);
  targetRiseTime = 200;
  targetHoldTime = 0;
  targetFallTime = 400;
  targetIdleTime = 0;
  waveStartTime = internalTime;
  state = RISING;
  inAWave = true;
}

////////////////////////////////////////////////////////////////////////////////
// void ermButton::impulseSquare()
//////////////////////////////////////////////////////////////////////////////// 
void ermButton::impulseSquare() {

}

////////////////////////////////////////////////////////////////////////////////
// void ermButton::impulseSin()
//////////////////////////////////////////////////////////////////////////////// 
void ermButton::impulseSin() {

}

////////////////////////////////////////////////////////////////////////////////
// void ermButton::impulseSin()
//////////////////////////////////////////////////////////////////////////////// 
int ermButton::ableToTakeWave() {
  return targetAmp < .8 * 4096;
}

////////////////////////////////////////////////////////////////////////////////
// void ermButton::doRising()
//////////////////////////////////////////////////////////////////////////////// 
void ermButton::doRising() {
  // increase value until we reach cutoff
  if(targetRiseTime > timeSinceWave) {
    PWMValue = targetAmp * timeSinceWave / targetRiseTime;
  }
  else {
    PWMValue = targetAmp;
    state = HOLDING;
  }
  
}

////////////////////////////////////////////////////////////////////////////////
// void ermButton::doHolding()
//////////////////////////////////////////////////////////////////////////////// 
void ermButton::doHolding() {
  // keep track of how long we are holding the value
  if(targetHoldTime > (timeSinceWave - targetRiseTime)) {
    PWMValue = targetAmp;
  }
  else {
    state = FALLING;
  }
}

////////////////////////////////////////////////////////////////////////////////
// void ermButton::doFalling()
//////////////////////////////////////////////////////////////////////////////// 
void ermButton::doFalling() {
  // decrease value until we reach cutofff
  if(targetFallTime = (timeSinceWave - (targetRiseTime + targetHoldTime))) {
     PWMValue = targetAmp - targetAmp * timeSinceWave / (targetFallTime+targetRiseTime+targetHoldTime);
  }
  else {
    state = IDILING;
  }
}

////////////////////////////////////////////////////////////////////////////////
// void ermButton::doIdiling()
//////////////////////////////////////////////////////////////////////////////// 
void ermButton::doIdiling() {
  // keep track fo how long we are holding the other value.
  // but for now, reset the wave information
  targetAmp = 0;
  targetRiseTime = 0;
  targetHoldTime = 0;
  targetFallTime = 0;
  targetIdleTime = 0;
  inAWave = false;
}
