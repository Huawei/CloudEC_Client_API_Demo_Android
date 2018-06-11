package com.huawei.opensdk.demoservice;

import android.content.Context;
import android.view.ViewGroup;

import com.huawei.meeting.ConfGLView;
import com.huawei.meeting.ConfInfo;

/**
 * This class is about data conf manager
 * 数据会议UI回调管理类
 */
public interface IDataConfMgr
{
    /**
     * This method is used to join data conf
     * 加入数据会议
     * @param confInfo 会议信息
     */
    void joinDataConf(ConfInfo confInfo);

    /**
     * This method is used to leave conf
     * 离开会议
     * @return
     */
    boolean leaveConf();

    /**
     * This method is used to release conf
     * 结束会议
     */
    void confRelease();

    /**
     * This method is used to terminate conf
     * 终止会议
     * @return
     */
    boolean terminateConf();

    /**
     * This method is used to set user role
     * 设置用户角色
     * @param userId 用户id
     * @param nRole 角色类别
     * @return
     */
    boolean setUserRole(int userId, int nRole);

    //邀请其他与会者进行共享
    boolean inviteShare();

    void attachSurfaceView(ViewGroup container, Context context);

    /**
     * This method is used to add remote video
     * 加载远端视频
     * @param userID 用户id
     * @param deviceID 设备id
     * @return
     */
    boolean attachRemoteVideo(long userID, long deviceID);

    /**
     * This method is used to add local video
     * 加载本地视频
     * @param userID 用户id
     * @param deviceID 设备id
     * @return
     */
    boolean attachLocalVideo(long userID, long deviceID);

    /**
     * This method is used to delete remote video
     * 删除远端视频
     * @param userID 用户id
     * @param deviceID 设备id
     * @return
     */
    boolean detachRemoteVideo(long userID, long deviceID);

    /**
     * This method is used to delete local video
     * 删除本地视频
     * @param userID 用户id
     * @param deviceID 设备id
     * @return
     */
    boolean detachLocalVideo(long userID, long deviceID);

    /**
     * This method is used to set Local video visible
     * 设置本地视频可见
     * @param visible 是否可见
     */
    void changeLocalVideoVisible(boolean visible);

}
