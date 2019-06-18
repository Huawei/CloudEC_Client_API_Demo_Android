package com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.opensdk.contactservice.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;


/**
 * This class is about system head icon select activity.
 */
public class EnterpriseAddrIconSelectActivity extends BaseActivity
{
    private GridView gridView;
    private TextView tvBack;
    private ImageView ivBack;

    private int[] systemHeadIcons = {R.drawable.head0, R.drawable.head1, R.drawable.head2, R.drawable.head3, R.drawable.head4, R.drawable.head5,
            R.drawable.head6, R.drawable.head7, R.drawable.head8, R.drawable.head9};

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.activity_select_system_headphoto);
        initView();
        initData();
    }

    @Override
    public void initializeData()
    {

    }

    private void initView()
    {
        tvBack = (TextView) findViewById(R.id.title_text);
        ivBack = (ImageView) findViewById(R.id.back_iv);

        tvBack.setVisibility(View.VISIBLE);
        tvBack.setText("System Head Icon");

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        gridView = (GridView) findViewById(R.id.system_headphtoto);
        SystemPictureAdapter pictureAdapter = new SystemPictureAdapter(systemHeadIcons, this);
        gridView.setAdapter(pictureAdapter);
    }

    private void initData()
    {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int setIconResult = EnterpriseAddressBookMgr.getInstance().setSystemIcon(position);
                if (setIconResult >= 0)
                {
                    setResult(position);
                    finish();
                }
            }
        });
    }

    /**
     * The type System picture adapter.
     */
    static class SystemPictureAdapter extends BaseAdapter
    {
        private LayoutInflater inflater;
        private int[] images;

        /**
         * Instantiates a new System picture adapter.
         *
         * @param images  the images
         * @param context the context
         */
        public SystemPictureAdapter(int[] images, Context context)
        {
            inflater = inflater.from(context);
            this.images = images;
        }

        @Override
        public int getCount()
        {
            return images.length;
        }

        @Override
        public Object getItem(int position)
        {
            return images[position];
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            int imageId = images[position];
            convertView = inflater.inflate(R.layout.gridview_system_headphoto, null);
            ImageView systemImageView = (ImageView) convertView.findViewById(R.id.system_headphoto_imageview);
            systemImageView.setImageResource(imageId);
            return convertView;
        }
    }
}
