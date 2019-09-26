package in.mqtt.model;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.GsonUtils;

import java.io.Serializable;

import in.mqtt.Config;
import in.mqtt.fileservice.PathManager;

public class InputModel implements Serializable {

    private String cmd;
    private String ap_mac;//
    private int work_mode;//
    private String picture_path;
    private String coord;
    private String pricetag_mac;
    private long start;
    private long end;
    private long now;
    private boolean light;
    private int freq = 3;

    public InputModel(String ap_mac, String pricetag_mac) {
        this.ap_mac = ap_mac;
        this.pricetag_mac = pricetag_mac;
        this.cmd = "PictureUpdate";
        this.work_mode = 1;
        this.picture_path = "http://" + Config.MQTT_IP + ":" + Config.MQTT_FILE_PORT + "/";
        this.coord = "ff";
        this.start = 0;
        this.end = 0;
        this.now = 0;
        this.light = true;
        this.freq = 3;

    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getAp_mac() {
        return ap_mac;
    }

    public void setAp_mac(String ap_mac) {
        this.ap_mac = ap_mac;
    }

    public int getWork_mode() {
        return work_mode;
    }

    public void setWork_mode(int work_mode) {
        this.work_mode = work_mode;
    }

    public String getPicture_path() {
        return picture_path;
    }

    public void setPicture_path(String picture_path) {
        this.picture_path = picture_path;
    }

    public String getCoord() {
        return coord;
    }

    public void setCoord(String coord) {
        this.coord = coord;
    }

    public String getPricetag_mac() {
        return pricetag_mac;
    }

    public void setPricetag_mac(String pricetag_mac) {
        this.pricetag_mac = pricetag_mac;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public boolean isLight() {
        return light;
    }

    public void setLight(boolean light) {
        this.light = light;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    @NonNull
    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
