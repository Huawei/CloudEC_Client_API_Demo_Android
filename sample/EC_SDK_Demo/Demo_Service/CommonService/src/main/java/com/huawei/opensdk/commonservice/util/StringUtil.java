package com.huawei.opensdk.commonservice.util;

/**
 * This class is about the type String util.+
 */
public final class StringUtil
{

    private StringUtil()
    {
    }

    /**
     * This method is used to converts String to int.
     * 将String类型转换为int类型
     *
     * @param str the str
     * @return the int
     */
    public static int stringToInt(String str)
    {
        return stringToInt(str, -1);
    }

    /**
     * This method is used to converts a string to int type.
     * 将String类型转换为int类型
     * @param str          Indicates converted String
     *                     被转换的字符串
     * @param defaultValue Indicates default value
     *                     默认值
     * @return int Return int value
     *             返回转换的int值
     */
    private static int stringToInt(String str, int defaultValue)
    {
        if (str == null)
        {
            return defaultValue;
        }

        try
        {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException var3)
        {
            return defaultValue;
        }

    }

    /**
     * This method is used to Convert strings to numbers.
     * 将字符串转换为数字
     *
     * @param source the source
     * @return the int
     */
    public static int parseInt(String source)
    {
        try
        {
            int target = Integer.parseInt(source);
            return target;
        } catch (NumberFormatException e)
        {
            return -1;
        }
    }

    /**
     * This method is used to remove string.
     *
     * @param source the source
     * @param pos    the pos
     * @param c      the c
     * @return the string
     */
    public static String remove(String source, int pos, char c)
    {
        String result = source;
        if (source != null && !"".equals(source))
        {
            if (pos >= 0 && pos < source.length())
            {
                if (c == source.charAt(pos))
                {
                    result = source.substring(pos + 1);
                }

                return result;
            }
            else
            {
                return source;
            }
        }
        else
        {
            return "";
        }
    }

    /**
     * This method is used to is fail boolean.
     *
     * @param retCode the ret code
     * @return the boolean
     */
    public static boolean isFail(String retCode)
    {
        return retCode == null || retCode.equals("-1") || retCode.equals("-2") || retCode.equals("-7");
    }

    /**
     * This method is used to parse string
     * 解析字符串
     *
     * @param retStr the ret str
     * @return boolean Return true /false
     */
    public static boolean parseString(String retStr)
    {
        boolean bRet = false;
        if (null != retStr && "0".equals(retStr))
        {
            bRet = true;
        }
        return bRet;
    }
}
