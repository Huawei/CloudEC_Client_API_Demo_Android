package com.huawei.opensdk.ec_sdk_demo.logic.im;

import com.huawei.data.entity.InstantMessage;

/**
 * This class is about message item type.
 */
public class MessageItemType
{
    /**
     * The enum Item type.
     */
    public enum ItemType
    {
        /**
         * No msg view item type.
         */
        NoMsgView,
        /**
         * Msg send text item type.
         */
        MsgSendText,
        /**
         * Msg receive text item type.
         */
        MsgReceiveText,
        /**
         * Msg send pic item type.
         */
        MsgSendPic,
        /**
         * Msg receive pic item type.
         */
        MsgReceivePic,
        /**
         * Msg send audio item type.
         */
        MsgSendAudio,
        /**
         * Msg receive audio item type.
         */
        MsgReceiveAudio,
        /**
         * Msg send video item type.
         */
        MsgSendVideo,
        /**
         * Msg receive video item type.
         */
        MsgReceiveVideo,
        /**
         * Msg send file item type.
         */
        MsgSendFile,
        /**
         * Msg receive file item type.
         */
        MsgReceiveFile;
    }

    /**
     * Instantiates a new Message item type.
     */
    public MessageItemType()
    {
    }

    /**
     * The Instant msg.
     */
    public InstantMessage instantMsg = null;

    /**
     * The Content.
     */
    public CharSequence content = null;

    /**
     * The Progress.
     */
    public int progress = -1;
}
