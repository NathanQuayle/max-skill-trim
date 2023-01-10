package com.maxskilltrim;

public class MaxSkillTrimFile
{
    public String name;
    public String fileName;

    public MaxSkillTrimFile(String name, String fileName)
    {
        this.name = name;
        this.fileName = fileName;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
