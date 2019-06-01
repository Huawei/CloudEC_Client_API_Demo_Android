package com.huawei.opensdk.ec_sdk_demo.module.headphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

//import com.huawei.common.library.asyncimage.ImageWorker;
//import com.huawei.ecs.mtk.log.Logger;
//import com.huawei.log.TagInfo;
//import com.huawei.utils.img.BitmapUtil;

import java.io.File;
import java.util.concurrent.RejectedExecutionException;

/**
 * This abstract class is about get avatar
 * 供头像获取使用
 */
//public abstract class HeadFetcher extends ImageWorker
public abstract class HeadFetcher
{
    protected final Bitmap outlineBitmap;

    protected File sysFile = null;

    protected HeadFetcher(Context context, int defaultRes)
    {
//        super(context);

        // 获取存放文件的根目录
        sysFile = context.getFilesDir();
//        Logger.debug(TagInfo.APPTAG, "" + sysFile);

        //设置正在加载时显示的头像
        outlineBitmap = HeadCache.getIns().getRoundCornerBgSmall();

        //设置默认头像
        setDefaultHead(defaultRes);

//        setImageFadeIn(false);
//        setForHeadShow(true);

//        setImageCache(HeadCache.getIns().getHeadCache());
    }


    private void setDefaultHead(int res)
    {
        Bitmap bitmap = HeadCache.getIns().getDefaultBitmap(String.valueOf(res));
        if (bitmap == null)
        {
//            bitmap = BitmapFactory.decodeResource(mContext.getResources(), res);
//            bitmap = BitmapUtil.getRoundCornerBitmap(bitmap, outlineBitmap);
            HeadCache.getIns().setDefaultBitmap(String.valueOf(res), bitmap);
        }
//        setLoadingImage(bitmap);
    }

    protected void execute(AsyncTask<Object, Void, Object> task, Object data)
    {
        try
        {
            // 有一定耗时操作
            task.execute(data);
        }
        catch (RejectedExecutionException e)
        {
//            Logger.warn(TagInfo.TAG, e);
        }
    }



}
