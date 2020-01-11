package com.huawei.opensdk.ec_sdk_demo.ui.conference;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.huawei.ecterminalsdk.base.TsdkConfMediaType;
import com.huawei.ecterminalsdk.base.TsdkConfRecordMode;
import com.huawei.ecterminalsdk.base.TsdkConfRole;
import com.huawei.ecterminalsdk.base.TsdkContactsInfo;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.CreateConfAdapter;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.ConfCreateContract;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.ConfCreatePresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.CommonUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.ConfirmSimpleDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.EditDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.ThreeInputDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.TripleDialog;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class CreateConfActivity extends BaseActivity implements View.OnClickListener, ConfCreateContract.ConfCreateView
{

    private ConfCreatePresenter mPresenter;
    private EditText subjectET;
    private RelativeLayout confTimeRL;
    private RelativeLayout confTypeRL;
    private RelativeLayout accessNumberRL;
    private RelativeLayout recordTypeRL;
    private TextView accessNumberTV;
    private ListView listView;
    private Button addMemberBtn;
    private TextView rightTV;
    private ImageButton clearSubjectBtn;
    private TextView startTimeText;
    private TextView confTypeText;
    private TextView recordTypeText;
    private DateEntity dateEntity;
    private LinearLayout rightButtonLL;
    private ScrollView createConfScroll;

    private CreateConfAdapter adapter;


    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.conference_create_layout);
        rightTV = (TextView) findViewById(R.id.right_text);
        subjectET = (EditText) findViewById(R.id.conf_subject_et);
        confTimeRL = (RelativeLayout) findViewById(R.id.conference_time_view);
        confTypeRL = (RelativeLayout) findViewById(R.id.rl_conference_type);
        recordTypeRL = (RelativeLayout) findViewById(R.id.record_mode_type);
        accessNumberTV = (TextView) findViewById(R.id.conference_members_number);
        listView = (ListView) findViewById(R.id.member_list);
        addMemberBtn = (Button) findViewById(R.id.add_member_btn);
        clearSubjectBtn = (ImageButton) findViewById(R.id.meeting_clear_subject);
        startTimeText = (TextView) findViewById(R.id.conf_create_time);
        confTypeText = (TextView) findViewById(R.id.tv_conference_type);
        recordTypeText = (TextView) findViewById(R.id.tv_recode_mode_type);
        accessNumberRL = (RelativeLayout) findViewById(R.id.conference_end_time_view);
        rightButtonLL = (LinearLayout) findViewById(R.id.right_img_layout);
        createConfScroll = (ScrollView) findViewById(R.id.create_conf_scroll);

        rightTV.setText(R.string.create_conf);
        rightButtonLL.setOnClickListener(this);
        confTypeRL.setOnClickListener(this);
        recordTypeRL.setOnClickListener(this);
        confTimeRL.setOnClickListener(this);
        addMemberBtn.setOnClickListener(this);
        clearSubjectBtn.setOnClickListener(this);
        accessNumberRL.setOnClickListener(this);

        listView.setAdapter(adapter);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    createConfScroll.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (0 == position)
                {
                    return true;
                }
                else
                {
                    ConfirmSimpleDialog simpleDialog = new ConfirmSimpleDialog(parent.getContext(),
                            getString(R.string.delete_participant));
                    simpleDialog.setVisible();
                    simpleDialog.setRightButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPresenter.delMember(position);
                        }
                    });
                    simpleDialog.show();
                }
                return false;
            }
        });

        //Set default subject
        String defaultSubject = LoginMgr.getInstance().getAccount() + "'s Meeting";
        mPresenter.setSubject(defaultSubject);
        mPresenter.setMediaType(TsdkConfMediaType.TSDK_E_CONF_MEDIA_VOICE);
        mPresenter.setRecordType(TsdkConfRecordMode.TSDK_E_CONF_RECORD_DISABLE);
        mPresenter.setAutoRecord(false);

        //Join the meeting as chairman
        Member chairman = new Member();
        TsdkContactsInfo contactSelf = LoginMgr.getInstance().getSelfInfo();
        if (contactSelf != null)
        {
            chairman.setDisplayName(contactSelf.getPersonName());
        }


        chairman.setNumber(LoginMgr.getInstance().getTerminal());
        chairman.setAccountId(LoginMgr.getInstance().getAccount());
        chairman.setRole(TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN);


        //Other fields are optional, and can be filled according to need

        mPresenter.addMember(chairman);

        subjectET.setText(defaultSubject);
        accessNumberTV.setText(LoginMgr.getInstance().getTerminal());
    }

    @Override
    public void initializeData()
    {
        mPresenter = new ConfCreatePresenter(this);
        adapter = new CreateConfAdapter(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.meeting_clear_subject:
                subjectET.setText("");
                break;
            case R.id.conference_time_view:
                showDatePicker();
                break;
            case R.id.rl_conference_type:
                showTypePicker();
                break;
            case R.id.record_mode_type:
                showModePicker();
                break;
            case R.id.right_img_layout:
                mPresenter.setSubject(subjectET.getText().toString());
                mPresenter.createConference();
                finish();
                break;
            case R.id.add_member_btn:
                showAddMemberDialog();
                break;
            case R.id.conference_end_time_view:
                showAccessNumberDialog();
                break;
            default:
                break;
        }
    }

    private void showAccessNumberDialog()
    {
        final EditDialog dialog = new EditDialog(this, R.string.input_access_number);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CommonUtil.hideSoftInput(CreateConfActivity.this);
                if (TextUtils.isEmpty(dialog.getText()))
                {
                    showToast(R.string.invalid_number);
                    return;
                }
                mPresenter.updateAccessNumber(dialog.getText());
            }
        });
        dialog.show();
    }

    private void showModePicker()
    {
        TripleDialog typePickerDialog = new TripleDialog(this);
        typePickerDialog.setLeftText(R.string.record_conference_disable);
        typePickerDialog.setRightText(R.string.record_conference_enable);

        typePickerDialog.setLeftButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.setRecordType(TsdkConfRecordMode.TSDK_E_CONF_RECORD_DISABLE);
                updateRecordModeView(TsdkConfRecordMode.TSDK_E_CONF_RECORD_DISABLE);
            }
        });
        typePickerDialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.setRecordType(TsdkConfRecordMode.TSDK_E_CONF_RECORD_RECORD_BROADCAST);
                updateRecordModeView(TsdkConfRecordMode.TSDK_E_CONF_RECORD_RECORD_BROADCAST);

            }
        });

        typePickerDialog.show();
    }

    private void showTypePicker()
    {
        TripleDialog typePickerDialog = new TripleDialog(this);
        typePickerDialog.setLeftText(R.string.conference_voice);

        //EC
        typePickerDialog.setRightText(R.string.conference_video);
        typePickerDialog.setThirdText(R.string.conference_voice_data);
        typePickerDialog.setFourText(R.string.conference_video_data);

        typePickerDialog.setLeftButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.setMediaType(TsdkConfMediaType.TSDK_E_CONF_MEDIA_VOICE);
                updateTypeView(TsdkConfMediaType.TSDK_E_CONF_MEDIA_VOICE);
            }
        });
        typePickerDialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //EC
                mPresenter.setMediaType(TsdkConfMediaType.TSDK_E_CONF_MEDIA_VIDEO);
                updateTypeView(TsdkConfMediaType.TSDK_E_CONF_MEDIA_VIDEO);
            }
        });
        typePickerDialog.setThirdButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.setMediaType(TsdkConfMediaType.TSDK_E_CONF_MEDIA_VOICE_DATA);
                updateTypeView(TsdkConfMediaType.TSDK_E_CONF_MEDIA_VOICE_DATA);
            }
        });
        typePickerDialog.setFourButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.setMediaType(TsdkConfMediaType.TSDK_E_CONF_MEDIA_VIDEO_DATA);
                updateTypeView(TsdkConfMediaType.TSDK_E_CONF_MEDIA_VIDEO_DATA);
            }
        });
        typePickerDialog.show();
    }

    private void updateRecordModeView(TsdkConfRecordMode type)
    {
        switch (type)
        {
            case TSDK_E_CONF_RECORD_DISABLE:
                recordTypeText.setText(R.string.record_conference_disable);
                break;
            case TSDK_E_CONF_RECORD_RECORD_BROADCAST:
                recordTypeText.setText(R.string.record_conference_enable);
                break;

            default:
                break;
        }
    }

    private void updateTypeView(TsdkConfMediaType type)
    {
        switch (type)
        {
            case TSDK_E_CONF_MEDIA_VOICE:
                confTypeText.setText(R.string.conference_voice);
                break;
            case TSDK_E_CONF_MEDIA_VIDEO:
                confTypeText.setText(R.string.conference_video);
                break;
            case TSDK_E_CONF_MEDIA_VOICE_DATA:
                confTypeText.setText(R.string.conference_voice_data);
                break;
            case TSDK_E_CONF_MEDIA_VIDEO_DATA:
                confTypeText.setText(R.string.conference_video_data);
                break;
            default:
                break;
        }
    }

    private void showDatePicker()
    {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateEntity = new DateEntity();
                dateEntity.setYear(year);
                dateEntity.setMonth(month);
                dateEntity.setDay(dayOfMonth);
                showTimePicker();
            }
        }, gregorianCalendar.get(Calendar.YEAR), gregorianCalendar.get(Calendar.MONTH), gregorianCalendar.get(Calendar.DATE));
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.sure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        datePickerDialog.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    }
                });
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.exit),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        datePickerDialog.show();
    }

    private void showTimePicker()
    {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                if (dateEntity != null)
                {
                    dateEntity.setHour(hourOfDay);
                    dateEntity.setMin(minute);
                    updateTime();
                }
            }
        }, gregorianCalendar.get(Calendar.HOUR_OF_DAY), gregorianCalendar.get(Calendar.MINUTE), true);
        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.sure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timePickerDialog.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    }
                });
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.exit),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        timePickerDialog.show();
    }

    private void updateTime()
    {
        Date date = new Date();
        date.setYear(dateEntity.getYear() - 1900);
        date.setMonth(dateEntity.getMonth());
        date.setDate(dateEntity.getDay());
        date.setHours(dateEntity.getHour());
        date.setMinutes(dateEntity.getMin());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formatStr = dateFormat.format(date);
        mPresenter.setStartTime(formatStr);
        mPresenter.setBookType(false);
        mPresenter.setDuration(120);
        startTimeText.setText(formatStr);
    }

    private void showAddMemberDialog()
    {
        final ThreeInputDialog addMemberDialog = new ThreeInputDialog(this);
        addMemberDialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                //The number of participants required, others are optional
                //If invited users need to query through conference list to conference, accountId must fill in
                if (TextUtils.isEmpty(addMemberDialog.getInput1()))
                {
                    showToast(R.string.invalid_number);
                    return;
                }

                Member attendee = new Member();
                attendee.setNumber(addMemberDialog.getInput1());
                attendee.setDisplayName(addMemberDialog.getInput2());
                attendee.setAccountId(addMemberDialog.getInput3());
                
                attendee.setRole(TsdkConfRole.TSDK_E_CONF_ROLE_ATTENDEE);
                //Other fields are optional, and can be filled according to need

                mPresenter.addMember(attendee);
            }
        });
        addMemberDialog.setHint1(R.string.input_number);
        addMemberDialog.setHint2(R.string.input_name);
        addMemberDialog.setHint3(R.string.input_account);
        addMemberDialog.show();
    }

    @Override
    public void refreshListView(List<Member> memberList)
    {
        adapter.setData(memberList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void createFailed()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showToast(R.string.create_conf_fail);
                finish();
            }
        });

    }

    @Override
    public void createSuccess()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showToast(R.string.create_conf_success);
                finish();
            }
        });
    }


    @Override
    public void updateAccessNumber(String accessNumber)
    {
        accessNumberTV.setText(accessNumber);
    }

    @Override
    public void showLoading()
    {

    }

    @Override
    public void dismissLoading()
    {

    }

    @Override
    public void showCustomToast(final int resID)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showCustomToast(resID);
            }
        });
    }

    private static class DateEntity
    {
        private int year;
        private int month;
        private int day;
        private int hour;
        private int min;

        public int getYear()
        {
            return year;
        }

        public void setYear(int year)
        {
            this.year = year;
        }

        public int getMonth()
        {
            return month;
        }

        public void setMonth(int month)
        {
            this.month = month;
        }

        public int getDay()
        {
            return day;
        }

        public void setDay(int day)
        {
            this.day = day;
        }

        public int getHour()
        {
            return hour;
        }

        public void setHour(int hour)
        {
            this.hour = hour;
        }

        public int getMin()
        {
            return min;
        }

        public void setMin(int min)
        {
            this.min = min;
        }
    }
}
