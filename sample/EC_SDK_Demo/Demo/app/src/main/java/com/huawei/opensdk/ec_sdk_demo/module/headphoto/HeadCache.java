package com.huawei.opensdk.ec_sdk_demo.module.headphoto;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.huawei.common.library.asyncimage.ImageCache;
import com.huawei.common.res.LocContext;
import com.huawei.ecs.mtk.log.Logger;
import com.huawei.log.TagInfo;
import com.huawei.opensdk.ec_sdk_demo.R;


public final class HeadCache
{

    private static final int MAX_SIZE = 20;

    private ImageCache imageCache;

    private ImageCache localCache;

    private ImageCache publicCache;

    private Bitmap circleBgBig = null;
    private Bitmap roundCornerBgBig = null;
    private Bitmap roundCornerBgSmall = null;

    private LruCache<String, Bitmap> bitmapMap;

    private static HeadCache instance = new HeadCache();

    public static HeadCache getIns()
    {
        return instance;
    }

    /**
     *  1、初始化头像保存目录
     *  2、缓存默认头像Bitmap
     *  注意 : 比较
     */
    private HeadCache()
    {
        Logger.debug(TagInfo.APPTAG, "init");

        imageCache = new ImageCache();

        localCache = new ImageCache();

        bitmapMap = new LruCache<String, Bitmap>(MAX_SIZE);
    }

    public void setDefaultBitmap(String key, Bitmap defaultBitmap)
    {
        bitmapMap.put(key, defaultBitmap);
    }

    public Bitmap getDefaultBitmap(String key)
    {
        return bitmapMap.get(key);
    }

    public Bitmap getCircleBgBig()
    {
        if (circleBgBig == null)
        {
            int resId = R.drawable.bg_call_head;
            Resources resources = LocContext.getResources();
            circleBgBig = BitmapFactory.decodeResource(resources, resId);
        }

        return circleBgBig;
    }

    public Bitmap getRoundCornerBgBig()
    {
        if (roundCornerBgBig == null)
        {
            int resId = R.drawable.head_bg_big;
            Resources resources = LocContext.getResources();
            roundCornerBgBig = BitmapFactory.decodeResource(resources, resId);
        }

        return roundCornerBgBig;
    }

    public Bitmap getRoundCornerBgSmall()
    {
        if (roundCornerBgSmall == null)
        {
            int resId = R.drawable.head_bg;
            Resources resources = LocContext.getResources();
            roundCornerBgSmall = BitmapFactory.decodeResource(resources, resId);
        }

        return roundCornerBgSmall;
    }

    /**
     *清空图片缓存
     */
    public void cleanCache()
    {
        imageCache.clearCaches();
        localCache.clearCaches();
        if (publicCache != null)
        {
            publicCache.clearCaches();
        }
        bitmapMap.evictAll();
    }

    public ImageCache getHeadCache()
    {
        return imageCache;
    }

    public ImageCache getPublicHeadCache()
    {
        if (publicCache == null)
        {
            publicCache = new ImageCache();
        }

        return publicCache;
    }

    public ImageCache getLocalHeadCache()
    {
        return localCache;
    }
}
