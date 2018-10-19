package com.huawei.opensdk.ec_sdk_demo.logic.im;

import com.huawei.opensdk.ec_sdk_demo.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public final class FileTypeLogic
{
    /*
    过滤条件：文档，图片，媒体（视频+音乐），其他；对应定义：0:文档; 1:图片; 2:媒体 3:其他
    文档包含：*.doc、*.docx、*.xlsx、*.xls、*.ppt、*.pptx、*.pdf、*.txt
    图片包含：*.png、*.gif、*.jpg、*.bmp
    媒体包含：*.mp4、*.webm、*.mkv 、*.mp3、*.mid、*.amr、*.awb、*.3gp、*.m4a、*.aac、*.wav、*.ogg、*.flac
    其他：……
    */
    private final static Map<String, Integer> SUFFIX_MAP = new HashMap<String, Integer>();

    static
    {
        // word
        SUFFIX_MAP.put("doc", R.drawable.gf_logo_word);
        SUFFIX_MAP.put("docx", R.drawable.gf_logo_word);
        // excel
        SUFFIX_MAP.put("xls", R.drawable.gf_logo_excel);
        SUFFIX_MAP.put("xlsx", R.drawable.gf_logo_excel);
        // ppt
        SUFFIX_MAP.put("ppt", R.drawable.gf_logo_ppt);
        SUFFIX_MAP.put("pptx", R.drawable.gf_logo_ppt);
        // pdf
        SUFFIX_MAP.put("pdf", R.drawable.gf_logo_pdf);
        // txt
        SUFFIX_MAP.put("txt", R.drawable.gf_logo_txt);
        // rar & zip
        SUFFIX_MAP.put("rar", R.drawable.gf_logo_rar);
        SUFFIX_MAP.put("zip", R.drawable.gf_logo_rar);
        // image
        SUFFIX_MAP.put("jpg", R.drawable.gf_logo_image);
        SUFFIX_MAP.put("png", R.drawable.gf_logo_image);
        SUFFIX_MAP.put("gif", R.drawable.gf_logo_image);
        SUFFIX_MAP.put("bmp", R.drawable.gf_logo_image);
        SUFFIX_MAP.put("jpeg", R.drawable.gf_logo_image);
        //vidio
        SUFFIX_MAP.put("mp4", R.drawable.gf_logo_other);
        SUFFIX_MAP.put("mkv", R.drawable.gf_logo_other);
        SUFFIX_MAP.put("webm", R.drawable.gf_logo_other);
        SUFFIX_MAP.put("mp3", R.drawable.gf_logo_other);
        SUFFIX_MAP.put("mid", R.drawable.gf_logo_other);
        SUFFIX_MAP.put("amr", R.drawable.gf_logo_other);
        SUFFIX_MAP.put("awb", R.drawable.gf_logo_other);
        SUFFIX_MAP.put("3gp", R.drawable.gf_logo_other);
        SUFFIX_MAP.put("m4a", R.drawable.gf_logo_other);
        SUFFIX_MAP.put("aac", R.drawable.gf_logo_other);
        SUFFIX_MAP.put("wav", R.drawable.gf_logo_other);
        SUFFIX_MAP.put("ogg", R.drawable.gf_logo_other);
        SUFFIX_MAP.put("flac", R.drawable.gf_logo_other);
    }

    /**
     * 私有构造方法
     */
    private FileTypeLogic()
    {
    }

    public static int getLogoIdByType(String fileName)
    {
        int start = fileName == null ? 0 : fileName.lastIndexOf(".");
        if (0 >= start || fileName.length() <= start)
        {
            return R.drawable.gf_logo_other;
        }
        // 取点后面的后缀，不包括点号
        String suffix = fileName.substring(start + 1).toLowerCase(Locale.getDefault());
        if (SUFFIX_MAP.containsKey(suffix))
        {
            return SUFFIX_MAP.get(suffix);
        }
        return R.drawable.gf_logo_other;
    }
}
