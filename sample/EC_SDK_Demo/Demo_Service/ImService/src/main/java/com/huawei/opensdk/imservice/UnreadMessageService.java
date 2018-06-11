package com.huawei.opensdk.imservice;

import android.text.TextUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is about the type Unread message service.
 * 未读消息处理类
 */
public class UnreadMessageService
{
    /**
     * The Unread message map.
     * 未读消息集合
     */
    public Map<String, Integer> unreadMessageMap = new ConcurrentHashMap<String, Integer>();

    /**
     * Instance object of UnreadMessageService component.
     * UnreadMessageService对象实例
     */
    private static volatile UnreadMessageService instance;

    private UnreadMessageService()
    {
    }

    /**
     * This method is used to get instance object of UnreadMessageService.
     * 获取UnreadMessageService对象实例
     * @return ImMgr Return instance object of UnreadMessageService
     *               返回一个UnreadMessageService对象实例
     */
    public static UnreadMessageService getInstance()
    {
        if (instance == null)
        {
            instance = new UnreadMessageService();
        }
        return instance;
    }

    /**
     * This method is used to save unread message.
     * 通过账号、消息的map集合来保存未读消息
     *
     * @param account        Indicates the account
     *                       账号
     * @param unreadMsgCount Indicates the unread msg count
     *                       未读消息数量
     */
    public synchronized void saveUnreadMessage(String account, int unreadMsgCount)
    {
        if (unreadMessageMap != null)
        {
            unreadMessageMap.put(account, unreadMsgCount);
        }
    }

    /**
     * This method is used to get unread message count by account.
     * 通过账号获取对应的未读消息数量
     *
     * @param account Indicates the account
     *                账号
     * @return int Return the unread message count by account
     *             返回获取到的未读消息个数
     */
    public synchronized int getUnreadMessageCountByAccount(String account)
    {
        Integer number = null;
        try
        {
            number = unreadMessageMap.get(account);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (number == null)
        {
            return 0;
        }
        else
        {
            return number.intValue();
        }
    }

    /**
     * This method is used to get unread message count.
     *
     * @return int Return the unread message count
     */
    public synchronized int getUnreadMessageCount()
    {
        int sumCount = 0;
        for (Map.Entry<String, Integer> entry : unreadMessageMap.entrySet())
        {
            sumCount += entry.getValue();
        }
        return sumCount;
    }

    /**
     * This method is used to clear unread map.
     * 清空map集合
     */
    public void clearUnreadMap()
    {
        unreadMessageMap.clear();
    }

    /**
     * This method is used to clear unread message account.
     * 清理此未读消息的发送方账号
     *
     * @param account Indicates the account
     *                账号
     */
    public void clearUnreadMessageAccount(String account)
    {
        if (!TextUtils.isEmpty(account))
        {
            unreadMessageMap.remove(account);
        }
    }

}
