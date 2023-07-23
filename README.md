# Android Mqtt Client App

### 1. Introduction

This App is a mobile lightweight Mqtt client that implements the Mqtt protocol. More information on the protocol can be found here: https://mqtt.org

For testing the App functionality you can use the HiveMq online service: https://www.hivemq.com/demos/websocket-client/?

The App might be useful to anybody who wants to
- explore and learn how the Mqtt protocol works
- debug and test existing Mqtt infrastructure
- have a simple communication with IoT devices

### 2. Getting started

To see the App in action perform the following steps: 

#### 2.2 Run the app

1. Check if internet is available to device or emulator
2. Run app and connect to broker
   - For testing use predefined "Default broker" menu option to quickly connect to the HiveMq online broker
3. Subscribe to topic
   - For testing use predefined "testtopic/#" as default choice
4. Send custom messages and monitor incoming messages
   - For testing leave all settings to thier predefined values

#### 2.2 Test client as App counterpart

1. Navigate to online Mqtt broker: https://www.hivemq.com/demos/websocket-client/?
2. Setup a simple test client and test communication with App

### 3. Features

The App consists of major feature that make up basic functionality and minor feature to improve the User Exoerience

#### 3.1 Major features

- Connecting to a broker ("Connect activity")
- Communicating with a connected broker ("Main activity")
  - Subscribe to a topic ("Subscribed topics fragment")
  - Publish custom messages ("Publish message fragment")
  - Monitor incoming messages ("Received messages fragment")

#### 3.2 Minor features

- Bookmark previous connections for faster reuse ("Recent brokers fragment")
- Bookmark previously subscribed topics for faster reuse ("Subscribed topics fragment")
- Monitor incoming messages more easily with "Scroll to top" button ("Received messages fragment")
- Display Mqtt error messages using Android Snackbar mechanism