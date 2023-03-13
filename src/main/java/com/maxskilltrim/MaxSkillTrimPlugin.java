package com.maxskilltrim;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.GameState;
import net.runelite.api.SpritePixels;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.widgets.*;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Slf4j
@PluginDescriptor(
        name = "Max Skill Trim"
)
public class MaxSkillTrimPlugin extends Plugin
{
    private static final int SPRITE_ID = -666;
    private MaxSkillTrimPanel maxSkillTrimPanel;
    @Inject
    private MaxSkillTrimConfig maxSkillTrimConfig;
    private NavigationButton navButton;
    @Inject
    private ClientToolbar pluginToolbar;
    @Inject
    private ConfigManager configManager;
    public static final File MAXSKILLTRIMS_DIR = new File(RuneLite.RUNELITE_DIR.getPath() + File.separator + "max-skill-trims");
    private static final int SCRIPTID_STATS_INIT = 393;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    private Widget currentWidget;
    private Widget[] trims = new Widget[SkillData.values().length];

    @Provides
    MaxSkillTrimConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(MaxSkillTrimConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        if (!MAXSKILLTRIMS_DIR.exists())
        {
            MAXSKILLTRIMS_DIR.mkdirs();
        }

        addDefaultTrims();

        maxSkillTrimPanel = injector.getInstance(MaxSkillTrimPanel.class);

        BufferedImage icon;
        synchronized (ImageIO.class)
        {
            icon = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png")));
        }

        navButton = NavigationButton.builder()
                .tooltip("Max Skill Trim")
                .priority(5)
                .icon(icon)
                .panel(maxSkillTrimPanel)
                .build();

        pluginToolbar.addNavigation(navButton);

        overrideSprites();

        if (client.getGameState() == GameState.LOGGED_IN) {
            clientThread.invoke(this::buildTrimWidgetContainers);
        }
    }

    @Override
    protected void shutDown() throws Exception
    {
        pluginToolbar.removeNavigation(navButton);
        clientThread.invoke(this::removeTrimWidgetContainers);
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired event) {
        if (event.getScriptId() != SCRIPTID_STATS_INIT)	{
            return;
        }
        currentWidget = event.getScriptEvent().getSource();
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        if (event.getScriptId() == SCRIPTID_STATS_INIT && currentWidget != null) {
            buildTrim(currentWidget);
        }
    }

    private void buildTrimWidgetContainers() {
        Widget skillsContainer = client.getWidget(WidgetInfo.SKILLS_CONTAINER);
        if (skillsContainer == null) {
            return;
        }

        for (Widget skillTile : skillsContainer.getStaticChildren()) {
            buildTrim(skillTile);
        }
    }

    private void removeTrimWidgetContainers() {
        for ( Widget trim : trims ) {
            if (trim == null) {
                continue;
            }

            Widget[] children = trim.getParent().getChildren();
            for (int i = 0; i < children.length; i++) {
                if (trim == children[i]) {
                    children[i] = null;
                }
            }
        }
        trims = new Widget[SkillData.values().length];
    }

    private void buildTrim(Widget parent) {
        int idx = WidgetInfo.TO_CHILD(parent.getId()) - 1;
        SkillData skill = SkillData.get(idx);
        if (skill == null) {
            return;
        }

        Widget trim = parent.createChild(-1, WidgetType.GRAPHIC);
        trim.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
        trim.setOriginalHeight(parent.getOriginalHeight());
        trim.setOriginalWidth(parent.getOriginalWidth());
        trim.setWidthMode(parent.getWidthMode());
        trim.setHeightMode(parent.getHeightMode());
        trim.setOpacity(255);
        trim.setSpriteId(SPRITE_ID);
        JavaScriptCallback cb = ev -> updateTrim(skill, trim);

        trim.setHasListener(true);
        trim.setOnVarTransmitListener(cb);

        trim.revalidate();
        trims[idx] = trim;
    }

    private void updateTrim(SkillData skill, Widget trim) {
        final int currentXP = client.getSkillExperience(skill.getSkill());
        final int currentLevel = Experience.getLevelForXp(currentXP);

        if(currentLevel >= Experience.MAX_REAL_LEVEL) {
            trim.setOpacity(0);
        }
    }

    private void addDefaultTrims()
    {
        try
        {
            Files.copy(Objects.requireNonNull(getClass().getResourceAsStream("/full-trim.png")), Paths.get(MAXSKILLTRIMS_DIR.toString(), "/full-trim.png"), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e)
        {
            log.warn(null, e);
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals(MaxSkillTrimConfig.GROUP_NAME))
        {
            switch(event.getKey()) {
                case MaxSkillTrimConfig.SELECTED_MAX_SKILL_TRIM:
                    overrideSprites();
                    break;
                case MaxSkillTrimConfig.SHOW_NAV_BUTTON:
                    boolean showNavButton = Boolean.TRUE.toString().equals(event.getNewValue());

                    if(showNavButton) pluginToolbar.addNavigation(navButton);
                    if(!showNavButton) pluginToolbar.removeNavigation(navButton);
                    break;
            }
        }
    }

    public SpritePixels getSpritePixels()
    {
        File spriteFile = new File(MAXSKILLTRIMS_DIR + File.separator + maxSkillTrimConfig.selectedMaxSkillTrimFilename());
        if (!spriteFile.exists())
        {
            log.debug("Sprite doesn't exist (" + spriteFile.getPath() + "): ");
            return null;
        }
        try
        {
            synchronized (ImageIO.class)
            {
                BufferedImage image = ImageIO.read(spriteFile);
                return ImageUtil.getImageSpritePixels(image, client);
            }
        }
        catch (RuntimeException | IOException ex)
        {
            log.debug("Unable to find image (" + spriteFile.getPath() + "): ");
        }
        return null;
    }

    void overrideSprites()
    {
        SpritePixels spritePixels = getSpritePixels();

        if (spritePixels == null)
        {
            return;
        }

        client.getSpriteOverrides().remove(SPRITE_ID);
        client.getWidgetSpriteCache().reset();
        client.getSpriteOverrides().put(SPRITE_ID, spritePixels);
    }

    public void setImage(MaxSkillTrimFile file)
    {
        configManager.setConfiguration(MaxSkillTrimConfig.GROUP_NAME, MaxSkillTrimConfig.SELECTED_MAX_SKILL_TRIM, file.fileName);
    }
}
