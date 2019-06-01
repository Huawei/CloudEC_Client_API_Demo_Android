package com.huawei.opensdk.ec_sdk_demo.logic.im;


import java.io.Serializable;
import java.util.Comparator;


public class ChatComparator implements Comparator<MessageItemType>,Serializable
{
    @Override
    public int compare(MessageItemType itemType1, MessageItemType itemType2)
    {
        return compareChatMessage(itemType1.chatMsgInfo.getUtcStamp(), itemType2.chatMsgInfo.getUtcStamp());
    }

    private int compareChatMessage(long o1, long o2)
    {
        long len = o1 - o2;
        if (len > 0)
        {
            return 1;
        }
        if (len < 0)
        {
            return -1;
        }
        return 0;
    }
}
