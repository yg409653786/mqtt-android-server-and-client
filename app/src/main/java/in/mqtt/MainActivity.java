package in.mqtt;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.Utils;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Collections;

import in.mqtt.mqttserver.R;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;

import static io.netty.util.CharsetUtil.UTF_8;

public class MainActivity extends AppCompatActivity {

    private TextView showServerToast;
    private TextView showClientToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.init(getApplicationContext());
        showServerToast = findViewById(R.id.showServerToast);
        showClientToast = findViewById(R.id.showClientToast);

        BasicConfigurator.configure();
    }

    public void startService(View v) {
        MqttServerManger.getInstance().init(Collections.singletonList(new MainActivity.PublisherListener()));
    }

    private class PublisherListener extends AbstractInterceptHandler {

        @Override
        public void onPublish(InterceptPublishMessage msg) {
            final String decodedPayload = new String(msg.getPayload().array(), UTF_8);
            showServerToast("接收到的消息 : 主题" + msg.getTopicName() + " 内容: " + decodedPayload);
        }

        @Override
        public void onConnect(InterceptConnectMessage msg) {
            super.onConnect(msg);
            showServerToast("连接成功 : " + msg.getClientID());
        }

        @Override
        public void onDisconnect(InterceptDisconnectMessage msg) {
            super.onDisconnect(msg);
            showServerToast("断开连接 : " + msg.getClientID());
        }

        @Override
        public void onSubscribe(InterceptSubscribeMessage msg) {
            super.onSubscribe(msg);
            showServerToast("绑定订阅 : " + msg.getClientID());
        }

        @Override
        public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
            super.onUnsubscribe(msg);
            showServerToast("解除订阅 : " + msg.getClientID());
        }
    }

    public void stopService(View v) {
        MqttServerManger.getInstance().onDestroy();
    }

    public void startClient(View v) {
        MqttClientManger.getInstance().init(this, new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                showClientToast("连接成功  :" + serverURI);
            }

            @Override
            public void connectionLost(Throwable cause) {
                showClientToast("连接丢失");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                showClientToast("接收到消息 :" + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                showClientToast("消息发送成功");
            }
        }, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                showClientToast("收到订阅消息 :主题" + topic + new String(message.getPayload()));
            }
        });
    }

    public void endClient(View v) {
        MqttClientManger.getInstance().onDestroy();
    }


    public void clientSendMessage(View v) {
        MqttClientManger.getInstance().sendMessage("Hello Word 服务端");
    }

    public void serverSendMessage(View v) {
        MqttClientManger.getInstance().sendMessage("Hello Word 客户端");
    }

    public void showServerToast(CharSequence charSequence) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showServerToast.setText(showServerToast.getText().toString() +
                        "\n" + charSequence);
            }
        });
    }

    public void showClientToast(CharSequence charSequence) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showClientToast.setText(showClientToast.getText().toString() +
                        "\n" + charSequence);
            }
        });
    }
}
