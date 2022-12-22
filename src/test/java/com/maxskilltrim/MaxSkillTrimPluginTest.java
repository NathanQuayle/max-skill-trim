package com.maxskilltrim;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MaxSkillTrimPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MaxSkillTrimPlugin.class);
		RuneLite.main(args);
	}
}