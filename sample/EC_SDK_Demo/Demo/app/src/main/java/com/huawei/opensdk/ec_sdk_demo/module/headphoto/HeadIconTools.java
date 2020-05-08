package com.huawei.opensdk.ec_sdk_demo.module.headphoto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.ActivityStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is about Head icon tools.
 */
public class HeadIconTools
{
    private static final String TAG = HeadIconTools.class.getSimpleName();

    private static HeadIconTools instance = new HeadIconTools();
    /**
     * The constant LARGE_PICTURE_DEFAULT_NAME.
     */
    public static final String LARGE_PICTURE_DEFAULT_NAME = "head1";
    /**
     * The constant MEDIUM_PICTURE_DEFAULT_NAME.
     */
//    public static final String MEDIUM_PICTURE_DEFAULT_NAME = "head2";
    /**
     * The constant SMALL_PICTURE_DEFAULT_NAME.
     */
//    public static final String SMALL_PICTURE_DEFAULT_NAME = "head3";
    /**
     * The constant LARGE_PICTURE_DEFAULT_SIZE.
     */
    public static final int LARGE_PICTURE_DEFAULT_SIZE = 360;
    /**
     * The constant MEDIUM_PICTURE_DEFAULT_SIZE.
     */
    public static final int MEDIUM_PICTURE_DEFAULT_SIZE = 120;
    /**
     * The constant SMALL_PICTURE_DEFAULT_SIZE.
     */
    public static final int SMALL_PICTURE_DEFAULT_SIZE = 60;

    /**
     * The constant SELECT_PICTURE_FROM_LOCAL.
     */
    public static final int SELECT_PICTURE_FROM_LOCAL = 0;
    /**
     * The constant SELECT_PICTURE_FROM_CAMERA.
     */
    public static final int SELECT_PICTURE_FROM_CAMERA = 1;
    /**
     * The constant SELECT_VIDEO_FROM_LOCAL.
     */
    public static final int SELECT_VIDEO_FROM_LOCAL = 2;
    private static Uri photoUri;
    private static File picFile;
    private static Activity mContext;
    /**
     * The System head icons.
     */
    protected static int[] systemHeadIcons = {R.drawable.head0, R.drawable.head1, R.drawable.head2, R.drawable.head3, R.drawable.head4, R.drawable.head5,
            R.drawable.head6, R.drawable.head7, R.drawable.head8, R.drawable.head9, R.drawable.default_head};
    private static final String ECSDKDemo = "/ECSDKDemo";
    private static final String AVATAR_PATH = "/avatarPath";

    private Map<String, Bitmap> headImageMap = new ConcurrentHashMap<>();

    /**
     * Gets instance.
     * @return the instance
     */
    public static HeadIconTools getInstance()
    {
        return instance;
    }

    /**
     * Gets bitmap by path.
     * @param iconPath the icon path
     * @return the bitmap by path
     */
    public static Bitmap getBitmapByPath(String iconPath)
    {
        Bitmap bitmap = null;
        if (isIconExists(iconPath))
        {
            try
            {
                FileInputStream inputStream = new FileInputStream(iconPath);
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
            catch (Exception e)
            {
                LogUtil.e(TAG, e.getMessage());
            }
        }
        return bitmap;
    }

    /**
     * Gets bitmap by icon id.
     * @param sysHeadIconId the sys head icon id
     * @return the bitmap by icon id
     */
    public static Bitmap getBitmapByIconId(int sysHeadIconId)
    {
        int id = systemHeadIcons[sysHeadIconId];
        Bitmap sysBitmap = BitmapFactory.decodeResource(LocContext.getContext().getResources(), id);
        return sysBitmap;
    }

    private static boolean isIconExists(String path)
    {
        if (new File(path).exists())
        {
            return true;
        }
        return false;
    }
    //-----------------------------------------get Head Icon ending--------------------------------

    //-----------------------------------------Upload HeadPhoto beginning--------------------------------

    /**
     * Select pic by type.
     * @param type the type
     */
    public static void selectPicByType(int type)
    {
        mContext = ActivityStack.getIns().getCurActivity();
        try
        {
            //sd card root directory
            File pictureFileDir = new File(Environment.getExternalStorageDirectory().getPath() + ECSDKDemo, AVATAR_PATH);
            // Folder does not exist to create
            if (!pictureFileDir.exists())
            {
                pictureFileDir.mkdirs();
            }
            // Store this picture under the SD card upload folder
            picFile = new File(pictureFileDir, LARGE_PICTURE_DEFAULT_NAME + ".jpeg");
            // The file does not exist
            if (!picFile.exists())
            {
                picFile.createNewFile();
            }
            // photoUri Identifies the address of the picture
            photoUri = Uri.fromFile(picFile);

            if (type == SELECT_PICTURE_FROM_LOCAL)
            {
                Intent intent = getCropImageIntent();
                mContext.startActivityForResult(intent, SELECT_PICTURE_FROM_LOCAL);
            }
            else if (type == SELECT_PICTURE_FROM_CAMERA)
            {
                // Implicit camera program
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Photos taken will be entered
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                LogUtil.e(UIConstants.DEMO_TAG, "selectPicByType: " + MediaStore.EXTRA_OUTPUT);
                mContext.startActivityForResult(cameraIntent, SELECT_PICTURE_FROM_CAMERA);
            }
        }
        catch (Exception e)
        {
            LogUtil.i(UIConstants.DEMO_TAG, "Handle Head Icon Error");
        }
    }

    /**
     * Take photo.
     */
    public static void takePhoto()
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        setIntentParams(intent);
        mContext.startActivityForResult(intent, SELECT_PICTURE_FROM_LOCAL);
    }

    /**
     * Gets crop image intent.
     * @return the crop image intent
     */
    public static Intent getCropImageIntent()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        // example  file:///storage/emulated/0/upload/upload.jpeg
        setIntentParams(intent);
        return intent;
    }

    private static void setIntentParams(Intent intent)
    {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", LARGE_PICTURE_DEFAULT_SIZE);
        intent.putExtra("outputY", LARGE_PICTURE_DEFAULT_SIZE);
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    }

    /**
     * Decode uri to bitmap bitmap.
     * @param uri the uri
     * @return the bitmap
     */
    public static Bitmap decodeUriToBitmap(Uri uri)
    {
        Bitmap bitmap = null;
        try
        {
            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * Gets photo uri.
     * @return the photo uri
     */
    public static Uri getPhotoUri()
    {
        if (photoUri != null)
        {
            return photoUri;
        }
        return null;
    }

    //-----------------------------------------Upload headPhoto ending--------------------------------

//    /**
//     * Load head image boolean.
//     * @param contacts the contacts
//     * @param headPhotoData the head photo data
//     * @return the boolean
//     */
//    public synchronized boolean loadHeadImage(List<PersonalContact> contacts, List<ViewHeadPhotoData> headPhotoData)
//    {
//        for (PersonalContact contact : contacts)
//        {
//            if (contact == null)
//            {
//                continue;
//            }
//            Bitmap bitmap = null;
//            String headIdStr = contact.getHead();
//            long headId = -1;
//            if (!TextUtils.isEmpty(headIdStr))
//            {
//                headId = Long.parseLong(headIdStr);
//            }
//            else
//            {
//                headId = 0;
//            }
//            int sysHeadImageSize = systemHeadIcons.length;
//            if (headId >= 0 && headId < sysHeadImageSize)
//            {
//                bitmap = getBitmapByIconId((int) headId);
//            }
//
//            for (ViewHeadPhotoData data : headPhotoData)
//            {
//                if (contact.getEspaceNumber().equals(data.getEspaceNumber()))
//                {
//                    bitmap = getBitmapFromHeadPhotoData(contact.getEspaceNumber(), headPhotoData);
//                }
//            }
//            if (bitmap != null)
//            {
//                headImageMap.put(contact.getEspaceNumber(), bitmap);
//            }
//        }
//        return true;
//    }

//    private Bitmap getBitmapFromHeadPhotoData(String account, List<ViewHeadPhotoData> headPhotoData)
//    {
//        Bitmap bitmap = null;
//        for (ViewHeadPhotoData data : headPhotoData)
//        {
//            if (data.getEspaceNumber().equals(account))
//            {
//                bitmap = bytes2Bitmap(data.getData());
//            }
//        }
//        return bitmap;
//    }

    private Bitmap bytes2Bitmap(byte[] b)
    {
        Bitmap bitmap = null;
        try
        {
            if (b.length != 0)
            {
                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Gets head image.
     * @param account the account
     * @return the head image
     */
    public synchronized Bitmap getHeadImage(String account)
    {
        return headImageMap.get(account);
    }
}
