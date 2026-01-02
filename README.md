# IOT-Dashboard

🏠 Student Smart Home Ecosystem

A high-performance, open-source IoT (Internet of Things) solution that bridges the gap between desktop software and physical hardware. This ecosystem features a modern Java Swing Dashboard that communicates in real-time with Arduino hardware via Firebase Cloud.



🚀 Breakthrough Features

Dynamic "Pin to Home" System: Unlike static dashboards, you can choose exactly which devices appear on your main screen for a personalized experience.
Interactive Control Cards: Every device card on the home screen is clickable, instantly taking you to its specific manual control panel.
Universal Signal Support: Handle both Digital (On/Off) and Analog (Range-based) signals within the same interface.
Intelligent Automation Engine: Create custom "Tasks" without writing code, including If/Then logic, delay timers, and repeat loops.
Real-Time Cloud Bridge: Uses a custom-built Firebase handler to ensure that commands from the Java app reflect on the hardware almost instantly.


🛠️ The Tech Stack

Frontend: Java Swing with a custom "Modern Shadow" UI for a sleek, glassmorphic look.
Cloud Backend: Firebase Realtime Database using REST API for lightning-fast synchronization.
Hardware: Arduino (C++) / LPC1768 logic for physical device and relay control.


☁️ Backend Visualization
The system uses a hierarchical JSON structure to manage device states in the cloud. Each entry tracks the device role, signal type, and live value.

![Firebase Data Structure](screenshots/firebase_structure.jpeg
)

Example Path: /devices/{deviceID}/value.



📂 Project Architecture

The project follows an iterative, component-based approach with clear separation between layers:
IoTDashboard.java: The main hub managing navigation and the dynamic home screen.
DeviceManager.java: The "Sync Engine" that keeps the local app and cloud database in perfect harmony.
ControlPanelPage.java: The interactive interface for manual overrides and device pinning.
AutomationPage.java: A visual form for creating complex automation logic without programming.
/arduino: The core source code for the physical microcontrollers.


🔧 Setup & Security

Clone the Repo:

  git clone https://github.com/Akashbr-2006/IOT-Dashboard.git

Configuration: This project uses a config.properties file (excluded from Git) to hide sensitive database links. Create this file in your root folder:

Properties

  firebase.url=https://YOUR_DATABASE_LINK.firebaseio.com/

Run: Launch IoTDashboard.java and watch your hardware come to life.
