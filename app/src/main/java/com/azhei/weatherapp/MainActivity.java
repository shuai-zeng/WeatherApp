package com.azhei.weatherapp;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create instances of the displayed data text fields
        final TextView connectionText = (TextView) findViewById(R.id.connectionValue);
        final TextView tempText = (TextView) findViewById(R.id.tempValue);
        final TextView humidityText = (TextView) findViewById(R.id.humidityValue);
        final TextView windText = (TextView) findViewById(R.id.windValue);
        final TextView pressureText = (TextView) findViewById(R.id.pressureValue);
        final TextView rainText = (TextView) findViewById(R.id.rainValue);
        final TextView lightText = (TextView) findViewById(R.id.lightValue);

        // Comment to select cloud or self hosted mqtt server
        // final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.0.13:1883", "androidSampleClient");
        final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), "tcp://iot.eclipse.org:1883", "androidSampleClient");

        // Set MQTT callback
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("Connection", "Connection Lost");
                connectionText.setText("MQTT connection status: " + "Connection Lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.e("androidClient", "Message Arrived");
                System.out.println("Message Arrived!: " + topic + ": " + new String(message.getPayload()));
                // tempText.setText("Message Arrived!: " + topic + ": " + new String(message.getPayload()));

                // Switch case to set the corresponding data field depending on the topic of the message
                switch (topic){
                    case "zoggus/temp" :
                        tempText.setText("Temperature is " + new String(message.getPayload()) + "°C");
                        break;
                    case "zoggus/humidity" :
                        humidityText.setText("Humidity is " + new String(message.getPayload()) + "%");
                        break;
                    case "zoggus/wind" :
                        windText.setText("Wind speed is " + new String(message.getPayload()) +"m/s");
                        break;
                    case "zoggus/pressure" :
                        pressureText.setText("Pressure is " + new String(message.getPayload()) + "Pa");
                        break;
                    case "zoggus/rain" :
                        rainText.setText("Rain level is " + new String(message.getPayload()));
                        break;
                    case "zoggus/light" :
                        lightText.setText("Light level is " + new String(message.getPayload()) + "lux");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.e("androidClient", "Delivery Complete");
                // System.out.println("Delivery Complete!");
            }
        });

        try {
            mqttAndroidClient.connect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e("Connection", "Success");
                    connectionText.setText("Connection status: Success!");

                    // Subscribing to MQTT topics
                    try {
                        Log.e("Connection", "Subscribing to mqtt");
                        connectionText.setText("Connection status: Subscribing to mqtt...");
                        // mqttAndroidClient.subscribe("zoggus_arduinoEsp", 0);
                        mqttAndroidClient.subscribe("zoggus/temp", 0);
                        mqttAndroidClient.subscribe("zoggus/humidity", 0);
                        mqttAndroidClient.subscribe("zoggus/wind", 0);
                        mqttAndroidClient.subscribe("zoggus/pressure", 0);
                        mqttAndroidClient.subscribe("zoggus/rain", 0);
                        mqttAndroidClient.subscribe("zoggus/light", 0);

                        Log.e("Connection", "Subscribed to mqtt");
                        connectionText.setText("Connection status: Subscribed to mqtt");

//                        Log.e("Connection", "Publishing message..");
//                        mqttAndroidClient.publish("zoggus_arduinoEsp", new MqttMessage("Hello world!".getBytes()));
                    } catch (MqttException ex) {
                        ex.printStackTrace();
                    }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("Connection", "Connection Failure!");
                    exception.printStackTrace();
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }


    }
}