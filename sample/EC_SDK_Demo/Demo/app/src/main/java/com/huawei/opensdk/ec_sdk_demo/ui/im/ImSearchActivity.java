package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

//import com.huawei.contacts.PersonalContact;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.SearchAdapter;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.im.mvp.ImSearchContract;
import com.huawei.opensdk.ec_sdk_demo.logic.im.mvp.ImSearchPresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.IntentConstant;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.ActivityUtil;
import com.huawei.opensdk.ec_sdk_demo.util.SoftInputUtil;
import com.huawei.opensdk.imservice.ImMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about search contacts list activity.
 */
public class ImSearchActivity extends MVPBaseActivity<ImSearchContract.ImSearchView, ImSearchPresenter>
        implements ImSearchContract.ImSearchView
{
    private ListView listView;
    private SearchAdapter adapter;
    private EditText inputEdit;
//    private List<PersonalContact> contactList = new ArrayList<>();
    private String mExtra;


    @Override
    protected ImSearchContract.ImSearchView createView()
    {
        return this;
    }

    @Override
    protected ImSearchPresenter createPresenter()
    {
        return new ImSearchPresenter();
    }

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.im_search_activiity);
        listView = (ListView) findViewById(R.id.im_contact_list);
        inputEdit = (EditText) findViewById(R.id.et_search);

        inputEdit.setOnEditorActionListener(new MyActionListener());
        adapter = new SearchAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
//                PersonalContact personalContact = contactList.get(position);
//                List<PersonalContact> friends = ImMgr.getInstance().getFriends();
//                for (PersonalContact contacts : friends)
//                {
//                    if (contacts.getEspaceNumber().equals(personalContact.getEspaceNumber()))
//                    {
//                        personalContact.setFriend(1);
//                        personalContact.setContactId(contacts.getContactId());
//                        personalContact.setTeamId(contacts.getTeamId());
//                    }
//                }
                if (UIConstants.GROUP_OPERATE_ADD.equals(mExtra))
                {
                    Intent intent = new Intent();
//                    intent.putExtra(UIConstants.PERSONAL_CONTACT, personalContact);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(IntentConstant.CONTACT_DETAIL_ACTIVITY_ACTION);
                    Bundle bundle = new Bundle();
//                    bundle.putSerializable(UIConstants.PERSONAL_CONTACT, personalContact);
                    intent.putExtra(UIConstants.BUNDLE_KEY, bundle);
                    ActivityUtil.startActivity(ImSearchActivity.this, intent);
                }
            }
        });
    }

    @Override
    public void initializeData()
    {
        mPresenter.registerBroadcast();
        mExtra = getIntent().getStringExtra(UIConstants.GROUP_OPERATE_MODE);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.unregisterBroadcast();
    }

    private void searchContact(String text)
    {
//        ImMgr.getInstance().searchFuzzyContact(text);
    }

//    @Override
//    public void refreshContactList(final List<PersonalContact> contactList)
//    {
//        runOnUiThread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                ImSearchActivity.this.contactList = contactList;
//                adapter.setContactList(contactList);
//                adapter.notifyDataSetChanged();
//            }
//        });
//    }

    class MyActionListener implements TextView.OnEditorActionListener
    {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
        {
            if (EditorInfo.IME_ACTION_NEXT == actionId || EditorInfo.IME_ACTION_DONE == actionId)
            {
                SoftInputUtil.hideSoftInput(ImSearchActivity.this);
                searchContact(v.getText().toString());
                return true;
            }
            return false;
        }
    }

}
