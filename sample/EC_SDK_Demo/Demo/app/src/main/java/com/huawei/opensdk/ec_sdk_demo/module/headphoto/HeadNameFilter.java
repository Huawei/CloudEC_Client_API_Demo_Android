package com.huawei.opensdk.ec_sdk_demo.module.headphoto;

import java.io.File;
import java.io.FilenameFilter;


public class HeadNameFilter implements FilenameFilter
{
    String filter;

    HeadNameFilter(String filter)
    {
        this.filter = filter;
    }

    @Override
    public boolean accept(File dir, String filename)
    {
        if (HeadPhotoUtil.SUFFIX.equalsIgnoreCase(filter))
        {
            return filename.endsWith(HeadPhotoUtil.SUFFIX);
        }
        else
        {
            return matchEspaceNumber(filename, filter);
        }
    }

    private boolean matchEspaceNumber(String fileName, String eSpaceNum)
    {
        int index = fileName.indexOf(HeadPhotoUtil.SEPARATOR);
        if (-1 != index)
        {
            String str = fileName.substring(0, index);
            return str.equals(eSpaceNum);
        }
        return false;
    }
}
