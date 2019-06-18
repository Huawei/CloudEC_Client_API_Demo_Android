package com.huawei.opensdk.ec_sdk_demo.module.headphoto;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ContactTools
{
    private static final ContactTools mInstance = new ContactTools();
    public static final String HEAD_PHOTO_PATH = Environment.getExternalStorageDirectory().getPath() + "/ECSDKDemo/avatarPath/";

    private ContactTools()
    {
    }

    public static ContactTools getInstance()
    {
        return mInstance;
    }

    public void getBitmap(Bitmap bitmap, int size, String filename)
    {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
        saveBitmap(newBitmap, filename);
    }

    /**
     * Save bitmap.
     *
     * @param mBitmap the m bitmap
     * @param bitName the bit name
     */
    private void saveBitmap(Bitmap mBitmap, String bitName)
    {
        File f = new File(HEAD_PHOTO_PATH + bitName + ".jpeg");
        FileOutputStream fileOutputStream = null;
        try
        {
            fileOutputStream = new FileOutputStream(f);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        try
        {
            fileOutputStream.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            fileOutputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
