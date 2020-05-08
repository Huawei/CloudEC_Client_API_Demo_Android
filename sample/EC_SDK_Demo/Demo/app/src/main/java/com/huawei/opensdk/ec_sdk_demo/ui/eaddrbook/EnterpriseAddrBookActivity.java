package com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.contactservice.eaddr.EntAddressBookIconInfo;
import com.huawei.opensdk.contactservice.eaddr.EntAddressBookInfo;
import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.contactservice.eaddr.QueryContactsInfoResult;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.EnterpriseListAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is about search contacts activity.
 */
public class EnterpriseAddrBookActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, LocBroadcastReceiver {

    private ImageView eaddrBack = null;
    private EditText eaddrKeys = null;
    private ImageView eaddrSearch = null;
    private ListView eaddrList = null;
    private Toast toast;
    private EnterpriseListAdapter enterpriseListAdapter;

    private List<EntAddressBookInfo> list = new ArrayList<>();
    private String mIconPath;
    private int mIconId;
    private int searchSeq;

    /**
     * Stop the portrait query annotation, When exit or query the contact again,
     * Determine whether is less than zero, Is less than the does not invoke the portrait query method
     * 停止查询头像的标注，当退出界面或者再次查询联系人的时候，判断是否小于0，小于则不再调用查询头像的方法
     */
    private int stopFlag;

    /**
     * Waiting for querying a contact ID, In this user interface invokes the query again,
     * Ensure that in the last query results after
     * 等候查询联系人的标示，在本界面再次调用查询的时，必须保证在上次的查询结果出来之后进行
     */
    private boolean waitSearch = true;

    private String[] eActions = new String[]{
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_RESULT,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_NULL,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_FAILED,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_DEF_PHOTO,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED,
            CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_SYS_PHOTO
    };

    @Override
    public void initializeComposition() {
        setContentView(R.layout.activity_enterprise_addr_book);
        eaddrBack = (ImageView)findViewById(R.id.book_back);
        eaddrKeys = (EditText)findViewById(R.id.book_keys);
        eaddrSearch = (ImageView)findViewById(R.id.book_right);
        eaddrList = (ListView)findViewById(R.id.search_list);

        enterpriseListAdapter = new EnterpriseListAdapter(this);
        eaddrList.setAdapter(enterpriseListAdapter);

        eaddrBack.setOnClickListener(this);
        eaddrSearch.setOnClickListener(this);
        eaddrList.setOnItemClickListener(this);
    }

    @Override
    public void initializeData() {

    }

    /**
     * This method is used to show Toast content.
     * @param text Indicates show content
     */
    public void showToast(String text)
    {
        if (toast == null)
        {
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }
        else
        {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    /**
     * This method is used to cancel Toast content.
     */
    public void cancelToast()
    {
        if (toast != null)
        {
            toast.cancel();
        }
    }

    /**
     * This method is used to the date(Contacts list).
     */
    public void refreshEnterpriseList(final List<EntAddressBookInfo> list)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enterpriseListAdapter.setData(list);
                enterpriseListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        cancelToast();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        LocBroadcast.getInstance().registerBroadcast(this, eActions);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.book_back:
                stopFlag = -1;
                list.clear();
                LocBroadcast.getInstance().unRegisterBroadcast(this, eActions);
                finish();
                break;
            case R.id.book_right:
                String keywords = eaddrKeys.getText().toString();
                if(null == keywords || keywords.isEmpty())
                {
                    showToast("Search content can not be empty!");
                }
                else if (waitSearch)
                {
                    if (list.size() == 0)
                    {
                        searchSeq = EnterpriseAddressBookMgr.getInstance().searchContacts(keywords);
                        waitSearch = false;
                    }
                    else
                    {
                        stopFlag = -1;
                        list.clear();
                        searchSeq = EnterpriseAddressBookMgr.getInstance().searchContacts(keywords);
                        waitSearch = false;
                    }
                }
                else
                {
                    showToast("Querying, Please try again later...");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(IntentConstant.EADDR_INFO_ACTIVITY_ACTION);
        intent.putExtra(UIConstants.CONTACT_INFO, list.get(position));
        ActivityUtil.startActivity(this, intent);
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case UIConstants.ENTERPRISE_SEARCH_SUCCESS:
                    QueryContactsInfoResult contactsInfoResult = (QueryContactsInfoResult) msg.obj;
                    if (contactsInfoResult.getQuerySeq() == searchSeq)
                    {
                        list = contactsInfoResult.getList();
                    }
                    refreshEnterpriseList(list);
                    if (null == list || list.size() == 0)
                    {
                        return;
                    }
                    stopFlag = 0;
                    EnterpriseAddressBookMgr.getInstance().getUserIcon(list.get(stopFlag).getEaddrAccount()); //查询用户头像
                    waitSearch = true;
                    break;
                case UIConstants.ENTERPRISE_SEARCH_NULL:
                    list.clear();
                    refreshEnterpriseList(list);
                    waitSearch = true;
                    showToast("There is no inquiry to the contact!");
                    eaddrKeys.setText("");
                    break;
                case UIConstants.ENTERPRISE_SEARCH_FAILED:
                    list.clear();
                    refreshEnterpriseList(list);
                    waitSearch = true;
                    showToast("Search contact failed!");
                    break;
                case UIConstants.ENTERPRISE_HEAD_SYS:
                    if (msg.obj instanceof EntAddressBookIconInfo)
                    {
                        EntAddressBookIconInfo iconInfo = (EntAddressBookIconInfo) msg.obj;
                        for (int i = 0; i < list.size(); i++)
                        {
                            if (list.get(i).getEaddrAccount().equals(iconInfo.getAccount()) && iconInfo.getIconId() >= 0)
                            {
                                mIconId = iconInfo.getIconId();
                                list.get(i).setSysIconID(mIconId);
                                break;
                            }
                        }

                        if (stopFlag >= 0 && stopFlag < list.size() - 1)
                        {
                            EnterpriseAddressBookMgr.getInstance().getUserIcon(list.get(++stopFlag).getEaddrAccount()); //查询用户头像
                        }
                        refreshEnterpriseList(list);
                    }
                    break;
                case UIConstants.ENTERPRISE_HEAD_DEF:
                    if (msg.obj instanceof EntAddressBookIconInfo)
                    {
                        EntAddressBookIconInfo defIconInfo = (EntAddressBookIconInfo) msg.obj;
                        String defIcon = defIconInfo.getIconFile();
                        for (int j = 0; j < list.size(); j++)
                        {
                            if (list.get(j).getEaddrAccount().equals(defIconInfo.getAccount()) &&  !"".equals(defIcon))
                            {
                                mIconPath = Environment.getExternalStorageDirectory() + File.separator + "ECSDKDemo" + File.separator + "icon" + File.separator + defIcon;
                                list.get(j).setHeadIconPath(mIconPath);
                                break;
                            }
                        }

                        if (stopFlag >= 0 && stopFlag < list.size() - 1)
                        {
                            EnterpriseAddressBookMgr.getInstance().getUserIcon(list.get(++stopFlag).getEaddrAccount()); //查询用户头像
                        }
                        refreshEnterpriseList(list);
                    }
                    break;
                case UIConstants.ENTERPRISE_HEAD_NULL:
                    EntAddressBookIconInfo defIconInfo = (EntAddressBookIconInfo) msg.obj;
                    for (int w = 0; w < list.size(); w++)
                    {
                        if (list.get(w).getEaddrAccount().equals(defIconInfo.getAccount()))
                        {
                            list.get(w).setSysIconID(10);
                        }

                        if (stopFlag >= 0 && stopFlag < list.size() - 1)
                        {
                            EnterpriseAddressBookMgr.getInstance().getUserIcon(list.get(++stopFlag).getEaddrAccount()); //查询用户头像
                        }
                        refreshEnterpriseList(list);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            if (null != this.getCurrentFocus())
            {
                if (null != this.getCurrentFocus().getWindowToken())
                {
                    methodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken()
                            , InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_RESULT:
                Message msgContactSuccess = handler.obtainMessage(UIConstants.ENTERPRISE_SEARCH_SUCCESS, obj);
                handler.sendMessage(msgContactSuccess);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_NULL:
                Message msgContactNull = handler.obtainMessage(UIConstants.ENTERPRISE_SEARCH_NULL, obj);
                handler.sendMessage(msgContactNull);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_CONTACT_FAILED:
                Message msgContactFailed = handler.obtainMessage(UIConstants.ENTERPRISE_SEARCH_FAILED, obj);
                handler.sendMessage(msgContactFailed);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_SYS_PHOTO:
                Message msgSys = handler.obtainMessage(UIConstants.ENTERPRISE_HEAD_SYS, obj);
                handler.sendMessage(msgSys);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_DEF_PHOTO:
                Message msgDef = handler.obtainMessage(UIConstants.ENTERPRISE_HEAD_DEF, obj);
                handler.sendMessage(msgDef);
                break;
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_HEAD_PHOTO_FAILED:
                Message msgFailed = handler.obtainMessage(UIConstants.ENTERPRISE_HEAD_NULL, obj);
                handler.sendMessage(msgFailed);
                break;
            default:
                break;
        }
    }
}
