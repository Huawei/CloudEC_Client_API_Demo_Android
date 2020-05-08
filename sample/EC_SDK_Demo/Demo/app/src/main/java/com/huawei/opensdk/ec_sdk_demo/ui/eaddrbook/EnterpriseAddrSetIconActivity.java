package com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.huawei.opensdk.ec_sdk_demo.widget.CircleView;
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
    private CircleView circleView; // 头像
    private Bitmap mBitmap;

    private SimpleListDialog mPhotoDialog;
    private String mMyAccount;

    private String mIconPath;
    private int mIconId;
    private String[] mActions = new String[]{CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_PHOTO_RESULT};

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.setting_more);
        personalHeadLayout = (RelativeLayout) findViewById(R.id.personal_head_layout);
        circleView = (CircleView) findViewById(R.id.headImgCircle);

        personalHeadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoOptionsDialog();
            }
        });

        updateMyHeadPhoto();
    }

    @Override
    public void initializeData()
    {
        LocBroadcast.getInstance().registerBroadcast(this, mActions);
        mMyAccount = LoginMgr.getInstance().getAccount();
    }

    private void updateMyHeadPhoto()
    {
        EnterpriseAddressBookMgr.getInstance().getSelfIcon(mMyAccount);
    }

    private void showMyHeadPhone(final Bitmap bitmap)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                circleView.setBitmapParams(bitmap);
                circleView.invalidate();
            }
        });
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
                if (obj instanceof EntAddressBookIconInfo)
                {
                    EntAddressBookIconInfo iconInfo = (EntAddressBookIconInfo) obj;
                    String defIcon = iconInfo.getIconFile();
                    if (defIcon.isEmpty())
                    {
                        mIconId = iconInfo.getIconId();
                        mBitmap = HeadIconTools.getBitmapByIconId(mIconId);
                    }
                    else
                    {
                        mIconPath = Environment.getExternalStorageDirectory() + File.separator + "ECSDKDemo" + File.separator + "icon" + File.separator + defIcon;
                        mBitmap = HeadIconTools.getBitmapByPath(mIconPath);
                    }
                    showMyHeadPhone(mBitmap);
                }
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED:
                showMyHeadPhone(HeadIconTools.getBitmapByIconId(10));
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
                mBitmap = HeadIconTools.getBitmapByIconId(position);
                if (mBitmap != null)
                {
                    showMyHeadPhone(mBitmap);
                    updateMyHeadPhoto();
                }
                break;
            default:
                break;
        }
    }

}
