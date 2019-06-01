package com.huawei.opensdk.contactservice.eaddr;

import android.text.TextUtils;
import android.util.Log;

import com.huawei.ecterminalsdk.base.TsdkContactsInfo;
import com.huawei.ecterminalsdk.base.TsdkDepartmentInfo;
import com.huawei.ecterminalsdk.base.TsdkGetIconParam;
import com.huawei.ecterminalsdk.base.TsdkGetIconResult;
import com.huawei.ecterminalsdk.base.TsdkIconInfo;
import com.huawei.ecterminalsdk.base.TsdkSearchContactsParam;
import com.huawei.ecterminalsdk.base.TsdkSearchContactsResult;
import com.huawei.ecterminalsdk.base.TsdkSearchDepartmentParam;
import com.huawei.ecterminalsdk.base.TsdkSearchDepartmentResult;
import com.huawei.ecterminalsdk.models.TsdkCommonResult;
import com.huawei.ecterminalsdk.models.TsdkManager;
import com.huawei.ecterminalsdk.models.eaddr.TsdkEAddrManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is about enterprise address book manager.
 * 通讯录模块管理类
 */
public class EnterpriseAddressBookMgr {

    private static final String TAG = EnterpriseAddressBookMgr.class.getSimpleName();

    /**
     * The EnterpriseAddressBookMgr function object.
     * EnterpriseAddressBookMgr对象
     */
    private static EnterpriseAddressBookMgr instance;

    /**
     * UI notification.
     * 回调接口对象
     */
    private IEntAddressBookNotification notification;

    /**
     * The TsdkEAddrManager object.
     * TsdkEAddrManager对象
     */
    private TsdkEAddrManager tsdkEAddrManager;

    /**
     * Query the contact's serial number
     * 查询联系人的序列号
     */
    private int queryContactsInfoSeq;

    /**
     * Query the serial number of the contact avatar
     * 查询联系人头像的序列号
     */
    private int queryContactsIconSeq;

    /**
     * Query the contact department's serial number
     * 查询联系人部门的序列号
     */
    private int queryDepartmentSeq;

    /**
     * Query the serial number of your own information
     * 查询自己信息的序列号
     */
    private int querySelfInfoSeq;

    /**
     * Query the serial number of your own icon
     * 查询自己头像的序列号
     */
    private int querySelfIconSeq;

    /**
     * Map collection When querying a user's avatar
     * 查询用户头像时的map集合
     */
    private static Map<Integer, String>querySeqAccountMap = new HashMap<>();

    /**
     * List of contacts that are queried
     * 查询到的联系人列表
     */
    private List<TsdkContactsInfo> list;

    /**
     * This method is used to get contacts list
     * 获取查询到的联系人列表
     * @return List<TsdkContactsInfo>      Return the list of contacts that are queried
     *                                     返回查询到的联系人列表
     */
    public List<TsdkContactsInfo> getList()
    {
        return list;
    }

    /**
     * This is a constructor of this class
     * 构造函数
     */
    public EnterpriseAddressBookMgr() {
        tsdkEAddrManager = TsdkManager.getInstance().geteAddrManager();
        queryContactsInfoSeq = 1;
        queryContactsIconSeq = 1;
        queryDepartmentSeq = 1;
    }

    /**
     * This method is used to get instance object of EnterpriseAddressBookMgr.
     * 获取EnterpriseAddressBookMgr对象实例
     * @return EnterpriseAddressBookMgr Return instance object of EnterpriseAddressBookMgr
     *                                  返回一个对象的示例
     */
    public static EnterpriseAddressBookMgr getInstance()
    {
        if (instance == null) {
            instance = new EnterpriseAddressBookMgr();
        }
        return instance;
    }

    /**
     * This method is used to register EnterpriseAddressBookMgr module UI callback.
     * 注册回调
     * @param notification
     */
    public void registerNotification(IEntAddressBookNotification notification)
    {
        this.notification = notification;
    }

    /**
     * This method is used to search self's information.
     * 获取自己的信息
     * @param keyWords Indicates keyWords
     *                 搜索条件
     * @return int Return seq
     *             返回查询的序列号
     */
    public int searchSelfInfo(String keyWords)
    {
        querySelfInfoSeq = searchContacts(keyWords);
        return querySelfInfoSeq;
    }

    /**
     * This method is used to search contact's information.
     * 获取联系人的信息
     * @param keyWords Indicates keyWords
     *                 搜索条件
     * @return int Return seq
     *             返回查询的序列号
     */
    public int searchContacts(String keyWords)
    {
        int seq = queryContactsInfoSeq++;
        if (null == keyWords)
        {
            Log.e(TAG, "Search condition is empty");
        }

        TsdkSearchContactsParam searchContactsParam = new TsdkSearchContactsParam();
        searchContactsParam.setDepartmentId("");
        searchContactsParam.setIsExactSearch(0);
        searchContactsParam.setPageIndex(1);
        searchContactsParam.setSearchKeyword(keyWords);
        searchContactsParam.setSeqNo(seq);

        int result = tsdkEAddrManager.searchContacts(searchContactsParam);

        Log.i(TAG, "searchResult -->" + result);
        return seq;
    }

    /**
     * This method is used to get self's icon.
     * 获取自己的头像
     * @param selfAccount Indicates account.
     *                    登陆的账号
     * @return int Return the seq
     *             返回查询的序列号
     */
    public int getSelfIcon(String selfAccount)
    {
        querySelfIconSeq = getUserIcon(selfAccount);
        return querySelfIconSeq;
    }

    /**
     * This method is used to get user's icon.
     * 获取用户头像
     * @param account Indicates account.
     *                用户账号
     * @return int Return the seq
     *             返回查询的序列号
     */
    public int getUserIcon(String account)
    {
        int seq = queryContactsIconSeq++;
        TsdkGetIconParam iconParam = new TsdkGetIconParam();
        iconParam.setSeqNo(seq);
        iconParam.setAccount(account);
        querySeqAccountMap.put(seq, account);
        int result = tsdkEAddrManager.getUserIcon(iconParam);

        if (result != 0)
        {
            Log.e(TAG, "search user icon failed -->" + result);
        }
        return seq;

    }

    /**
     * This method is used to search department structure.
     * 搜索部门结构
     * @param departmentId  Indicates department id.
     *                      部门id
     * @return int          Return the seq
     *                      返回查询的序列号
     */
    public int searchDepartment(String  departmentId)
    {
        int reqNo = queryDepartmentSeq++;

        TsdkSearchDepartmentParam searchDepartmentParam = new TsdkSearchDepartmentParam();
        searchDepartmentParam.setSeqNo(reqNo);
        searchDepartmentParam.setDepartmentId(departmentId);
        int result = tsdkEAddrManager.searchDepartment(searchDepartmentParam);

        if (result != 0)
        {
            Log.e(TAG, "search department failed -->" + result);
        }
        return reqNo;
    }

    /**
     * This method is used to set system icon.
     * 设置系统头像
     * @param resId    Indicates system icon id.
     *                 头像id
     * @return int     Return the result of setting system icon. If success return 0,otherwise return corresponding error code
     *                 返回设置系统头像的结果，取值；成功返回0，失败返回相应的错误码
     */
    public int setSystemIcon(int resId)
    {
        int result = tsdkEAddrManager.setSystemIcon(resId);
        if (result != 0)
        {
            Log.e(TAG, "Set user system icon filed, result -->" + result);
        }
        return result;
    }

    /**
     * This method is used to set self-defined icon.
     * 设置自定义头像
     * @param smallIconFilePath  Indicates the small icon file path
     *                           小尺寸头像路径
     * @param mediumIconFilePath Indicates the medium icon file path
     *                           中等尺寸头像路径
     * @param largeIconFilePath  Indicates the large icon file path
     *                           大尺寸头像路径
     * @return int               Return the result of setting system icon. If success return 0,otherwise return -1
     *                           返回设置自定义头像的结果，取值；成功返回0，失败返回-1
     */
    public int setDefinedIcon(String smallIconFilePath, String mediumIconFilePath, String largeIconFilePath)
    {
        TsdkIconInfo iconInfo = new TsdkIconInfo(smallIconFilePath, mediumIconFilePath, largeIconFilePath);
        String result = tsdkEAddrManager.setUserDefIcon(iconInfo);

        if (null != result)
        {
            return 0;
        }

        return -1;
    }

    /**
     * This method is used to get search contacts result.
     * 查询联系人信息返回结果
     *
     * @param querySeqNo              Indicates sequence number
     *                                查询序列号
     * @param result                  Indicates search result
     *                                查询结果
     * @param searchContactResult     Indicates search contact information
     *                                查询到的联系人信息
     */
    public void handleSearchContactResult(int querySeqNo, TsdkCommonResult result, TsdkSearchContactsResult searchContactResult) {
        //获取序列号-->和调用查询方法返回的序列号相一致
        int seqNo = querySeqNo;
        int ret = result.getResult();

        //获取联系人成功返回0
        if (ret == 0)
        {
            //获取查询到的联系人列表以及查询到的联系人总数
            List<TsdkContactsInfo> contactsInfos = searchContactResult.getContactInfo();
            int totalNum = searchContactResult.getTotalNum();
            //查询到0个联系人
            if (0 == totalNum)
            {
                notification.onEntAddressBookNotify(EntAddressBookConstant.Event.SEARCH_CONTACTS_NOT_FOUND, null);
            }
            //查询的登陆的用户信息
            else if (querySelfInfoSeq == seqNo)
            {
                notification.onEntAddressBookNotify(EntAddressBookConstant.Event.SEARCH_SELF_COMPLETE, contactsInfos);
            }
            //其余查询结果
            else
            {
                list = contactsInfos;
                QueryContactsInfoResult queryContactsResult = new QueryContactsInfoResult();
                queryContactsResult.setQuerySeq(seqNo);
                List<EntAddressBookInfo> contactsList = new ArrayList<>();
                for (TsdkContactsInfo contactsInfo : contactsInfos)
                {
                    EntAddressBookInfo entAddressBookInfo = new EntAddressBookInfo();
                    entAddressBookInfo.setEaddrAccount(contactsInfo.getStaffAccount());
                    entAddressBookInfo.setEaddrName(contactsInfo.getPersonName());
                    if (TextUtils.isEmpty(contactsInfo.getTerminal()))
                    {
                        entAddressBookInfo.setTerminal(contactsInfo.getTerminal2());
                    }
                    else
                    {
                        entAddressBookInfo.setTerminal(contactsInfo.getTerminal());
                    }
                    entAddressBookInfo.setEaddrDept(contactsInfo.getDepartmentName());
                    entAddressBookInfo.setAddress(contactsInfo.getAddress());
                    entAddressBookInfo.setEmail(contactsInfo.getEmail());
                    entAddressBookInfo.setGender(contactsInfo.getGender());
                    entAddressBookInfo.setMobile(contactsInfo.getMobile());
                    entAddressBookInfo.setSignature(contactsInfo.getSignature());
                    entAddressBookInfo.setTitle(contactsInfo.getTitle());
                    entAddressBookInfo.setZipCode(contactsInfo.getZipCode());
                    entAddressBookInfo.setSysIconID(10);
                    contactsList.add(entAddressBookInfo);
                }
                queryContactsResult.setList(contactsList);
                notification.onEntAddressBookNotify(EntAddressBookConstant.Event.SEARCH_CONTACTS_COMPLETE, queryContactsResult);
            }
            Log.i(TAG, totalNum + "Get the total number of returned contacts");
        }
        else
        {
            Log.e(TAG, "Search contacts failed, result -->" + result);
            notification.onEntAddressBookNotify(EntAddressBookConstant.Event.SEARCH_CONTACTS_FAILED, null);
        }
    }

    /**
     * This method is used to get icon result.
     * 查询头像信息返回结果
     * @param querySeqNo             Indicates sequence number
     *                               查询序列号
     * @param result                 Indicates search result
     *                               查询结果
     * @param getIconResult          Indicates get icon information
     *                               查询到的头像信息
     */
    public void handleGetIconResult(int querySeqNo, TsdkCommonResult result, TsdkGetIconResult getIconResult) {
        int ret = result.getResult();
        int seqNo = querySeqNo;

        //获取到某个用户的头像
        String account = querySeqAccountMap.get(seqNo);

        //获取头像成功返回0
        if (ret == 0)
        {
            int sysId = getIconResult.getIconId();
            String avatarFile = getIconResult.getIconPath();

            //查询的是登陆用户的头像
            if (querySelfIconSeq == seqNo)
            {
                EntAddressBookIconInfo selfIcon = new EntAddressBookIconInfo();
                selfIcon.setAccount(account);
                selfIcon.setIconFile(avatarFile);
                selfIcon.setIconId(sysId);
                selfIcon.setIconSeq(seqNo);
                notification.onEntAddressBookIconNotify(EntAddressBookConstant.Event.GET_SELF_ICON, selfIcon);
            }
            //获取到的是系统头像
            else if (sysId >= 0 && avatarFile.isEmpty())
            {
                EntAddressBookIconInfo iconInfo = new EntAddressBookIconInfo();
                iconInfo.setAccount(account);
                iconInfo.setIconId(sysId);
                iconInfo.setIconSeq(seqNo);
                notification.onEntAddressBookIconNotify(EntAddressBookConstant.Event.GET_CONTACTS_SYSTEM_ICON, iconInfo);
            }
            //获取到的是自定义头像
            else
            {
                EntAddressBookIconInfo iconInfo = new EntAddressBookIconInfo();
                iconInfo.setAccount(account);
                iconInfo.setIconFile(avatarFile);
                iconInfo.setIconSeq(seqNo);
                notification.onEntAddressBookIconNotify(EntAddressBookConstant.Event.GET_CONTACTS_CUSTOM_ICON, iconInfo);
            }
            Log.i(TAG, sysId + "System Avatar ID  " + avatarFile + "Custom Avatar filename");
        }
        else
        {
            //获取头像失败
            EntAddressBookIconInfo iconInfo = new EntAddressBookIconInfo();
            iconInfo.setAccount(account);
            iconInfo.setIconSeq(seqNo);
            Log.e(TAG, "User get icon failed, result -->" + result);
            notification.onEntAddressBookIconNotify(EntAddressBookConstant.Event.GET_CONTACTS_ICON_FAILED, iconInfo);
        }
    }

    /**
     * This method is used to get search department result.
     * 查询部门信息返回结果
     * @param querySeqNo             Indicates sequence number
     *                               查询序列号
     * @param result                 Indicates search result
     *                               查询结果
     * @param searchDeptResult       Indicates search department information
     *                               查询到的部门信息
     */
    public void handleSearchDepartmentResult(int querySeqNo, TsdkCommonResult result, TsdkSearchDepartmentResult searchDeptResult)
    {
        int ret = result.getResult();
        int seqNo = querySeqNo;

        if (ret == 0)
        {
            //获取查询到的部门列表以及查询到的部门总数
            List<TsdkDepartmentInfo> departmentInfoList = searchDeptResult.getDepartmentInfo();
            int totalNum = searchDeptResult.getItemNum();
            //查询到0个部门
            if (0 == totalNum)
            {
                notification.onEntAddressBookDepartmentNotify(EntAddressBookConstant.Event.SEARCH_DEPARTMENTS_NOT_FOUND, null);
            }
            //其余查询结果
            else
            {
                QueryDepartmentResult queryDepartmentResult = new QueryDepartmentResult();
                queryDepartmentResult.setQuerySeq(seqNo);
                queryDepartmentResult.setList(departmentInfoList);
                notification.onEntAddressBookDepartmentNotify(EntAddressBookConstant.Event.SEARCH_DEPARTMENT_RESULT, queryDepartmentResult);
            }
            Log.i(TAG, totalNum + "Get the total number of returned departments");
        }
        else
        {
            Log.e(TAG, "Search departments failed, result -->" + result);
            notification.onEntAddressBookDepartmentNotify(EntAddressBookConstant.Event.SEARCH_DEPARTMENT_FAILED, null);
        }
    }
}
