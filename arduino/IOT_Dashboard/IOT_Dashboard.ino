#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"
#include <TFMPlus.h> // TFMini Plus Library

// --- 1. NEW Wi-Fi Credentials ---
#define WIFI_SSID       "JioFi3_85CEC8"
#define WIFI_PASSWORD   "tcrexb6u5c"

// --- Firebase Config ---
#define API_KEY         "AIzaSyC18RPeuOWoAbwriKxyAoTf1fHYa_51BHM"
#define DATABASE_URL    "https://student-iot-e10f9-default-rtdb.asia-southeast1.firebasedatabase.app/"
#define USER_EMAIL      "test@test.com"
#define USER_PASSWORD   "123456"

// --- Hardware Pins ---
const int PIN_LIGHT1 = 2;   // Relay 1 (D2)
const int PIN_LIGHT2 = 4;   // Relay 2 (D4)
const int PIN_LED    = 12;  // LED (D12)

// --- Device IDs ---
String ID_LIGHT1 = "C4652C7B";
String ID_LIGHT2 = "5493C92C";
String ID_LED    = "4F06E37F";
String ID_LIDAR  = "29AE86BB";

// --- Objects ---
TFMPlus tfmP;          // Lidar Object
FirebaseData streamLight1;
FirebaseData streamLight2;
FirebaseData streamLed;
FirebaseData fbUpload; // Separate object for uploading Lidar data
FirebaseAuth auth;
FirebaseConfig config;

// --- Variables ---
int16_t tfDist = 0;
int16_t tfFlux = 0;
int16_t tfTemp = 0;
unsigned long lastUploadTime = 0;
const int uploadInterval = 1000; // Upload Lidar data every 1000ms (1 second)

// --- Helper: Handle incoming commands ---
void handleUpdate(FirebaseStream data, int pin, String name) {
  Serial.print("Update for " + name + ": ");
  Serial.println(data.stringData());
  
  int val = data.intData();
  
  // Logic: Relays are usually Active LOW. LED is Active HIGH.
  if (pin == PIN_LED) {
    digitalWrite(pin, val == 1 ? HIGH : LOW); // LED: 1 = ON
  } else {
    digitalWrite(pin, val == 1 ? LOW : HIGH); // Relay: 1 = ON (Active Low)
  }
}

// --- Stream Callbacks ---
void cbLight1(FirebaseStream data) { handleUpdate(data, PIN_LIGHT1, "Light 1"); }
void cbLight2(FirebaseStream data) { handleUpdate(data, PIN_LIGHT2, "Light 2"); }
void cbLed(FirebaseStream data)    { handleUpdate(data, PIN_LED,    "LED"); }

void timeoutCallback(bool timeout) {
  if (timeout) Serial.println("Stream timeout!");
}

void setup() {
  Serial.begin(115200);
  
  // 1. Initialize Output Pins
  pinMode(PIN_LIGHT1, OUTPUT);
  pinMode(PIN_LIGHT2, OUTPUT);
  pinMode(PIN_LED, OUTPUT);
  
  // Default State: Relays OFF (HIGH), LED OFF (LOW)
  digitalWrite(PIN_LIGHT1, HIGH);
  digitalWrite(PIN_LIGHT2, HIGH);
  digitalWrite(PIN_LED, LOW);

  // 2. Initialize LiDAR (Serial2 on Pins 16 & 17)
  Serial2.begin(115200, SERIAL_8N1, 16, 17);
  delay(20);
  tfmP.begin(&Serial2);
  Serial.println("TFMini Plus Initialized.");

  // 3. Connect to Wi-Fi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(350);
  }
  Serial.println("\nConnected!");
  Serial.println(WiFi.localIP());

  // 4. Connect to Firebase
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  config.token_status_callback = tokenStatusCallback;

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  // 5. Start Listening (Streams)
  // We use separate streams so we can distinguish devices easily
  if (Firebase.RTDB.beginStream(&streamLight1, ("/devices/" + ID_LIGHT1 + "/value").c_str())) {
    Firebase.RTDB.setStreamCallback(&streamLight1, cbLight1, timeoutCallback);
  }
  
  if (Firebase.RTDB.beginStream(&streamLight2, ("/devices/" + ID_LIGHT2 + "/value").c_str())) {
    Firebase.RTDB.setStreamCallback(&streamLight2, cbLight2, timeoutCallback);
  }

  if (Firebase.RTDB.beginStream(&streamLed, ("/devices/" + ID_LED + "/value").c_str())) {
    Firebase.RTDB.setStreamCallback(&streamLed, cbLed, timeoutCallback);
  }

  Serial.println("System Ready: Listening for commands...");
}

void loop() {
  // --- A. Read LiDAR Data ---
  delay(10); // Small stability delay
  if (tfmP.getData(tfDist, tfFlux, tfTemp)) {
    // New distance is now in 'tfDist'
  }

  // --- B. Upload LiDAR Data to Firebase ---
  if (millis() - lastUploadTime > uploadInterval) {
    lastUploadTime = millis();
    
    if (Firebase.ready()) {
      // Upload path: /devices/29AE86BB/value
      String path = "/devices/" + ID_LIDAR + "/value";
      
      // Print to Serial for debugging
      Serial.print("LiDAR: " + String(tfDist) + "cm -> Uploading... ");
      
      if (Firebase.RTDB.setInt(&fbUpload, path.c_str(), tfDist)) {
        Serial.println("OK");
      } else {
        Serial.println("Error: " + fbUpload.errorReason());
      }
    }
  }
}
