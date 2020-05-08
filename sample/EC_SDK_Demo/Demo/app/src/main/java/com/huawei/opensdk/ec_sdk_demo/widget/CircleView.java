package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.huawei.opensdk.ec_sdk_demo.R;

/**
 * This class is about set a round avatar.
 */
public class CircleView extends View {

    private Bitmap mBitmap;
    private Paint mPaint;

    private int width; // 视图控件的宽
    private int height; // 视图控件的高
    private float radius; // 半径

    private int bitmapWidth = 0; // 图片的宽
    private int bitmapHeight = 0; // 图片的高

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_head);
        mPaint = new Paint();
        mPaint.setAntiAlias(true); // 设置抗锯齿
        mPaint.setDither(true); // 设置防抖动

        setBitmapParams(mBitmap);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        if (0 == width || 0 == height)
        {
            return;
        }
        radius = Math.min(width, height) / 2.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        shaderBitmap();
        canvas.drawCircle(width / 2.0f, height / 2.0f, radius - 3.0f, mPaint);
    }

    /**
     * 设置要显示为头像的图片参数
     * @param bitmap
     */
    public void setBitmapParams(Bitmap bitmap)
    {
        this.mBitmap = bitmap;
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
    }

    /**
     * 图形着色处理(图像渲染)
     */
    private void shaderBitmap()
    {
        if (0 == bitmapWidth || 0 == bitmapHeight)
        {
            return;
        }

        float scale = Math.max((float) this.width / bitmapWidth, (float) this.height / bitmapHeight);
        if (scale > -0.000001 && scale < 0.000001)
        {
            return;
        }

        Matrix matrix = new Matrix();
        // 设置缩放比例
        matrix.postScale(scale, scale);
        Bitmap newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        Shader shader = new BitmapShader(newBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
    }
}
