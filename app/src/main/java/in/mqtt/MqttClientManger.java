package in.mqtt;

import android.content.Context;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import in.mqtt.mqttclient.MqttAndroidClient;

public class MqttClientManger {
    private final static Object s_lockObj = new Object();
    private static MqttClientManger inst;

    public static MqttClientManger getInstance() {
        if (inst == null) {
            synchronized (s_lockObj) {
                if (inst == null) {
                    inst = new MqttClientManger();
                }
            }
        }
        return inst;
    }

    public static final String TOPIC = "TEST";
    private MqttAndroidClient mqttAndroidClient;
    private String serverUri = "tcp://192.168.16.184:1883";

    public void init(Context context, MqttCallbackExtended mqttCallbackExtended, IMqttMessageListener iMqttMessageListener) {
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, "Android-Client-" + System.currentTimeMillis());
        mqttAndroidClient.setCallback(mqttCallbackExtended);

        try {
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttConnectOptions.setCleanSession(true);

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(1024);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(true);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    try {
                        mqttAndroidClient.subscribe(TOPIC, 2, iMqttMessageListener);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    exception.printStackTrace();
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    public void onDestroy() {
        if (mqttAndroidClient != null) {
            try {
                mqttAndroidClient.disconnect();
                mqttAndroidClient.close();
                mqttAndroidClient = null;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        if (mqttAndroidClient == null || !mqttAndroidClient.isConnected()) {
            return;
        }
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setQos(2);
            mqttMessage.setPayload(message.getBytes());
            mqttAndroidClient.publish(TOPIC, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
