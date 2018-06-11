package com.huawei.opensdk.ec_sdk_demo.logic.im.mvp;

import android.text.TextUtils;
import android.util.Log;

import com.huawei.dao.impl.RecentChatContactDao;
import com.huawei.data.entity.RecentChatContact;
import com.huawei.opensdk.ec_sdk_demo.logic.im.RecentChatComparator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is about recent chat helper.
 */
public class RecentChatHelper
{
    private static RecentChatHelper instance = new RecentChatHelper();
    private final List<RecentSessionReceiver> sessionReceivers = new LinkedList<RecentSessionReceiver>();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static RecentChatHelper getInstance()
    {
        return instance;
    }

    /**
     * The interface Recent session receiver.
     */
    public interface RecentSessionReceiver
    {
        /**
         * Recent session changed.
         */
        void recentSessionChanged();

        /**
         * Unread message update notify.
         */
        void unreadMessageUpdateNotify();
    }

    /**
     * Register recent session receiver boolean.
     *
     * @param receiver the receiver
     * @return the boolean
     */
    public boolean registerRecentSessionReceiver(RecentSessionReceiver receiver)
    {
        if (receiver == null)
        {
            return false;
        }
        synchronized (new Object())
        {
            sessionReceivers.add(receiver);
        }
        return true;
    }

    /**
     * Unregister recent session receiver boolean.
     *
     * @param receiver the receiver
     * @return the boolean
     */
    public boolean unregisterRecentSessionReceiver(RecentSessionReceiver receiver)
    {
        if (receiver == null)
        {
            return false;
        }
        synchronized (new Object())
        {
            sessionReceivers.remove(receiver);
        }
        return true;
    }

    /**
     * On recent session changed.
     */
    public void onRecentSessionChanged()
    {
        synchronized (new Object())
        {
            for (RecentSessionReceiver receiver : sessionReceivers)
            {
                receiver.recentSessionChanged();
            }
        }
    }

    /**
     * On unread message update notify.
     */
    public void onUnreadMessageUpdateNotify()
    {
        synchronized (new Object())
        {
            for (RecentSessionReceiver receiver : sessionReceivers)
            {
                receiver.unreadMessageUpdateNotify();
            }
        }
    }



    private static RecentChatContact getRecentChatContact(String chatId, int msgType, String nickName)
    {
        RecentChatContact chatContact = RecentChatContactDao.getContact(chatId, msgType);

        if (chatContact == null)
        {
            chatContact = new RecentChatContact(chatId, msgType, nickName);
        }
        else if (TextUtils.isEmpty(chatContact.getNickname()))
        {
            chatContact.setNickname(nickName);
        }

        return chatContact;
    }

    /**
     * Sort.
     *
     * @param recentChatContacts the recent chat contacts
     */
    public static void sort(List<RecentChatContact> recentChatContacts)
    {
        try
        {
            Collections.sort(recentChatContacts, new RecentChatComparator());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("RecentChatHelper", e.toString());
        }
    }
}
