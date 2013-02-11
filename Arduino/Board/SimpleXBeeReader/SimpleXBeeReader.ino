#define BUFFLEN 1024
#define BLINK_LED 13

char inData[BUFFLEN];

void setup() {
  Serial.begin(9600);
  Serial.setTimeout(2000);
  
  pinMode(BLINK_LED, OUTPUT);
  digitalWrite(BLINK_LED, LOW);
}

void loop() {  
  int len = Serial.readBytesUntil('\n', inData, BUFFLEN);
  if (len == 0) {
    // Ignore command if nothing received
    return;
  }
  
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
  
  
}
