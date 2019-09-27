package in.mqtt.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.blankj.utilcode.util.ImageUtils;

public class BitmapUtils {

    public static Bitmap generateBitmap(String content, boolean select) {
        Bitmap bitmap = generateBitmap(select ? Color.RED : Color.WHITE, 296, 152);
        String[] arrStr = content.split("\n");
        for (int i = 0; i < arrStr.length; i++) {
            bitmap = ImageUtils.addTextWatermark(bitmap, arrStr[i], 20, select ? Color.WHITE : Color.BLACK, 10, 10 + i * (27));
        }
        return bitmap;
    }

    private static Bitmap generateBitmap(int color, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setColor(color);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(new Rect(0, 0, width, height), paint);
        return bitmap;
    }

    public static byte[] changeSingleBytes(Bitmap bmp) {
        bmp = ImageUtils.rotate(bmp, 90, 0.5f, 0.5f);
        int[] binarys = gray2Binary(bmp);
        return compressMonoBitmap(bmp, binarys);
    }


    private static byte[] compressMonoBitmap(Bitmap bmp, int[] binarys) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        byte[] newss = new byte[width * height / 8];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (binarys[width * i + j] > 0)  // 第几行第几个字节
                    newss[(width * i + j) / 8] |= (byte) (1 << (7 - j % 8));  // 新压缩的第几行第几个
            }
        }
        return newss;
    }

    private static int[] gray2Binary(Bitmap bmp) {
        int width = bmp.getWidth();   // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height];  // 通过位图的大小创建像素点数组,
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);   // int 0 代表0XFFFFFFFF,即是1.0完全不透明，0.0f完全透明。黑色完全透明。
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];  // 第几行，第几个
                // 分离三原色
                int alpha = ((grey & 0xFF000000) >> 24); // 透明度
                int red = ((grey & 0x00FF0000) >> 16);   // 红色
                int green = ((grey & 0x0000FF00) >> 8);  // 绿色
                int blue = (grey & 0x000000FF);          // 蓝色
                if (alpha == 0) {  // 透明度为0，则说明没有颜色，那变更为白色
                    pixels[width * i + j] = 0;           // 白色是0
                    continue;
                }
                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);  // 转化为灰度图  灰度值：255为白色，0为黑色
                // TODO: 2016/12/27 灰度值为200，可调整该参数
                grey = grey < 200 ? 1 : 0;  // 灰度小于200就转化为黑色，不然就为白色。200为可调整参数。// 二值化
                pixels[width * i + j] = grey;
            }
        }
        return pixels;
    }
}
