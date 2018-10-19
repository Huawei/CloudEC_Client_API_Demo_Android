package com.huawei.opensdk.ec_sdk_demo.module.headphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;

import com.huawei.contacts.MyOtherInfo;
import com.huawei.data.ViewHeadPhotoData;
import com.huawei.data.ViewHeadPhotoParam;
import com.huawei.ecs.mtk.log.Logger;
import com.huawei.log.TagInfo;
import com.huawei.msghandler.maabusiness.GetHeadImageRequest;
import com.huawei.utils.FileUtil;
import com.huawei.utils.img.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about contact avatar
 * 联系人头像请求。
 */
public class ContactHeadDownloader
{
    private final Context mContext;

    private final Bitmap outlineBitmap;

    public ContactHeadDownloader(Context context, Bitmap outlineBitmap)
    {
        this.mContext =context;
        this.outlineBitmap = outlineBitmap;
    }

    /**
     * Load a picture from the server
     * 从服务器加载图片
     *
     * @param headPhoto
     * @param sideLength
     * @return
     */
    protected BitmapDrawable loadBitmapFromServer(HeadPhoto headPhoto,
            int sideLength, ServerPhotoLoadedListener listener)
    {
        String account = headPhoto.getAccount();
        String id = headPhoto.getId();
        if (isInValidParam(account, id))
        {
            return null;
        }

        Bitmap bitmap = requestBitmap(account, id, sideLength);
        if (bitmap == null)
        {
            return null;
        }

        //通知更新
        if (listener != null)
        {
            listener.onLoadSuccess();
        }

        return new BitmapDrawable(mContext.getResources(),
                BitmapUtil.getRoundCornerBitmap(bitmap, outlineBitmap));
    }

    /**
     * 参数是否有效
     * @param account
     * @param id
     * @return
     */
    private boolean isInValidParam(String account, String id)
    {
        return TextUtils.isEmpty(account) || TextUtils.isEmpty(id);
    }

    protected Bitmap doRequest(List<ViewHeadPhotoParam> list)
    {
        GetHeadImageRequest request = new GetHeadImageRequest();
        request.setWaitTime(15000);  //等待15秒
        List<ViewHeadPhotoData> dataList = request.requestPhoto(list);
        if (dataList == null)
        {
            return null;
        }

        return saveHeadPhoto(dataList, list);
    }

    /**
     * 请求图片数据
     * @param account 联系人账号
     * @param headId 联系人头像id
     * @param sideLength 联系人请求大小（边长）
     * @return
     */
    private Bitmap requestBitmap(String account, String headId, int sideLength)
    {
        Logger.debug(TagInfo.APPTAG, "account=" + account + "/id=" + headId);
        List<ViewHeadPhotoParam> list = getParam(account, headId, sideLength);

        return doRequest(list);
    }

    private List<ViewHeadPhotoParam> getParam(String account, String id, int sideLength)
    {
        List<ViewHeadPhotoParam> list = new ArrayList<ViewHeadPhotoParam>();
        ViewHeadPhotoParam param = new ViewHeadPhotoParam();
        param.setJid(account);
        param.setHeadId(id);
        param.setH(getSideLength(sideLength));
        param.setW(getSideLength(sideLength));
        list.add(param);

        return list;
    }

    /**
     * 获取头像的边长。
     * @param sideLength
     * @return
     */
    private String getSideLength(int sideLength)
    {
        return (sideLength < 0 ? MyOtherInfo.PICTURE_DEFAULT_HEIGHT : sideLength) + "";
    }


    /**
     * 收到响应时保存头像
     * @param photoDatas
     * @param headPhoto
     * @return resp 为null,直接返回.
     */
    public Bitmap saveHeadPhoto(List<ViewHeadPhotoData> photoDatas,
            List<ViewHeadPhotoParam> headPhoto)
    {
        if (!isInValidParam(photoDatas, headPhoto))
        {
            return null;
        }

        ViewHeadPhotoData photoData = photoDatas.get(0);
        ViewHeadPhotoParam mHeadPhoto = headPhoto.get(0);
        return saveBytes(photoData, mHeadPhoto);
    }

    private boolean isInValidParam(List<ViewHeadPhotoData> photoDatas, List<ViewHeadPhotoParam> headPhoto)
    {
        return headPhoto != null && headPhoto.size() == 1 && photoDatas.size() == 1;
    }

    private Bitmap saveBytes(ViewHeadPhotoData photoData, ViewHeadPhotoParam mHeadPhoto)
    {
        String account = photoData.getEspaceNumber();
        if (TextUtils.isEmpty(account))
        {
            Logger.debug(TagInfo.APPTAG, "eSpaceNumber = null or \"\"");
            return null;
        }

        //删除上次使用的头像
        HeadPhotoUtil.deletePhoto(mContext, account);

        byte[] data = photoData.getData();
        String fileName = save(account, mHeadPhoto.getHeadId(), data);
        HeadPhotoUtil.getIns().addAccount(account, fileName);

        //存入内存
        return BitmapUtil.decodeByteArray(data, MyOtherInfo.PICTURE_DEFAULT_HEIGHT);
    }

    private String save(String account, String headId, byte[] data)
    {
        if (data == null)
        {
            Logger.debug(TagInfo.APPTAG, "headId = null");
            return null;
        }

        String fileName = HeadPhotoUtil.createHeadFileName(account, headId);
        FileUtil.saveBytes(mContext, fileName, data, true);

        return fileName;
    }


    public interface ServerPhotoLoadedListener
    {
        void onLoadSuccess();
    }
}
