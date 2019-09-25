package in.mqtt.fileservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.util.concurrent.TimeUnit;

import in.mqtt.Config;


public class FileService extends Service {

    private Server server;

    @Override
    public void onCreate() {
        server = AndServer.serverBuilder(this)
                .inetAddress(NetUtils.getLocalIPAddress())  //服务器要监听的网络地址
                .port(Config.MQTT_FILE_PORT) //服务器要监听的端口
                .timeout(30, TimeUnit.SECONDS) //Socket超时时间
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (server != null && !server.isRunning()) {
            server.startup();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (server != null && server.isRunning()) {
            server.shutdown();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
