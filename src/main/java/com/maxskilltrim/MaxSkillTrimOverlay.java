package com.maxskilltrim;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;;

@Slf4j
public class MaxSkillTrimOverlay extends Overlay
{
    @Inject
    private Client client;
    @Inject
    private MaxSkillTrimPlugin plugin;

    public MaxSkillTrimOverlay()
    {
        setPosition(OverlayPosition.DYNAMIC);
        drawAfterInterface(WidgetID.SKILLS_GROUP_ID);
    }

    /**
     * @param graphics
     * @return
     */
    @Override
    public Dimension render(Graphics2D graphics)
    {
        Widget skillsWidget = client.getWidget(WidgetInfo.SKILLS_CONTAINER);

        if (skillsWidget.isHidden())
        {
            return null;
        }

        for (Widget w : skillsWidget.getStaticChildren())
        {
            int idx = WidgetInfo.TO_CHILD(w.getId()) - 1;
            SkillData skill = SkillData.get(idx);

            if (skill == null) continue;

            final int currentXP = client.getSkillExperience(skill.getSkill());
            final int currentLevel = Experience.getLevelForXp(currentXP);

            if (currentLevel < Experience.MAX_REAL_LEVEL) continue;

            Rectangle bounds = w.getBounds();
            graphics.drawImage(plugin.currentTrimImage, bounds.x, bounds.y, null);
        }

        return null;
    }
}
