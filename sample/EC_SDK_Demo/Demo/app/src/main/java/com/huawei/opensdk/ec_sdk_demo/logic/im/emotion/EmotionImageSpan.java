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


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is about emotion image span.
 */
public class EmotionImageSpan extends ImageSpan
{
    /**
     * Instantiates a new Emotion image span.
     *
     * @param d the d
     */
    public EmotionImageSpan(Drawable d)
    {
        super(d);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint)
    {
        // TODO Auto-generated method stub
        Pattern pattern = Pattern.compile("(\\u2026|\\056\\056\\056)(\\uFEFF)+");
        Matcher matcher = pattern.matcher(text);

        int lastStart = -1;
        int lastEnd = -1;
        while (matcher.find())
        {
            lastStart = matcher.start();
            lastEnd = matcher.end();
        }

        if ((lastStart >= 0) && (lastEnd == text.length()))
        {
            if (start > lastStart)
            {
                return;
            }

            if (start == lastStart)
            {
                canvas.drawText("\u2026", x, y, paint);
                return;
            }
        }

        super.draw(canvas, text, start, end, x, top, y, bottom, paint);
    }
}
