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

// TODO Add lower updates for rainbow()

// Length of LEDs
#define UPPER_LENGTH 8 //16
#define LOWER_LENGTH 8 //60

// Pins where the LEDs are connected to the Arduino
#define UPPER_DATAPIN 2
#define LOWER_DATAPIN 3

// WS2811 LED strip
#define LED_TYPE WS2811

String currentPattern;

Adafruit_NeoPixel upper = Adafruit_NeoPixel(UPPER_LENGTH, UPPER_DATAPIN, NEO_GRB + NEO_KHZ800);
Adafruit_NeoPixel lower = Adafruit_NeoPixel(LOWER_LENGTH, LOWER_DATAPIN, NEO_GRB + NEO_KHZ800);

void setup() {
    upper.begin();
    lower.begin();
    Serial.begin(9600);
    fillUpper(0, 0, 0);
    fillLower(0, 0, 0);
}

void loop() {
    String currentPattern = receiveData(); // receiveData() has a while statement and will hang, pausing the loop until data is received from serial

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
        launcherAiming();
    } else if(currentPattern == "LAUNCHER_READY_ID") {
        launcherReady();
    } else if(currentPattern == "LAUNCHER_SHOOTING_ID") {
        launcherShooting();
    } else if(currentPattern == "CLIMB_RUNNING_ID") {
        climbRunning();
    } else if(currentPattern == "CLIMB_COMPLETE_ID") {
        climbComplete();
    } else if(currentPattern == "FEEDER_JAM_ID") {
        feederJammed();
    } else if(currentPattern == "OFF_ID") {
        allOff();
    } else if(currentPattern == "RAINBOW_ID") {
        rainbow(30);
    } else {
        allOff();
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void rainbow(uint8_t wait) {
    uint16_t i, j;

    for(j=0; j<256; j++) {
        for(i=0; i<upper.numPixels(); i++) {
            upper.setPixelColor(i, Wheel((i*1+j) & 255));
        }
        upper.show();
        delay(wait);
    }
}


// Input a value 0 to 255 to get a color value
// The colors are transition r - g - b - back to r
uint32_t Wheel(byte WheelPos) {
    if(WheelPos < 85) {
        return upper.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
    } else if(WheelPos < 170) {
        WheelPos -= 85;
        return upper.Color(255 - WheelPos * 3, 0, WheelPos * 3);
    } else {
        WheelPos -= 170;
        return upper.Color(0, WheelPos * 3, 255 - WheelPos * 3);
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    fillUpper(0, 0, 0);
    fillLower(0, 0, 0);
}

void launcherAiming() {
    fillUpper(255, 255, 0);
    fillLower(255, 255, 0);
}

void launcherReady() {
    fillUpper(0, 255, 0);
    fillLower(0, 255, 0);
}

void launcherShooting() {
    fillUpper(255, 0, 0);
    fillLower(255, 0, 0);
}

void climbRunning() {
    fillUpper(255, 255, 0);
    fillLower(255, 255, 0);
}

void climbComplete() {
    fillUpper(0, 255, 0);
    fillLower(0, 255, 0);
}

void feederJammed() {
    fillUpper(255, 0, 0);
    fillLower(255, 0, 0);
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

String receiveData() {
    while(Serial.available() == 0);
        String currentPattern = Serial.readString();
        currentPattern.remove(currentPattern.length()-1); //To get rid of the newline character
        Serial.println(currentPattern); // For debugging
    return currentPattern;
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
