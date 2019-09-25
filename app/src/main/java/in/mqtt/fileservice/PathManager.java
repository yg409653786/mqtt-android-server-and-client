package in.mqtt.fileservice;

import android.os.Environment;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SDCardUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;

public class PathManager {
    private static PathManager sInstance;

    public static PathManager getInstance() {
        if (sInstance == null) {
            synchronized (PathManager.class) {
                if (sInstance == null) {
                    sInstance = new PathManager();
                }
            }
        }
        return sInstance;
    }

    private File mRootDir;

    private PathManager() {
        if (SDCardUtils.isSDCardEnableByEnvironment()) {
            mRootDir = Environment.getExternalStorageDirectory();
        } else {
            mRootDir = Utils.getApp().getFilesDir();
        }
        mRootDir = new File(mRootDir, "AndServer");
        FileUtils.createOrExistsDir(mRootDir);
    }

    public String getWebDir() {
        return mRootDir.getAbsolutePath();
    }
}
