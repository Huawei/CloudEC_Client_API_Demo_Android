/*
 * Copyright 2015 Huawei Technologies Co., Ltd. All rights reserved.
 * eSDK is licensed under the Apache License, Version 2.0 ^(the "License"^);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.opensdk.ec_sdk_demo.logic.im.emotion;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.ec_sdk_demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is about spannable string parser.
 */
public class SpannableStringParser
{
    private final Pattern emotionPattern;
    private final Pattern numberPattern;

    /**
     * Instantiates a new Spannable string parser.
     */
    public SpannableStringParser()
    {
        numberPattern = Pattern.compile("(/:|/){0,1}[0-9]+");
        emotionPattern = Pattern.compile("(\\u2060)*(/:D|/:\\)|/:\\*"
                + "|/:8|/D~|/\\-\\(|/\\-O|/:\\$|/CO|/YD|/;\\)|/;P"
                + "|/:!|/:0|/GB|/:S|/:\\?|/:Z|/88|/SX"
                + "|/TY|/OT|/NM|/\\:X|/DR|/:<|/ZB|/BH|/HL"
                + "|/XS|/YH|/KI|/DX|/KF|/KL|/LW|/PG|/XG"
                + "|/CF|/TQ|/DH|/\\*\\*|/@@|/:\\{|/FN|/0\\(|/;>"
                + "|/FD|/ZC|/JC|/ZK|/:\\(|/LH|/SK|/\\$D|/CY"
                + "|/\\%S|/LO|/PI|/DB|/MO|/YY|/FF|/ZG|/;I"
                + "|/XY|/MA|/GO|/\\%@|/ZD|/SU|/MI|/BO"
                + "|/GI|/DS|/YS|/DY|/SZ|/DP|/:\\\\)");  //remove|/00

    }

    private int getIndex(String tagName, String[] array)
    {
        int length = array.length;
        for (int i = 0; i < length; i++)
        {
            if (tagName.equals(array[i]))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets emotion span.
     *
     * @param tagName the tag name
     * @return the emotion span
     */
    public EmotionImageSpan getEmotionSpan(String tagName)
    {
        String name = tagName;
        if (tagName.indexOf("\u2060") == 0)
        {
            int start = tagName.lastIndexOf("\u2060");
            name = tagName.substring(start + 1);
        }
        int index = getIndex(name, ChatCommon.EMOTION_STR_OLD.split("\\|"));
        //int index = getIndex(tagName, ChatCommon.EMOTION_STR.split("\\|"));
        if (index != -1)
        {
            Drawable drawable = LocContext.getResources().getDrawable(R.drawable.emotion01 + index);
            if (drawable != null)
            {
                int width = Math.round(20f * (LocContext.getResources().getDisplayMetrics().density));
                drawable.setBounds(0, 0, width, width);
                return new EmotionImageSpan(drawable);
            }
        }
        return null;
    }

    /**
     * Parse span char sequence.
     *
     * @param text the text
     * @return the char sequence
     */
    public CharSequence parseSpan(String text)
    {
        if (TextUtils.isEmpty(text))
        {
            return text;
        }
        SpannableString ss = null;
        try
        {
            ss = parseEmotion(text);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ss == null ? text : ss;
    }


    /**
     * Parse emotion spannable string.
     *
     * @param text the text
     * @return the spannable string
     */
    public SpannableString parseEmotion(String text)
    {

        List<Tag> emotionTags = getTags(text, emotionPattern);
        if (emotionTags == null || emotionTags.isEmpty())
        {
            return null;
        }

        SpannableString ss = SpannableString.valueOf(text);
        EmotionImageSpan span1;
        for (Tag emotion : emotionTags)
        {
            span1 = getEmotionSpan(emotion.getTagStr());
            if (span1 != null)
            {
                ss.setSpan(span1, emotion.getIndexBegin(), emotion.getIndexEnd(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return ss;
    }

    /**
     * Parse inner emotion spannable string.
     *
     * @param text the text
     * @return the spannable string
     */
    public SpannableString parseInnerEmotion(String text)
    {
        List<Tag> listTag = getTags(text, emotionPattern);
        if (listTag == null || listTag.isEmpty())
        {
            return new SpannableString(text);
        }
        SpannableString ss = SpannableString.valueOf(text);
        EmotionImageSpan span1;
        for (Tag t : listTag)
        {
            span1 = getEmotionSpan(t.getTagStr());
            if (span1 != null)
            {
                ss.setSpan(span1, t.getIndexBegin(), t.getIndexEnd(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return ss;
    }

    private static class Tag
    {
        /**
         * The Index begin.
         */
        int indexBegin;
        /**
         * The Index end.
         */
        int indexEnd;
        /**
         * The Tag str.
         */
        String tagStr;

        /**
         * Gets index begin.
         *
         * @return the index begin
         */
        public int getIndexBegin()
        {
            return indexBegin;
        }

        /**
         * Sets index begin.
         *
         * @param indexBegin the index begin
         */
        public void setIndexBegin(int indexBegin)
        {
            this.indexBegin = indexBegin;
        }

        /**
         * Gets index end.
         *
         * @return the index end
         */
        public int getIndexEnd()
        {
            return indexEnd;
        }

        /**
         * Sets index end.
         *
         * @param indexEnd the index end
         */
        public void setIndexEnd(int indexEnd)
        {
            this.indexEnd = indexEnd;
        }

        /**
         * Gets tag str.
         *
         * @return the tag str
         */
        public String getTagStr()
        {
            return tagStr;
        }

        /**
         * Sets tag str.
         *
         * @param tagStr the tag str
         */
        public void setTagStr(String tagStr)
        {
            this.tagStr = tagStr;
        }
    }

    private List<Tag> getTags(String text, Pattern pattern)
    {
        List<Tag> listTag = new ArrayList<Tag>();
        if (text == null || text.length() == 0)
        {
            return null;
        }
        Matcher urlMatcher = pattern.matcher(text);
        int indexBegin;
        int indexEnd;
        Tag t;
        while (urlMatcher.find())
        {
            indexBegin = urlMatcher.start();
            indexEnd = urlMatcher.end();
            t = new Tag();
            t.setIndexBegin(indexBegin);
            t.setIndexEnd(indexEnd);
            t.setTagStr(text.substring(indexBegin, indexEnd));
            if (pattern.equals(numberPattern))
            {
                if (t.getTagStr().length() <= 21 && t.getTagStr().length() >= 5)
                {
                    listTag.add(t);
                }
            }
            else
            {
                listTag.add(t);
            }
        }
        return listTag;
    }
}
