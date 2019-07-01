package com.huawei.opensdk.ec_sdk_demo.floatView.annotation.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.floatView.annotation.common.AnnotationConstants;

import java.util.ArrayList;

public class ColorPickAdapter extends BaseAdapter {

    private ArrayList<Integer> colorList;

    private LayoutInflater mInflater;

    @Override
    public int getCount() {
        return colorList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    public ColorPickAdapter(ArrayList<Integer> colorList, Context context) {
        this.colorList = new ArrayList<Integer>();
        this.colorList.clear();
        this.colorList.addAll(colorList);
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.float_color_pick_item, null);
            viewHolder = new ViewHolder();

            viewHolder.colorImg = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.colorImg.setImageResource(getImage(colorList.get(position)));

        return convertView;
    }

    private int getImage(int id) {
        int img = 0;
        switch (id) {
            case AnnotationConstants.COLOR_BLACK:
                img = R.drawable.float_color_black;
                break;
            case AnnotationConstants.COLOR_RED:
                img = R.drawable.float_color_red;
                break;
            case AnnotationConstants.COLOR_GREEN:
                img = R.drawable.float_color_green;
                break;
            case AnnotationConstants.COLOR_BLUE:
                img = R.drawable.float_color_blue;
                break;
            default:
                break;
        }

        return img;
    }

    static class ViewHolder {
        ImageView colorImg;
    }
}
