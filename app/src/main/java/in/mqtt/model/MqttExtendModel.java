package in.mqtt.model;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.GsonUtils;

import java.io.Serializable;

public class MqttExtendModel implements Serializable {
    private String cmd;
    private String cmd_id;

    public MqttExtendModel() {
        this.cmd = "2000";
        this.cmd_id = "3345";
    }

    private InputModel input;

    public InputModel getInput() {
        return input;
    }

    public void setInput(InputModel input) {
        this.input = input;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCmd_id() {
        return cmd_id;
    }

    public void setCmd_id(String cmd_id) {
        this.cmd_id = cmd_id;
    }

    @NonNull
    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
