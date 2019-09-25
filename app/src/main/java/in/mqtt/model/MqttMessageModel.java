package in.mqtt.model;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.GsonUtils;

import java.io.Serializable;

public class MqttMessageModel implements Serializable {

    private MqttExtendModel extend;//存放指令的拓展数据
    private String msg;//存放指令的字符串数据
    private String cmd;//存放指令值
    private String ser_id;//存放消息的唯一 id，用于唯一标记一个消息，单次会话回传该字段


    public MqttExtendModel getExtend() {
        return extend;
    }

    public void setExtend(MqttExtendModel extend) {
        this.extend = extend;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getSer_id() {
        return ser_id;
    }

    public void setSer_id(String ser_id) {
        this.ser_id = ser_id;
    }

    @NonNull
    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
