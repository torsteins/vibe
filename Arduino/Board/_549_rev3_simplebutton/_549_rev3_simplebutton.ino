/* wooo, header information
    Basic Pin setup:
    ------------                                  ---u----
    ARDUINO   13|-> SCLK (pin 25)           OUT1 |1     28| OUT channel 0
              12|                           OUT2 |2     27|-> GND (VPRG)
              11|-> SIN (pin 26)            OUT3 |3     26|-> SIN (pin 11)
              10|-> BLANK (pin 23)          OUT4 |4     25|-> SCLK (pin 13)
               9|-> XLAT (pin 24)             .  |5     24|-> XLAT (pin 9)
               8|                             .  |6     23|-> BLANK (pin 10)
               7|                             .  |7     22|-> GND
               6|                             .  |8     21|-> VCC (+5V)
               5|                             .  |9     20|-> 2K Resistor -> GND
               4|                             .  |10    19|-> +5V (DCPRG)
               3|-> GSCLK (pin 18)            .  |11    18|-> GSCLK (pin 3)
               2|                             .  |12    17|-> SOUT
               1|                             .  |13    16|-> XERR
               0|                           OUT14|14    15| OUT channel 15
    ------------                                  --------

    -  Put the longer leg (anode) of the LEDs in the +5V and the shorter leg
         (cathode) in OUT(0-15).
    -  +5V from Arduino -> TLC pin 21 and 19     (VCC and DCPRG)
    -  GND from Arduino -> TLC pin 22 and 27     (GND and VPRG)
    -  digital 3        -> TLC pin 18            (GSCLK)
    -  digital 9        -> TLC pin 24            (XLAT)
    -  digital 10       -> TLC pin 23            (BLANK)
    -  digital 11       -> TLC pin 26            (SIN)
    -  digital 13       -> TLC pin 25            (SCLK)
    -  The 2K resistor between TLC pin 20 and GND will let ~20mA through each
       LED.  To be precise, it's I = 39.06 / R (in ohms).  This doesn't depend
       on the LED driving voltage.
    - (Optional): put a pull-up resistor (~10k) between +5V and BLANK so that
                  all the LEDs will turn off when the Arduino is reset.
*/
#include "Tlc5940.h"
#include "button.h"
#include "MemoryFree.h"

/*
#ifndef DEBUG
#define DEBUG
#endif
*/

#define FRONT 0
#define BACK 1
#define LEFT 2
#define RIGHT 3

#define DEF_DURATION 1000*60*5 // 5 minutes

#define BUFFLEN 512
#define BLINK_LED 13

char inData[BUFFLEN];

#define BUTTONS 12
ermButton *erm[BUTTONS] = {
  new ermButton(0,1),
  new ermButton(2,3),
  new ermButton(4,5),
  new ermButton(6,7),
  new ermButton(8,9),
  new ermButton(10,11),
  new ermButton(12,13),
  new ermButton(14,15),
  new ermButton(16,17),
  new ermButton(18,19),
  new ermButton(20,21),
  new ermButton(22,23)
};

////////////////////////////////////////////////////////////////////////////////
// void setup()
////////////////////////////////////////////////////////////////////////////////
void setup() {
  // Set up communication
  Serial.begin(9600);
  Serial.setTimeout(30);
  
  Tlc.init(2048);  // can pass an intial PWM value for each channel
  
  pinMode(BLINK_LED, OUTPUT);
  digitalWrite(BLINK_LED, LOW);
  
  // Initialise neighbours
  /*
  for (int i = 0; i < BUTTONS; i++) {
    erm[i]->setNeighbors(erm[(i+BUTTONS-1)%BUTTONS], erm[(i+1)%BUTTONS]);
  }
  */
  
  #ifdef DEBUG
  Serial.print("Free memory: ");
  // 5869 with stuff initialised
  // 6327 with no stuff
  Serial.println(freeMemory());
  #endif
}

////////////////////////////////////////////////////////////////////////////////
// void loop()
////////////////////////////////////////////////////////////////////////////////
void loop() {
  ////////////////
  // Do always! //
  ////////////////
  updateButtonStates();
  updatePWM_Driver(); 
  
  // Check if there is a message
  if (!Serial.available()) return;
  int len = Serial.readBytesUntil('\n', inData, BUFFLEN);
  if (len == 0) {
    // Ignore command if nothing received
    return;
  }
  
  ////////////////////////////////
  // Do if there was a message! //
  ////////////////////////////////
  
  // Send acknowledgement
  Serial.print("(" + String(len) + ") ");
  
  // Add terminating null character, convert to string
  inData[len] = '\0';
  String cmd = String(inData);
  parseMessage(cmd, len);
  Serial.println("");
}

////////////////////////////////////////////////////////////////////////////////
// void parseMessage()
////////////////////////////////////////////////////////////////////////////////
void parseMessage(String cmd, int len) {
  
  // Acknowledge with length of received message  
  if (cmd.startsWith("UPDATE:")) {
    cmd = cmd.substring(7);
    
    int colon = 0;
    while (colon >= 0) {
      
      String val;
      colon = cmd.indexOf(':');
      if (colon > 0) {
        val = cmd.substring(0,colon);
        cmd = cmd.substring(1+colon);
      } else {
        val = cmd;
      }
      parseVibratorString(val);
      
    }
  }
}

////////////////////////////////////////////////////////////////////////////////
// void parseVibratorString()
////////////////////////////////////////////////////////////////////////////////
void parseVibratorString(String cmd) {
  int mod, vib, amp, dur, ivl;
  char buf[8];
  
  // First value: Module
  int comma = cmd.indexOf(',');
  String val = cmd.substring(0,comma);
  val.toCharArray(buf,8);
  mod = atoi(buf);
  cmd = cmd.substring(comma+1);
  
  // Second value: Vibrator
  comma = cmd.indexOf(',');
  val = cmd.substring(0,comma);
  val.toCharArray(buf,8);
  vib = atoi(buf);
  cmd = cmd.substring(comma+1);
  
  // Third value: Amplitude
  comma = cmd.indexOf(',');
  val = cmd.substring(0,comma);
  val.toCharArray(buf,8);
  amp = atoi(buf);
  cmd = cmd.substring(comma+1);
  
  // Fourth value: Duration
  comma = cmd.indexOf(',');
  val = cmd.substring(0,comma);
  val.toCharArray(buf,8);
  dur = atoi(buf);
  cmd = cmd.substring(comma+1);
  
  // Fifth value: Interval
  comma = cmd.indexOf(',');
  val = cmd.substring(0,comma);
  val.toCharArray(buf,8);
  ivl = atoi(buf);
  cmd = cmd.substring(comma+1);
  
  // Sixth value: Type - this is the remainder, so do nothing
  
  // Call method to treat the data
  receivedMessage(mod, vib, amp, dur, ivl, cmd);
}
    
////////////////////////////////////////////////////////////////////////////////
// void receivedMessage()
//////////////////////////////////////////////////////////////////////////////// 
void receivedMessage(int mod, int vib, int amp, int dur, int ivl, String typ) {
  String res = "Vib"+String(mod)+"-"+String(vib)+" A:"+String(amp);
  res = res + " D:"+String(dur)+" I:"+String(ivl)+" T:"+typ;
  Serial.print(res+"   ");
  
  dur = (dur <= 1) ? DEF_DURATION : dur;
  erm[vib]->sendPulse(amp, dur);
  
  if (vib == 0) {
    if (amp == 0) {
    digitalWrite(BLINK_LED, LOW);
    } else {
    digitalWrite(BLINK_LED, HIGH);
    }
  }
}


////////////////////////////////////////////////////////////////////////////////
// void updateButtonStates()
////////////////////////////////////////////////////////////////////////////////
void updateButtonStates() {
  for (int i = 0; i<BUTTONS; i++) {
    erm[i]->update();
  }
}

////////////////////////////////////////////////////////////////////////////////
// void updatePWM_Driver()
// send info to the PWM ()
////////////////////////////////////////////////////////////////////////////////
void updatePWM_Driver() {
  Tlc.update();
}
