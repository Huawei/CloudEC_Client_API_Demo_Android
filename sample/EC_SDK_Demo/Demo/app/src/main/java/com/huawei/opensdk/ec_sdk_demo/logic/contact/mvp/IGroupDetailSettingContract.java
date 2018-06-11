package com.huawei.opensdk.ec_sdk_demo.logic.contact.mvp;

import com.huawei.data.ConstGroup;
import com.huawei.data.ConstGroupContact;

import java.util.List;

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
         * @param obj
         */
        void updateGroupInfo(String obj);
    }

    interface IGroupDetailSettingPresenter
    {
        void setConstGroup(ConstGroup constGroup);

        List<ConstGroupContact> getGroupMembers();

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
         * @param constGroup
         */
        void modifyGroup(ConstGroup constGroup);
    }
}
