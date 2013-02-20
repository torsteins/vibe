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


#define BUFFLEN 1024
#define BLINK_LED 13

char inData[BUFFLEN];

const int period = 8;
const int  LilyPad1 = 0;  // channel 0
const int  LilyPad2 = 15;  // channel 15

const int Button1 = LilyPad1;  // channel 2
const int Button2 = 2;
const int Button3 = 8;
const int Button4 = LilyPad2;  // channel 8

const int LED1 = 1;
const int LED2 = 3;
const int LED3 = 5;
const int LED4 = 7;

const int ERMdecayRate = 40;
const int ERMspikeValue = 4080;

// starting values for the ERMs. These values also get written to the LEDs
int ERM1value = 0;
int ERM2value = 4096/7;
int ERM3value = 4096/7 * 2;
int ERM4value = 4096/7 * 3;


////////////////////////////////////////////////////////////////////////////////
// void setup()
////////////////////////////////////////////////////////////////////////////////
void setup() {
  Serial.begin(9600);
  Serial.setTimeout(2000);
  
  Tlc.init(2048);  // can pass an intial PWM value for each channel
  
  pinMode(BLINK_LED, OUTPUT);
  digitalWrite(BLINK_LED, LOW);
}

////////////////////////////////////////////////////////////////////////////////
// void loop()
////////////////////////////////////////////////////////////////////////////////
void loop() {  
  int len = Serial.readBytesUntil('\n', inData, BUFFLEN);
  if (len == 0) {
    // Ignore command if nothing received
    return;
  }
  
  // call startWaveReponse() to initiate a wave
  
  // Add terminating null character, then convert to string
  inData[len] = '\0';
  String cmd = String(inData);
  
  Serial.print("(");
  Serial.print(len);
  Serial.print(") ");
  
  if (cmd.startsWith("ON")) {
    Serial.print("Received message: ");
    Serial.println(cmd);
    digitalWrite(BLINK_LED, HIGH);
  }
  else if (cmd.startsWith("OFF")) {
    Serial.print("Received message: ");
    Serial.println(cmd);
    digitalWrite(BLINK_LED, LOW);
  }
  else {
    Serial.print("Received unknown message: ");
    Serial.println(inData);
  }
  
  updateButtonStates();
  updatePWM_Driver(); 
}

////////////////////////////////////////////////////////////////////////////////
// void startWaveResponse()
////////////////////////////////////////////////////////////////////////////////
void startWaveReponse()
{
  ERM1value = ERMspikeValue;
}

////////////////////////////////////////////////////////////////////////////////
// void updateButtonStates()
////////////////////////////////////////////////////////////////////////////////
void updateButtonStates() {
  if(ERM1value > 0) {
    ERM1value -= ERMdecayRate;
  }
  if(ERM1value <= 0) {
    ERM1value = 0;
  }
  
  if(ERM2value > 0) {
    ERM2value -= ERMdecayRate;
  }
  if(ERM2value <= 0) {
    ERM2value = 0;
  }
  
  if(ERM3value > 0) {
    ERM3value -= ERMdecayRate;
  }
  if(ERM3value <= 0) {
    ERM3value = 0;
  }
  
  if(ERM4value > 0) {
    ERM4value -= ERMdecayRate;
  }
  if(ERM4value <= 0) {
    ERM4value = 0;
  }
  
  if(ERM1value > 3000 && ERM1value < 3200) { ERM2value = ERMspikeValue; }
  if(ERM2value > 3000 && ERM2value < 3200) { ERM3value = ERMspikeValue; }
  if(ERM3value > 3000 && ERM3value < 3200) { ERM4value = ERMspikeValue; }
}


////////////////////////////////////////////////////////////////////////////////
// void updatePWM_Driver()
// send info to the PWM ()
////////////////////////////////////////////////////////////////////////////////
void updatePWM_Driver() {
  Tlc.set(LED1,    ERM1value);
  Tlc.set(Button1, ERM1value);
  Tlc.set(LED2,    ERM2value);
  Tlc.set(Button2, ERM2value);
  Tlc.set(LED3,    ERM3value);
  Tlc.set(Button3, ERM3value);
  Tlc.set(LED4,    ERM4value);
  Tlc.set(Button4, ERM4value);
  Tlc.update();
}
