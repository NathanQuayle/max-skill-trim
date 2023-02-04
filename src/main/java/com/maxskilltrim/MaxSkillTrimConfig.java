package com.maxskilltrim;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(MaxSkillTrimConfig.GROUP_NAME)
public interface MaxSkillTrimConfig extends Config
{
    String GROUP_NAME = "maxskilltrim";
    String SELECTED_MAX_SKILL_TRIM = "selectedMaxSkillTrim";
    String SHOW_NAV_BUTTON = "showNavButton";

    @ConfigItem(
            keyName = SELECTED_MAX_SKILL_TRIM,
            name = "Selected max skill trim",
            description = "Name of the selected max skill trim",
            hidden = true
    )
    default String selectedMaxSkillTrimFilename()
    {
        return "full-trim.png";
    }

    @ConfigItem(
            keyName = SHOW_NAV_BUTTON,
            name = "Show navigation button in sidebar?",
            description = "Toggles whether or not show the navigation button (icon) in the Runelite sidebar"
    )
    default boolean showNavButton() { return true; }
}
