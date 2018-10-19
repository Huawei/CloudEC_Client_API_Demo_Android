package com.huawei.opensdk.ec_sdk_demo.ui.eaddrbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.huawei.opensdk.ec_sdk_demo.R;

import java.io.File;
import java.io.FileInputStream;

/**
 * This class is about Tool class to get the user's avatar.
 */
public class EnterpriseAddrTools {

    private static int[] systemIcon = {R.drawable.head0, R.drawable.head1,
            R.drawable.head2, R.drawable.head3, R.drawable.head4, R.drawable.head5,
            R.drawable.head6, R.drawable.head7, R.drawable.head8, R.drawable.head9, R.drawable.default_head};

    public static int[] getSystemIcon() {
        return systemIcon.clone();
    }

    /**
     * Get avatar through a custom avatar path.
     * @param path the path
     * @return
     */
    public static Bitmap getBitmapByPath(String path)
    {
        Bitmap bitmap = null;
        if (isIconExists(path))
        {
            try
            {
                FileInputStream inputStream = new FileInputStream(path);
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
            catch (Exception e)
            {
                Log.e("Eaddr", e.getMessage());
            }
        }
        return bitmap;
    }

    /**
     * Determine if the path exists.
     * @param path the path
     * @return the result
     */
    private static boolean isIconExists(String path)
    {
        if (new File(path).exists())
        {
            return true;
        }
        return false;
    }
}
