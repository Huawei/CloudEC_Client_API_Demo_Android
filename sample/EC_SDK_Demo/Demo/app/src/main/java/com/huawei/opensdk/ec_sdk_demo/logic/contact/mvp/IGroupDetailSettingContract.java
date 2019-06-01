package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;

//import com.huawei.data.ConstGroup;
//import com.huawei.data.ConstGroupContact;

import com.huawei.opensdk.imservice.ImChatGroupInfo;

/**
 * Group information related functional interface.
 */
public interface IGroupDetailSettingContract
{
    interface IGroupDetailSettingView
    {
        /**
         * Load user head image.
         */
        void loadHead();

        void updateTotalMember(int count);

        /**
         * Modify group info.
         * @param imChatGroupInfo
         */
        void updateGroupInfo(ImChatGroupInfo imChatGroupInfo);

        void toast(int id);

        void finishActivity();
    }

    interface IGroupDetailSettingPresenter
    {
        void setChatGroupInfo(ImChatGroupInfo chatGroupInfo);

//        List<ConstGroupContact> getGroupMembers();

        void enterChat();

        void showGroupMembers();

        /**
         * Set group type.
         */
        void lockGroup();

        void clearHistory();

        void leaveGroup();

        void enterAddMember();

        void enterDelMembers();

        /**
         * Register broadcast.
         */
        void registerBroadcast();

        /**
         * Unregister broadcast.
         */
        void unregisterBroadcast();

        /**
         * Query group members.
         */
        void queryGroupMembers();

        /**
         * Leave group.
         */
        void quitGroup();

        /**
         * Modify the group info.
         * @param imChatGroupInfo
         */
        void modifyGroup(ImChatGroupInfo imChatGroupInfo, int type);
    }
}
