package com.huawei.opensdk.ec_sdk_demo.logic.im;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

//import com.huawei.data.entity.InstantMessage;
//import com.huawei.data.unifiedmessage.MediaResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is about chat tools.
 */
public class ChatTools
{
    private static final String TAG = ChatTools.class.getSimpleName();
    public static final String APP_PATH = Environment.getExternalStorageDirectory() + File.separator + "ECSDKDemo";
    private static Map<String, Bitmap> videoThumbnailMap = new ConcurrentHashMap<>();

    /**
     * Sets video thumbnail map.
     * @param key the key
     * @param bitmap the bitmap
     */
    public static void setVideoThumbnailMap(String key, Bitmap bitmap)
    {
        if (bitmap != null)
        {
            videoThumbnailMap.put(key, bitmap);
        }
    }

    /**
     * Gets video thumbnail map.
     * @param key the key
     * @return the video thumbnail map
     */
    public static Bitmap getVideoThumbnailMap(String key)
    {
        return videoThumbnailMap.get(key);
    }

//    /**
//     * Copy video file.
//     * @param instantMessage the instant message
//     */
//    public static void copyVideoFile(InstantMessage instantMessage)
//    {
//        if (instantMessage == null || instantMessage.getMediaRes() == null)
//        {
//            Log.e(TAG, "copyVideoFile:instantMessage is null");
//            return;
//        }
//        MediaResource resource = instantMessage.getMediaRes();
//        String originalLocalPath = "";
//        String destinationPath = "";
//        if (resource.getResourceType() == MediaResource.RES_LOCAL)
//        {
//            destinationPath = APP_PATH + File.separator + "Video" + File.separator + resource.getName();
//            File file = new File(destinationPath);
//            if (file.exists())
//            {
//                return;
//            }
//            originalLocalPath = resource.getLocalPath();
//            copyFile(originalLocalPath, destinationPath);
//            Bitmap bitmap = getVideoThumbnail(destinationPath);
//            ChatTools.setVideoThumbnailMap(instantMessage.getMessageId(), bitmap);
//        }
//    }

    private static void copyFile(String oldPath, String newPath)
    {
        try
        {
            int byteSum = 0;
            int byteRead = 0;
            File oldFile = new File(oldPath);
            if (oldFile.exists())
            {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteRead = inStream.read(buffer)) != -1)
                {
                    byteSum += byteRead; //Byte count file size
                    System.out.println(byteSum);
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
                fs.close();
            }
        }
        catch (Exception e)
        {
            System.out.println("copy file failed");
            e.printStackTrace();
        }
    }

    /**
     * Gets video thumbnail.
     * @param videoPath the video path
     * @return the video thumbnail
     */
    public static Bitmap getVideoThumbnail(String videoPath)
    {
        Bitmap bitmap = null;
        try
        {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(videoPath);
            bitmap = media.getFrameAtTime();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }
}
