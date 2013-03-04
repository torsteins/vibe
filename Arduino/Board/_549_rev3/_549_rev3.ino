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

#define FRONT 0
#define BACK 1
#define LEFT 2
#define RIGHT 3

#define BUFFLEN 1024
#define BLINK_LED 13

char inData[BUFFLEN];

#define BUTTONS 12
ermButton *erm[BUTTONS] = {
  new ermButton(1,2),
  new ermButton(3,4),
  new ermButton(5,6),
  new ermButton(7,8),
  new ermButton(9,10),
  new ermButton(11,12),
  new ermButton(13,14),
  new ermButton(15,16),
  new ermButton(17,18),
  new ermButton(19,20),
  new ermButton(21,22),
  new ermButton(23,24)
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
  for (int i = 0; i < BUTTONS; i++) {
    erm[i]->setNeighbors(erm[(i+BUTTONS-1)%BUTTONS], erm[(i+1)%BUTTONS]);
  }
 
  Serial.print("Free memory: ");
  // 5869 with stuff initialised
  // 6327 with no stuff
  Serial.println(freeMemory());
}

////////////////////////////////////////////////////////////////////////////////
// void loop()
////////////////////////////////////////////////////////////////////////////////
void loop() {
  updateButtonStates();
  updatePWM_Driver(); 
  
  if (!Serial.available()) return;
  
  int len = Serial.readBytesUntil('\n', inData, BUFFLEN);
  if (len == 0) {
    // Ignore command if nothing received
    return;
  }
  
  // Simple acknowledgement
  Serial.println("(" + String(len) + ") ");
  
  // Add terminating null character, then convert to string
  inData[len] = '\0';
  String cmd = String(inData);
  parseMessage(cmd, len);
  Serial.println("");
}

////////////////////////////////////////////////////////////////////////////////
// void parseMessage()
////////////////////////////////////////////////////////////////////////////////
void parseMessage(String cmd, int len) {
  
  Serial.println("1: "+cmd);
  Serial.flush();
  // Acknowledge with length of received message  
  if (cmd.startsWith("UPDATE:")) {
    cmd = cmd.substring(7);
    
    Serial.println("1b: "+cmd);
    Serial.flush();
    int colon = 0;
    while (colon >= 0) {
      
      String val;
      colon = cmd.indexOf(':');
      Serial.println("1c: "+colon);
      Serial.flush();
      if (colon > 0) {
        val = cmd.substring(0,colon);
        Serial.println("1i: "+val);
        Serial.flush();
        cmd = cmd.substring(1+colon);
        Serial.println("1j: "+cmd);
        Serial.flush();
      } else {
        val = cmd;
      }
      Serial.println("2: "+val);
      Serial.flush();
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
  
  Serial.println("3: "+cmd);
  Serial.flush();
  
  // First value: Module
  int comma = cmd.indexOf(',');
  String val = cmd.substring(0,comma);
  val.toCharArray(buf,8);
  mod = atoi(buf);
  cmd = cmd.substring(comma+1);

  Serial.println("4: "+cmd);
  Serial.flush();
  
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
  
  
  ermButton *targetERM;
  
  switch(vib) {
  case FRONT:
    targetERM = erm[0];
    break;
  case BACK:
    targetERM = erm[4];
    break;
  case LEFT:
    targetERM = erm[6];
    break;
  case RIGHT:
    targetERM = erm[2];
    break;
  default:
    break;
    
  }
  
  // currently ignoring the wave type
  
  targetERM->impulseTriangle(amp);
  
  
  
  
  if (vib == 0) {
    digitalWrite(BLINK_LED, LOW);
  } else {
    digitalWrite(BLINK_LED, LOW);
  }
}


////////////////////////////////////////////////////////////////////////////////
// void startWaveResponse()
////////////////////////////////////////////////////////////////////////////////
void startWaveReponse()
{
  // do stuff
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
