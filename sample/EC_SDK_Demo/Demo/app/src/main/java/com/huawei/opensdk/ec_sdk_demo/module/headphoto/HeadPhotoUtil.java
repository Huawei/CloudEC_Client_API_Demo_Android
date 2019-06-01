package com.huawei.opensdk.ec_sdk_demo.module.headphoto;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

//import com.huawei.common.res.LocContext;
//import com.huawei.contacts.ContactLogic;
//import com.huawei.contacts.PersonalContact;
//import com.huawei.ecs.mtk.log.Logger;
//import com.huawei.log.TagInfo;
//import com.huawei.msghandler.maabusiness.GetHeadImageRequest;
import com.huawei.opensdk.ec_sdk_demo.R;
//import com.huawei.utils.StringUtil;
//import com.huawei.utils.img.BitmapUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is about single case mode is used to provide the Avatar interface for all modules. and provide avatar cache,
 * so that all the same person's head in different places of display, will not cause the avatar cache to become larger
 * 采用单例模式，给所有模块提供获取头像接口。并提供头像缓存，使对所有同一个人的头像在不
 * 同地方的显示，不会导致头像缓存变大。
 */
public final class HeadPhotoUtil
{
    public static final String SUFFIX = ".jpg";

    private static final int[] DEF_HEADS = {R.drawable.head0, R.drawable.head1, R.drawable.head2,
            R.drawable.head3, R.drawable.head4, R.drawable.head5, R.drawable.head6,
            R.drawable.head7, R.drawable.head8, R.drawable.head9};

    /**
     * 分隔符
     */
    public static final String SEPARATOR = "_";

    private static HeadPhotoUtil instance = new HeadPhotoUtil();

    /** 存储已下载的联系账号，用于过滤 */
    private final Map<String, String> accounts = new HashMap<String, String>();


    private HeadPhotoUtil()
    {
        // 1、初始化头像保存目录
        // 2、缓存默认头像Bitmap
    }

    public static HeadPhotoUtil getIns()
    {
        return instance;
    }


//    public static void loadBgHeadPhoto(PersonalContact pContact, ImageView imageView)
//    {
//        if (null == pContact)
//        {
//            //联系人为空,设置为默认头像
//            imageView.setImageResource(R.drawable.default_head);
//            return;
//        }
//
//        String eSpaceNumber = pContact.getEspaceNumber();
//        if (TextUtils.isEmpty(eSpaceNumber))
//        {
//            //联系人账号为空,设置为自定义头像.
//            imageView.setImageResource(R.drawable.default_head_local);
//            return;
//        }
//
//        String headId = pContact.getHead();
//        if (TextUtils.isEmpty(headId))
//        {
//            imageView.setImageResource(R.drawable.default_head);
//        }
//        else
//        {
//            loadBgHeadPhoto(imageView, eSpaceNumber, headId);
//        }
//    }

    private static void loadBgHeadPhoto(ImageView imageView, String eSpaceNumber, String headId)
    {
        Bitmap defaultHeadBitmap = HeadPhotoUtil.getDefaultHeadImg(headId);
        if (defaultHeadBitmap == null)
        {
            imageView.setImageResource(R.drawable.default_head);

            String fileName = HeadPhotoUtil.createHeadFileName(eSpaceNumber, headId);
//            File file = new File(LocContext.getFilesDir(), fileName);
//            if (!file.exists())
//            {
//                Logger.warn(TagInfo.APPTAG, fileName + "file not exit!");
//            }

//            int sideLength = ContactLogic.getIns().getMyOtherInfo().getPictureSideLength();
//            imageView.setImageBitmap(BitmapUtil.decodeBitmapFromFile(file.getAbsolutePath(), sideLength, sideLength));
        }
        else
        {
            imageView.setImageBitmap(defaultHeadBitmap);
        }
    }

//    private void initBigLogoFetch(PublicAccount publicAccount, ImageView imageView)
//    {
//        int length = ContactLogic.getIns().getMyOtherInfo().getPictureHeight();
//        File file = new File(publicAccount.getBigLogoPath());
//        Bitmap bitmapBig = BitmapUtil.decodeBitmapFromFile(file.getAbsolutePath(),
//                length, length);
//
//        String loadingImagePath = publicAccount.getSmallLogoPath();
//        publicBigLogoFetcher = new JsonImageFetcher(EspaceApp.getApp(), false, loadingImagePath);
//
//        // 加载公众号logo大图时，由于未缓存，所以每次都先从硬盘加载一下。
//
//        BitmapDrawable bd ;
//
//        if (bitmapBig != null && imageView != null) {
//            bd = new BitmapDrawable(EspaceApp.getIns().getResources(),bitmapBig);
//            imageView.setImageDrawable(bd);
//            return;
//        }
//
//        if (imageView != null)
//        {
//            publicBigLogoFetcher.loadImage(publicAccount, imageView);
//        }
//    }

    public void addAccount(String account, String fileName)
    {
        if (account == null || fileName == null)
        {
            return;
        }

        synchronized (accounts)
        {
            accounts.put(account, fileName);
        }
    }

    public String getFileName(String account)
    {
        if (account == null)
        {
            return null;
        }

        synchronized (accounts)
        {
            return accounts.get(account);
        }
    }

    /**
     *清空图片缓存
     */
    public void cleanPhotos()
    {
        HeadCache.getIns().cleanCache();

        synchronized (accounts)
        {
            accounts.clear();
        }
    }

    /**
     * 删除所有.png图片
     */
    public void deletePhotoDir()
    {
//        deletePhoto(LocContext.getContext(), SUFFIX);
    }

    /**
     * 删除用户的所有头像
     * @author hute
     * @param filter 过滤字符串
     * @return [说明]
     */
    public static void deletePhoto(Context context, String filter)
    {
        File sysFile = context.getFilesDir();
        File[] files = sysFile.listFiles(new HeadNameFilter(filter));
        if (files != null && files.length > 0)
        {
//            Logger.debug(TagInfo.APPTAG, "/length=" + files.length);
            for (File sFile : files)
            {
                deleteFile(sFile);
            }
        }
    }

    private static void deleteFile(File sFile)
    {
        if (sFile.isFile())
        {
            if(!sFile.delete())
            {
//                Logger.debug(TagInfo.APPTAG, "Delete photo " +
//                        "file fail, File is " + sFile.getPath());
            }
        }
    }

    /**
     * 获取默认头像，默认头像id为0~9
     * @param headid 为0时采用统一默认头像。
     * @return
     */
    public static Bitmap getDefaultHeadImg(String headid)
    {
//        int head = StringUtil.stringToInt(headid);
//        if (head == GetHeadImageRequest.DEFAULT_HEAD_ID_LITTLE)
//        {
//            int mResource = R.drawable.default_head;
//            Resources r = LocContext.getResources();
//            return BitmapFactory.decodeResource(r, mResource);
//        }
//
//        if (head > GetHeadImageRequest.DEFAULT_HEAD_ID_LITTLE
//                && head <= GetHeadImageRequest.DEFAULT_HEAD_ID_LARGE)
//        {
//            int drawable = DEF_HEADS[head];
//            Resources r = LocContext.getResources();
//            return BitmapFactory.decodeResource(r, drawable);
//        }

        return null;
    }

    /**
     * 对应parse函数
     * @param account
     * @param id
     * @return
     */
    public static String createHeadFileName(String account, String id)
    {
        return account + SEPARATOR + id + SUFFIX;
    }

    /**
     * @param account
     * @param fileName
     * @return
     */
    public static String parseHeadId(String account, String fileName)
    {
        if (fileName == null || account == null)
        {
            return "";
        }

        String headId = fileName.replace(account + SEPARATOR, "");
        return headId.replace(SUFFIX, "");
    }
}
