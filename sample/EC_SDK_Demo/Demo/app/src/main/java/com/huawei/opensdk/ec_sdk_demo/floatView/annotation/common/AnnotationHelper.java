package com.huawei.opensdk.ec_sdk_demo.floatView.annotation.common;

import com.huawei.opensdk.ec_sdk_demo.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AnnotationHelper {


    private HashMap<Integer,Integer> colorMap = new HashMap<Integer,Integer>();

    private ArrayList<Integer> colorList = new ArrayList<Integer>();

    public int getAnnoColorImg(int color) {
        int img = 0;
        switch (color) {
            case AnnotationConstants.COLOR_BLACK:
                img = R.drawable.float_anno_color_black;
                break;
            case AnnotationConstants.COLOR_RED:
                img = R.drawable.float_anno_color_red;
                break;
            case AnnotationConstants.COLOR_GREEN:
                img = R.drawable.float_anno_color_green;
                break;
            case AnnotationConstants.COLOR_BLUE:
                img = R.drawable.float_anno_color_blue;
                break;
            default:
                break;
        }

        return img;
    }

    public int getAnnoPenImg(int color) {
        int img = 0;

        switch (color) {
            case AnnotationConstants.COLOR_BLACK:
                img = R.drawable.float_anno_pen_black;
                break;
            case AnnotationConstants.COLOR_RED:
                img = R.drawable.float_anno_pen_red;
                break;
            case AnnotationConstants.COLOR_GREEN:
                img = R.drawable.float_anno_pen_green;
                break;
            case AnnotationConstants.COLOR_BLUE:
                img = R.drawable.float_anno_pen_blue;
                break;
            default:
                break;
        }

        return img;
    }

    public void initColorList() {
        colorList.clear();
        colorList.add(AnnotationConstants.COLOR_BLACK);
        colorList.add(AnnotationConstants.COLOR_RED);
        colorList.add(AnnotationConstants.COLOR_GREEN);
        colorList.add(AnnotationConstants.COLOR_BLUE);

        colorMap.clear();
        colorMap.put(AnnotationConstants.COLOR_BLACK, AnnotationConstants.PEN_COLOR_BLACK);
        colorMap.put(AnnotationConstants.COLOR_BLUE, AnnotationConstants.PEN_COLOR_BLUE);
        colorMap.put(AnnotationConstants.COLOR_GREEN, AnnotationConstants.PEN_COLOR_GREEN);
        colorMap.put(AnnotationConstants.COLOR_RED, AnnotationConstants.PEN_COLOR_RED);
    }

    public HashMap<Integer, Integer> getColorMap() {
        return colorMap;
    }

    public ArrayList<Integer> getColorList() {
        return colorList;
    }
}
