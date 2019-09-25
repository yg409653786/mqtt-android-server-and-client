package in.mqtt;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import io.moquette.BrokerConstants;
import io.moquette.interception.InterceptHandler;
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
    }

    private MemoryConfig defaultConfig() {
        MemoryConfig memoryConfig = new MemoryConfig(new Properties());
        memoryConfig.setProperty(BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + BrokerConstants.DEFAULT_MOQUETTE_STORE_MAP_DB_FILENAME);
        memoryConfig.setProperty(BrokerConstants.HOST_PROPERTY_NAME, Config.MQTT_IP);
        memoryConfig.setProperty(BrokerConstants.PORT_PROPERTY_NAME, "1883");
        return memoryConfig;
    }

    public void onDestroy() {
        if (server != null) {
            server.stopServer();
        }
    }
}
