package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.contactservice.eaddr.EntAddressBookIconInfo;
import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.im.ContactTools;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook.EnterpriseAddrTools;
import com.huawei.opensdk.ec_sdk_demo.ui.im.contact.HeadIconTools;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.util.CommonUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.EditDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.RoundCornerPhotoView;
import com.huawei.opensdk.ec_sdk_demo.widget.SimpleListDialog;
import com.huawei.opensdk.imservice.ImConstant;
import com.huawei.opensdk.imservice.ImContactInfo;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is about set user info activity.
 */
public class SettingMoreActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver
{
    private RelativeLayout personalHeadLayout;
    private LinearLayout signatureLayout;
    private RelativeLayout personalInfoLayout;
    private TextView signatureDesc; //签名信息
    private TextView statusDesc;//状态描述(在线、忙碌、离开，免打扰)
    private RoundCornerPhotoView headImg;//头像
    private SimpleListDialog mStatusDialog;
    private SimpleListDialog mPhotoDialog;

    private String mMyAccount;
    private ImContactInfo mSelfContact;
    private ImConstant.ImStatus mStatus;
    private boolean mImIsLogin = LoginMgr.getInstance().isImLogin();

    private String mIconPath;
    private int mIconId;
    private static int[] mSystemIcon = EnterpriseAddrTools.getSystemIcon();
    private String[] mActions = new String[]{CustomBroadcastConstants.ACTION_IM_LOGIN_SUCCESS,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_PHOTO_RESULT,
            CustomBroadcastConstants.ACTION_IM_USER_INFO_CHANGE,
            CustomBroadcastConstants.ACTION_IM_USER_INFO_CHANGE_FAILED
    };

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
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.ACTION_IM_SET_HEAD_PHOTO, null);
                    Log.i(UIConstants.DEMO_TAG, "Set Defined HeadPhoto Success");
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
                            mIconPath = Environment.getExternalStorageDirectory() + File.separator + "tupcontact" + File.separator + "icon" + File.separator + defIcon;
                            Bitmap headIcon = EnterpriseAddrTools.getBitmapByPath(mIconPath);
                            headImg.setImageBitmap(headIcon);
                        }
                    }
                    break;
                case UIConstants.ENTERPRISE_HEAD_NULL:
                    headImg.setBackgroundResource(R.drawable.default_head_local);
                    break;
                case UIConstants.IM_CHANGE_INFO_FAILED:
                    showToast(R.string.set_info_failed);
                    break;
                case UIConstants.IM_CHANGE_INFO:
                    List<ImContactInfo> list = (List<ImContactInfo>) msg.obj;
                    for (ImContactInfo info : list)
                    {
                        if (info.getAccount().equals(mMyAccount))
                        {
                            mSelfContact.setSignature(info.getSignature());
                            signatureDesc.setText(mSelfContact.getSignature());
                        }
                    }
                    return;
                /*case RELOAD_SIGNATURE:
                    loadMySignatureAndStatus();
                    break;
                case LOAD_FRIENDS_STATUS:
                    loadFriends();
                    break;
                case REFRESH_VIEW:
                    contactAdapter.notifyDataSetChanged(friendsIndexList);
                    break;*/
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
        signatureLayout = (LinearLayout) findViewById(R.id.signature_layout);
        personalInfoLayout = (RelativeLayout) findViewById(R.id.personal_info);
        headImg = (RoundCornerPhotoView) findViewById(R.id.headImg);
        signatureDesc = (TextView) findViewById(R.id.signature_txt);
        if (null != mSelfContact)
        {
            signatureDesc.setText(mSelfContact.getSignature());
        }
        ViewGroup statusVG = (ViewGroup) findViewById(R.id.status_setting_layout);
        statusDesc = (TextView) findViewById(R.id.mystate_txt);
        if (mImIsLogin)
        {
            updateMyStatus();
        }
        else
        {
            signatureLayout.setVisibility(View.GONE);
            statusVG.setVisibility(View.GONE);
            personalInfoLayout.setVisibility(View.GONE);
        }

        mHandler.sendEmptyMessageDelayed(UIConstants.PRO_LOAD_HEADICON, 1000);
        statusVG.setOnClickListener(this);
        personalHeadLayout.setOnClickListener(this);
        signatureLayout.setOnClickListener(this);
        personalInfoLayout.setOnClickListener(this);
        updateMyHeadPhoto();
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
        if (mImIsLogin)
        {
            mSelfContact = ImMgr.getInstance().getUserInfo(mMyAccount);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        dismissDialog(mPhotoDialog);
        dismissDialog(mStatusDialog);
    }

    private void dismissDialog(SimpleListDialog dialog)
    {
        if (null != dialog)
        {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.personal_head_layout: //头像
                showPhotoOptionsDialog();
                break;
            case R.id.signature_layout: //签名
                showSignatureDialog();
                break;
            case R.id.status_setting_layout://状态
                showStatusChooseDialog();
                break;
            case R.id.personal_info: //个人信息
                // TODO: 2019/1/24 所有im相关的功能使用前是否都需要添加im登录成功的判断呢？？？
                Intent intent = new Intent(IntentConstant.CONTACT_DETAIL_ACTIVITY_ACTION);
                intent.putExtra(UIConstants.IM_CONTACT_INFO, mSelfContact);
                ActivityUtil.startActivity(this, intent);
                break;
            default:
                break;
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
                ActivityUtil.startActivityForResult(this, IntentConstant.SYSTEM_HEAD_SELECT_ACTIVITY_ACTION,
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

    private void showSignatureDialog()
    {
        final EditDialog dialog = new EditDialog(this, R.string.signature_default);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CommonUtil.hideSoftInput(SettingMoreActivity.this);
                ImContactInfo contactInfo = new ImContactInfo();
                contactInfo.setSignature(dialog.getText());
                contactInfo.setAccount(mMyAccount);
                ImMgr.getInstance().setUserInfo(contactInfo);
            }
        });
        dialog.show();
    }

    /**
     * 显示选择状态弹出框
     */
    public void showStatusChooseDialog()
    {
        List<Object> data = new ArrayList<>();
        data.add(getString(R.string.online));
        data.add(getString(R.string.busy));
        data.add(getString(R.string.leave));
        data.add(getString(R.string.dnd));
        mStatusDialog = new SimpleListDialog(this, data);
        mStatusDialog.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mStatusDialog.dismiss();
                mStatusDialog = null;
                setStatus(position);
            }
        });
        mStatusDialog.show();
    }

    /**
     * 设置状态
     * @param position 选中的状态序号
     */
    private void setStatus(int position)
    {
        ImConstant.ImStatus status = ImConstant.ImStatus.ON_LINE;
        switch (position)
        {
            case UIConstants.STATUS_ON_LINE:
                status = ImConstant.ImStatus.ON_LINE;
                break;
            case UIConstants.STATUS_BUSY:
                status = ImConstant.ImStatus.BUSY;
                break;
            case UIConstants.STATUS_XA:
                status = ImConstant.ImStatus.XA;
                break;
            case UIConstants.STATUS_DND:
                status = ImConstant.ImStatus.DND;
                break;
            default:
                break;
        }
        int result = ImMgr.getInstance().setUserStatus(status, "");
        if (0 != result)
        {
            showToast(R.string.set_status_fail);
        }
        else
        {
            updateMyStatus();
        }
    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_IM_LOGIN_SUCCESS:
                updateMyStatus();
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_PHOTO_RESULT:
                Message msgSelfInfo = mHandler.obtainMessage(UIConstants.ENTERPRISE_HEAD_SELF, obj);
                mHandler.sendMessage(msgSelfInfo);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED:
                Message msgFailed = mHandler.obtainMessage(UIConstants.ENTERPRISE_HEAD_NULL, obj);
                mHandler.sendMessage(msgFailed);
                break;
            case CustomBroadcastConstants.ACTION_IM_USER_INFO_CHANGE:
                Message selfInfo = mHandler.obtainMessage(UIConstants.IM_CHANGE_INFO, obj);
                mHandler.sendMessage(selfInfo);
                break;
            case CustomBroadcastConstants.ACTION_IM_USER_INFO_CHANGE_FAILED:
                mHandler.sendEmptyMessage(UIConstants.IM_CHANGE_INFO_FAILED);
                break;
            default:
                break;
        }
    }

    private void updateMyStatus()
    {
        mStatus = ImMgr.getInstance().getStatus();
        mSelfContact.setStatus(mStatus);
        switch (mStatus)
        {
            case ON_LINE:
                statusDesc.setText(getString(R.string.online));
                break;
            case BUSY:
                statusDesc.setText(getString(R.string.busy));
                break;
            case XA:
                statusDesc.setText(getString(R.string.leave));
                break;
            case AWAY:
                statusDesc.setText(getString(R.string.offline));
                break;
            case DND:
                statusDesc.setText(getString(R.string.dnd));
                break;
            default:
                statusDesc.setText(getString(R.string.offline));
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
