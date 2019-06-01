package com.huawei.opensdk.ec_sdk_demo.module.headphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.widget.ImageView;

//import com.huawei.common.library.asyncimage.ImageCache;
//import com.huawei.contacts.ContactLogic;
//import com.huawei.contacts.MyOtherInfo;
//import com.huawei.contacts.group.ConstGroupManager;
//import com.huawei.data.ConstGroup;
//import com.huawei.ecs.mtk.log.Logger;
//import com.huawei.log.TagInfo;
import com.huawei.opensdk.ec_sdk_demo.R;
//import com.huawei.utils.img.BitmapUtil;

import java.io.File;

/**
 * This class is about Get group avatar information
 * 获取群组头像信息
 */
public class GroupHeadFetcher extends HeadFetcher
{
    private final GroupHeadDownloader groupHeadRequester;

    public GroupHeadFetcher(Context context)
    {
        super(context, R.drawable.group_head);

        groupHeadRequester = new GroupHeadDownloader(context, sysFile, outlineBitmap);
    }

//    @Override
//    protected BitmapDrawable getBitmapFromDiskCache(Object data)
//    {
//        ConstGroup group = (ConstGroup) data;
//        String heads = group.getHeads();
//        //群头像id为空，直接返回空。
//        if (TextUtils.isEmpty(heads))
//        {
//            return null;
//        }
//
//        //缓存已经存在，不从文件获取。
//        ImageCache imageCache = getImageCache();
//        if (imageCache.contains(group.getGroupId()))
//        {
//            return null;
//        }
//
//        return getBitmapFromFile(group);
//    }

//    /**
//     * 从文件中获取bitmap
//     * @param group
//     * @return
//     */
//    private BitmapDrawable getBitmapFromFile(ConstGroup group)
//    {
//        String fileName = group.getHeadFileName(); //.getFileName(group);
//        Bitmap bitmap = decodeBitmapFromLocal(fileName);
//        if (bitmap == null)
//        {
//            return null;
//        }
//
//        HeadPhotoUtil.getIns().addAccount(group.getGroupId(), fileName);
//        return new BitmapDrawable(mContext.getResources(), bitmap);
//    }

    /**
     * 从本地提取文件。
     * @param fileName
     * @return
     */
    private Bitmap decodeBitmapFromLocal(String fileName)
    {
//        Logger.debug(TagInfo.APPTAG, fileName);
        File mFile = new File(sysFile, fileName);
//        return BitmapUtil.decodeBitmapFromFile(mFile.getAbsolutePath(),
//                MyOtherInfo.PICTURE_DEFAULT_WIDTH, MyOtherInfo.PICTURE_DEFAULT_HEIGHT);
        return null;
    }

//    @Override
    public void loadImageFromCache(String key, ImageView imageView)
    {
        if (key == null)
        {
//            imageView.setImageBitmap(getDefaultBitmap(null));
            return;
        }

//        super.loadImageFromCache(key, imageView);
    }


//    @Override
    public BitmapDrawable processBitmap(Object data)
    {
//        ConstGroup group = (ConstGroup)data;

//        groupHeadRequester.requestAndGetHeads(group);

//        String file = group.getHeadFileName();
        // Logger.debug(LocalLog.APPTAG, file);

//        String fileName = HeadPhotoUtil.getIns().getFileName(group.getGroupId());
//        if (fileName != null && fileName.equals(file))
        {
            return null;
        }

//        return groupHeadRequester.load(group);
    }

//    @Override
//    protected boolean forceRequestFromServer(Object data)
//    {
//////        ConstGroup group = (ConstGroup)data;
//////        String heads = group.getHeads();
////
//////        boolean containsInSync = ConstGroupManager.ins().containsInSyncHeads(group.getGroupId());
//////        if (TextUtils.isEmpty(heads))
////        {
////            // 如果已经同步过了，就不用再同步了
//////            return !containsInSync;
////        }
////
//////        return !containsInSync || isFileNameNotExist(group);
////    }
//
////    private boolean isFileNameNotExist(ConstGroup group)
////    {
////        String file = group.getHeadFileName();
////        // Logger.debug(LocalLog.APPTAG, file);
////
////        String fileName = HeadPhotoUtil.getIns().getFileName(group.getGroupId());
////
////        //判断fileName是否为空只是为了避免重复多次调用。
////        return fileName != null && !fileName.equals(file);
//    }

//    @Override
    public void loadImage(Object data, ImageView imageView)
    {
        //头像拼接接口是和讨论组特性一起上线的，之前的版本不支持获取群头像拼接接口
//        if (!ContactLogic.getIns().getAbility().isDiscussGroupAbility())
//        {
//            imageView.setImageBitmap(getDefaultBitmap(data));
//            return ;
//        }

//        super.loadImage(data, imageView);
    }

//    @Override
//    public String genKey(Object data)
//    {
//        return ((ConstGroup) data).getGroupId();
//    }

//    public void loadHead(ConstGroup group, ImageView bigImg)
//    {
//        if (group == null)
//        {
//            bigImg.setImageBitmap(getDefaultBitmap(null));
//            Logger.info(TagInfo.FLUENT, "group is null.");
//            return;
//        }
//
//        loadImage(group, bigImg);
//    }

    /**
     * 加载讨论组头像
     * @param groupId 群组信息
     * @param bigImg 大头像
     */
    public void loadGroupHead(String groupId, ImageView bigImg, boolean fromCache)
    {
        if (fromCache)
        {
            loadImageFromCache(groupId, bigImg);
        }
        else
        {
            loadHead(groupId, bigImg);
        }
    }

    /**
     * 加载头像
     * @param groupAccount 群组账号
     * @param headImage
     */
    public void loadHead(String groupAccount, ImageView headImage)
    {
        if (groupAccount == null)
        {
//            headImage.setImageBitmap(getDefaultBitmap(null));
            return;
        }

        // 请求群组成员的头像id
//        ConstGroup group = ConstGroupManager.ins().findConstGroupByIdInCache(groupAccount);
//        loadHead(group, headImage);
    }
}
