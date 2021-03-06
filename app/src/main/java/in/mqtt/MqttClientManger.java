package in.mqtt;

import android.content.Context;
import android.util.Base64;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.EncryptUtils;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

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

    private MqttAndroidClient mqttAndroidClient;

    public void init(Context context, MqttCallbackExtended mqttCallbackExtended, IMqttMessageListener iMqttMessageListener) {
        mqttAndroidClient = new MqttAndroidClient(context, "tcp://" + Config.MQTT_IP + ":" + Config.MQTT_PORT, Config.AP_MAC.toUpperCase());
        mqttAndroidClient.setCallback(mqttCallbackExtended);

        try {
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttConnectOptions.setCleanSession(false);
            mqttConnectOptions.setKeepAliveInterval(5);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(1024);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    try {
                        mqttAndroidClient.subscribe(getReportTopic(), 2, iMqttMessageListener);
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
            mqttMessage.setPayload(EncodeUtils.base64Encode(message));
            mqttAndroidClient.publish(getEmitTopic(), mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //上报消息表示 AP 端主动发送消息到服务器使用的 topic
    //上报消息 /BeesmartReport/md5(<beesmart_key>)/md5(<device_mac>)
    private String getReportTopic() {
        String beesmart_key = EncryptUtils.encryptMD5ToString("bee-CPRICE").toUpperCase();
        String device_mac = EncryptUtils.encryptMD5ToString(Config.AP_MAC).toUpperCase();
        return "/BeesmartReport/" + beesmart_key + "/" + device_mac;
    }

    //下发消息表示服务器 主动发送消息到 AP 使用的 topic
    //下发消息 /BeesmartEmit/md5(<beesmart_key>)/md5(<device_mac>)
    private String getEmitTopic() {
        String beesmart_key = EncryptUtils.encryptMD5ToString("bee-CPRICE").toUpperCase();
        String device_mac = EncryptUtils.encryptMD5ToString(Config.AP_MAC).toUpperCase();
        return "/BeesmartEmit/" + beesmart_key + "/" + device_mac;
    }
}
