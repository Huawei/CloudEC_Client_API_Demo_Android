package com.huawei.opensdk.ec_sdk_demo.module.headphoto;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.media.ThumbnailUtils;

import com.huawei.contacts.MyOtherInfo;
import com.huawei.ecs.mtk.log.Logger;
import com.huawei.log.TagInfo;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.utils.io.Closeables;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;


public class GroupHeadMixture
{
    private final Context context;

    public GroupHeadMixture(Context context)
    {
        this.context = context;
    }

    /**
     * 根据Key 读取Value
     *
     * @param key
     * @return not null
     */
    private String readData(Context mContext, String key, int resId)
    {
        InputStream in = null;
        try
        {
            Resources resources = mContext.getResources();
            in = new BufferedInputStream(resources.openRawResource(resId));
            Properties props = new Properties();
            props.load(in);
            return props.getProperty(key);
        }
        catch (Resources.NotFoundException e)
        {
            Logger.error(TagInfo.APPTAG, e.toString());
            return "";
        }
        catch (IOException e)
        {
            Logger.error(TagInfo.APPTAG, e.toString());
            return "";
        }
        finally
        {
            Closeables.closeCloseable(in);
        }
    }

    public Bitmap getCombineBitmaps(List<Bitmap> bitmaps)
    {
        int count = bitmaps.size();
        List<BitmapProperty> properties = getBitmapProperty(context, count);

        int height = (int) (MyOtherInfo.PICTURE_DEFAULT_HEIGHT * 0.75f);
        // 最终合并在一起的混合图片
        Bitmap mixBitmap = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);
        // 按照尺寸百分比生成的小头像
        Bitmap thumbnailBitmap;
        for (BitmapProperty property : properties)
        {
            Bitmap bitmap = bitmaps.get(properties.indexOf(property));
            // 根据data.properties文件中的百分比，获取长宽和X，Y坐标位置
            int groupHeadWidth = (int) (height * property.width);
            int groupHeadHeight = (int) (height * property.height);
            // 根据长宽的百分比，生成每个成员的小头像，用于合并绘制
            thumbnailBitmap = ThumbnailUtils.extractThumbnail(bitmap, groupHeadWidth, groupHeadHeight);
            PointF pointF = new PointF(height * property.x, height * property.y);
            mixBitmap = mixtureBitmap(mixBitmap, thumbnailBitmap, pointF);
        }

        return mixBitmap;
    }

    /**
     * Mix two Bitmap as one.
     *
     * where the second bitmap is painted.
     * @return
     */
    private Bitmap mixtureBitmap(Bitmap first, Bitmap second, PointF fromPoint)
    {
        if (first == null || second == null || fromPoint == null)
        {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth(),
                first.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        cv.drawColor(context.getResources().getColor(android.R.color.transparent));
        cv.drawBitmap(first, 0, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return newBitmap;
    }

    private List<BitmapProperty> getBitmapProperty(Context context, int count)
    {
        List<BitmapProperty> mList = new LinkedList<BitmapProperty>();
        String value = readData(context, String.valueOf(count), R.raw.data);
        String[] arr1 = value.split(";");
        int length = arr1.length;
        for (int i = 0; i < length; i++)
        {
            String content = arr1[i];
            String[] arr2 = content.split(",");
            BitmapProperty entity = null;
            for (int j = 0; j < arr2.length; j++)
            {
                entity = new BitmapProperty();
                entity.x = Float.valueOf(arr2[0]);
                entity.y = Float.valueOf(arr2[1]);
                entity.width = Float.valueOf(arr2[2]);
                entity.height = Float.valueOf(arr2[3]);
            }
            mList.add(entity);
        }
        return mList;
    }
}
