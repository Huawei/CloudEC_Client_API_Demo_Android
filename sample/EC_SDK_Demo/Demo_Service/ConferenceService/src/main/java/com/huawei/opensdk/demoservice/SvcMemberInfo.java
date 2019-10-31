package com.huawei.opensdk.demoservice;

import com.huawei.ecterminalsdk.base.TsdkWatchAttendees;
import com.huawei.opensdk.commonservice.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SvcMemberInfo {
    private static final String TAG = SvcMemberInfo.class.getSimpleName();

    private final int MAX_SMALL_WIND_NUM = 3;

    private List<Long> svcLabel;

    private List<Member> watchableMemberList;

    private List<TsdkWatchAttendees> currentWatchMemberList;

    private List<TsdkWatchAttendees> beWatchMemberList;

    private int currentWatchPage;

    private int totalWatchablePage;


    public SvcMemberInfo() {
        this.svcLabel = new ArrayList<>();
        this.watchableMemberList = new ArrayList<>();
        this.beWatchMemberList = new ArrayList<>();
        this.currentWatchMemberList = new ArrayList<>();
        this.currentWatchPage = 1;
        this.totalWatchablePage = 1;
    }

    public List<Long> getSvcLabel() {
        return svcLabel;
    }

    public void setSvcLabel(List<Long> svcLabel) {
        this.svcLabel = svcLabel;
    }

    public List<Member> getWatchableMemberList() {
        return watchableMemberList;
    }

    public void setWatchableMemberList(List<Member> watchableMemberList) {
        this.watchableMemberList = watchableMemberList;
    }

    public List<TsdkWatchAttendees> getCurrentWatchMemberList() {
        return currentWatchMemberList;
    }

    public void setCurrentWatchMemberList(List<TsdkWatchAttendees> currentWatchMemberList) {
        this.currentWatchMemberList = currentWatchMemberList;
    }

    public List<TsdkWatchAttendees> getBeWatchMemberList() {
        return beWatchMemberList;
    }

    public void setBeWatchMemberList(List<TsdkWatchAttendees> beWatchMemberList) {
        this.beWatchMemberList = beWatchMemberList;
    }

    public int getCurrentWatchPage() {
        return currentWatchPage;
    }

    public void setCurrentWatchPage(int currentWatchPage) {
        this.currentWatchPage = currentWatchPage;
    }

    public int getTotalWatchablePage() {
        return totalWatchablePage;
    }

    public void setTotalWatchablePage(int totalWatchablePage) {
        this.totalWatchablePage = totalWatchablePage;
    }

    public int getCurrentWatchSmallCount(){

        if (beWatchMemberList.size() > 0) {
            return beWatchMemberList.size() - 1;
        }else{
            return 0;
        }
    }

    public boolean svcMemberListUpdateHandle(List<Member> members) {

        LogUtil.i(TAG, "svcMemberListUpdateHandle() enter");

        if (!watchableMemberList.isEmpty()) {
            watchableMemberList.clear();
        }

        for (Member member : members) {
            if (member.isSelf()) {
                continue;
            }

            if (member.getStatus() != ConfConstant.ParticipantStatus.IN_CONF) {
                continue;
            }

            if (!member.isVideo())
            {
                continue;
            }

            watchableMemberList.add(member);
        }

        int watchableCount = watchableMemberList.size();
        if (watchableCount > 0)
        {
            int currentWatchPage = this.currentWatchPage;
            int totalWatchablePage = this.totalWatchablePage;

            //更新当前正在看的页面，和总页面数
            while (watchableCount <= (this.currentWatchPage - 1) * MAX_SMALL_WIND_NUM) {
                this.currentWatchPage--;
                if (this.currentWatchPage == 1) {
                    break;
                }
            }
            if (watchableCount % MAX_SMALL_WIND_NUM == 0)
            {
                this.totalWatchablePage = watchableCount / MAX_SMALL_WIND_NUM;
            }
            else {
                this.totalWatchablePage = watchableCount / MAX_SMALL_WIND_NUM + 1;
            }


            //更新当前可选看的小窗口数
            int maxSmallWatchCount = MAX_SMALL_WIND_NUM;
            if (this.totalWatchablePage == 1)
            {
                if (watchableCount <= MAX_SMALL_WIND_NUM) {
                    maxSmallWatchCount = watchableCount;
                }
            } else {
                if (this.totalWatchablePage > this.currentWatchPage) {
                    maxSmallWatchCount = MAX_SMALL_WIND_NUM;
                } else {
                    maxSmallWatchCount = watchableCount - (this.currentWatchPage - 1) * MAX_SMALL_WIND_NUM;
                }
            }

            boolean needRewatch = updateBeWatchMemberList(maxSmallWatchCount, false);
            if (needRewatch){
                return true;
            }

            if ((currentWatchPage != this.currentWatchPage) || (totalWatchablePage != this.totalWatchablePage)) {
                return true;
            }

            return false;
        }
        else
        {
            beWatchMemberList.clear();
            currentWatchMemberList.clear();

            this.currentWatchPage = 1;
            this.totalWatchablePage = 1;

            return false;
        }
    }


    public boolean updateBeWatchMemberList(int needSmallWatchCount, boolean forceUpdateSmallWnd) {

        LogUtil.i(TAG, "updateBeWatchMemberList() enter, needSmallWatchCount->" + needSmallWatchCount);

        TsdkWatchAttendees tempWatch;
        boolean update_big_wnd = false;
        boolean update_small_wnd = false;
        int loop;

        if (beWatchMemberList.size() == 0) {
            tempWatch = new TsdkWatchAttendees();
            tempWatch.setLabel(svcLabel.get(0));
            tempWatch.setWidth(960);
            tempWatch.setHeight(540);
            tempWatch.setNumber("");
            beWatchMemberList.add(tempWatch);

            update_big_wnd = true;
        }

        tempWatch = beWatchMemberList.get(0);

        // 大画面被指定过
        if (!tempWatch.getNumber().isEmpty() && !tempWatch.getNumber().equals("")) {
            Member tempMember =  getMemberByNumber(tempWatch.getNumber());
            if (tempMember == null) {
                tempWatch.setNumber("");
                update_big_wnd = true;
            }
        }

        if (forceUpdateSmallWnd){
            update_small_wnd = true;
        } else if (needSmallWatchCount != beWatchMemberList.size() - 1) {
            update_small_wnd = true;
        } else {

            for (loop = 1; loop < beWatchMemberList.size(); loop++) {
                tempWatch = beWatchMemberList.get(loop);

                if (tempWatch == null) {
                    continue;
                }
                Member tempMember = getMemberByNumber(tempWatch.getNumber());
                if (tempMember == null) {
                    update_small_wnd = true;
                    break;
                }
            }
        }


        int startIndex= 0;
        if (update_small_wnd) {

            TsdkWatchAttendees backupBigWatch = new TsdkWatchAttendees();
            tempWatch = beWatchMemberList.get(0);
            backupBigWatch.setLabel(tempWatch.getLabel());
            backupBigWatch.setWidth(tempWatch.getWidth());
            backupBigWatch.setHeight(tempWatch.getHeight());
            backupBigWatch.setNumber(tempWatch.getNumber());

            beWatchMemberList.clear();

            beWatchMemberList.add(backupBigWatch);

            startIndex = (currentWatchPage - 1) * MAX_SMALL_WIND_NUM;
            for (loop = 0; loop < needSmallWatchCount; loop++) {
                tempWatch = new TsdkWatchAttendees();
                tempWatch.setLabel(svcLabel.get(loop+1));
                tempWatch.setWidth(160);
                tempWatch.setHeight(90);
                if (watchableMemberList.size() > 0)
                {
                    // 规避下标越界异常
                    Member tempMember = watchableMemberList.get(startIndex++);
                    if (tempMember != null) {
                        tempWatch.setNumber(tempMember.getNumber());
                    }
                }
                beWatchMemberList.add(tempWatch);
            }

        }

        return (update_big_wnd || update_small_wnd);
    }

    public boolean setBeWatchMemberList(int beWatchPage) {

        LogUtil.i(TAG, "setBeWatchMemberList() enter, beWatchPage->" + beWatchPage);

        boolean forceUpdateSmallWnd = false;
        int lastWatchPage = this.currentWatchPage;

        if (beWatchPage >= this.totalWatchablePage) {
            this.currentWatchPage = this.totalWatchablePage;
        } else if (beWatchPage <= 1) {
            this.currentWatchPage = 1;
        } else {
            this.currentWatchPage = beWatchPage;
        }

        if (lastWatchPage != this.currentWatchPage) {

            forceUpdateSmallWnd = true;
        }

        int watchableCount = watchableMemberList.size();
        int maxSmallWatchCount = MAX_SMALL_WIND_NUM;
        if (this.totalWatchablePage == 1) {
            if (watchableCount < MAX_SMALL_WIND_NUM) {
                maxSmallWatchCount = watchableCount;
            }
        } else {
            if (this.totalWatchablePage > this.currentWatchPage) {
                maxSmallWatchCount = MAX_SMALL_WIND_NUM;
            } else {
                maxSmallWatchCount = watchableCount - (this.currentWatchPage - 1) * MAX_SMALL_WIND_NUM;
            }
        }

        boolean needReWatch = updateBeWatchMemberList(maxSmallWatchCount, forceUpdateSmallWnd);
        if (needReWatch) {
            return true;
        }

        return false;
    }

    private Member getMemberByNumber(String number) {

        for (Member tempMember : watchableMemberList) {
            if (tempMember.getNumber().equals(number)) {
                return tempMember;
            }
        }

        return null;
    }
}
