#include <Wire.h>
#include <Adafruit_NeoPixel.h>
#include <SPI.h>

/*
    You will need to download the Arduino IDE in order to compile this code to the Arduino.
    - Wire is included by default in the Arduino hardware library and is used for serial IC2 communication.
    - SPI is also included by default in the Arduino hardware library and is used for general serial communication.
    - Adafruit_NeoPixel is a dependency to communicate with the LEDs and should be located in either the same folder
      as this code or under the libraries folder on the local machine.
*/

// Length of LEDs
#define UPPER_LENGTH 8 //16
#define LOWER_LENGTH 8 //60

// Pins where the LEDs are connected to the Arduino
#define UPPER_DATAPIN 1
#define LOWER_DATAPIN 2

// WS2811 LED strip
#define LED_TYPE WS2811
#define COLOR_ORDER NEO_GRB

Adafruit_NeoPixel upper = Adafruit_NeoPixel(UPPER_LENGTH, UPPER_DATAPIN, COLOR_ORDER);
Adafruit_NeoPixel lower = Adafruit_NeoPixel(LOWER_LENGTH, LOWER_DATAPIN, COLOR_ORDER);

// Pattern IDs
//#define OFF_ID            1
//#define DISABLED_ID       2
//#define AUTO_ID           3
//#define ALLIANCE_ID       4
//#define CONTROL_PANEL_ID  5
//#define LAUNCHER_ID       6
//#define CLIMB_ID          7
//#define FEEDER_JAM_ID     8

// For raw data packets from the LED Java class; they are read only and should not be modified
//static const byte i2cAddress = 0x10;
//unsigned char dataByte1 = 0;
//unsigned char dataByte2 = 0;
//unsigned char dataByte3 = 0;
//unsigned char dataByte4 = 0;

// Raw data values get sent to these variables
unsigned string currentPattern = "DISABLED_ID"; //dataByte1
//unsigned int redValue = 0; //dataByte2
//unsigned int greenValue = 0; //dataByte3
//unsigned int blueValue = 0; //dataByte4

boolean dataUpdateReady;

void setup() {
    // Begin I2C communications as a SLAVE. receiveData() will be called when new data arrives.
    // We call this last to avoid a nasty bug involving the LED initialization code
    upper.begin();
    lower.begin();
    Serial.begin(9600);
    //Wire.begin(i2cAddress);
    //Wire.onReceive(receiveData);
    digitalWrite(13, LOW);
}

void loop() {
    //if(Serial.read()>=0){
    //    digitalWrite(13, HIGH);
    //    fillUpper(0, 255, 0);
    //    fillLower(0, 255, 0);
    //}
    if(dataUpdateReady) {
        dataUpdateReady = false;
        if(currentPattern == "DISABLED_ID") {
            disabled();
        } else if(currentPattern == "AUTO_ID") {
            autonomous();
        } else if(currentPattern == "ALLIANCE_BLUE_ID") {
            allianceBlue();
        } else if(currentPattern == "ALLIANCE_RED_ID") {
            allianceRed();
        } else if(currentPattern == "ALLIANCE_PURPLE_ID") {
            alliancePurple();
        } else if(currentPattern == "CONTROL_PANEL_ID") {
            controlPanel();
        } else if(currentPattern == "LAUNCHER_AIMING_ID") {
            launcher();
        } else if(currentPattern == "LAUNCHER_READY_ID") {
            launcher();
        } else if(currentPattern == "LAUNCHER_SHOOTING_ID") {
            launcher();
        } else if(currentPattern == "CLIMB_RUNNING_ID") {
            climb();
        } else if(currentPattern == "CLIMB_COMPLETE_ID") {
            climb();
        } else if(currentPattern == "FEEDER_JAM_ID") {
            feederJammed();
        } else if(currentPattern == "OFF_ID") \{
            allOff();
        }
    }
}

// White
void disabled() {
    fillUpper(255, 255, 255);
    fillLower(255, 255, 255);
}

// Yellow
void autonomous() {
    fillUpper(255, 255, 0);
    fillLower(255, 255, 0);
}

void allianceBlue() {
    fillUpper(0, 0, 255);
    fillLower(0, 0, 255);
}

void allianceRed() {
    fillUpper(255, 0, 0);
    fillLower(255, 0, 0);
}

void alliancePurple() {
    fillUpper(255, 0, 255);
    fillLower(255, 0, 255);
}

void controlPanel() {
    fillUpper(redValue, greenValue, blueValue);
    fillLower(redValue, greenValue, blueValue);
}

void launcher() {
    fillUpper(redValue, greenValue, blueValue);
    fillLower(redValue, greenValue, blueValue);
}

void climb() {
    fillUpper(redValue, greenValue, blueValue);
    fillLower(redValue, greenValue, blueValue);
}

void feederJammed() {
    fillUpper(redValue, greenValue, blueValue);
    fillLower(redValue, greenValue, blueValue);
}

void allOff() {
    fillUpper(0, 0, 0);
    fillLower(0, 0, 0);
}

void allOn() {
  fillUpper(255, 255, 255);
  fillLower(255, 255, 255);
}

void fillUpper(unsigned int red, unsigned int green, unsigned int blue) {
    for (unsigned int i = 0; i < upper.numPixels(); i++) {
        upper.setPixelColor(i, red, green, blue);
    }
    upper.show();
}

void fillLower(unsigned int red, unsigned int green, unsigned int blue) {
    for (unsigned int i = 0; i < lower.numPixels(); i++) {
        lower.setPixelColor(i, red, green, blue);
    }
    lower.show();
}

void receiveData() {
    if(Serial.available() >= 0) {
        currentPattern = Serial.read();
        dataUpdateReady = true;
    }
}

// This gets called every time new data is received over the I2C bus
//void receiveData(int byteCount)
//{
//    Serial.println("Got message");
//    // Check the byte count to ensure that a 4 byte packet is received
//    if (byteCount == 4) {
//        dataByte1 = (0x000000FF & Wire.read()); // Pattern ID
//        dataByte2 = (0x000000FF & Wire.read()); // Red
//        dataByte3 = (0x000000FF & Wire.read()); // Green
//        dataByte4 = (0x000000FF & Wire.read()); // Blue
//
//        // Set the variables to the value of the data
//        currentPattern = dataByte1;
//        redValue       = dataByte2;
//        greenValue     = dataByte3;
//        blueValue      = dataByte4;
//
//        // Set the flag to state that new data is ready
//        dataUpdateReady = true;
//    } else if (byteCount > 4) {
//        // Keep on reading the bytes from the buffer until they're gone since they aren't used
//        while (Wire.available() > 0) {
//            Wire.read();
//        }
//    }
//}
