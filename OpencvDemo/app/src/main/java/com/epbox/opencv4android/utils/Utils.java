package com.epbox.opencv4android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dawn on 2017/12/28.
 */

public class Utils {
    public static boolean usingIsdigit(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    //方法一：用JAVA自带的函数
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断手机号码是不是合格
     *
     * @param phoneNo
     * @return
     */
    public static boolean isPhoneNumber(String phoneNo) {
        if (TextUtils.isEmpty(phoneNo)) {
            return false;
        }
        if (phoneNo.length() == 11) {
            for (int i = 0; i < 11; i++) {
                if (!PhoneNumberUtils.isISODigit(phoneNo.charAt(i))) {
                    return false;
                }
            }
            Pattern p = Pattern.compile("^1[3456789][\\d]{9}$");
            Matcher m = p.matcher(phoneNo);
            return m.matches();
        }
        return false;
    }

    /**
     * 判断身份证号码是否正确
     *
     * @param content
     * @return
     */
    public static boolean validationIDNumber(String content) {
        String IDNumber = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";
        Pattern pattern = Pattern.compile(IDNumber);
        Matcher m = pattern.matcher(content);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean listEmpty(List<String> list) {
        if (null == list || list.size() == 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Compress image by size, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param image
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     * @return
     */
    public static Bitmap zoomImageRatio(Bitmap image, String fileName, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        Bitmap.CompressFormat compressFormat = getCompressFormat(fileName);

        image.compress(compressFormat, 100, os);
        if (os.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();//重置baos即清空baos
            image.compress(compressFormat, 50, os);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
        return compressImage(bitmap, 400); // 这里再进行质量压缩的意义不大，反而耗资源，删除
//        return bitmap;
    }


    private static Bitmap.CompressFormat getCompressFormat(String fileName) {
        if (TextUtils.isEmpty(fileName)) return Bitmap.CompressFormat.JPEG;

        if (!TextUtils.isEmpty(fileName)) {
            if (fileName.toLowerCase().endsWith("jpg") || fileName.toLowerCase().endsWith("jpeg")) {
                return Bitmap.CompressFormat.JPEG;
            } else if (fileName.toLowerCase().endsWith("png")) {
                return Bitmap.CompressFormat.PNG;
            } else if (fileName.toLowerCase().endsWith("webp")) {
                return Bitmap.CompressFormat.WEBP;
            }
        }

        return Bitmap.CompressFormat.JPEG;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int size) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4093];
        int len = in.read(buf);

        while (len != -1) {
            out.write(buf, 0, len);
            len = in.read(buf);
        }
    }

    /**
     * 判断是否是过去的日期
     *
     * @param str
     * @return
     */
    public static boolean isPastDate(String str) {

        boolean flag = false;
        Date nowDate = new Date();
        Date pastDate = null;
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        //在日期字符串非空时执行
        if (str != null && !"".equals(str)) {
            try {
                //将字符串转为日期格式，如果此处字符串为非合法日期就会抛出异常。
                pastDate = sdf.parse(str);
                //调用Date里面的before方法来做判断
                flag = pastDate.before(nowDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static String diffTime(String startTime, String endTime) {
        String format = "yyyy-MM-dd hh:mm:ss";
        //按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);

        long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
        long nh = 1000 * 60 * 60;//一小时的毫秒数
        long nm = 1000 * 60;//一分钟的毫秒数
        long ns = 1000;//一秒钟的毫秒数long diff;try {
        //获得两个时间的毫秒时间差异
        long diff = 0;
        try {
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (diff < 0) {
            return "";
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            long day = diff / nd;//计算差多少天
            if (day > 0) {
                stringBuffer.append(day + ",天");
            } else {
                long hour = diff % nd / nh;//计算差多少小时
                if (hour > 0) {
                    stringBuffer.append(hour + ",小时");
                } else {
                    long min = diff % nd % nh / nm;//计算差多少分钟
                    if (min > 0) {
                        stringBuffer.append(min + ",分钟");
                    } else {
//                        long sec = diff%nd%nh%nm/ns;//计算差多少秒//输出结果
//                        if(min > 0){
//                            stringBuffer.append(sec+"秒");
//                        }
                    }
                }
            }
            return stringBuffer.toString();
        }
    }


    private static final String SD_PATH = "/sdcard/rice/pic/";
    private static final String IN_PATH = "/rice/pic/";

    /**
     * 随机生产文件名
     *
     * @return
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            savePath = SD_PATH;
//        } else {
        savePath = context.getApplicationContext().getFilesDir().getAbsolutePath() + IN_PATH;
//        }

        try {
            filePic = new File(savePath + generateFileName() + ".jpg");

            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }


    /**
     * 文件转base64字符串
     *
     * @param file
     * @return
     */
    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return base64;
    }
    /**
     * money格式化处理
     *
     * @param str
     * @return
     */
    public static String addComma(String str) {
        try {
            DecimalFormat decimalFormat = new DecimalFormat(",###");
            return decimalFormat.format(Double.parseDouble(str)) + ".00";
        } catch (Exception e) {
            return str;
        }
    }

}