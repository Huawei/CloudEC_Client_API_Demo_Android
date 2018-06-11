package com.huawei.opensdk.ec_sdk_demo.logic.im;

import com.huawei.data.entity.RecentChatContact;

import java.io.Serializable;
import java.util.Comparator;

public class RecentChatComparator implements Comparator<RecentChatContact>, Serializable
{
    @Override
    public int compare(RecentChatContact lhs, RecentChatContact rhs)
    {
        return compareRecentChat(lhs.getEndTime().getTime(), rhs.getEndTime().getTime());
    }

    private int compareRecentChat(long o1, long o2)
    {
        long len = o2 - o1;
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