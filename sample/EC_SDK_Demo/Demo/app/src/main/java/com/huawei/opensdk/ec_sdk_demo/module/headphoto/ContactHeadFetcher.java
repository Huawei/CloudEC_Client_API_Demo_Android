package com.huawei.opensdk.ec_sdk_demo.module.headphoto;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.widget.ImageView;

//import com.huawei.contacts.ContactCache;
//import com.huawei.contacts.PersonalContact;
import com.huawei.opensdk.ec_sdk_demo.R;

/**
 * This class is about picture picker,get avatar for all contacts
 * 图片获取器；
 * 获取所有的espace联系人的头像
 */
public class ContactHeadFetcher extends HeadFetcher
{
    private final ContactHeadDownloader contactHeadRequester;

    private ContactHeadLocalLoader localLoader;

    public void setListener(ContactHeadDownloader.ServerPhotoLoadedListener listener)
    {
        this.listener = listener;
    }

    private ContactHeadDownloader.ServerPhotoLoadedListener listener;

    public ContactHeadFetcher(Context context)
    {
        super(context, R.drawable.default_head);

        contactHeadRequester = new ContactHeadDownloader(context, outlineBitmap);
        localLoader = new ContactHeadLocalLoader(context, outlineBitmap, sysFile);
    }

//    @Override
    protected BitmapDrawable processBitmap(Object data)
    {
        return loadBitmapFromServer((HeadPhoto) data, -1);
    }

//    @Override
    protected BitmapDrawable getBitmapFromDiskCache(Object data)
    {
        HeadPhoto headPhoto = (HeadPhoto) data;

        return localLoader.load(headPhoto);
    }

//    @Override
    public void loadImageFromCache(String key, ImageView iv)
    {
        if (TextUtils.isEmpty(key))
        {
            iv.setImageResource(R.drawable.default_head_local);
            return;
        }

//        super.loadImageFromCache(key, iv);
    }

    /**
     * 传入联系人账号。
     * @param account 联系人的账号。
     * @param headImage
     * @param supportDel
     */
    public void loadHead(String account, ImageView headImage, boolean supportDel)
    {
        if (TextUtils.isEmpty(account))
        {

            //联系人账号为空,设置为自定义头像.
            headImage.setImageResource(R.drawable.default_head_local);
            return;
        }

        // 获取头像的id
//        PersonalContact pContact = ContactCache.getIns().getContactByAccount(account);
//        if (pContact != null)
//        {
//            loadHead(pContact, headImage, supportDel);
//        }
        else
        {
            //支持陌生人只通过espacenumber获取头像
            HeadPhoto headPhoto = new HeadPhoto(account, "");
//            loadImage(headPhoto, headImage);
        }
    }


    /**
     * 提供头像加载功能。
     * @param account
     * @param headImage
     * @param onlyFromCache
     */
    public void loadHead(String account, ImageView headImage
            , boolean onlyFromCache, boolean supportDel)
    {
        if (onlyFromCache)
        {
            loadImageFromCache(account, headImage);
        }
        else
        {
            loadHead(account, headImage, supportDel);
        }
    }

    public void loadHead(String account, String headId, ImageView headImage)
    {
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(headId))
        {
            headImage.setImageResource(R.drawable.default_head);
            return;
        }

        HeadPhoto headPhoto = new HeadPhoto(account, headId);
//        loadImage(headPhoto, headImage);
    }

//    /**
//     * 只用于列表中加载联系人头像，pContact不为null的情况。
//     * 加载联系人头像，从缓存联系人中获取头像ID，如缓存不存在该联系人，显示默认头像
//     * @param pContact 联系人信息
//     * @param headImage 头像显示的View
//     * @param supportDel 是否支持删除头像
//     */
//    public void loadHead(PersonalContact pContact, ImageView headImage, boolean supportDel)
//    {
//        if (pContact == null)
//        {
//            //联系人为空,设置为默认头像
//            headImage.setImageResource(R.drawable.default_head);
//            return;
//        }
//
//        String account = pContact.getEspaceNumber();
//        if (TextUtils.isEmpty(account))
//        {
//            //联系人账号为空,设置为自定义头像.
//            headImage.setImageResource(R.drawable.default_head_local);
//            return;
//        }
//
//        //头像id为空，删除头像。
//        if (isHeadChangeOrDelete(pContact, supportDel))
//        {
//            deletePhoto(account);
//            headImage.setImageBitmap(getDefaultBitmap(null));
//            return;
//        }
//
//        //支持陌生人只通过espacenumber获取头像
//        HeadPhoto headPhoto = new HeadPhoto(account, pContact.getHead());
//        loadImage(headPhoto, headImage);
//    }

//    @Override
    protected boolean forceRequestFromServer(Object data)
    {
        HeadPhoto headPhoto = (HeadPhoto) data;

        String account = headPhoto.getAccount();
        String fileName = HeadPhotoUtil.getIns().getFileName(account);
        if (TextUtils.isEmpty(fileName)
                || TextUtils.isEmpty(headPhoto.getId()))
        {
            return false;
        }

        String headId = HeadPhotoUtil.parseHeadId(account, fileName);
        return !headId.equals(headPhoto.getId());
    }

    private void deletePhoto(String account)
    {
//        HeadPhotoUtil.deletePhoto(mContext, account);
//        getImageCache().removeBitmapFromCache(account);
    }

//    private boolean isHeadChangeOrDelete(PersonalContact pContact, boolean supportDel)
//    {
//        return (pContact.isSelf() || supportDel || pContact.isFriend()) && TextUtils.isEmpty(pContact.getHead());
//    }

    public void loadHead(String espacenumber, ImageView bigHead)
    {
        loadHead(espacenumber, bigHead, false);
    }

    /**
     * 从服务器同步加载bitmap
     * @param photo
     * @param sideLength
     */
    public BitmapDrawable loadBitmapFromServer(HeadPhoto photo, int sideLength)
    {
        return contactHeadRequester.loadBitmapFromServer(photo, sideLength, listener);
    }
}
