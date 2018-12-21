package yc.pointer.trip.untils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.transition.Transition;
import android.util.Base64;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import yc.pointer.trip.network.URLUtils;

/**
 * Created by Administrator on 2016/12/16.
 * 图片压缩、缩放等工具类
 */

public class ImageUtils {

    private ImageUtils() {
        /** cannot be instantiated 不能被实例化*/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 图片转换成byte[]数组
     *
     * @param bitmap bitmap类型的图片
     * @return 返回转换之后的数据
     */
    public static byte[] bitmapToByte(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    /**
     * byte数组转化成bitmap类型的图片
     *
     * @param b byte数组
     * @return 返回转化之后的图片
     */
    public static Bitmap byteToBitamp(byte[] b) {
        return (b == null || b.length == 0) ? null : BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /**
     * 位图绘制
     *
     * @param b
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap b) {
        return b == null ? null : new BitmapDrawable(b);
    }

    /**
     * 绘制位图
     *
     * @param d
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable d) {
        return d == null ? null : ((BitmapDrawable) d).getBitmap();
    }

    /**
     * 缩放图片到多大
     *
     * @param org       图片
     * @param newWidth  宽度
     * @param newHeight 高度
     * @return
     */
    public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
        return scaleImage(org, (float) newWidth / org.getWidth(), (float) newHeight / org.getHeight());
    }

    /**
     * 缩放图片
     *
     * @param org         图片
     * @param scaleWidth  宽度
     * @param scaleHeight 高度
     * @return
     */
    public static Bitmap scaleImage(Bitmap org, float scaleWidth, float scaleHeight) {
        if (org == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(), matrix, true);
    }


    /**
     * 按大小压缩图片
     * @param image 图片
     * @return 返回压缩之后的图片
     */
//    public static Bitmap compressImage(Bitmap image) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//        int options = 100;
//        while (baos.toByteArray().length / 1024 > 400) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
//            baos.reset();//重置baos即清空baos
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//            options -= 10;//每次都减少10
//        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
//        return bitmap;
//    }

    /**
     * 图片按比例大小压缩方法
     *
     * @param path （根据路径获取图片并压缩）
     * @return
     */


    /**
     * 压缩图片
     *
     * @param fileUri
     * @return
     */
    public static File scal(Uri fileUri) {
        String path = fileUri.getPath();
        File outputFile = new File(path);
        long fileSize = outputFile.length();
        final long fileMaxSize = 200 * 1024;
        if (fileSize >= fileMaxSize) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int height = options.outHeight;
            int width = options.outWidth;

            double scale = Math.sqrt((float) fileSize / fileMaxSize);
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            outputFile = new File(createImageFile().getPath());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            } else {
                File tempFile = outputFile;
                outputFile = new File(createImageFile().getPath());
                copyFileUsingFileChannels(tempFile, outputFile);
            }

        }
        return outputFile;
    }

    /**
     * 压缩图片
     *
     * @param bitmap
     * @return
     */
    public static String Bitmap2StrByBase64(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bytes = bos.toByteArray();
        String base64Temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        String base64 = base64Temp.replaceAll("\n", "");
        return base64;
    }


    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    // 使用系统当前日期加以调整作为照片的名称
    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    public static Uri createImageFile() {
        // Create an image file name
        SimpleDateFormat t = new SimpleDateFormat("yyyyMMddssSSS");
        String filename = "MT" + (t.format(new Date())) + ".jpg";
        String imageFileName = "JPEG_" + filename;
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "photos");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = null;
        try {
            image = new File(storageDir, imageFileName + ".jpg");
            image.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        return Uri.fromFile(image);
    }

    /**
     * 拍照之后bitmap转成path
     *
     * @param bitmap bitmap
     * @return path
     */
    public static String BitmapToPath(Bitmap bitmap) {
        // 获取 SD 卡根目录
        String saveDir = Environment.getExternalStorageDirectory() + "/photos";
        File file = createFile(saveDir);
        // 打开文件输出流
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            // 生成图片文件
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            // 相片的完整路径
            return file.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 草稿之后bitmap转成path
     *
     * @param bitmap bitmap
     * @return path
     */
    public static String BitmapPath(Bitmap bitmap) {
        // 获取 SD 卡根目录
        String saveDir = Environment.getExternalStorageDirectory() + "/trip/" + System.currentTimeMillis() + ".png";
        File file = new File(saveDir);
        // 打开文件输出流
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            // 生成图片文件
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            // 相片的完整路径
            return file.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 新建目录
     *
     * @param saveDir 保存路径
     * @return 返回的文件
     */
    public static File createFile(String saveDir) {
        // 新建目录
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // 生成文件名
        SimpleDateFormat t = new SimpleDateFormat("yyyyMMddssSSS");
        String filename = "MT" + (t.format(new Date())) + ".jpg";
        // 新建文件
        File file = new File(saveDir, filename);
        return file;
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath, int newWidth, int newHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        Bitmap newBitmap = compressImage(bitmap, 500);
        if (bitmap != null) {
            bitmap.recycle();
        }
        return newBitmap;
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * 质量压缩
     *
     * @param image
     * @param maxSize
     */
    public static Bitmap compressImage(Bitmap image, int maxSize) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 80;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while (os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
            os.reset();
            // interval 10
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
        }

        Bitmap bitmap = null;
        byte[] b = os.toByteArray();
        if (b.length != 0) {
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return bitmap;
    }

    /**
     * 将URi转化成Bitmap
     *
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {

        // 读取uri所在的图片
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    //public static Bitmap getBitMBitmap(String urlpath) {
    //    Bitmap map = null;
    //    try {
    //        URL url = new URL(urlpath);
    //        URLConnection conn = url.openConnection();
    //        conn.connect();
    //        InputStream in= conn.getInputStream();
    //        map = BitmapFactory.decodeStream(in);
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    }
    //    return map;
    //}


    public static void getBitMBitmap(String urlpath, Activity activity, SimpleTarget<Bitmap> simpleTarget) {
        if (urlpath.contains("http")) {
            Glide.with(activity).load(urlpath).asBitmap().into(simpleTarget);
        } else {
            Glide.with(activity).load(URLUtils.BASE_URL + urlpath).asBitmap().into(simpleTarget);
        }

    }


    private static void copyFileUsingFileChannels(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } finally {
            try {
                if (inputChannel != null && outputChannel != null) {
                    inputChannel.close();
                    outputChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改小米不能裁剪问题
     *
     * @return
     */
    public static Uri getUri() {
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/image/");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        File img = new File(tmpDir.getAbsolutePath() + "/pic.jpeg");
        if (img.exists()) {
            img.delete();
        }
        return Uri.fromFile(img);
    }
}