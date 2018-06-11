package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.logic.im.emotion.ChatCommon;


/**
 * This class is about Emotion adapter.
 */
public class EmotionAdapter extends BaseAdapter
{
    private String[] emotionStrings;

    private LayoutInflater mInflater = null;

    private ViewHolder holder = null;

    /**
     * Instantiates a new Emotion adapter.
     *
     * @param context the context
     */
    public EmotionAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
        emotionStrings = ChatCommon.EMOTION_STR.split("\\|");
    }


    @Override
    public int getCount()
    {
        return emotionStrings.length - 1;
    }

    @Override
    public Object getItem(int position)
    {
        return emotionStrings[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (null == convertView)
        {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.emotion_item, null);
            holder.image = (ImageView) convertView.findViewById(R.id.emotion_image);
            holder.image.setClickable(false);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.image.setImageResource(R.drawable.emotion01 + position);

        return convertView;
    }

    private static class ViewHolder
    {
        /**
         * emotion
         */
        public ImageView image;

    }
}
