package com.huawei.opensdk.demoservice.data;

/**
 * This class is about attendee Information
 * 与会者信息类
 */
public class AddMemberEntity
{
    /**
     * name
     * 与会者名
     */
    private String name;

    /**
     * number
     * 与会者号码
     */
    private String number;

    public AddMemberEntity(String name, String number)
    {
        this.name = name;
        this.number = number;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }
}
