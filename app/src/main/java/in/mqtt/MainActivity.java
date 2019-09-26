package in.mqtt;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.Utils;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Collections;

import in.mqtt.model.InputModel;
import in.mqtt.model.MqttExtendModel;
import in.mqtt.model.MqttMessageModel;
import in.mqtt.mqttserver.R;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        BasicConfigurator.configure();
        MqttServerManger.getInstance().init(Collections.singletonList(new MainActivity.PublisherListener()));

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
                final String decodedPayload = new String(EncodeUtils.base64Decode(message.getPayload()));
                showClientToast("接收到消息 :" + decodedPayload);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                showClientToast("消息发送成功");
            }
        }, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String decodedPayload = new String(EncodeUtils.base64Decode(message.getPayload()));
                processReportMessage(decodedPayload);
                showClientToast("收到订阅消息 :主题" + topic + decodedPayload);
            }
        });
    }

    @Override
    protected void onPause() {
        MqttClientManger.getInstance().onDestroy();
        MqttServerManger.getInstance().onDestroy();
        super.onPause();
    }

    private class PublisherListener extends AbstractInterceptHandler {

        @Override
        public void onPublish(InterceptPublishMessage message) {
            final String decodedPayload = new String(EncodeUtils.base64Decode(message.getPayload().array()));
            showServerToast("接收到的消息 : 主题" + message.getTopicName() + " 内容: " + decodedPayload);
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

    public void processReportMessage(String message) {
        MqttMessageModel mqttMessageModel = GsonUtils.fromJson(message, MqttMessageModel.class);
        switch (mqttMessageModel.getCmd()) {
            case "3801":
                MqttMessageModel emitModel = new MqttMessageModel();
                emitModel.setCmd("4801");
                emitModel.setSer_id(mqttMessageModel.getSer_id());
                MqttClientManger.getInstance().sendMessage(emitModel.toString());
                break;
        }
    }


    public void clientSendMessage(View v) {
        MqttMessageModel messageModel = new MqttMessageModel();
        messageModel.setCmd("3815");
        messageModel.setSer_id("0011524284317");

        MqttExtendModel mqttExtendModel = new MqttExtendModel();
        mqttExtendModel.setInput(new InputModel(Config.AP_MAC, "60 00 00 00 00 0b"));
        messageModel.setExtend(mqttExtendModel);
        MqttClientManger.getInstance().sendMessage(messageModel.toString());

//        Bitmap bitmap = ImageUtils.getBitmap(PathManager.getInstance().getWebDir() + "/a.jpg");
//        Bitmap bitmap1 = BitmapUtils.convertToBlackWhite(bitmap);
//        Bitmap bitmap2 = BitmapUtils.convertGreyImgByFloyd(bitmap);
//        ImageUtils.save(bitmap2, PathManager.getInstance().getWebDir() + "/c.jpg", Bitmap.CompressFormat.JPEG);
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
