package in.mqtt;

import android.content.Intent;
import android.os.Environment;

import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

import in.mqtt.fileservice.FileService;
import io.moquette.BrokerConstants;
import io.moquette.interception.InterceptHandler;
import io.moquette.proto.messages.AbstractMessage;
import io.moquette.proto.messages.PublishMessage;
import io.moquette.server.Server;
import io.moquette.server.config.MemoryConfig;

public class MqttServerManger {
    private final static Object s_lockObj = new Object();
    private static MqttServerManger inst;

    public static MqttServerManger getInstance() {
        if (inst == null) {
            synchronized (s_lockObj) {
                if (inst == null) {
                    inst = new MqttServerManger();
                }
            }
        }
        return inst;
    }

    private Server server;

    public void init(List<? extends InterceptHandler> userHandlers) {
        server = new Server();
        try {
            server.startServer(defaultConfig(), userHandlers);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Utils.getApp().startService(new Intent(Utils.getApp(), FileService.class));
    }

    private MemoryConfig defaultConfig() {
        MemoryConfig memoryConfig = new MemoryConfig(new Properties());
        memoryConfig.setProperty(BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + BrokerConstants.DEFAULT_MOQUETTE_STORE_MAP_DB_FILENAME);
        memoryConfig.setProperty(BrokerConstants.HOST_PROPERTY_NAME, Config.MQTT_IP);
        memoryConfig.setProperty(BrokerConstants.PORT_PROPERTY_NAME, Config.MQTT_PORT);
        memoryConfig.setProperty(BrokerConstants.WEB_SOCKET_PORT_PROPERTY_NAME, Config.MQTT_WEB_SOCKET_PORT);
        return memoryConfig;
    }

    public void onDestroy() {
        if (server != null) {
            server.stopServer();
        }

        Utils.getApp().stopService(new Intent(Utils.getApp(), FileService.class));
    }

    public void sendMessage(String message) {
        if (server == null) {
            return;
        }
        try {
            PublishMessage mqttMessage = new PublishMessage();
            mqttMessage.setQos(AbstractMessage.QOSType.EXACTLY_ONCE);
            mqttMessage.setTopicName(MqttClientManger.getInstance().getEmitTopic());
            mqttMessage.setPayload(ByteBuffer.wrap(message.getBytes(Charset.forName("UTF-8"))));
            server.internalPublish(mqttMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
