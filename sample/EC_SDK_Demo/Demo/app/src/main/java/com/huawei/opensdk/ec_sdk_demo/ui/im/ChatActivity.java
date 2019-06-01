package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.im.MessageItemType;
import com.huawei.opensdk.ec_sdk_demo.logic.im.emotion.ChatCommon;
import com.huawei.opensdk.ec_sdk_demo.logic.im.mvp.ChatContract;
import com.huawei.opensdk.ec_sdk_demo.logic.im.mvp.ChatPresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.ui.im.contact.HeadIconTools;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.util.SoftInputUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.TripleDialog;
import com.huawei.opensdk.imservice.ImChatMsgInfo;
import com.huawei.opensdk.imservice.ImConstant;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is about chat Activity.
 */
public class ChatActivity extends MVPBaseActivity<ChatContract.ChatView, ChatPresenter> implements ChatContract.ChatView, View.OnClickListener
{
    private static final int REFRESH_CHAT_MESSAGE = 100;
    private static final int SHOW_TOAST = 102;
    private static final int PERSONAL_STATUS = 103;

    private ImageView mCallBtn;
    private ImageView mContactsBtn;
    private ListView mChatLv;
    private ChatAdapter mAdapter;
    private TextView mTitleTv;
    private TextView mStatusTv;
    private TextView mInputtingTv;
    private ProgressDialog mLoadHistoryDialog;
    private Button mHistoryBtn;
    private ViewGroup mEmotionArea;
    private GridView mEmotionGridView;
    private EditText mInputEt;
    private ImageView mMoreBtn;
    private ImageView mSendBtn;
    private ImageView mEmotionBtn;
    private FrameLayout mMoreLayout;
    private FrameLayout mEmotionLayout;
    private ImageView mAudioLayout;
    private ImageView mRecordAudioIv;
    private LinearLayout mRecordAudioLayout;
    private LinearLayout mMoreArea;
    private ImageView mPictureSendBtn;
    private ImageView mVideoSendBtn;

    private String mMyAccount;
    private boolean isInput = false;
    private boolean isStart = true;
    private int lastLength; // 执行一次计时器前的文本长度
    private int currentLength; // 当前文本长度
    private Timer mTimer;
    private MyTimerTask myTimerTask;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case REFRESH_CHAT_MESSAGE:
                    if (msg.obj instanceof List)
                    {
                        List<MessageItemType> list = (List<MessageItemType>) msg.obj;
                        LogUtil.i(UIConstants.DEMO_TAG, "refresh chat list view, size = " + list.size());
                        mAdapter.setMessages(list);
                        mAdapter.notifyDataSetChanged();
                        setListViewSelection();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private Dialog mDeleteMsgDialog;

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.chat_im_single);
        mCallBtn = (ImageView) findViewById(R.id.call_top);
        mContactsBtn = (ImageView) findViewById(R.id.right_img);
        mSendBtn = (ImageView) findViewById(R.id.btn_chat_send);
        mEmotionBtn = (ImageView) findViewById(R.id.emotion_button);
        mMoreBtn = (ImageView) findViewById(R.id.btn_more);
        mTitleTv = (TextView) findViewById(R.id.title_text);
        mStatusTv = (TextView) findViewById(R.id.status_tv);
        mInputtingTv = (TextView) findViewById(R.id.inputting_tv);
        mHistoryBtn = (Button) findViewById(R.id.btn_history);
        mChatLv = (ListView) findViewById(R.id.chat_lv);
        mInputEt = (EditText) findViewById(R.id.et_txt_input);
        mEmotionLayout = (FrameLayout) findViewById(R.id.more_emotion_layout);
        mMoreLayout = (FrameLayout) findViewById(R.id.more_layout);
        mAudioLayout = (ImageView) findViewById(R.id.btn_audio_start);
        mRecordAudioLayout = (LinearLayout) findViewById(R.id.record_audio);
        mRecordAudioIv = (ImageView) findViewById(R.id.btn_say_pressed);
        mMoreArea = (LinearLayout) findViewById(R.id.more_area);
        mPictureSendBtn = (ImageView) findViewById(R.id.btn_picture_send);
        mVideoSendBtn = (ImageView) findViewById(R.id.btn_video_send);

        LayoutInflater inflater = LayoutInflater.from(this);
        mEmotionArea = (ViewGroup) inflater.inflate(R.layout.emotion_layout, null);
        mEmotionGridView = (GridView) mEmotionArea.findViewById(R.id.grid_more_emotion);
        mEmotionGridView.setAdapter(new EmotionAdapter(this));
        mEmotionGridView.setOnItemClickListener(itemClickListener);
        mChatLv.setAdapter(mAdapter);

        mTitleTv.setText(mPresenter.getName());
        if (mPresenter.isIsGroup())
        {
            mStatusTv.setVisibility(View.GONE);
        }
        else
        {
            mStatusTv.setVisibility(View.VISIBLE);
            mStatusTv.setText("[" + getString(R.string.offline) + "]");
        }

        if (mPresenter.getChatType() != ImConstant.ChatMsgType.SINGLE_CHAT)
        {
            mCallBtn.setVisibility(View.GONE);
            mContactsBtn.setImageResource(R.drawable.im_setting_group_selector);
        }

        mContactsBtn.setOnClickListener(this);
        mCallBtn.setOnClickListener(this);
        mHistoryBtn.setOnClickListener(this);
        mMoreBtn.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mEmotionBtn.setOnClickListener(this);
        mInputEt.setOnClickListener(this);
        mAudioLayout.setOnClickListener(this);
        mPictureSendBtn.setOnClickListener(this);
        mVideoSendBtn.setOnClickListener(this);
        mRecordAudioIv.setOnTouchListener(mRecordBtnListener);

        mInputEt.addTextChangedListener(mTextWatcher);

        if (!mPresenter.isIsGroup())
        {
            mPresenter.loadStatus();
        }

//        mPresenter.loadHistoryMessage();
        mPresenter.loadUnReadMessage();

        mChatLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                hideSoftBoard();
                hideUnderLayout();
                showDeleteDialog(position);
                return true;
            }
        });

        mChatLv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                hideSoftBoard();
                hideUnderLayout();
                MessageItemType item = mPresenter.getMessages().get(position);
                if (item.chatMsgInfo == null)
                {
                    return;
                }

//                InstantMessage message = item.instantMsg;
//                if (message.getMediaType() == MediaResource.MEDIA_PICTURE || message.getMediaType() == MediaResource.MEDIA_VIDEO)
//                {
//                    Intent intent = new Intent(IntentConstant.MEDIA_SCAN_ACTIVITY_ACTION);
//                    intent.putExtra(UIConstants.MEDIA_RESOURCE, message);
//                    ActivityUtil.startActivity(ChatActivity.this, intent);
//                }
            }
        });
    }

    @Override
    public void initializeData()
    {
        mAdapter = new ChatAdapter(this);
        Object date = getIntent().getSerializableExtra(UIConstants.CHAT_TYPE);
//        recentChat = getIntent().getSerializableExtra(UIConstants.RECENT_CHAT);
        mMyAccount = mPresenter.getMyAccount();
        mPresenter.initData(date);
        mPresenter.registerBroadcast();
        mPresenter.subscribeContactState();
    }

    private void showDeleteDialog(final int position)
    {
        final List<MessageItemType> messages = mPresenter.getMessages();
        final MessageItemType item = messages.get(position);

        final TripleDialog dialog = new TripleDialog(this);
        dialog.hideTitleButton();
        if (item.chatMsgInfo.getFromId().equals(mMyAccount))
        {
            dialog.setLeftText(R.string.opr_msg_withdraw);
            dialog.setRightText(R.string.delete);
        }
        else
        {
            dialog.hideDownloadButton();
            dialog.setRightText(R.string.delete);
        }
        dialog.setLeftButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.withdrawMessage(position);
            }
        });
        dialog.setRightButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.delHistoryMessage(position);
            }
        });
        dialog.show();
    }

    @Override
    public void updatePersonalStatus(final ImConstant.ImStatus status)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (status) {
                    case ON_LINE:
                        mStatusTv.setText("[" + getString(R.string.online) + "]");
                        break;
                    case BUSY:
                        mStatusTv.setText("[" + getString(R.string.busy) + "]");
                        break;
                    case XA:
                        mStatusTv.setText("[" + getString(R.string.leave) + "]");
                        break;
                    case AWAY:
                        mStatusTv.setText("[" + getString(R.string.offline) + "]");
                        break;
                    case DND:
                        mStatusTv.setText("[" + getString(R.string.dnd) + "]");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void updateGroupName(final String obj)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mTitleTv.setText(obj);
            }
        });
    }

    @Override
    public void showInputtingStatus(final boolean isInputting) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isInputting)
                {
                    mInputtingTv.setVisibility(View.VISIBLE);
                }
                else
                {
                    mInputtingTv.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void showWithdrawResult(final String origin) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMyAccount.equals(origin))
                {
                    Toast.makeText(ChatActivity.this, getString(R.string.you) + " " + getString(R.string.opr_msg_withdraw_hint), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ChatActivity.this, origin + " " + getString(R.string.opr_msg_withdraw_hint), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void hideUnderLayout()
    {
        mEmotionLayout.removeAllViews();
        mEmotionLayout.setVisibility(View.GONE);
        mRecordAudioLayout.setVisibility(View.GONE);
        mMoreArea.setVisibility(View.GONE);
        mMoreLayout.setVisibility(View.GONE);
        mAudioLayout.setSelected(false);
    }

    private View.OnTouchListener mRecordBtnListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    LogUtil.i(UIConstants.DEMO_TAG, "ACTION_DOWN");
                    mRecordAudioIv.setPressed(true);
                    mPresenter.startRecord();
                    break;
                case MotionEvent.ACTION_UP:
                    LogUtil.i(UIConstants.DEMO_TAG, "ACTION_UP");
                    mRecordAudioIv.setPressed(false);
//                    mPresenter.stopRecord();
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private void setListViewSelection()
    {
        if (mAdapter.getCount() > 0)
        {
            mChatLv.setSelection(mAdapter.getCount() - 1);
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            String msgContent = s.toString();
            currentLength = msgContent.trim().length();
            switchSendMsgBtn(currentLength);

            if (mPresenter.isIsGroup())
            {
                return;
            }

            if (isInput)
            {
                return;
            }

            if (0 == currentLength)
            {
                return;
            }

            if (isStart)
            {
                startTimer();
            }
        }
    };

    private void switchSendMsgBtn(int length)
    {
        if (length > 0)
        {
            mMoreBtn.setVisibility(View.GONE);
            mSendBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            mMoreBtn.setVisibility(View.VISIBLE);
            mSendBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected ChatContract.ChatView createView()
    {
        return this;
    }

    @Override
    protected ChatPresenter createPresenter()
    {
        return new ChatPresenter(this);
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            String ss = ChatCommon.EMOTION_STR.split("\\|")[position];
            if (null != mInputEt.getEditableText())
            {
                mInputEt.getEditableText().insert(mInputEt.getSelectionStart(), mPresenter.parseInnerEmotion(ss));
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent)
    {
        Object date = intent.getSerializableExtra(UIConstants.CHAT_TYPE);
        mPresenter.initData(date);
        initializeComposition();
        initBackView();
        mAdapter.clearMessages();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void refreshRecentChatList(List<MessageItemType> list)
    {
        Message msg = Message.obtain();
        msg.what = REFRESH_CHAT_MESSAGE;
        msg.obj = list;
        mHandler.sendMessage(msg);
    }

    @Override
    public void toast(final int id)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChatActivity.this, getString(id), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Get the roaming message for the first time
     */
    private void loadHistoryMessage()
    {
        if (mLoadHistoryDialog == null)
        {
            mLoadHistoryDialog = new ProgressDialog(this);
            mLoadHistoryDialog.setCanceledOnTouchOutside(false);
            mLoadHistoryDialog.setMessage(getString(R.string.loading));
            mLoadHistoryDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mLoadHistoryDialog.show();
    }

    private void dismissLoadHistoryDialog()
    {
        if (mLoadHistoryDialog != null && mLoadHistoryDialog.isShowing())
        {
            mLoadHistoryDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_history:
                mPresenter.loadMoreHistoryMessage();
                break;
            case R.id.btn_more:

                if (mMoreArea.getVisibility() == View.VISIBLE)
                {
                    mMoreLayout.setVisibility(View.GONE);
                    mMoreArea.setVisibility(View.GONE);
                    mEmotionLayout.setVisibility(View.GONE);
                    mRecordAudioLayout.setVisibility(View.GONE);
                    showSoftBoard();
                }
                else
                {
                    mMoreLayout.setVisibility(View.VISIBLE);
                    mMoreArea.setVisibility(View.VISIBLE);
                    mEmotionLayout.setVisibility(View.GONE);
                    mRecordAudioLayout.setVisibility(View.GONE);
                    hideSoftBoard();
                }
                break;
            case R.id.btn_audio_start:
                if (mRecordAudioLayout.getVisibility() == View.VISIBLE)
                {
                    mMoreLayout.setVisibility(View.GONE);
                    mRecordAudioLayout.setVisibility(View.GONE);
                    mMoreArea.setVisibility(View.GONE);
                    mEmotionLayout.setVisibility(View.GONE);
                    mAudioLayout.setSelected(false);
                    showSoftBoard();
                }
                else
                {
                    mMoreLayout.setVisibility(View.VISIBLE);
                    mRecordAudioLayout.setVisibility(View.VISIBLE);
                    mAudioLayout.setSelected(true);
                    mMoreArea.setVisibility(View.GONE);
                    mEmotionLayout.setVisibility(View.GONE);
                    hideSoftBoard();
                }
                break;
            case R.id.btn_picture_send:
                selectSysPictureAndSend();
                break;
            case R.id.btn_video_send:
                selectSysVideoAndSend();
                break;
            case R.id.et_txt_input:
                if (mEmotionLayout.getVisibility() == View.VISIBLE)
                {
                    mMoreLayout.setVisibility(View.GONE);
                    mEmotionLayout.setVisibility(View.GONE);
                    mMoreArea.setVisibility(View.GONE);
                    mEmotionLayout.removeAllViews();
                }
                break;
            case R.id.emotion_button:
                hideSoftBoard();
                if (mEmotionLayout.isShown())
                {
                    mEmotionLayout.removeAllViews();
                    mEmotionLayout.setVisibility(View.GONE);
                    mMoreLayout.setVisibility(View.GONE);
                    mRecordAudioLayout.setVisibility(View.GONE);
                    mMoreArea.setVisibility(View.GONE);

                }
                else
                {
                    mEmotionLayout.removeAllViews();
                    mEmotionLayout.addView(mEmotionArea);
                    mMoreLayout.setVisibility(View.VISIBLE);
                    mEmotionLayout.setVisibility(View.VISIBLE);
                    mRecordAudioLayout.setVisibility(View.GONE);
                    mMoreArea.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_chat_send:
                if (!mPresenter.isIsGroup())
                {
                    mPresenter.setInputStatus(false);
                    isInput = false;
                    stopTimer();
                    isStart = true;
                }

                ImChatMsgInfo chatMsgInfo = mPresenter.sendMessage(mInputEt.getText().toString().trim());
                if (null != chatMsgInfo)
                {
                    mPresenter.refreshViewAfterSendMessage(chatMsgInfo);
                    mInputEt.setText("");
                }
                break;
            case R.id.right_img:
//                mPresenter.gotoDetailActivity();
                break;
            case R.id.call_top:
                mPresenter.makeCall();
                break;
            default:
                break;
        }
    }

    private void selectSysPictureAndSend()
    {
        HeadIconTools.selectPicByType(HeadIconTools.SELECT_PICTURE_FROM_LOCAL);
    }

    private void selectSysVideoAndSend()
    {
        Intent intent = new Intent(IntentConstant.VIDEO_SELECTED_ACTIVITY_ACTION);
        ActivityUtil.startActivityForResult(this, intent, ChatPresenter.SELECT_VIDEO_FROM_LOCAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mPresenter.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mPresenter.isIsGroup())
        {
            return;
        }
        if (isInput)
        {
            mPresenter.setInputStatus(false);
        }
        stopTimer();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.unregisterBroadcast();
        if (null != mDeleteMsgDialog)
        {
            mDeleteMsgDialog.dismiss();
            mDeleteMsgDialog = null;
        }

        if (mPresenter.isIsGroup())
        {
            return;
        }
        if (isInput)
        {
            mPresenter.setInputStatus(false);
        }
        stopTimer();
    }

    private void hideSoftBoard()
    {
        SoftInputUtil.hideSoftInput(this, mInputEt);
    }

    private void showSoftBoard()
    {
        SoftInputUtil.showSoftInput(this, mInputEt);
    }

    private void initTimer()
    {
        mTimer = new Timer();
        myTimerTask = new MyTimerTask();
    }

    private void startTimer()
    {
        initTimer();
        try {
            mTimer.schedule(myTimerTask, 0, 1000);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            initTimer();
        }
    }

    private void stopTimer()
    {
        if (null != mTimer)
        {
            mTimer.cancel();
            mTimer = null;
        }
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            if (isStart)
            {
                lastLength = currentLength;
                mPresenter.setInputStatus(true);
                isInput = true;
                isStart = false;
                return;
            }

            if (0 == currentLength || currentLength == lastLength)
            {
                mPresenter.setInputStatus(false);
                stopTimer();
                isInput = false;
                isStart = true;
                return;
            }

            lastLength = currentLength;
        }
    }

}
