package com.huawei.opensdk.ec_sdk_demo.module.headphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;

//import com.huawei.contacts.MyOtherInfo;
//import com.huawei.utils.img.BitmapUtil;

import java.io.File;

/**
 * This class is about load contact local avatar
 * 联系人本地头像加载
 */
public class ContactHeadLocalLoader
{
    private Context mContext;

    private Bitmap outlineBitmap;

    private File sysFile;

    public ContactHeadLocalLoader(Context context, Bitmap outlineBitmap, File sysFile)
    {
        this.mContext = context;
        this.outlineBitmap = outlineBitmap;
        this.sysFile = sysFile;
    }

    public BitmapDrawable load(HeadPhoto headPhoto)
    {
        //账号为空，直接返回。
        String account = headPhoto.getAccount();
        if (TextUtils.isEmpty(account))
        {
            return null;
        }
        Bitmap bitmap = getBitmap(account, headPhoto.getId());
        if (bitmap == null)
        {
            return null;
        }

//        bitmap = BitmapUtil.getRoundCornerBitmap(bitmap, outlineBitmap);
        return new BitmapDrawable(mContext.getResources(), bitmap);
        // return new BitmapDrawable(mContext.getResources(), bitmap);
    }

    private Bitmap getBitmapFromFile(String account, String headid)
    {
        File file = getPhotoFile(account, headid);
        if (file.exists())
        {
            HeadPhotoUtil.getIns().addAccount(account, file.getName());
//            return BitmapUtil.decodeBitmapFromFile(file.getAbsolutePath(),
//                    MyOtherInfo.PICTURE_DEFAULT_WIDTH, MyOtherInfo.PICTURE_DEFAULT_WIDTH);
        }

        return null;
    }

    /**
     * 获取头像bitmap
     * @param account
     * @param headid
     * @return
     */
    private Bitmap getBitmap(String account, String headid)
    {
        if (TextUtils.isEmpty(headid))
        {
            return readUnknownHeadPhoto(account);
        }

        Bitmap bitmap = HeadPhotoUtil.getDefaultHeadImg(headid);
        if (bitmap != null)
        {
            //添加到缓存后，需要刷新。
            HeadPhotoUtil.getIns().addAccount(account, headid);
            return bitmap;
        }

        return getBitmapFromFile(account, headid);
    }


    private File getPhotoFile(String account, String headId)
    {
        //根据账号和头像id获取头像
        String fileName = HeadPhotoUtil.createHeadFileName(account, headId);
        return new File(sysFile, fileName);
    }



    /**
     * 根据eSpaceNum读取陌生人头像
     * @param eSpaceNum
     * @return
     */
    protected Bitmap readUnknownHeadPhoto(String eSpaceNum)
    {
        Bitmap bitmap = null;
        File[] files = sysFile.listFiles(new HeadNameFilter(eSpaceNum));
        if (files != null && files.length > 0)
        {
            File file = files[0];
            HeadPhotoUtil.getIns().addAccount(eSpaceNum, file.getName());
//            bitmap = BitmapUtil.decodeBitmapFromFile(file.getAbsolutePath(),
//                    MyOtherInfo.PICTURE_DEFAULT_WIDTH, MyOtherInfo.PICTURE_DEFAULT_WIDTH);
        }

        return bitmap;
    }
}
