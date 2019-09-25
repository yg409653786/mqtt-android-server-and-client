package in.mqtt.fileservice;

import com.yanzhenjie.andserver.annotation.Website;
import com.yanzhenjie.andserver.framework.website.StorageWebsite;


@Website
public class InternalWebsite extends StorageWebsite {


    public InternalWebsite() {
        super(PathManager.getInstance().getWebDir());
    }
}
