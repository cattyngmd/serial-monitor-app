#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64

#define BUTTON_PIN_UP 6
#define BUTTON_PIN_DOWN 7

#define CENTER(_width) ((display.width() / 2) - (_width / 2))

Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, -1);

typedef struct {
  byte cores;
  float* coresLoad;
  float coresLoadAvg;

  float ramUsage;
  float temp;
} sensors_t;

sensors_t sensors;
unsigned long lastIdle = 0;
boolean lastButton = false;
boolean currentButton = false;
byte index = 0;

byte readByte() {
  while (Serial.available() < 1) {}
  return Serial.read();
}

float readFloat() {
  while (Serial.available() < 2) {}
  uint16_t i = ((uint16_t)Serial.read()) << 8 | Serial.read();
  return i * 0.1f;
}

String getCpuText(sensors_t* sensors, int index) {
  if (index == 0) {
    return "CPU " + String(sensors->coresLoadAvg, 1) + "%";
  }
  return "CPU" + String(index) + " " + String(sensors->coresLoad[index - 1], 1) + "%";
}

String getTemperatureText(String original, float temperature) {
  if (temperature >= 100) return "!!! " + original + " !!!";
  if (temperature >= 75) return "! " + original + " !";
  return "< " + original + " >";
}

void updateDisplay(sensors_t* sensors) {
  if (!sensors->coresLoad) return;
  int16_t x1, y1, w, h;

  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);

  String tempText = getTemperatureText(String(sensors->temp, 1) + String((char) 247) + "C", sensors->temp);
  display.getTextBounds(tempText, 0, 0, &x1, &y1, &w, &h);
  display.setCursor(CENTER(w),0);
  display.println(tempText);

  display.setTextSize(2);
  int16_t cpuY = h + 15;
  String cpuText = getCpuText(sensors, index);
  display.getTextBounds(cpuText, 0, 0, &x1, &y1, &w, &h);
  display.setCursor(CENTER(w),cpuY);
  display.println(cpuText);

  display.setTextSize(1);
  String ramText = "RAM [----------] " + String(round(sensors->ramUsage * 100)) + "%";
  byte ramUsageInt = round(sensors->ramUsage * 10);
  for (byte i = 0; i < ramUsageInt; i++) {
    ramText[5 + i] = '=';
  }

  display.getTextBounds(ramText, 0, 0, &x1, &y1, &w, &h);
  int16_t ramY = display.height() - h + 1;
  display.setCursor(CENTER(w),ramY);
  display.println(ramText);

  display.display();
}

void updateIdleDisplay() {
  display.clearDisplay();
  display.setTextSize(1);
  display.setCursor(0, 0);
  display.setTextColor(SSD1306_WHITE);
  display.println("idle.");
  display.display();
}

void updateIndex(sensors_t* sensors, boolean up) {
		if (!sensors->coresLoad) return;
    if (index == 0 && !up) {
      index = sensors->cores;
    } else {
      index = (index + (up ? 1 : -1)) % (sensors->cores + 1);
    }
    updateDisplay(sensors);
}

void handleButton(uint8_t pin, bool isUp) {
  if (digitalRead(pin) == LOW) {
    updateIndex(&sensors, isUp);
    delay(50);
    while(digitalRead(pin) == LOW);
  }
}

void setup() {
  	pinMode(BUTTON_PIN_UP, INPUT);
  	pinMode(BUTTON_PIN_DOWN, INPUT);

    Serial.begin(38400);
    Serial.setTimeout(20);

    if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
      for(;;);
    }
}

void loop() {
  while (Serial.available() < 1) {
    handleButton(BUTTON_PIN_UP, true);
    handleButton(BUTTON_PIN_DOWN, false);

    if (lastIdle == 0 || millis() > lastIdle) {
      updateIdleDisplay();
      lastIdle = millis() + 10000;
    }
    delay(10);
  }

  byte totalCores = readByte();

  if (!sensors.coresLoad) {
    sensors.cores = totalCores;
    sensors.coresLoad = (float*)malloc(sensors.cores * sizeof(float));
    if (!sensors.coresLoad) {
      Serial.println("couldn't allocate memory for sensors " + String(totalCores));
      return;
    }
  }

  for (byte i = 0; i < sensors.cores; i++)
    sensors.coresLoad[i] = readFloat();
  sensors.coresLoadAvg = readFloat();

  sensors.ramUsage = readFloat();
  sensors.temp = readFloat();

  updateDisplay(&sensors);
  Serial.println("pong");
  lastIdle = millis() + 10000;

  delay(10);
}