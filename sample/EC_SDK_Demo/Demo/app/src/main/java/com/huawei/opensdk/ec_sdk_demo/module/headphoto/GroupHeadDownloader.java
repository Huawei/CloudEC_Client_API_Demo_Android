package com.huawei.opensdk.ec_sdk_demo.module.headphoto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;

//import com.huawei.common.constant.CustomBroadcastConst;
//import com.huawei.contacts.ContactLogic;
//import com.huawei.contacts.MyOtherInfo;
//import com.huawei.contacts.group.ConstGroupManager;
//import com.huawei.data.ConstGroup;
//import com.huawei.data.ViewHeadPhotoData;
//import com.huawei.data.ViewHeadPhotoParam;
//import com.huawei.dispatcher.Dispatcher;
//import com.huawei.ecs.mtk.log.Logger;
//import com.huawei.log.TagInfo;
//import com.huawei.msghandler.maabusiness.GetGroupPicRequester;
//import com.huawei.msghandler.maabusiness.GetHeadImageRequest;
import com.huawei.opensdk.ec_sdk_demo.R;
//import com.huawei.service.EspaceService;
//import com.huawei.utils.img.BitmapUtil;
//import com.huawei.utils.img.PhotoUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is about get group avatar
 * 群组头像请求
 */
public class GroupHeadDownloader
{
    private final GroupHeadMixture headMixture;

    private final Context mContext;

    private final File sysFile;

    private final Bitmap outlineBitmap;

    public GroupHeadDownloader(Context context, File sysFile, Bitmap outlineBitmap)
    {
        this.mContext = context;
        this.sysFile = sysFile;
        this.outlineBitmap = outlineBitmap;
        headMixture = new GroupHeadMixture(context);
    }

//    /**
//     * 执行请求操作，并存入返回的数据。
//     * @param list
//     * @param data
//     * @return
//     */
//    private Bitmap doRequest(List<ViewHeadPhotoParam> list, ConstGroup data)
//    {
//        GetHeadImageRequest request = new GetHeadImageRequest();
//        request.setWaitTime(15000);  //等待15秒
//        List<ViewHeadPhotoData> dataList = request.requestPhoto(list);
//        if (dataList == null)
//        {
//            return null;
//        }
//
//        return saveHeadPhoto(dataList, list, data);
//    }


//    /**
//     * 保存头像信息
//     * @param photoDatas
//     * @param headPhoto
//     * @param data
//     * @return
//     */
//    private Bitmap saveHeadPhoto(List<ViewHeadPhotoData> photoDatas,
//                                 List<ViewHeadPhotoParam> headPhoto, ConstGroup data)
//    {
//        List<Bitmap> bitmaps = decode(photoDatas, headPhoto);
//        Bitmap bitmap1 = headMixture.getCombineBitmaps(bitmaps);
//        if (null == bitmap1 || data == null)
//        {
//            return null;
//        }
//
//        //保存头像
//        String fileName = data.getHeadFileName();
//        saveBitmap(bitmap1, fileName);
//
//        HeadPhotoUtil.getIns().addAccount(data.getGroupId(), fileName);
//
////        发送广播
//        Logger.debug(TagInfo.APPTAG, "[group head]-----> ACTION  GET_GROUP_PIC_SUCCESS...");
//        Intent intent = new Intent(CustomBroadcastConst.ACTION_GET_GROUP_PIC_SUCCESS);
//        Dispatcher.postLocBroadcast(intent);
//
//        return bitmap1;
//    }

//    /**
//     * 通过返回的头像数据解析出bitmap列表。
//     * @param photoDatas
//     * @param headPhoto
//     * @return
//     */
//    private List<Bitmap> decode(List<ViewHeadPhotoData> photoDatas, List<ViewHeadPhotoParam> headPhoto)
//    {
//        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
//
//        Bitmap bitmap;
//        for (ViewHeadPhotoParam param : headPhoto)
//        {
//            bitmap = decodeBitmap(param, photoDatas);
//            if (bitmap != null)
//            {
//                bitmaps.add(bitmap);
//            }
//        }
//
//        return bitmaps;
//    }

//    /**
//     * 解析出单个bitmap
//     * @param param
//     * @param photoDatas
//     * @return
//     */
//    private Bitmap decodeBitmap(ViewHeadPhotoParam param, List<ViewHeadPhotoData> photoDatas)
//    {
//        Bitmap bitmap = HeadPhotoUtil.getDefaultHeadImg(param.getHeadId());
//        if (bitmap == null)
//        {
//            bitmap = getBitmapFromPhotoData(photoDatas, param.getJid());
//            if (bitmap == null)
//            {
//                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_head);
//            }
//        }
//
//        return BitmapUtil.getRoundCornerBitmap(bitmap, outlineBitmap);
//    }

    private void saveBitmap(Bitmap bitmap1, String fileName)
    {
        File file = new File(sysFile, fileName);
//        PhotoUtil.saveMyBitmap(file, bitmap1, true, PhotoUtil.COMPRESS_RATE_100, false);
    }

//    /**
//     * 获取对应账号的bitmap
//     * @param photoDatas 服务器返回的数据对象
//     * @param jid
//     * @return
//     */
//    private Bitmap getBitmapFromPhotoData(List<ViewHeadPhotoData> photoDatas, String jid)
//    {
//        ViewHeadPhotoData mData = getPhotoData(photoDatas, jid);
//        return mData != null ? BitmapUtil.decodeByteArray(mData.getData(),
//                MyOtherInfo.PICTURE_DEFAULT_HEIGHT) : null;
//    }

//    private ViewHeadPhotoData getPhotoData(List<ViewHeadPhotoData> photoDatas, String jid)
//    {
//        if (jid == null || photoDatas == null)
//        {
//            return null;
//        }
//
//        ViewHeadPhotoData mData = null;
//        for (ViewHeadPhotoData data : photoDatas)
//        {
//            mData = data;
//            if (jid.equals(data.getEspaceNumber()))
//            {
//                break;
//            }
//        }
//        return mData;
//    }

//    /**
//     * 获取群头像列表参数。
//     * @param heads
//     * @return
//     */
//    private List<ViewHeadPhotoParam> getHeadListParam(String heads)
//    {
//        List<ViewHeadPhotoParam> headList = new ArrayList<ViewHeadPhotoParam>();
//        if (TextUtils.isEmpty(heads))
//        {
//            return headList;
//        }
//
//        String[] headInfo = heads.split(";");
//        for (String temp : headInfo)
//        {
//            addToHeadList(temp, headList);
//        }
//        return headList;
//    }

//    /**
//     * 将数据添加到list列表中
//     * @param temp
//     * @param headList
//     */
//    private void addToHeadList(String temp, List<ViewHeadPhotoParam> headList)
//    {
//        String[] accounts = temp.split("\\|");
//        if (accounts.length == 2)
//        {
//            headList.add(getParam(accounts[0], accounts[1]));
//        }
//    }

//    private ViewHeadPhotoParam getParam(String account, String headId)
//    {
//        String length = MyOtherInfo.PICTURE_DEFAULT_HEIGHT / 2 + "";
//        ViewHeadPhotoParam item = new ViewHeadPhotoParam();
//        item.setJid(account);
//        item.setHeadId(TextUtils.isEmpty(headId) || "-1".equals(headId) ? "" : headId);
//        item.setH(length);
//        item.setW(length);
//        return item;
//    }

//    /**
//     * 请求并获取头像id字符串。
//     * @param group
//     * @return
//     */
//    public String requestAndGetHeads(ConstGroup group)
//    {
//        /*String heads = ConstGroupManager.ins().getGroupHeadSync(
//                group.getGroupId(), group.getGroupType(), false);*/
//        int type = group.getGroupType();
//        String groupId = group.getGroupId();
//        String heads = getGroupHeadSync(groupId, type, false);
//
//        if (TextUtils.isEmpty(heads))
//        {
//            heads = group.getHeads();
//        }
//        else
//        {
//            group.setHead(heads);
//        }
//
//        return heads;
//    }

    private String getGroupHeadSync(String groupId, int groupType, boolean forceUpdate)
    {
        // 头像拼接接口是和讨论组特性一起上线的，之前的版本不支持获取群头像拼接接口
//        if (!ContactLogic.getIns().getAbility().isDiscussGroupAbility())
        {
            return null;
        }

//        EspaceService service = EspaceService.getService();
//        if (null == service || !service.isLoginSuccess())
//        {
//            return null;
//        }

        //成员变更时强制更新
//        if (forceUpdate || !ConstGroupManager.ins().isHeadsSyncEnd(groupId))
//        {
//            Logger.debug(TagInfo.TAG, "get group head for groupId#" + groupId);
//            return new GetGroupPicRequester(groupId, groupType).sendSyncRequest();
//        }

//        return null;
    }

//    /**
//     * 请求完成后保存头像，并返回获取到的bitmap
//     * @param headPhotos
//     * @param group
//     * @return
//     */
//    private Bitmap getBitmap(List<ViewHeadPhotoParam> headPhotos, ConstGroup group)
//    {
//        Bitmap bitmap;
//        if (isNeedRequestFromServer(headPhotos))
//        {
//            bitmap = doRequest(headPhotos, group);
//        }
//        else
//        {
//            bitmap = saveHeadPhoto(null, headPhotos, group);
//        }
//        return bitmap;
//    }

//    /**
//     * 获取需要向服务器请求的头像个数
//     * @param headPhotos
//     * @return  true需要从服务器获取
//     */
//    private boolean isNeedRequestFromServer(List<ViewHeadPhotoParam> headPhotos)
//    {
//        for (ViewHeadPhotoParam viewHeadPhotoParam : headPhotos)
//        {
//            if (GetHeadImageRequest.isSupportGetFromServer(viewHeadPhotoParam))
//            {
//                return true;
//            }
//        }
//        return false;
//    }

//    public BitmapDrawable load(ConstGroup group)
//    {
//        //服务器请求头像id列表。
//        String heads = group.getHeads();
//
//        //获取头像请求的参数
//        List<ViewHeadPhotoParam> headPhotos = getHeadListParam(heads);
//        if (headPhotos.isEmpty())
//        {
//            return null;
//        }
//
//        Bitmap bitmap = getBitmap(headPhotos, group);
//        if (bitmap == null)
//        {
//            return null;
//        }
//
//        return new BitmapDrawable(mContext.getResources(), bitmap);
//    }
}
