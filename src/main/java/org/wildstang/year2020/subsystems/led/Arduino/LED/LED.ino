#include <Adafruit_NeoPixel.h>
#include <SPI.h>

/*
    You will need to download the Arduino IDE in order to upload this code to the Arduino.
    - SPI is also included by default in the Arduino hardware library and is used for serial communication.
    - Adafruit_NeoPixel is a library used to communicate with the LEDs and should be under the libraries
      folder on the local machine
*/

// TODO Add lower updates for allianceRainbow()

// Length of LEDs
#define UPPER_LENGTH 16 //16
#define LOWER_LENGTH 100 //98 - 100

// Data pins for the Arduino to communicate with LED strips
#define UPPER_DATAPIN 3
#define LOWER_DATAPIN 5

// WS2811 LED strip
#define LED_TYPE WS2811

String currentPattern;
int index;

Adafruit_NeoPixel upper = Adafruit_NeoPixel(UPPER_LENGTH, UPPER_DATAPIN, NEO_GRB + NEO_KHZ800);
Adafruit_NeoPixel lower = Adafruit_NeoPixel(LOWER_LENGTH, LOWER_DATAPIN, NEO_GRB + NEO_KHZ800);

void setup() {
    upper.begin();
    lower.begin();
    Serial.begin(9600);
    fillUpper(0, 0, 0);
    fillLower(0, 0, 0);
    currentPattern = "ALLIANCE_RAINBOW_ID";
    //digitalWrite(13, HIGH); // For debugging purposes
}

void loop() {
    patternCheck();
    if (Serial.available() > 0) {
        currentPattern = Serial.readStringUntil('\n');
        Serial.println(currentPattern);
    }
}

void patternCheck() {
    if (currentPattern == "DISABLED_ID") {
        white();
    } else if (currentPattern == "AUTO_ID") {
        yellow();
    } else if (currentPattern == "ALLIANCE_RAINBOW_ID") {
        alliancePurple(15);
    } else if (currentPattern == "ALLIANCE_BLUE_ID") {
        blue();
    } else if (currentPattern == "ALLIANCE_RED_ID") {
        red();
    } else if (currentPattern == "CONTROL_PANEL_RED_ID") {
        red();
    } else if (currentPattern == "CONTROL_PANEL_YELLOW_ID") {
        yellow();
    } else if (currentPattern == "CONTROL_PANEL_GREEN_ID") {
        green();
    } else if (currentPattern == "CONTROL_PANEL_BLUE_ID") {
        blue();
    } else if (currentPattern == "LAUNCHER_AIMING_ID") {
        superchargedYellow();
    } else if (currentPattern == "LAUNCHER_READY_ID") {
        superchargedBlue();
    } else if (currentPattern == "INNER_PORT_ID") {
        superchargedPurple();
    } else if (currentPattern == "LAUNCHER_SHOOTING_ID") {
        superchargedPurple();
    } else if (currentPattern == "CLIMB_COMPLETE_ID") {
        alliancePurple(1);
    } else if (currentPattern == "CLIMB_RUNNING_ID") {
        superchargedYellow();
    } else if (currentPattern == "FEEDER_JAM_ID") {
        feederJammed();
    } else if (currentPattern == "IDLE_ID") {
        alliancePurple(15);
    } else if (currentPattern == "OFF_ID") {
        allOff();
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void allianceRainbow(int time) {
    uint16_t i, j;
    for (j = 0; j < 256; j++) {
        for (i = 0; i < upper.numPixels(); i++) {
            upper.setPixelColor(i, Wheel((i*1+j) & 255));
        }
        for (i = 0; i < lower.numPixels(); i++) {
            lower.setPixelColor(i, Wheel((i*1+j) & 255));
        }
        upper.show();
        lower.show();
        delay(time);
        if (Serial.available() > 0) {
            currentPattern = Serial.readStringUntil('\n');
            Serial.println(currentPattern);
            allOff();
            return;
        }
    }
}

uint32_t Wheel(byte WheelPos) {
    if (WheelPos < 85) {
        return upper.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
        return lower.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
    } else if (WheelPos < 170) {
        WheelPos -= 85;
        return upper.Color(255 - WheelPos * 3, 0, WheelPos * 3);
        return lower.Color(255 - WheelPos * 3, 0, WheelPos * 3);
    } else {
        WheelPos -= 170;
        return upper.Color(0, WheelPos * 3, 255 - WheelPos * 3);
        return lower.Color(0, WheelPos * 3, 255 - WheelPos * 3);
    }
}

void alliancePurple(int time) {
    uint16_t i, j;
    for (j = 0; j < 171; j++) {
        for (i = 0; i < upper.numPixels(); i++) {
            upper.setPixelColor(i, PurpleWheel((i*1+j) & 255));
        }
        for (i = 0; i < lower.numPixels(); i++) {
            lower.setPixelColor(i, PurpleWheel((i*1+j) & 255));
        }
        upper.show();
        lower.show();
        delay(time);
        if (Serial.available() > 0) {
            currentPattern = Serial.readStringUntil('\n');
            Serial.println(currentPattern);
            allOff();
            return;
        }
    }
}

uint32_t PurpleWheel(byte WheelPos) {
    if (WheelPos < 85) {
        return upper.Color(255 - WheelPos * 3, 0, WheelPos * 3);
        return lower.Color(255 - WheelPos * 3, 0, WheelPos * 3);
    } else {//if (WheelPos < 170) {
        WheelPos -= 85;
        return upper.Color(WheelPos * 3, 0, 255 - WheelPos * 3);
        return lower.Color(255 - WheelPos * 3, 0, WheelPos * 3);
    //} else {
    //    WheelPos -= 170;
    //    return upper.Color(0, WheelPos * 3, 255 - WheelPos * 3);
    //}
}

void superchargedYellow() {
    for (int i = 0; i < upper.numPixels(); i++) {
        if ((i + index) % 3 == 0) {
            upper.setPixelColor(i, 0, 0, 0);
        } else {
            upper.setPixelColor(i, 255, 255, 0);
        }
    }
    for (int i = 0; i < lower.numPixels(); i++) {
        if ((i + index) % 5 == 0) {
            lower.setPixelColor(i, 0, 0, 0);
        } else {
            lower.setPixelColor(i, 255, 255, 0);
        }
    }
    upper.show();
    lower.show();
    delay(100);
    index = index + 1;
}

void superchargedGreen() {
    for (int i = 0; i < upper.numPixels(); i++) {
        if ((i + index) % 3 == 0) {
            upper.setPixelColor(i, 0, 0, 0);
        } else {
            upper.setPixelColor(i, 0, 255, 0);
        }
    }
    for (int i = 0; i < lower.numPixels(); i++) {
        if ((i + index) % 5 == 0) {
            lower.setPixelColor(i, 0, 0, 0);
        } else {
            lower.setPixelColor(i, 0, 255, 0);
        }
    }
    upper.show();
    lower.show();
    delay(100);
    index = index + 1;
}

void superchargedBlue() {
    for (int i = 0; i < upper.numPixels(); i++) {
        if ((i + index) % 3 == 0) {
            upper.setPixelColor(i, 0, 0, 0);
        } else {
            upper.setPixelColor(i, 0, 223, 255);
        }
    }
    for (int i = 0; i < lower.numPixels(); i++) {
        if ((i + index) % 3 == 0) {
            lower.setPixelColor(i, 0, 0, 0);
        } else {
            lower.setPixelColor(i, 0, 223, 255);
        }
    }
    upper.show();
    lower.show();
    delay(100);
    index = index + 1;
}

void superchargedPurple() {
    for (int i = 0; i < upper.numPixels(); i++) {
        if ((i + index) % 3 == 0) {
            upper.setPixelColor(i, 0, 0, 0);
        } else {
            upper.setPixelColor(i, 153, 0, 204);
        }
    }
    for (int i = 0; i < lower.numPixels(); i++) {
        if ((i + index) % 3 == 0) {
            lower.setPixelColor(i, 0, 0, 0);
        } else {
            lower.setPixelColor(i, 153, 0, 204);
        }
    }
    upper.show();
    lower.show();
    delay(100);
    index = index + 1;
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
