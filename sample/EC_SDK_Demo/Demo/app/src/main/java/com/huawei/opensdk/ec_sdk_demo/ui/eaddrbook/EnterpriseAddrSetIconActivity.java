package com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.contactservice.eaddr.EntAddressBookIconInfo;
import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.ContactTools;
import com.huawei.opensdk.ec_sdk_demo.module.headphoto.HeadIconTools;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.RoundCornerPhotoView;
import com.huawei.opensdk.ec_sdk_demo.widget.SimpleListDialog;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is about set user info activity.
 */
public class EnterpriseAddrSetIconActivity extends BaseActivity implements LocBroadcastReceiver
{
    private RelativeLayout personalHeadLayout;
//    private RoundCornerPhotoView headImg;//头像
    private ImageView headImg;//头像

    private SimpleListDialog mPhotoDialog;
    private String mMyAccount;

    private String mIconPath;
    private int mIconId;
    private static int[] mSystemIcon = EnterpriseAddrTools.getSystemIcon();
    private String[] mActions = new String[]{CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_PHOTO_RESULT};

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case UIConstants.LOAD_ALL_HEAD_ICON:
                    Bitmap myHeadIcon = HeadIconTools.getInstance().getHeadImage(mMyAccount);
                    headImg.setImageBitmap(myHeadIcon);
                    break;
                case UIConstants.PRO_LOAD_HEADICON:
//                    mContacts.clear();
//                    mContacts.add(mSelfContact);
                    break;
                case UIConstants.LOAD_SELF_HEADIMAGE:
//                    mContacts.clear();
//                    mContacts.add(mSelfContact);
//                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_SET_HEAD_PHOTO, null);
//                    Log.i(UIConstants.DEMO_TAG, "Set Defined HeadPhoto Success");
                    break;
                case UIConstants.ENTERPRISE_HEAD_SELF:
                    if (msg.obj instanceof EntAddressBookIconInfo)
                    {
                        EntAddressBookIconInfo iconInfo = (EntAddressBookIconInfo) msg.obj;
                        String defIcon = iconInfo.getIconFile();
                        if (defIcon.isEmpty())
                        {
                            mIconId = iconInfo.getIconId();
                            headImg.setImageResource(mSystemIcon[mIconId]);
                        }
                        else
                        {
                            mIconPath = Environment.getExternalStorageDirectory() + File.separator + "ECSDKDemo" + File.separator + "icon" + File.separator + defIcon;
                            Bitmap headIcon = EnterpriseAddrTools.getBitmapByPath(mIconPath);
                            headImg.setImageBitmap(headIcon);
                        }
                    }
                    break;
                case UIConstants.ENTERPRISE_HEAD_NULL:
                    headImg.setBackgroundResource(R.drawable.default_head_local);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.setting_more);
        personalHeadLayout = (RelativeLayout) findViewById(R.id.personal_head_layout);
//        headImg = (RoundCornerPhotoView) findViewById(R.id.headImg);
        headImg = (ImageView) findViewById(R.id.headImg);

//        mHandler.sendEmptyMessageDelayed(UIConstants.PRO_LOAD_HEADICON, 1000);

        updateMyHeadPhoto();

        personalHeadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoOptionsDialog();
            }
        });
    }

    private void updateMyHeadPhoto()
    {
        EnterpriseAddressBookMgr.getInstance().getSelfIcon(mMyAccount);
    }

    @Override
    public void initializeData()
    {
        LocBroadcast.getInstance().registerBroadcast(this, mActions);
        mMyAccount = LoginMgr.getInstance().getAccount();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        dismissDialog(mPhotoDialog);
    }

    private void dismissDialog(SimpleListDialog dialog)
    {
        if (null != dialog)
        {
            dialog.dismiss();
        }
    }

    private void showPhotoOptionsDialog()
    {
        final List<Object> photoData = getPhotoOptions();

        mPhotoDialog = new SimpleListDialog(this, photoData);
        mPhotoDialog.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l)
            {
                mPhotoDialog.dismiss();
                mPhotoDialog = null;
                Object obj = photoData.get(pos);
                if (obj instanceof String)
                {
                    onPopupItemClick(pos);
                }
            }
        });
        mPhotoDialog.show();
    }

    /**
     * 点击头像选择修改头像方式 0.系统图像 2.相册选取
     * @param position item序号
     */
    private void onPopupItemClick(int position)
    {
        switch (position)
        {
            case UIConstants.SYSTEM_PICTURE:
                ActivityUtil.startActivityForResult(this, IntentConstant.EADDR_ICON_SELECT_ACTIVITY_ACTION,
                        UIConstants.SET_SYSTEM_HEAD_PHOTO_CODE);
                break;
            case UIConstants.ALBUM_PICTURE:
                HeadIconTools.selectPicByType(HeadIconTools.SELECT_PICTURE_FROM_LOCAL);
                break;
            default:
                break;
        }
    }

    protected List<Object> getPhotoOptions()
    {
        List<Object> photoData = new ArrayList<>();
        photoData.add(getString(R.string.pick_system_photo));
        photoData.add(getString(R.string.pick_local_photo));
        return photoData;
    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_PHOTO_RESULT:
                Message msgSelfInfo = mHandler.obtainMessage(UIConstants.ENTERPRISE_HEAD_SELF, obj);
                mHandler.sendMessage(msgSelfInfo);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED:
                Message msgFailed = mHandler.obtainMessage(UIConstants.ENTERPRISE_HEAD_NULL, obj);
                mHandler.sendMessage(msgFailed);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case HeadIconTools.SELECT_PICTURE_FROM_LOCAL:
                if (data == null)
                {
                    Log.e("SELECT_PICTURE data->", "data is null.");
                    return;
                }
                try
                {
                    Uri photoUri = HeadIconTools.getPhotoUri();
                    if (photoUri == null)
                    {
                        Log.e("ContactsActivity", "photoUri is null");
                        return;
                    }

                    Bitmap bitmap = HeadIconTools.decodeUriToBitmap(photoUri);
                    if (null != bitmap)
                    {
                        Bitmap bit = BitmapFactory.decodeFile(photoUri.getPath());

                        ContactTools.getInstance().getBitmap(bit, 120, "head2");
                        ContactTools.getInstance().getBitmap(bit, 52, "head3");

                        String filePath1 = ContactTools.HEAD_PHOTO_PATH + File.separator + "head1.jpeg";
                        String filePath2 = ContactTools.HEAD_PHOTO_PATH + File.separator + "head2.jpeg";
                        String filePath3 = ContactTools.HEAD_PHOTO_PATH + File.separator + "head3.jpeg";
                        //Set user defined head image.
                        int result = EnterpriseAddressBookMgr.getInstance().setDefinedIcon(filePath1, filePath2, filePath3);
                        if (result == 0)
                        {
                            updateMyHeadPhoto();
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            case UIConstants.SET_SYSTEM_HEAD_PHOTO_CODE:
                int position = resultCode;
                Log.e("position", "" + position);
                Bitmap bitmap = HeadIconTools.getBitmapByIconId(position);
                if (bitmap != null)
                {
                    headImg.setImageBitmap(bitmap);
                    updateMyHeadPhoto();
                }
                break;
            default:
                break;
        }
    }

}
