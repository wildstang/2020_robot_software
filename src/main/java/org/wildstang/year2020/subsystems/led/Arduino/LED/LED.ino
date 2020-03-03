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

// TODO Add lower updates for allianceRainbow()

// Length of LEDs
#define UPPER_LENGTH 8 //16
#define LOWER_LENGTH 8 //98

// Pins where the LEDs are connected to the Arduino
#define UPPER_DATAPIN 3
#define LOWER_DATAPIN 4

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
    currentPattern = "ALLIANCE_RAINBOW_ID";
    //digitalWrite(13, HIGH);
}

void loop() {
    patternCheck();
    if(Serial.available() > 0) {
        currentPattern = Serial.readStringUntil('\n');
        Serial.println(currentPattern);
    }
}

void patternCheck() {
    if(currentPattern == "DISABLED_ID") {
        white();
    } else if(currentPattern == "AUTO_ID") {
        yellow();
    } else if(currentPattern == "ALLIANCE_RAINBOW_ID") {
        allianceRainbow();
    } else if(currentPattern == "ALLIANCE_BLUE_ID") {
        blue();
    } else if(currentPattern == "ALLIANCE_RED_ID") {
        red();
    } else if(currentPattern == "CONTROL_PANEL_RED_ID") {
        red();
    } else if(currentPattern == "CONTROL_PANEL_YELLOW_ID") {
        yellow();
    } else if(currentPattern == "CONTROL_PANEL_GREEN_ID") {
        green();
    } else if(currentPattern == "CONTROL_PANEL_BLUE_ID") {
        blue();
    } else if(currentPattern == "LAUNCHER_AIMING_ID") {
        yellow();
    } else if(currentPattern == "LAUNCHER_READY_ID") {
        green();
    } else if(currentPattern == "LAUNCHER_SHOOTING_ID") {
        red();
    } else if(currentPattern == "CLIMB_RUNNING_ID") {
        yellow();
    } else if(currentPattern == "CLIMB_COMPLETE_ID") {
        green();
    } else if(currentPattern == "FEEDER_JAM_ID") {
        feederJammed();
    } else if(currentPattern == "IDLE_ID") {
        allianceRainbow();
    } else if(currentPattern == "OFF_ID") {
        allOff();
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void allianceRainbow() {
    uint16_t i, j;
    for(j=0; j<256; j++) {
        for(i=0; i<upper.numPixels(); i++) {
            upper.setPixelColor(i, Wheel((i*1+j) & 255));
        }
        upper.show();
        delay(30);
        if(Serial.available() > 0) {
            currentPattern = Serial.readStringUntil('\n');
            Serial.println(currentPattern);
            allOff();
            return;
        }
    }
}

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

void allOff() {
    fillUpper(0, 0, 0);
    fillLower(0, 0, 0);
}

void white() {
    fillUpper(255, 255, 255);
    fillLower(255, 255, 255);
}

void red() {
    fillUpper(255, 0, 0);
    fillLower(255, 0, 0);
}

void yellow() {
    fillUpper(255, 255, 0);
    fillLower(255, 255, 0);
}

void green() {
    fillUpper(0, 255, 0);
    fillLower(0, 255, 0);
}

void blue() {
    fillUpper(0, 0, 255);
    fillLower(0, 0, 255);
}

void feederJammed() {
    fillUpper(255, 0, 0);
    fillLower(255, 0, 0);
}

void fillUpper(unsigned int red, unsigned int green, unsigned int blue) {
    for (unsigned int i = 0; i < upper.numPixels(); i++) {
        upper.setPixelColor(i, red, green, blue);
    }
    upper.show();
    delay(1);
}

void fillLower(unsigned int red, unsigned int green, unsigned int blue) {
    for (unsigned int i = 0; i < lower.numPixels(); i++) {
        lower.setPixelColor(i, red, green, blue);
    }
    lower.show();
    delay(1);
}
