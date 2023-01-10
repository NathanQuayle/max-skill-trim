package com.maxskilltrim;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;

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
    public BufferedImage currentTrimImage;
    @Inject
    private MaxSkillTrimOverlay overlay;
    @Inject
    private OverlayManager overlayManager;
    private MaxSkillTrimPanel maxSkillTrimPanel;
    @Inject
    private MaxSkillTrimConfig maxSkillTrimConfig;
    private NavigationButton navButton;
    @Inject
    private ClientToolbar pluginToolbar;
    @Inject
    private ConfigManager configManager;
    public static final File MAXSKILLTRIMS_DIR = new File(RuneLite.RUNELITE_DIR.getPath() + File.separator + "max-skill-trims");

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

        overlayManager.add(overlay);

        maxSkillTrimPanel = injector.getInstance(MaxSkillTrimPanel.class);

        BufferedImage icon;
        synchronized (ImageIO.class)
        {
            icon = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png")));
            currentTrimImage = ImageIO.read(new File(MAXSKILLTRIMS_DIR, maxSkillTrimConfig.selectedMaxSkillTrimFilename()));
        }

        navButton = NavigationButton.builder()
                .tooltip("Max Skill Trim")
                .priority(5)
                .icon(icon)
                .panel(maxSkillTrimPanel)
                .build();

        pluginToolbar.addNavigation(navButton);
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

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        pluginToolbar.removeNavigation(navButton);
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals(MaxSkillTrimConfig.GROUP_NAME) && event.getKey().equals(MaxSkillTrimConfig.SELECTED_MAX_SKILL_TRIM))
        {
            try
            {
                synchronized (ImageIO.class)
                {
                    currentTrimImage = ImageIO.read(new File(MAXSKILLTRIMS_DIR, event.getNewValue()));
                }
            }
            catch (IOException ioException)
            {
                log.warn(null, ioException);
            }
        }
    }

    public void setImage(MaxSkillTrimFile file)
    {
        configManager.setConfiguration(MaxSkillTrimConfig.GROUP_NAME, MaxSkillTrimConfig.SELECTED_MAX_SKILL_TRIM, file.fileName);
    }
}
