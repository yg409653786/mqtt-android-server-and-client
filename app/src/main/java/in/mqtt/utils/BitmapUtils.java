package in.mqtt.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {

    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return newBmp;
    }

    //抖动算法来对图像进行二值化处理
    public static Bitmap convertGreyImgByFloyd(Bitmap img) {
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();      //获取位图的高
        int[] pixels = new int[width * height];//通过位图的大小创建像素点数组
        img.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] gray = new int[height * width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                int red = (grey & 0x00FF0000) >> 16;
                gray[width * i + j] = red;
            }
        }

        int e = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int g = gray[width * i + j];
                if (g >= 128) {
                    pixels[width * i + j] = -0x1;
                    e = g - 255;

                } else {
                    pixels[width * i + j] = -0x1000000;
                    e = g - 0;
                }
                if (j < width - 1 && i < height - 1) {
                    //右边像素处理
                    gray[width * i + j + 1] += 3 * e / 8;
                    //下
                    gray[width * (i + 1) + j] += 3 * e / 8;
                    //右下
                    gray[width * (i + 1) + j + 1] += e / 4;
                } else if (j == width - 1 && i < height - 1) {//靠右或靠下边的像素的情况
                    //下方像素处理
                    gray[width * (i + 1) + j] += 3 * e / 8;
                } else if (j < width - 1 && i == height - 1) {
                    //右边像素处理
                    gray[width * i + j + 1] += e / 4;
                }
            }
        }
        Bitmap mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return mBitmap;
    }

    private void bitmap2bmp(Bitmap bitmap, File bmpFile) {
        if (bitmap == null)
            return;
        // 位图大小
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
        // 图像数据大小
        int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);
        try {
            if (!bmpFile.exists()) {
                bmpFile.createNewFile();
            }
            FileOutputStream fileos = new FileOutputStream(bmpFile);
            // bmp文件头
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + bufferSize;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40;
            // 保存bmp文件头
            writeWord(fileos, bfType);
            writeDword(fileos, bfSize);
            writeWord(fileos, bfReserved1);
            writeWord(fileos, bfReserved2);
            writeDword(fileos, bfOffBits);
            // bmp信息头
            long biSize = 40L;
            long biWidth = nBmpWidth;
            long biHeight = nBmpHeight;
            int biPlanes = 1;
            int biBitCount = 24;
            long biCompression = 0L;
            long biSizeImage = 0L;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
            // 保存bmp信息头
            writeDword(fileos, biSize);
            writeLong(fileos, biWidth);
            writeLong(fileos, biHeight);
            writeWord(fileos, biPlanes);
            writeWord(fileos, biBitCount);
            writeDword(fileos, biCompression);
            writeDword(fileos, biSizeImage);
            writeLong(fileos, biXpelsPerMeter);
            writeLong(fileos, biYPelsPerMeter);
            writeDword(fileos, biClrUsed);
            writeDword(fileos, biClrImportant);
            // 像素扫描
            byte bmpData[] = new byte[bufferSize];
            int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
            for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol) {
                for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {
                    int clr = bitmap.getPixel(wRow, nCol);
                    bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);
                }
            }
            fileos.write(bmpData);
            fileos.flush();
            fileos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeWord(FileOutputStream stream, int value) throws IOException {
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        stream.write(b);
    }

    private void writeDword(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    private void writeLong(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }
}
