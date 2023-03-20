package com.maxskilltrim;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.LinkBrowser;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.Objects;

@Slf4j
@Singleton
public class MaxSkillTrimPanel extends PluginPanel
{
    @Inject
    ConfigManager configManager;

    @Inject
    public MaxSkillTrimPanel(MaxSkillTrimConfig config)
    {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JComboBox<String> maxLevelComboBox = buildComboBoxPanel(MaxSkillTrimConfig.SELECTED_MAX_LEVEL_TRIM, config.getSelectedMaxLevelTrimFilename());
        JPanel maxLevelComboBoxPanel = new JPanel();
        maxLevelComboBoxPanel.add( new JLabel("Max level trim"), GroupLayout.Alignment.BASELINE);
        maxLevelComboBoxPanel.add(maxLevelComboBox, GroupLayout.Alignment.BASELINE);

        JComboBox<String> maxExperienceComboBox = buildComboBoxPanel(MaxSkillTrimConfig.SELECTED_MAX_EXPERIENCE_TRIM, config.getSelectedMaxExperienceTrimFilename());
        JPanel maxExperienceComboBoxPanel = new JPanel();
        maxExperienceComboBoxPanel.add( new JLabel("Max experience trim"), GroupLayout.Alignment.BASELINE);
        maxExperienceComboBoxPanel.add(maxExperienceComboBox, GroupLayout.Alignment.BASELINE);

        JPanel buttonPanel = buildButtonPanel(maxLevelComboBox, maxExperienceComboBox);
        JPanel getMoreTrimsPanel = buildGetMoreTrimsPanel();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(buttonPanel)
                        .addComponent(getMoreTrimsPanel)
                        .addComponent(maxLevelComboBoxPanel)
                        .addComponent(maxExperienceComboBoxPanel)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(buttonPanel)
                .addGap(10)
                .addComponent(getMoreTrimsPanel)
                .addGap(10)
                .addComponent(maxLevelComboBoxPanel)
                .addGap(10)
                .addComponent(maxExperienceComboBoxPanel)
        );
    }

    private JComboBox<String> buildComboBoxPanel(String selectedTrimConfigKey, String selectedFilename) {
        JComboBox<String> comboBox = new JComboBox<>();

        refreshComboBoxOptions(comboBox);

        comboBox.addItemListener((e) -> comboBoxEventHandler(e, selectedTrimConfigKey));

        comboBox.setSelectedItem(selectedFilename);

        return comboBox;
    }

    private void comboBoxEventHandler(ItemEvent e, String selectedTrimConfigKey)
    {
        if (e.getStateChange() == ItemEvent.SELECTED && configManager != null)
        {
            configManager.setConfiguration(MaxSkillTrimConfig.GROUP_NAME, selectedTrimConfigKey, e.getItem());
        }
    }

    private JPanel buildGetMoreTrimsPanel()
    {
        JButton getMoreTrimsButton = new JButton("Get more trims!");
        getMoreTrimsButton.addActionListener((ev) -> LinkBrowser.browse("https://github.com/NathanQuayle/max-skill-trim/tree/custom-trims"));

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(58, 58, 58)),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)));

        panel.add(getMoreTrimsButton, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildButtonPanel(JComboBox<String> maxLevelComboBox, JComboBox<String> maxExperienceComboBox)
    {
        JButton openMaxSkillTrimFileFolderButton = new JButton("Open Folder");
        openMaxSkillTrimFileFolderButton.addActionListener((ev) ->
        {
            try
            {
                Desktop.getDesktop().open(MaxSkillTrimPlugin.MAXSKILLTRIMS_DIR);
            }
            catch (Exception ex)
            {
                log.warn(null, ex);
            }
        });

        JButton refreshMaxSkillTrims = new JButton("Refresh");
        refreshMaxSkillTrims.addActionListener((ev) -> {
            refreshComboBoxOptions(maxLevelComboBox);
            refreshComboBoxOptions(maxExperienceComboBox);
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 1));

        panel.add(openMaxSkillTrimFileFolderButton);
        panel.add(refreshMaxSkillTrims);

        return panel;
    }

    private void refreshComboBoxOptions(JComboBox<String> comboBox)
    {
        Object selectedItem = comboBox.getSelectedItem();
        comboBox.removeAllItems();

        for (File f : Objects.requireNonNull(MaxSkillTrimPlugin.MAXSKILLTRIMS_DIR.listFiles()))
        {
            comboBox.addItem(f.getName());
        }

        comboBox.setSelectedItem(selectedItem);
    }
}
